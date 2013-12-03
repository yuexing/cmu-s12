/**
 * @file spinlock.h
 * @brief xchg-supported yield style spin-lock.
 *
 * The lock does not guarantee bounded waiting,
 * and can be used to implement other locks.
 *
 * The spinlock is suggested for short atomical
 * instructions.
 *
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#ifndef SPINLOCK_H
#define SPINLOCK_H

/**
 * @brief a spin lock contains the 
 * holder thread's id and the lock.
 */
typedef struct {
	int lock;
	int holder;
} spinlock_t;


int xchg(int *, int);

int spinlock_init(spinlock_t*);
void spinlock_lock(spinlock_t*);
void spinlock_unlock(spinlock_t*);

#define SPINLOCK_INIT {1,-1}
#endif
