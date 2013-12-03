/**
 * @file spinlock.c
 * @brief implements spinlock.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#include <libthread.h>

/**
 * @brief init the spinlock by giving
 * it a lock.
 */
int spinlock_init(spinlock_t* lock)
{
	lock->holder = -1;
	lock->lock = 1;
	return SUCCESS;
}

/**
 * @brief lock a spinlock by yield-style
 * spinning on failure or setting holder
 * on success.
 */
void spinlock_lock(spinlock_t* lock)
{
	while(!xchg(&lock->lock, 0)){
		yield(lock->holder);
	}

	lock->holder = gettid();
}

/**
 * @brief unlock the spinlock by simply
 * un-registering holder and releasing 
 * a lock. The sequence matters!
 */
void spinlock_unlock(spinlock_t *lock)
{
	lock->holder = -1;
	lock->lock = 1;
}
