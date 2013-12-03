/** 
 * @file sem_type.h
 * @brief This file defines 
 * the type for semaphores.
 *
 * A semaphore is supported
 * by mutex and cond var.
 *
 * @author Yue Xing
 * @bug No Found Bug
 */

#ifndef _SEM_TYPE_H
#define _SEM_TYPE_H

#include <cond.h>
#include <mutex.h>

typedef struct {
	int valid;
	/*semaphore value*/
	int count;
	cond_t cv;
	mutex_t lock;
} sem_t;

#endif /* _SEM_TYPE_H */
