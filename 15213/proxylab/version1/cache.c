/**
 *
 * cache.h
 *
 * Yue Xing, Tiantian Xu
 * yuexing@andrew.cmu.edu, tiantiax@andrew.cmu.edu
 *
 * 1) As having no idea whether reader-favor, or writer-favor, 
 * modify with no reader to improve efficiency and fairness:
 * while going through to get cache, move the found cache data to 
 * then end;
 * while creating, first check if has been cached by others, then
 * cache it to the end;
 * thus, evict should happen in the front :)
 * 2) Apply read-writer model to a single cache data.
 */
#include "cache.h"

/* 
 * cache list 
 * */
static cdata *head, *rear;
/**
 * mutex for queue
 */
static sem_t qmutex;
/**
 * logic time increased at each get and create
 */
static unsigned long cache_time;
/**
 * total size till now
 */
static int tsize; 
/**
 * cache methods
 */
static int create_cache(cdata* acache);
static void denode(cdata *p);
static void addtorear(cdata *p);
static cdata *get_cache(char *url);
/* this function has no use now
 * just for check cache
 * */
static unsigned long settime(cdata *acache);
static void setread(cdata *acache);
static int getread(cdata *acache);
static void seturead(cdata *acache);
void checkcache();

void Cache_init(){
	head = NULL;
	rear = NULL;
	cache_time = 0;
	tsize = 0;
	Sem_init(&qmutex, 0, 1);
}

/**
 * wrapper function of get_cache
 * search for cache, if exist write directly to browser
 * setread and seturead
 */
int Get_cache(char *url, int browserfd){
	cdata *acache;
	if((acache = get_cache(url)) == NULL){
		c_p("%s: UNCACHED\n", url);
		return UNCACHED;
	}

	//write to browser
	c_p("%s: CACHED\n", url);
	if(rio_writen(browserfd, acache->ptr, acache->size) == -1){
		return -1;
	}
	seturead(acache);
	return CACHED;
}
/**
 * wrapper function of create_cache
 * 1. check whether the url has been added by others
 * 2. populate a cdata and create_cache
 */
int Create_cache(char *url, char *ptr, int size){
	
	cdata *acache = (cdata *)malloc(sizeof(cdata));
	int status;

	if(acache){
		acache->url = strdup(url);
		if(!(acache->url)){
			Free(acache);
			Free(ptr);
			return -1;
		}
		acache->ptr = realloc(ptr, size); //space utility
		acache->size = size;
		acache->readcnt = 0;
		Sem_init(&(acache->cmutex), 0, 1);
		while((status = create_cache(acache)) == CACHE_FAILURE){
			//thread sleep for 2 sec and retry
			c_p("%s: Cache Failure\n", url);
			sleep(2);
		}
		if(status == CACHE_BY_OTHER){
			Free(acache->ptr);
			Free(acache->url);
			Free(acache);
		}
	}else{
		Free(ptr);
		return -1;
	}
	c_p("%s: Cache Success\n", url);
	return 0;
}
/*
 * 1. check if has been cached; (every path can only be cached once)
 * 2. if the cache is full, remove unused node from head;
 * (as we have moved the most recently used to the rear, things can happen
 * when a less recently used is for some reason set unread later,
 * a relatively more recently used one will be evicted)
 * So, wait !
 * 3. evict mean denode and free all the resources;
 */
static int create_cache(cdata* acache){
	P(&qmutex); 
	cdata *p = head, *tmp;
	while(p){
		if(!strcmp(p->url, acache->url)){ /* has been cached by other threads */
			V(&qmutex);
			return CACHE_BY_OTHER;
		}
		p = p->next;
	}

	if((tsize += acache->size) > MAX_CACHE_SIZE){
		c_p("%d > MAX_CACHE_SIZE\n", tsize);
		p = head; 
		while(p && tsize > MAX_CACHE_SIZE){
			/* remove unreaded node from head */
			if(!getread(p)){
				denode(p);
				c_p("Evict:%s, %d\n", p->url, p->size);
				tsize -= p->size;
				tmp = p;
				p = p->next; /* update p*/
				Free(tmp->url); /* free rsrc*/
				Free(tmp->ptr);
				Free(tmp);
			}else{
				V(&qmutex);
				return CACHE_FAILURE;
			}
		}
	}
	/* enough to add */
	addtorear(acache);
	V(&qmutex);
	return CACHE_SUCCESS;

}

/**
 * remove p out of cache list
 * without affecting p
 */
static void denode(cdata *p){
	if(p->prev){
		p->prev->next = p->next;
	}
	if(p->next){
		p->next->prev = p->prev;
	}
	if(p == head){
		head = p->next;
	}
	if(p == rear){
		rear = p->prev;
	}
}

/**
 * add p to the rear of the list
 * and settime
 */
static void addtorear(cdata *p){
	if(rear == NULL){
		/* an empty list */
		head = rear = p;
		p->prev = NULL;
		p->next = NULL;
	}else{
		rear->next = p;
		p->prev = rear;
		p->next = NULL;
		rear = p;
	}
	settime(p);
}

/*
 * go through the list and find the cdata
 * then setread and move it to the rear
 */
static cdata *get_cache(char *url){
	P(&qmutex);
	cdata *p = head;
	while(p){
		if(!strcmp(p->url, url)){
			setread(p);
			/* move p to rear */
			if(rear != p){
				denode(p);
				addtorear(p);
			}			
			break;
		}
		p = p->next;
	}

	V(&qmutex);
	return p;

}
/**
 * update acache's logic time
 * update cache's logic time
 * return next time
 * as it is called within the synchronized functions
 * no need to lock
 */
static unsigned long settime(cdata *acache){
	if(!acache)
		return cache_time;

	acache->logic_time = cache_time++;
	return cache_time;
}

/**
 * add the read cnt of a cache data
 * should be ATOMIC
 */
static void setread(cdata *acache){
	P(&(acache->cmutex));
	(acache->readcnt)++;	
	V(&(acache->cmutex));
}

/**
 * get the read cnt of a cache data
 * should be ATOMIC
 */
static int getread(cdata *acache){
	int readcnt;
	P(&(acache->cmutex));
	readcnt = (acache->readcnt);	
	V(&(acache->cmutex));
	return readcnt;
}

/**
 * decrease the read cnt of a cache data
 * should be ATOMIC
 */
static void seturead(cdata *acache){
	P(&(acache->cmutex));
	(acache->readcnt)--;	
	V(&(acache->cmutex));
}

/**
 * for debug
 * no need any lock
 */
void checkcache(){
	cdata *p = head;
	while(p){
		printf("url: %s, size: %d, logic time: %lu, read: %d\n",
				p->url,
				p->size,
				p->logic_time,
				p->readcnt);
		p = p->next;
	}
}
