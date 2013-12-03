/**
 * @file xchglock.h
 * @brief Prototype for the xchglock.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#ifndef _XCHGLOCK_H
#define _XCHGLOCK_H

int xchglock_init(xchglock_t*);
void xchglock_lock(xchglock_t*);
void xchglock_unlock(xchglock_t*);
void xchglock_destroy(xchglock_t*);

#endif
