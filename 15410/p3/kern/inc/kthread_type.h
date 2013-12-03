/**
 * @file kthread_tcb_type.h
 * @brief declaration of data structures related to 
 * threads.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#ifndef _KTHREAD_TYPE_H
#define _KTHREAD_TYPE_H

typedef struct process process_t;
typedef struct kthread kthread_t;
typedef struct kthread_tcb kthread_tcb_t;

typedef enum {
	RUNNING, RUNNABLE, SLEEP, BLOCKED
} state_t;

typedef struct {
	void *esp3;
	uint32_t eip;
	void *arg;
	uint32_t last;
} swexn_t;

typedef struct schedule_node{
	struct schedule_node *prev;
	struct schedule_node *next;
	/* related to kthread */
	kthread_tcb_t *tcb;
} schedule_node_t;

typedef struct pcb_node{
	struct pcb_node *prev;
	struct pcb_node *next;
	/* related to kthread */
	kthread_tcb_t *tcb;
} pcb_node_t;


struct kthread_tcb {
	uint32_t tid;

	/* points to the esp used for context switch */
	void *esp;
	/* the entry point */
	uint32_t eip;

	/* pcb the thread blongs to */
	process_t *pcb;

	/* wakeup time for sleeping thread */
	uint32_t ticks;

	/* thread state */
	state_t state;

	/* swexn */
	swexn_t swexn;

	/* for double ll */
	schedule_node_t schedule_node;
	pcb_node_t pcb_node;

	/* the canary guard */
	uint32_t canary;
};

#define KSTACK_SIZE	(PAGE_SIZE - sizeof(kthread_tcb_t))

struct kthread{
	kthread_tcb_t tcb;
	char esp0[KSTACK_SIZE];
};

#endif
