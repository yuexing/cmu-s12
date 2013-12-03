/**
 * @file varlock.c
 * @brief implementation of the varlock.
 *
 * A varlock is modeled as an optimizing
 * lock mechanism for the kernel running
 * on uni-processor. 
 * The lock can be applied only when we 
 * are sure that only one thread will 
 * access it. In other words, NO CTX 
 * SWITCH.
 * Thus, this lock can be used to protect
 * ctx_switch to be accessed by only one
 * thread.
 *
 * @author Yue Xing(yuexing)
 * @bug No Found Bug.
 */

#include <kernel.h>

int varlock_init(varlock_t* lock)
{
	lock->lock = 1;
	return SUCCESS;
}

void varlock_lock(varlock_t* lock)
{
	lock->lock = 0;
}

void varlock_unlock(varlock_t* lock)
{
	lock->lock = 1;
}

uint32_t varlock_trylock(varlock_t* lock)
{
	if(lock->lock){
		lock->lock = 0;
		return 1;
	} else {
		return 0;
	}
}
