/**
 * @file scheduler.h
 * @brief The function prototypes for kernel thread scheduler
 * @author Yue Xing(yuexing)
 * @bug No Known Bug
 */
#ifndef _SCHEDULER_H
#define _SCHEDULER_H

int init_mod_scheduler();

void runq_push(kthread_tcb_t*);
void runq_inject(kthread_tcb_t*);

kthread_tcb_t *get_next_runnable_thread();
kthread_tcb_t *get_blocked(int);
kthread_tcb_t *get_runnable(int tid);

void disable_ctx_switch();
void enable_ctx_switch();
int try_ctx_switch();

void unblock_no_switch(schedule_node_t**, schedule_node_t**, kthread_tcb_t*);
void unblock_and_switch(schedule_node_t**, schedule_node_t**, kthread_tcb_t*);
void block_and_switch(schedule_node_t**, schedule_node_t**, kthread_tcb_t*);
void block();
void sleep();
void yield_to(kthread_tcb_t*);

void switch_to(kthread_tcb_t *next);

void check_pending();

#endif
