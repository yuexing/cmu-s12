/**
 * @file exception.h
 * @brief prototype for exception handlers
 * @author Yue Xing(yuexing)
 * @bug No Found bug.
 */

#ifndef _EXCEPTION_H
#define _EXCEPTION_H

void basic_handle(ureg_t *ureg);

void pf_handle(ureg_t *ureg);

void kill_bad_thr(kthread_tcb_t *tcb);
#endif
