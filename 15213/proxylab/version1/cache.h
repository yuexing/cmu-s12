/**
 * cache.h
 * Yue Xing, Tiantian Xu
 * yuexing@andrew.cmu.edu, tiantiax@andrew.cmu.edu
 */
#include "csapp.h"

#define CACHED 1
#define UNCACHED 2
#define NOCACHE 3
#define CACHE_SUCCESS 4
#define CACHE_FAILURE 5
#define CACHE_BY_OTHER 6
#define MAX_CACHE_SIZE 1049000
#define MAX_OBJECT_SIZE 102400

/* cache data unit */
struct cdata_t{
	char *url; /* host/port+uri */
	int size;
	unsigned long logic_time; /* apply logic time here */
	void *ptr; /* point to the start of cache data in mem */
	int readcnt;
	sem_t cmutex; /* mutex for read count */
 	struct cdata_t *next;
	struct cdata_t *prev;
};

typedef struct cdata_t cdata;

/*
 * get cdata from the cache list by using url
 * if cached, write directly to browser
 * finally return status CACHED/UNCACHED
 */
int Get_cache(char *url, int browserfd);
/*
 * add a cache to cache list
 */
int Create_cache(char *url, char *ptr, int size);
/*
 * init cache varibles
 */
void Cache_init();
