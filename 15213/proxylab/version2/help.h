#ifndef _PROXY_H
#define _PROXY_H
#include <stdlib.h>
#include <ctype.h>
#include "csapp.h"

void punix_err(char *msg);
ssize_t Rrio_readlineb(rio_t *rp, void *usrbuf, size_t maxlen);
ssize_t Rrio_readnb(rio_t *rp, void *usrbuf, size_t n);
void Rrio_writen(int fd, void *usrbuf, size_t n);
int Oopen_clientfd(char *hostname, int port);
void init_help();
void close_ts(int fd);

sem_t gethost_lock;

#endif
