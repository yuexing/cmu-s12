#ifndef _CACHE_H
#define _CACHE_H

#include <stdlib.h>
#include "csapp.h"

#define MAX_CACHE_SIZE 1049000

// use list for cache
typedef struct cache_obj
{
	char *url;
	char *obj;
	int size;
	uint32_t logic_time;

	struct cache_obj *next;
}cache_obj_t;


cache_obj_t *make_cache(char *url, char *obj, int size);

void add_cache(cache_obj_t *acache);

int get_cache(char *url, char *obj);

void init_cache();

#endif



