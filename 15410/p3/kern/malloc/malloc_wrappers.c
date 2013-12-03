/* 
 * @brief safe versions of malloc functions 
 * Since memory allocation can be time consuming,
 * the block lock is used here.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */
#include <kernel.h>

static block_t lock;

int init_mod_malloc()
{
	return block_init(&lock);
}

void *malloc(size_t size)
{
	void *ret = NULL;
	block_lock(&lock);
	ret = _malloc(size);
	block_unlock(&lock);
	return ret;
}

void *memalign(size_t alignment, size_t size)
{
	void *ret = NULL;
	block_lock(&lock);
	ret = _memalign(alignment, size);
	block_unlock(&lock);
	return ret;
}

void *calloc(size_t nelt, size_t eltsize)
{
	void *ret = NULL;
	block_lock(&lock);
	ret = _calloc(nelt, eltsize);
	block_unlock(&lock);
	return ret;
}

void *realloc(void *buf, size_t new_size)
{
	void *ret = NULL;
	block_lock(&lock);
	ret = _realloc(buf, new_size);
	block_unlock(&lock);
	return ret;
}

void free(void *buf)
{
	block_lock(&lock);
	_free(buf);
	block_unlock(&lock);
	return;
}

/* Alternate version of the standard malloc
 * functions that expect the caller to keep
 * track of the size of allocated chunks.
 * These versions are _much_ more memory-eff
 * with no prefix.
 */
void *smalloc(size_t size)
{
	void *ret = NULL;
	block_lock(&lock);
	ret = _smalloc(size);
	block_unlock(&lock);
	return ret;
}

void *smemalign(size_t alignment, size_t size)
{
	void *ret = NULL;
	block_lock(&lock);
	ret = _smemalign(alignment, size);
	block_unlock(&lock);
	return ret;
}

void *scalloc(size_t size)
{
	void *ret = NULL;
	debug_mem("SHOULD NOT");
	return ret;
}

void sfree(void *buf, size_t size)
{
	block_lock(&lock);
	_sfree(buf, size);
	block_unlock(&lock);
	return;
}
