/**
 * @file kthread.h
 * @brief declaration of prototypes for a thread
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#ifndef _KTHREAD_H
#define _KTHREAD_H

#define CANARY		0x19890321

/* Get the kstack for a tcb */
#define KSTK(tcb)	(((void*)tcb)+KSTK_OFFSET)

#define IS_VALID(a)	((a)->canary == CANARY)

#define THREAD_TCB(esp)	((kthread_tcb_t*)PAGE_ROUND_DOWN(esp))

#define THREAD_TCB_CUR	(THREAD_TCB(get_esp()))

#define THREAD_DIR_CUR	(THREAD_TCB_CUR->pcb->dir)

#define THREAD_PCB_CUR	(THREAD_TCB_CUR->pcb)

#define USER_STACK		0xfffffffc

int init_mod_kthread();

uint32_t init_tcb(void*,ureg_t*, void*);

uint32_t fill_tcb(kthread_tcb_t *tcb);

void cp_swexn(kthread_tcb_t*, kthread_tcb_t*);

void destroy_tcb(kthread_tcb_t *meta);

kthread_tcb_t *load(char*);

void prepare_ctx_switch(kthread_tcb_t *tcb);

void run(kthread_tcb_t *tcb);

/**
 * @brief Get current esp
 */
void *get_esp();
#endif
