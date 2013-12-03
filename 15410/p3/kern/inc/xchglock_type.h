/**
 * @file xchglock_type.h
 * @brief Data structure for the xchglock
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#ifndef _XCHGLOCK_TYPE_H
#define _XCHGLOCK_TYPE_H

typedef struct xchglock xchglock_t;

struct xchglock{
	uint32_t lock;
	
	kthread_tcb_t *holder;
};

#endif
