/**
 * @file kcond.c
 * @brief implementation for the kernel
 * level cond var.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */
#include <kernel.h>
/**
 * @brief Initialize the cond var.
 */
int kcond_init(kcond_t *kcond)
{
	Q_INIT(kcond->head, kcond->tail);
	return SUCCESS;
}

/**
 * @brief release the lock and add to queue.
 */
void kcond_wait(kcond_t *kcond, block_t *lock)
{
	disable_ctx_switch();

	block_cond_unlock(lock);

	block_and_switch(&kcond->head, 
			&kcond->tail, lock->holder);

	enable_ctx_switch();

	block_lock(lock);
}

/**
 * @brief put the first one in the queue
 * to the run queue.
 */
void kcond_signal(kcond_t *kcond)
{
	disable_ctx_switch();

	if(!Q_EMPTY(kcond->head)){
		unblock_no_switch(&kcond->head, &kcond->tail, 
				NULL);
	}
	enable_ctx_switch();
}
