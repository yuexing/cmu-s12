/**
 * @file kcond.h
 * @brief prototypes for the kernel level cond var
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#ifndef _KCOND_H
#define _KCOND_H

int kcond_init(kcond_t *kcond);
void kcond_wait(kcond_t *kcond, block_t *lock);
void kcond_signal(kcond_t *kcond);

#endif
