/**
 * @file ksyscall.h
 * @brief This is the kernel level syscalls'
 * prototype.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#ifndef _KSYSCALL_H
#define _KSYSCALL_H

/**
 * @brief wrapper for kfork and 
 * kthread_fork. The wrapper simply
 * create a saved_esp area for 
 * ctx switch.
 */
void kfork_wrapper(ureg_t*);
void kthread_fork_wrapper(ureg_t*);

void kfork(ureg_t*, void*); /*0x41*/
void kthread_fork(ureg_t*, void*);/*0x52*/ 

/**
 * @brief terminate the life of a
 * thread.
 */
void terminate(kthread_tcb_t*, page_dir_t*, block_t*);

void kexec(ureg_t*); /* 0x42 */
/* void exit(ureg_t*);  0x43 */
void kwait(ureg_t*); /* 0x44 */
void kyield(ureg_t*); /* 0x45 */
void kdeschedule(ureg_t*); /* 0x46 */
void kmake_runnable(ureg_t*); /* 0x47 */
void kgettid(ureg_t*); /* 0x48 */
void knew_pages(ureg_t*);
void kremove_pages(ureg_t*);
void ksleep(ureg_t*); /* 0x4B */

int init_mod_syscall_io();
void kgetchar(ureg_t*); /* 0x4C */
void kreadline(ureg_t*); /* 0x4D */
void kprint(ureg_t*); /* 0x4E */
void kset_term_color(ureg_t*); /* 0x4F */
void kset_cursor_pos(ureg_t*); /* 0x50 */
void kget_cursor_pos(ureg_t*); /* 0x51 */

void kget_ticks(ureg_t*); /* 0x53 */
void kmisbehave(ureg_t*); /* 0x54 */
void khalt(ureg_t*); /* 0x55 */
void kls(ureg_t*); /* 0x56 */
void ktask_vanish(ureg_t*); /* 0x57 */
void kset_status(ureg_t*); /* 0x59 */
void kvanish(ureg_t*); /* 0x60 */

void kswexn(ureg_t*); /*0x74*/

#endif
