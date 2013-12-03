/** 
 * @file cond_type.h
 * @brief This file defines the type
 * and prototypes for cond vars.
 *
 * The cond var is backed by mutex
 * for atomical manipulation of
 * its queue because the broadcast
 * can be very time-consuming, and
 * mutex is built for bounded-
 * waiting and time-consuming task.
 *
 * @author Yue Xing
 */

#ifndef _COND_TYPE_H
#define _COND_TYPE_H

#include <mutex.h>

/**
 * @brief This is a condition 
 * variable
 */
typedef struct {
	int valid;
	/*queue of waiting threads*/
	node_t *head;
	node_t *tail;
	/*mutex for the queue*/
	mutex_t qlock;
} cond_t;

#endif /* _COND_TYPE_H */
