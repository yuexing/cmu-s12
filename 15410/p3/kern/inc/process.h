/**
 * @file process.h
 * @brief declaration of prototypes for a process
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#ifndef _PROCESS_H
#define _PROCESS_H

int init_mod_proc();

/* current pcb */
void init_pcb(process_t *, 
	kthread_tcb_t *, page_dir_t *);

void destroy_pcb(process_t *pcb);

/* thread */
void add_thr(process_t*, kthread_tcb_t*);

int del_thr(process_t*, kthread_tcb_t*);

/* children add_child is needed 
 * when fork, but is not needed
 * when load()*/
void add_child(process_t *parent, process_t *child);
void add_zombie(process_t *child);

void reap_child(process_t *child);

process_t *get_zombie();

#endif
