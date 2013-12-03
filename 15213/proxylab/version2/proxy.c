/**
 *
 */


#include <stdio.h>
#include "csapp.h"
#include "cache.h"
#include "help.h"


#define MAX_OBJECT_SIZE 102400

static const char *user_agent = "User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:10.0.3) Gecko/20120305 Firefox/10.0.3\r\n";
static const char *accept_str = "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n";
static const char *accept_encoding = "Accept-Encoding: gzip, deflate\r\n";


void * handle_thread(void *vargp);
int parse_uri(char *uri, char *hostname, char *pathname, int *port); 


/*
* main();
* main routine 
* Similar to The Example in the Text Book 
* Call handle thread function after
*
*/
int main(int argc, char **argv)
{
    
    int listenfd, port;
    long connfd;
    
    struct sockaddr_in clientaddr; 
    pthread_t tid;

    socklen_t clientlen = sizeof(clientaddr);

    /*check arguments*/
    if(argc != 2){
    	fprintf(stderr,"usage: %s <port>\n", argv[0]);
    	exit(0);
    }
    /*Ignore unwanted signals*/
    signal(SIGPIPE,SIG_IGN);

	init_help();
	init_cache();

    /*get port number*/
    /*the server listens on a port passed on the */
    /*command line*/
    port = atoi(argv[1]);
    listenfd = Open_listenfd(port);

    /*check status*/
    if(listenfd < 0){
        if(listenfd == -1){
            fprintf(stderr, "port %d: %s\n", port, strerror(errno));
        }
        exit(1);
    }


    while(1){
        /*is connected*/
    	connfd = Accept(listenfd, (SA *)& clientaddr,&clientlen);
        Pthread_create(&tid,NULL,handle_thread, (void*)connfd);
    }

    return 0;
}


/*Handel_thread */
/*
* Reads in HTTP request. Then forward the request to server
* Parse Request and Response when neccessary
* Similar to the Doit function in the text
*
*/
void * handle_thread(void *vargp)
{
     /*descriptor for connection and proxy*/
    long connfd, proxyfd;

    /*by default threads are created joinable.We detach them to */
    /*prevent memory leak*/
    Pthread_detach(pthread_self());

    /*set descriptor*/
    connfd = (long)vargp;

    /*set I/O buffer*/
    char buf[MAXLINE], obj[MAX_OBJECT_SIZE];
    char method[MAXLINE], uri[MAXLINE],version[MAXLINE];
    char host[MAXLINE], path[MAXLINE];
    int port, n;

    rio_t conn_rio, proxy_rio;

    Rio_readinitb(&conn_rio, connfd);

    if((n = rio_readlineb(&conn_rio, buf, MAXLINE))<=0){
	    close_ts(connfd);
	    Pthread_exit(0);
    }

    sscanf(buf, "%s %s %s", method, uri, version);

	if((n = get_cache(uri, obj))){
		printf("GET %s (%d)\n", uri, n);
		if(rio_writen(connfd, obj, n) < 0){
			close_ts(connfd);
			Pthread_exit(0);
		}
		return NULL;
	}

	/********************************************
	 * MISS
	 *
	 ***********************************/
    if(parse_uri(uri, host, path, &port)<0){
	    printf("error in parsing url");
	    close_ts(connfd);
	    Pthread_exit(0);
    }

    if((proxyfd = Oopen_clientfd(host,port))==-1){
	    printf("error in establishing connection with end server: %s\n", strerror(errno));
	    close_ts(connfd);
	    Pthread_exit(0);
    }

    Rio_readinitb(&proxy_rio, proxyfd);

    // forward first line
    if(rio_writen(proxyfd, buf, strlen(buf)) < 0){
		close_ts(connfd);
		close_ts(proxyfd);
		Pthread_exit(0);
	}
    // forward the asked line
    if(rio_writen(proxyfd, (void*)user_agent, strlen(user_agent))<0){
	    close_ts(connfd);
	    close_ts(proxyfd);
	    Pthread_exit(0);
	}
    if(rio_writen(proxyfd, (void*)accept_str, strlen(accept_str)) <0){
	    close_ts(connfd);
	    close_ts(proxyfd);
	    Pthread_exit(0);
	}
    if(rio_writen(proxyfd, (void*)accept_encoding, strlen(accept_encoding))<0){
	    close_ts(connfd);
	    close_ts(proxyfd);
	    Pthread_exit(0);
	}

    // forward the remaining
    while((n = rio_readlineb(&conn_rio, buf, MAXLINE)) > 0){
	 	if(rio_writen(proxyfd, buf, strlen(buf)) < 0){
			close_ts(connfd);
			close_ts(proxyfd);
			Pthread_exit(0);
		}
		if(!strcmp(buf, "\r\n") || !strcmp(buf, "\n"))
			break;
    }

    if(n < 0){
	    close_ts(connfd);
	    close_ts(proxyfd);
	    Pthread_exit(0);
    }

	char *obj_index = obj;
	int total_size = 0;
    while((n = rio_readnb(&proxy_rio, buf, MAXLINE)) > 0){
        if(rio_writen(connfd, buf, n) < 0){
			close_ts(connfd);
			close_ts(proxyfd);
			Pthread_exit(0);
		}
		total_size += n;
		if(total_size < MAX_OBJECT_SIZE){
			memcpy(obj_index, buf, n);
			obj_index += n;
		}
    }

    if(n < 0){
	    close_ts(connfd);
	    close_ts(proxyfd);
	    Pthread_exit(0);
    }

	cache_obj_t *acache;
	if(total_size < MAX_OBJECT_SIZE){
		if((acache = make_cache(uri, obj, total_size))){
			add_cache(acache);
			printf("CACHE %s (%d)\n", uri, total_size);
		}
	}

	close_ts(connfd);
	close_ts(proxyfd);
	return NULL;
}

/*
 * parse url: the imporved version of parse_uri 
 * from the student website
 * Takes in 4 parameters uri, host, path and port
 *
 */
int parse_uri(char *uri, char *hostname, char *pathname, int *port)  
{  
	char *hostbegin;  
	char *hostend;  
	char *pathbegin;  
	int len;  

	if (strncasecmp(uri, "http://", 7) != 0) {  
		hostname[0] = '\0';  
		return -1;  
	}  

	/* Extract the host name */  
	hostbegin = uri + 7;  
	hostend = strpbrk(hostbegin, " :/\r\n\0");  
	len = hostend - hostbegin;  
	strncpy(hostname, hostbegin, len);  
	hostname[len] = '\0';  

	/* Extract the port number */  
	*port = 80; /* default */  
	if (*hostend == ':')     
		*port = atoi(hostend + 1);  

	/* Extract the path */  
	pathbegin = strchr(hostbegin, '/');  
	if (pathbegin == NULL) {  
		pathname[0] = '\0';  
	}  
	else {  
		pathbegin++;      
		strcpy(pathname, pathbegin);  
	}  

	return 0;  
}  
