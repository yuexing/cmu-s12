/*
 * @file malloc.c
 * @brief thread-safe mallocs.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug.
 */

#include <libthread.h>

/**
 * @brief sync machanism.
 */
static mutex_t lock = MUTEX_INIT;

void *malloc(size_t __size)
{
	void *ret;
	mutex_lock(&lock);
	ret = _malloc(__size);
	mutex_unlock(&lock);

	return ret;
}

void *calloc(size_t __nelt, size_t __eltsize)
{
	void *ret;
	mutex_lock(&lock);
	ret = _calloc(__nelt, __eltsize);
	mutex_unlock(&lock);

	return ret;
}

void *realloc(void *__buf, size_t __new_size)
{
	void *ret;
	mutex_lock(&lock);
	ret = _realloc(__buf, __new_size);
	mutex_unlock(&lock);

	return ret;
}

void free(void *__buf)
{
	mutex_lock(&lock);
	_free(__buf);
	mutex_unlock(&lock);
}
