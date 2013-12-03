/**
 *
 * Yue Xing, Tiantian Xu
 * yuexing@andrew.cmu.edu, tiantiax@andrew.cmu.edu
 */
#include "csapp.h"

/***********************************
 * Thread Entrance:
 ***********************************/
/**
 * parse request line
 * if non-GET, just FWD request and response
 * else(GET), if cached, return
 * else parse request and response
 */
void *proxy(void *vargp);




/****************************
 * functions for GET reqs:
 ****************************/
/**
 * read from browser:
 * 1. parse url or Host: to connect host;
 * 2. REQ hdrs: always send Host and some other required hdrs,
 * forward every other hdr from browser;
 * 3. End req with /r/n
 * 4. populate the server_rp (rio_t)
 */
int parse_get_req(rio_t *browser_rp, int client_fd, char *uri, char *host, unsigned short port);
/**
 * 1. prepare to cache;
 * 2. parse response header to get the content length, and fwd all resp hdrs;
 * 3. apply rio_readnb to read the content;
 * 4. while reading, cache;
 */
int parse_get_resp(rio_t *server_rp, int browser_fd, char *url);





/****************************
 * functions for non-GET reqs:
 ****************************/
/*
 * parse url or Host: to open a connection to host
 * and forward request
 */
int fwd_oth_req(rio_t *browser_rp, int client_fd, char *rline);
/*
 * read from server 
 * parse resp header for content length
 * forward header and content to browser
 * as to header, readlineb; while content, readnb
 */
int fwd_oth_resp(rio_t *server_rio, int browser_fd);


