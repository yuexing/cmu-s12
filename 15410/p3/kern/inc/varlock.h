/**
 * @file varlock.h
 * @brief prototype for varlock 
 * functionality
 * @author Yue Xing(yuexing)
 * @bug No Found Bug.
 */

#ifndef _VARLOCK_H
#define _VARLOCK_H

int varlock_init(varlock_t*);
void varlock_lock(varlock_t*);
void varlock_unlock(varlock_t*);
uint32_t varlock_trylock(varlock_t*);

#endif
