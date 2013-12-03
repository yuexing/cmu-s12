/**
 * @file block_type.h
 * @brief declaration for data structures related to
 * block.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#ifndef _BLOCK_TYPE_H
#define _BLOCK_TYPE_H

typedef struct block block_t;

struct block {
	uint32_t lock;
	
	/* queue */
	schedule_node_t *qhead;
	schedule_node_t *qtail;

	kthread_tcb_t *holder;
};

#endif
