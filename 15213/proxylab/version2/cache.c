#include "cache.h"

// the total size can not exceed MAX_CACHE_SIZE
static int total_size = 0;
// the cache list
cache_obj_t *head, *tail;
// the mutex for the cache list
sem_t read_lock, write_lock;
// read cnt
int read_cnt;

static void evict_cache(cache_obj_t *acache);

/**
 * make a cache according to the cache obj
 * on the stack.
 */
cache_obj_t *make_cache(char *url, char *obj, int size)
{
	cache_obj_t *acache 
		= (cache_obj_t *)malloc(sizeof(cache_obj_t));

	if(acache == NULL){
		return NULL;
	}

	acache->url = (char *)malloc(strlen(url));
	if (acache->url == NULL){
		free(acache);
		return NULL;
	}
	strcpy(acache->url, url);

	acache->obj = (char *)malloc(size);
	if(acache->obj == NULL){
		free(acache->url);
		free(acache);
		return NULL;
	}
	memcpy(acache->obj, obj, size);

	acache->size = size;

	acache->logic_time = 0;

	acache->next = NULL;

	return acache;
}

void init_cache()
{
	total_size = read_cnt = 0;
	head = tail = NULL;

	Sem_init(&write_lock, 0, 1);
	Sem_init(&read_lock, 0, 1);
}

/**
 * add a cache obj to the list.
 * This will include eviction.
 */
void add_cache(cache_obj_t *acache)
{
	P(&write_lock);

	// add
	if(!head){
		head = tail = acache;
	} else {
		tail->next = acache;
		tail = acache;
	}
	total_size += acache->size;

	// evict now
	cache_obj_t *min, *index;
	while(total_size > MAX_CACHE_SIZE){
		
		min = index = head;
		// tail is very new, leave it!
		while(index->next){
			if(index->logic_time > min->logic_time){
				min = index;
			}
			index = index->next;
		}
		
		total_size -= min->size;
		printf("EVICT %s\n", min->url);
		evict_cache(min);
	}

	V(&write_lock);
}

/**
 * This kind of evict will make sure
 * there is always a next.
 * copy next to cur and free next.
 */
static void evict_cache(cache_obj_t *acache)
{
	cache_obj_t *next = acache->next;
	if(next){
		
		if(next == tail){
			tail = acache;
		}

		// url
		free(acache->url);
		acache->url = next->url;
		// obj
		free(acache->obj);
		acache->obj = next->obj;
		// size
		acache->size = next->size;
		// logic_time
		acache->logic_time = next->logic_time;

		next = next->next;
		free(acache->next);
		acache->next = next;
	}
}

/**
 * The safest and easiest way
 * is to copy the obj to the obj on the stack.
 *
 * @return cache_size got! 0 miss!
 */
int get_cache(char *url, char *obj)
{
	int ret = 0;
	P(&read_lock);
	read_cnt++;
	if(read_cnt == 1){
		P(&write_lock);
	}
	V(&read_lock);

	// read and get
	cache_obj_t *tmp = head;
	while(tmp){
		if(!strcmp(tmp->url, url)){
			memcpy(obj, tmp->obj, tmp->size);
			tmp->logic_time++;
			ret = tmp->size;
			break;
		}
		tmp = tmp->next;
	}

	P(&read_lock);
	read_cnt--;
	if(read_cnt == 0){
		V(&write_lock);
	}
	V(&read_lock);
	return ret;
}
