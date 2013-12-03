#include "help.h"
void init_help()
{
	Sem_init(&gethost_lock, 0, 1);
}

void punix_err(char *msg) /* unix-style error */
{
    fprintf(stderr, "%s: %s\n", msg, strerror(errno));
    Pthread_exit(0);
}


static struct hostent *gethostbyname_ts(char *name)
{
	struct hostent *p = malloc(sizeof(struct hostent));
	if(!p)
		return NULL;
	struct hostent *sharedp;

	P(&gethost_lock);
	sharedp = gethostbyname(name);
	if(sharedp){
		memcpy(p, sharedp, sizeof(struct hostent));
	} else {
		free(p);
		p = NULL;
	}
	V(&gethost_lock);

	return p;
}

int Oopen_clientfd(char *hostname, int port)
{
	int clientfd;
	struct hostent *hp;
	struct sockaddr_in serveraddr;

	if ((clientfd = socket(AF_INET, SOCK_STREAM, 0)) < 0)
		return -1; /* check errno for cause of error */

	/* Fill in the server's IP address and port */
	if ((hp = gethostbyname_ts(hostname)) == NULL)
		return -2; /* check h_errno for cause of error */

	bzero((char *) &serveraddr, sizeof(serveraddr));
	serveraddr.sin_family = AF_INET;
	bcopy((char *)hp->h_addr_list[0], 
			(char *)&serveraddr.sin_addr.s_addr, hp->h_length);
	serveraddr.sin_port = htons(port);
	free(hp);

	/* Establish a connection with the server */
	if (connect(clientfd, (SA *) &serveraddr, sizeof(serveraddr)) < 0)
		return -1;

	return clientfd;
}

void close_ts(int fd) 
{
	int rc;

	if ((rc = close(fd)) < 0)
		punix_err("Close error");
}
