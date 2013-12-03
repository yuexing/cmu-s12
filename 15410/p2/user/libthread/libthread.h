/**
 * @file lib_thread.h
 * @brief Collect all header files
 * needed by libthread implementation.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#ifndef LIBTHREAD_H
#define LIBTHREAD_H

#define SUCCESS	0
#define ERROR	-1

#define ENTRY_CK(a)\
	do{\
		if(!a)\
		return;\
	}while(0);

#define ENTRY_CK_RET(a)\
	do{\
		if(!a)\
		return ERROR;\
	}while(0);

#define ENTRY_CK_VALID(a)\
	do{\
		assert(a->valid==1);\
	}while(0);

#define ENTRY_CK_HOLDER(a)\
	do{\
		if(a->holder!=gettid()){\
			panic("holder:%d, unlocker:%d", a->holder, gettid());\
		}\
	}while(0);

// Automically make run
#define MAKE_RUN(node)\
	do{\
		spinlock_lock(&node->lock);\
		node->reject = 1;\
		make_runnable(node->tid);\
		spinlock_unlock(&node->lock);\
	}while(0)

#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include <stddef.h>
#include <assert.h>
#include <string.h>

#include <simics.h>

#include <syscall.h>
#include <ureg.h>

#include <spinlock.h>
#include <vq.h>
#include <mutex_type.h>
#include <cond_type.h>
#include <sem_type.h>
#include <rwlock_type.h>
#include <thread.h>

#include <mutex.h>
#include <cond.h>
#include <sem.h>
#include <rwlock.h>

#include <debug.h>
#endif
