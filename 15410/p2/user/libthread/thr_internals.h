/**
 * @file thr_internals.h
 * @brief Data struct and prototypes
 * for thread library implementation
 * .
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#ifndef _THREAD_INTERNALS_H
#define _THREAD_INTERNALS_H

#include <libthread.h>

#define ESP3_SIZE 	2048

/*a is a base*/
#define WORD_RD_DN(a)(((uint32_t)(a))&(~((1<<2)-1)))

/*a is a thread_tcb_t*/
#define ESP3(a)		((void*)(a->esp3+ESP3_SIZE))

/*a is a esp, nth from downward*/
#define NTH_STK(a)	(((uint32_t)(a-libthr.boundary))/libthr.size)

#define _BASE(a)	(libthr.boundary+NTH_STK(a)*libthr.size+(libthr.size-1))

#define BASE(a)		((void*)WORD_RD_DN(_BASE(a)))

/* page stuff */
#define PAGE_MASK	(PAGE_SIZE-1)
#define PAGE_RD_DN(a) (((uint32_t)(a))&(~PAGE_MASK))
#define PAGE_RD_UP(a) ((((uint32_t)(a))+PAGE_MASK)&(~PAGE_MASK))
#define IS_NON_PRESENT(a) 	(!((a)&1))

#define FREE_TCB(a)\
	do{\
		if(a==libthr.main){\
			libthr.main = 0;\
		}\
		free(a);\
	}while(0)

/**
 * @brief the user level tcb
 * modeled with tid, func, esp
 * and esp3, synced by mutex
 * and cond.
 */
typedef struct thread_tcb
{
	int tid;
	/*solve the race of creation*/
	int ready;

	/*function to run*/
	void *(*func)(void*arg);
	void *arg;

	/* limit is only needed when
	 * autogrowth*/
	void *limit;
	/* The top of the current 
	 * using stack*/
	void *top;
	
	/*esp3*/
	char esp3[ESP3_SIZE];

	/*exit and join*/
	void *status;
	int exited;

	/*synchronization*/
	mutex_t lock;
	cond_t cv;

	struct thread_tcb *next;
} thread_tcb_t;

/**
 * @brief stk_tcb_t is only
 * used during a thread's lifetime.
 */
typedef struct
{
	thread_tcb_t *tcb;
} stk_tcb_t;

/**
 * @brief hole_node_t is to track
 * the hole.
 */
typedef struct hole_node
{
	void *base;
	struct hole_node *next;
} hole_node_t;

typedef struct
{
	void *boundary;
	mutex_t block;

	/*a stack size*/
	uint32_t size;
	uint32_t size_pages;

	/*hole list*/
	hole_node_t *hhead;
	hole_node_t *htail;
	mutex_t hlock;

	/*tcb list*/
	thread_tcb_t *thead;
	thread_tcb_t *ttail;
	mutex_t tlock;

	/* main thread is different
	 * as has no base*/
	thread_tcb_t *main;
}libthr_t;

void after_thr_fork();

void free_stk(void *top);

void after_switch_stack(thread_tcb_t*);

/* ASM functions */
/**
 * @brief run on the other stack to
 * call after_switch_stack
 *
 * @param esp3 The stack to run on
 * @param top
 * @return never return
 */
void switch_stack(void* esp3, void *top);

/**
 * @brief fork a new thread and call
 * child() from its stack
 * @param the dedicated esp
 * @return tid/ERROR
 */
int thr_fork(void*);

/**
 * @brief unlock lock and vanish
 * @return never return
 */
void unlock_vanish(mutex_t*);

/**
 * @return the current esp
 */
void *get_esp();

/**
 * @return the ebp of the current
 * function.
 */
void *get_ebp();

extern void *_main_ebp;
#endif
