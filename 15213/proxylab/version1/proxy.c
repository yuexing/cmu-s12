/**
 * proxy.c
 *
 * Yue Xing, Tiantian Xu
 * yuexing@andrew.cmu.edu, tiantiax@andrew.cmu.edu
 *
 * Feature:
 * Efficiency:
 * According to HTTP 1.0, the resp/req should contain a content-length header,
 * or close the connection to notify the end of message. 
 * According to socket, read will block when being blocked; will return 0 when
 * connection is closed; will return -1 when error.
 * Parse Status code(1xx, 204, 304), Content-length to stop read for efficiency.
 *
 * HTTP:
 * 1) Robust parse_url. support [http(s):\\]host[:port][uri]
 * 2) support https, POST, PUT, DELETE, OPTIONS, GET, CONNECT
 *
 * Cache:
 * 1) strict LUR(sleep to wait when full but can not evict the LU)
 *
 * Reference:
 * http://www.ietf.org/rfc/rfc1945.txt
 * http://www.ietf.org/rfc/rfc2616.txt
 */

#include "proxy.h"
#include "cache.h"

static const char *user_agent = "User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:10.0.3) Gecko/20120305 Firefox/10.0.3\r\n";
static const char *accept_str = "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n";
static const char *accept_encoding = "Accept-Encoding: gzip, deflate\r\n";
static const char *connection_str = "Connection:close\r\nProxy-Connection:close\r\n";
/********************************************************
 * Util functions:
 *
 * parse_url : from http(s)://host:port+uri to extract uri,
 * host and port.
 *
 * is_body_empty: parse status line to determine whether there 
 * is no body.
 *
 * get_size: parse a content-length entity header to extract 
 * size, which determines how many bytes to read from the body 
 *
 * fwd_* : transfer fix size, bin(unknown size) from src_rp to dest_fd,
 * on error return -1. for non-GET 
 *
 * wrt_cache_*: write and cache buf, on error return -1. for GET
 *******************************************************/
static int parse_url(const char *url, char *uri, char *host, 
		unsigned short *portp);
static void is_body_empty(char *status_line, int *emptyp);
static void get_size(char *buf, int *sizep);
static int fwd_bin(rio_t *src_rp, int dest_fd);
static int fwd_fix_size(rio_t *src_rp, int dest_fd, int csize);
static int wrt_cache_asc(int fd, int *tsizep, char **cache, 
		char **pp, char *buf);
static int wrt_cache_bin(int fd, int *tsizep, char **cache,
		char **pp, char *buf, int length);
static int wrt_cache_b(int fd, char **cache, char **pp, 
		char *buf, int length);

/**
 * open a listen socket on the port defined by cmd
 * loop to accept conns
 * once accept one conn
 * spawn a new thread running proxy to handle it
 */
int main(int argc, char **argv)
{
	int listenfd, *connfdp, port;
	socklen_t clientlen;
	struct sockaddr_in clientaddr;
	pthread_t tid;

	/* set signal handler */
	Signal(SIGPIPE, SIG_IGN);
	/* init cache */
	Cache_init();

	/* Check command line args */
	if (argc != 2) {
		fprintf(stderr, "usage: %s <port>\n", argv[0]);
		exit(1);
	}
	port = atoi(argv[1]);

	listenfd = Open_listenfd(port);
	while (1) {
		clientlen = sizeof(clientaddr);
		while((connfdp = (int *)malloc(sizeof(int))) == NULL){
			sleep(1); /* keep trying malloc */
		}
		*connfdp = Accept(listenfd, (SA *)&clientaddr, &clientlen);
		Pthread_create(&tid, NULL, proxy, connfdp);
	}	
	return 0;
}

/**
 * parse request line
 * if non-GET, just FWD request and response
 * else(GET), if cached, return
 * else parse request and response
 */
void *proxy(void *vargp) 
{
	Pthread_detach(pthread_self());
	int browser_fd = *(int *)vargp;
	Free(vargp);

	rio_t browser_rio, server_rio;
	char buf[MAXLINE], method[MAXLINE], 
		 url[MAXLINE], version[MAXLINE],
		 uri[MAXLINE], host[MAXLINE];

	unsigned short port;
	int client_fd, cstat;

	Rio_readinitb(&browser_rio, browser_fd);

	/***************************
	 * parse request line 
	 ***************************/
	if(rio_readlineb(&browser_rio, buf, MAXLINE) == -1){
		Close(browser_fd);
		pthread_exit(NULL);
	}
	sscanf(buf, "%s %s %s", method, url, version);
	th_p("thread: %lu %s\n", pthread_self(), url);
	
	/* connect and init server rio */
	if(parse_url(url, uri, host, &port) == -1){
		Close(browser_fd);
		th_p("thread: %lu %s exit: parse uri error\n", pthread_self(), url);
		pthread_exit(NULL);
	}
	client_fd = Open_clientfd(host, port);
	if(client_fd == -1){
		Close(browser_fd);
		th_p("thread: %lu %s exit: open conn error\n", pthread_self(), url);
		pthread_exit(NULL);
	}
	Rio_readinitb(&server_rio, client_fd);

	/**************************
	 * non-GET 
	 ****************************/
	if (strcasecmp(method, "GET")) { 
		/*
		 * accoring to HTTP/1.1
		 * ignore CONNECT which trys to turn proxy as tunnel
		 * however, this will disturb working with Google
		 * */
		if(fwd_oth_req(&browser_rio, client_fd, buf) == -1){
			Close(browser_fd);
			Close(client_fd);
			th_p("thread: %lu %s exit: fwd error\n", pthread_self(), url);
			pthread_exit(NULL);
		}
		fwd_oth_resp(&server_rio, browser_fd);
		Close(client_fd);
		Close(browser_fd);
		th_p("thread: %lu %s exit\n", pthread_self(), url);
		pthread_exit(NULL);
	}

	/*****************************
	 * GET 
	 *****************************/
	if((cstat = Get_cache(url, browser_fd)) == -1){
		Close(browser_fd);
		Close(client_fd);
		th_p("thread: %lu %s exit: cached wrt error\n", pthread_self(), url);
		pthread_exit(NULL);
	}

	if(cstat == CACHED){
		Close(browser_fd);
		Close(client_fd);
		th_p("thread: %lu %s exit: cached\n", pthread_self(), url);
		pthread_exit(NULL);
	}

	if(parse_get_req(&browser_rio, client_fd, uri, host, port) == -1){
		Close(browser_fd);
		Close(client_fd);
		th_p("thread: %lu %s exit: parse get error\n", pthread_self(), url);
		pthread_exit(NULL);
	}
	parse_get_resp(&server_rio, browser_fd, url);
	Close(browser_fd);
	Close(client_fd);
	th_p("thread: %lu %s exit\n", pthread_self(), url);
	pthread_exit(NULL);
}

/*
 * forward request line, header (and maybe a body)
 */
int fwd_oth_req(rio_t *rp, int client_fd, char *rline) 
{
	char buf[MAXLINE];
	int  nrw = 0, csize = 0;
	req_p("fwd_request: %s", rline);

	nrw = rio_writen(client_fd, rline, strlen(rline));/* request line*/
	if(nrw == -1){
		return -1;
	}

	while((nrw = rio_readlineb(rp, buf, MAXLINE))) {
		if(nrw == -1){
			return -1;
		}
		
		/* get content-length */
		get_size(buf, &csize);

		nrw = rio_writen(client_fd, buf, strlen(buf));
		if(nrw == -1){
			return -1;
		}

		if(!strcmp(buf, "\r\n")){ /* forward until end */
			break;
		}
	}

	if(csize > 0){
		/* fwd request message body */
		return fwd_fix_size(rp, client_fd, csize);
	}
	return 0;
}

/*
 * read from server 
 * parse status line for status 'cause some status do not need body
 * parse resp header for content length
 * forward header and content to browser
 * as to header, readlineb;
 * as for content, fwd_bin(unknown size); fwd_fix_size(known size)
 */
int fwd_oth_resp(rio_t *server_rp, int browser_fd){
	char buf[MAXLINE];
	int	csize = 0, /* content length */
		nrw = 0,
		empty = 0;

	/* parse status line for empty body */
	if((nrw = rio_readlineb(server_rp, buf, MAXLINE)) == -1 ){
		return -1;
	}

	is_body_empty(buf, &empty);	

	if((nrw = rio_writen(browser_fd, buf, strlen(buf))) == -1){
			return -1;
	}

	/* parse response header */
	while((nrw = rio_readlineb(server_rp, buf, MAXLINE))){
		if(nrw == -1){
			return -1;
		}

		/* get content-length */
		get_size(buf, &csize);

		if((nrw = rio_writen(browser_fd, buf, strlen(buf))) == -1){
			return -1;
		}

		if(!strcmp(buf, "\r\n")){
			break;
		}
	}

	if(empty){
		/* empty body */
		return 0;
	}

	if(csize == 0){ /* unknown content-length */
		return fwd_bin(server_rp, browser_fd);	
	}else{
		return fwd_fix_size(server_rp, browser_fd, csize);
	}
	return 0;
}

/**
 * read from browser:
 * 1. REQ hdrs: always send Host and some other required hdrs,
 * forward every other hdr from browser, except "\r\n";
 * 2. End req with \r\n
 */
int parse_get_req(rio_t *browser_rp, int client_fd, 
		char *uri, char *host, unsigned short port){

	char buf[MAXLINE], get[MAXLINE];
	int nrw = 0;

	/* record req hdr status to complete request headers */
	int conn = 0, ua = 0, acc = 0, encd = 0, ihost = 0;  

	sprintf(get, "GET %s HTTP/1.0\r\n", uri);
	if(rio_writen(client_fd, get, strlen(get)) == -1){
		return -1;
	}
	req_p("%s", get);

	/* append modified or original hdr */
	while((nrw = rio_readlineb(browser_rp, buf, MAXLINE))){
		if(nrw == -1){
			return -1;
		}
		if(strcasestr(buf, "Host:")){
			ihost = 1;
		}else if(strcasestr(buf, "Connection:")){
			strcpy(buf, connection_str);
			conn = 1;
		}else if(strcasestr(buf, "User-Agent:")){
			strcpy(buf, user_agent);
			ua = 1;
		}else if(strcasestr(buf, "Accept:")){
			strcpy(buf, accept_str);
			acc = 1;
		}else if(strcasestr(buf, "Accept-Encoding:")){
			strcpy(buf, accept_encoding);
			encd = 1;
		}

		if(!strcmp(buf, "\r\n") || !strcmp(buf, "\n")){
			/* the end line or from catnet */
			break;
		}

		if(rio_writen(client_fd, buf, strlen(buf)) == -1){
			return -1;
		}

		req_p("%s", buf);
	}

	/* add required hdrs if not exist */
	if(!ihost){
		sprintf(buf, "Host: %s:%hu\r\n", host, port);
		nrw = rio_writen(client_fd, buf, strlen(buf));
		if(nrw == -1){
			return -1;
		}
		req_p("%s", buf);
	}
	if(!conn){
		strcpy(buf, connection_str);
		nrw = rio_writen(client_fd, buf, strlen(buf));
		if(nrw == -1){
			return -1;
		}
		req_p("%s", buf);
	}
	if(!ua){
		strcpy(buf, user_agent);
		nrw = rio_writen(client_fd, buf, strlen(buf));
		if(nrw == -1){
			return -1;
		}
		req_p("%s", buf);
	}
	if(!acc){
		strcpy(buf, accept_str);
		nrw = rio_writen(client_fd, buf, strlen(buf));
		if(nrw == -1){
			return -1;
		}
		req_p("%s", buf);
	}
	if(!encd){
		strcpy(buf, accept_encoding);
		nrw = rio_writen(client_fd, buf, strlen(buf));
		if(nrw == -1){
			return -1;
		}
		req_p("%s", buf);
	}

	/* end request */
	strcpy(buf, "\r\n");
	if(rio_writen(client_fd, buf, strlen(buf)) == -1){
		return -1;
	}

	return 0;
}

/**
 * 1. prepare to cache;
 * 2.1 parse status line for status 'cause some status do not need body
 * 2.2 parse response header to get the content length and fwd all resp hdrs;
 * 3. as to content, apply fwd_fix_size(known size), fwd_bin(unknown size);
 * 4. while reading, fwd, cache;
 *
 * CACHE discard:
 * 1. record total size from parsing hdrs;
 * 2. after parsing hdrs, total size += content-length, if too big;
 * 3. while parsing, monitor total size;
 * 4. when pipe broken
 */
int parse_get_resp(rio_t *server_rp, int browser_fd, char *url){

	char buf[MAXLINE];
	char *ptr = NULL, *p = NULL;
	int tsize = 0, /* total size for cache */
		csize = 0, /* content length */
		nrw = 0,
		empty = 0;

	/* *****************************
	 * prepare to cache 
	 * *****************************/
	p = ptr = malloc(MAX_OBJECT_SIZE);
	if(!p){
		return -1;
	}

	/* *******************************
	 * parse status line 
	 * *******************************/
	if((rio_readlineb(server_rp, buf, MAXLINE)) == -1){
		if(ptr){
			Free(ptr);
			ptr = NULL;
		}
		return -1;
	}

	resp_p("%s", buf);
	is_body_empty(buf, &empty);

	/* write ascii to browser and cache */
	if(wrt_cache_asc(browser_fd, &tsize, &ptr, &p, buf) == -1){
		return -1;
	}

	/* ****************************************
	 * parse response header and fwd to browser 
	 * ****************************************/
	while((nrw = rio_readlineb(server_rp, buf, MAXLINE))){
		if(nrw == -1){
			if(ptr){
				Free(ptr);
				ptr = NULL;
			}
			return -1;
		}

		resp_p("%s", buf);
		/* get content-length */
		get_size(buf, &csize);	

		/* write ascii to browser and cache */
		if(wrt_cache_asc(browser_fd, &tsize, &ptr, &p, buf) == -1){
			return -1;
		}

		if(!strcmp(buf, "\r\n")){
			break;
		}
	}//end header

	if(empty){ /* empty body */
		/* ********************************
		 * create cache 
		 * ********************************/
		if(ptr){
			if(Create_cache(url, ptr, tsize) == -1){
				return -1;
			}
		}
		return 0;
	}

	/* ****************************************
	 * parse response content and fwd to browser 
	 * ****************************************/
	if(csize == 0){
		/* unknown content-length */
		while((nrw = rio_readnb(server_rp, buf, MAXLINE))){
			if(nrw == -1){
				if(ptr){
					Free(ptr);
					ptr = NULL;
				}
				return -1;
			}

			/* write to browser and cache */
			if(wrt_cache_bin(browser_fd, &tsize, &ptr, &p,
						buf, nrw) == -1){
				return -1;
			}	
		}
	}else{
		/* content-length available */
		if((tsize += csize) > MAX_OBJECT_SIZE){
			/* discard as too big */
			if(ptr){
				Free(ptr);
				ptr = NULL;
			}
		}

		/* to read >= MAXLINE, batch read, write, cache */
		while(csize >= MAXLINE){
			if((nrw = rio_readnb(server_rp, buf, MAXLINE)) == -1){
				if(ptr){
					Free(ptr);
					ptr = NULL;
				}
				return -1;
			}

			/* write to browser and cache */
			if(wrt_cache_b(browser_fd, &ptr, &p, buf, nrw) == -1){
				return -1;
			}

			csize -= MAXLINE;
		}

		/* to read remaining, read, write, cache */
		if(csize > 0){
			if((nrw = rio_readnb(server_rp, buf, csize)) == -1){
				if(ptr){
					Free(ptr);
					ptr = NULL;
				}
				return -1;
			}

			/* write to browser and cache */
			if(wrt_cache_b(browser_fd, &ptr, &p, buf, nrw) == -1){
				return -1;
			}
		}
	}
	/* ********************************
	 * create cache 
	 * ********************************/
	if(ptr){
		if(Create_cache(url, ptr, tsize) == -1){
			return -1;
		}
	}

	return 0;
}

/**
 * Robust parse_url
 * from [http(s)://]host[:port][uri]
 * to extract uri, host, port
 */
static int parse_url(const char *aurl, char *uri, char *host, unsigned short *portp){
	char url[MAXLINE], *tmp, *tmp1, *tmp2;

	if(!strcmp(aurl, "*")){
		/* generic OPTIONS do not care */
		return -1;
	}

	/* copy to modify */
	strcpy(url, aurl);

	/* host */
	tmp = strstr(url, "://"); 
	if(!tmp){
		/* CONNECT or nasty input */
		tmp = url;
	}else{
		tmp += 3;
	}

	/* uri */
	tmp1 = tmp2 = strchr(tmp, '/');
	if(!tmp1){
		strcpy(uri,"/");
	}else{
		while(*(tmp2 + 1) == '/'){
			tmp2 += 1;
		}
		strcpy(uri, tmp2);
		*tmp1 = '\0';
	}

	/* host[:port] */
	if(index(tmp, ':')){
		sscanf(tmp, "%[^':']:%hu", host, portp);
	}else{
		strcpy(host, tmp);
		*portp = 80;
	}

	return 0;
}

/**
 * write buf to fd and cache. without caring total size
 * this is called by known size read.
 * cachep: char ** is the cache
 * pp: char ** is current index of cache
 */
static int wrt_cache_b(int fd, char **cachep, char **pp,
		char *buf, int length){
	int nrw;
	nrw = rio_writen(fd, buf, length);
	if(nrw == -1){
		if(*cachep){
			Free(*cachep);
			*cachep = NULL;
		}
		return -1;
	}
	if(*cachep){
		memcpy(*pp, buf, nrw);
		*pp += nrw;
	}
	return 0;
}

/**
 * write buf to fd and cache.
 * tsizep: int * is current total used size of the cache
 * cache: char ** is the cache
 * pp: char ** is current index of cache
 */
static int wrt_cache_bin(int fd, int *tsizep, char **cachep,
		char **pp, char *buf, int length){
	int nrw;
	nrw = rio_writen(fd, buf, length);
	if(nrw == -1){
		if(*cachep){
			Free(*cachep);
			*cachep = NULL;
		}
		return -1;
	}
	if(*cachep){
		*tsizep += nrw;
		if(*tsizep > MAX_OBJECT_SIZE){
			if(*cachep){
				Free(*cachep);
				*cachep = NULL;
			}
		}else{
			memcpy(*pp, buf, nrw);
			*pp += nrw;
		}
	}

	return 0;
}

/**
 * write buf (a string) to fd and cache.
 * tsizep: int * is current total used size of the cache
 * cache: char ** is the cache
 * pp: char ** is current index of cache
 */
static int wrt_cache_asc(int fd, int *tsizep, char **cachep, 
		char **pp, char *buf){
	int nrw, len = strlen(buf);

	nrw = rio_writen(fd, buf, len);
	if(nrw == -1){
		if(*cachep){ /* Free */
			Free(*cachep);
			*cachep = NULL;
		}
		return -1;
	}

	if(*cachep){
		*tsizep += nrw;
		if(*tsizep > MAX_OBJECT_SIZE){
			if(*cachep){
				Free(*cachep);
				*cachep = NULL;
			}
		}else{
			memcpy(*pp, buf, nrw);
			*pp += nrw;
		}
	}
	return 0;
}

/**
 * without knowing length
 */
static int fwd_bin(rio_t *src_rp, int dest_fd){
	char buf[MAXLINE];
	int nrw;

	while((nrw = rio_readnb(src_rp, buf, MAXLINE))){
		if(nrw == -1){
			return -1;
		}
		nrw = rio_writen(dest_fd, buf, nrw);
		if(nrw == -1){
			return -1;
		}
	}
	return 0;
}

/**
 * transfer fix size from src_rp to dest_fd, efficiency
 * csize > 0
 */
static int fwd_fix_size(rio_t *src_rp, int dest_fd, int csize){

	char buf[MAXLINE];
	int nrw;

	/* batch read length of MAXLINE*/
	while(csize >= MAXLINE){
		nrw = rio_readnb(src_rp, buf, MAXLINE);
		if(nrw == -1){
			return -1;
		}

		/* write to browser */
		nrw = rio_writen(dest_fd, buf, nrw);
		if(nrw == -1){
			return -1;
		}
		csize -= nrw;
	}

	/* read < MAXLINE*/
	if(csize > 0){
		nrw = rio_readnb(src_rp, buf, csize);
		if(nrw == -1){
			return -1;
		}
		nrw = rio_writen(dest_fd, buf, nrw);
		if(nrw == -1){
			return -1;
		}
	}

	return 0;
}

/**
 * parse status line
 * according to HTTP 1.0, 1xx, 204, 304 has empty body
 */
static void is_body_empty(char *status_line, int *emptyp){
	char sline[MAXLINE], *tmp, *tmp1;

	/* process on copy */
	strcpy(sline, status_line);

	tmp = strchr(sline, ' ') + 1;
	tmp1 = strchr(tmp, ' ');
	*tmp1 = '\0';
	if(*tmp == '1' || !strcmp(tmp, "204") || !strcmp(tmp, "304")){
		*emptyp = 1;
	}
}

/**
 * parse a content-length entity header to extract size 
 */
static void get_size(char *buf, int *sizep){
	if(strstr(buf, "Content-length")){
		sscanf(buf, "Content-length: %d", sizep);
	}

	if(strstr(buf, "Content-Length")){
		sscanf(buf, "Content-Length: %d", sizep);
	}

	if(strstr(buf, "content-length")){
		sscanf(buf, "content-length: %d", sizep);
	}
}
