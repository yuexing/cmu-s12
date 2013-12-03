/** @file rwlock_type.h
 *  @brief This file defines the type for reader/writer locks.
 */

#ifndef _RWLOCK_TYPE_H
#define _RWLOCK_TYPE_H

#include <mutex.h>
#include <cond.h>

typedef struct rwlock {
	int valid;
	cond_t rcv;
	cond_t wcv;
	mutex_t lock;

	int readers;
	int writer;
	int waiting_readers;
	int waiting_writers;

	int turn;
} rwlock_t;

#endif /* _RWLOCK_TYPE_H */
