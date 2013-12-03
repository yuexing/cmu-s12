/**
 * @file block.c
 * @brief A kernel version implementation 
 * of block-style mutex.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#include <kernel.h>

/**
 * @brief Initialize a block
 */
int block_init(block_t *block)
{
	block->lock = 1;	
	Q_INIT(block->qhead, block->qtail);
	block->holder = NULL;

	return SUCCESS;
}

/**
 * @brief Lock the block. If the block is held by 
 * others, deschedule the current thread and switch
 * to lock holder to run.
 */
void block_lock(block_t *block)
{
	kthread_tcb_t *cur = THREAD_TCB_CUR;

	disable_ctx_switch();
	if(block->lock){
		block->holder = cur;
		block->lock = 0;
		enable_ctx_switch();
		return;
	}

	block_and_switch(
	&block->qhead, &block->qtail, block->holder);
	enable_ctx_switch();
}

/**
 * @brief Unlock the block and switch to the first 
 * one in the queue to run.
 */
void block_unlock(block_t *block)
{
	schedule_node_t *cur = NULL;

	disable_ctx_switch();
	if(Q_EMPTY(block->qhead)){
		block->lock = 1;
		block->holder = NULL;
		enable_ctx_switch();
		return;
	} 

	Q_EJECT(block->qhead, block->qtail, cur);
	block->holder = cur->tcb;
	unblock_and_switch(NULL,NULL,cur->tcb);
	enable_ctx_switch();
}

/**
 * @brief special unlock for kcond.
 * @pre The thread hold the lock now, and 
 * would like to drop. disable_ctx_switch
 * has been called.
 */
void block_cond_unlock(block_t *block)
{
	if(Q_EMPTY(block->qhead)){
		block->lock = 1;
		block->holder = NULL;
		return;
	} 

	schedule_node_t *cur;
	Q_EJECT(block->qhead, block->qtail, cur);
	block->holder = cur->tcb;
	unblock_no_switch(NULL, NULL, block->holder);
}

/**
 * @brief unlock the block. switch to the first
 * one in the queue or a runnable thread.
 */
void block_unlock_vanish(block_t *block)
{
	schedule_node_t *node = NULL;
	kthread_tcb_t *next = NULL;

	disable_ctx_switch();
	if(Q_EMPTY(block->qhead)){
		block->lock = 1;
		block->holder = NULL;
		next = get_next_runnable_thread();
	} else {
		Q_EJECT(block->qhead,block->qtail,node);
		next = node->tcb;
		block->holder = next;
	}

	switch_to(next);
}

