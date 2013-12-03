/** 
 * @file mutex_type.h
 * @brief This file defines the 
 * type and prototypes for mutexes.
 * 
 * The thread who fail to get the 
 * lock will block itself.
 *
 * @author Yue Xing(yuexing)
 * @bug No Found Bug.
 */

#ifndef _MUTEX_TYPE_H
#define _MUTEX_TYPE_H

#include <spinlock.h>
#include <vq.h>

/**
 * @brief This a a mutex. 
 */
typedef struct {
	int valid;
	int holder;

	int lock; 
	/* queue of waiting threads */
	node_t *head;
	node_t *tail;
	/* spinlock for the queue */
	spinlock_t qlock; 
} mutex_t;

#define MUTEX_INIT {1,-1,1,NULL,NULL,SPINLOCK_INIT}

#endif /* _MUTEX_TYPE_H */
