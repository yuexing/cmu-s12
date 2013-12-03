/**
 * @@file cond.c
 * @@brief implementation of the
 * cond var.
 * @@author Yue Xing(yuexing)
 * @@bug No Found Bug
 */

#include <libthread.h>

/**
 * @@brief Initialize a cond var.
 *
 * @@pre A cond var should be
 * initialized once and only once.
 */
int cond_init (cond_t *cv)
{
	ENTRY_CK_RET(cv);

	cv->valid = 1;
	Q_INIT(cv->head,cv->tail);
	return mutex_init(&cv->qlock);
}

/**
 * @@brief Allows a thread to wait 
 * for a condition.
 * @@pre The cond var should have 
 * been initialized.
 */
void cond_wait(cond_t *cv, 
		mutex_t *pm)
{
	ENTRY_CK(cv);


	ENTRY_CK_VALID(cv);

	node_t me;
	me.tid = gettid();
	me.reject = 0;
	me.next = NULL;

	mutex_lock(&cv->qlock);
	Q_PUSH(cv->head,cv->tail,
			&me);
	spinlock_init(&me.lock);
	/*safe to release the lock*/
	mutex_unlock(pm);
	mutex_unlock(&cv->qlock);

	deschedule(&me.reject);
	// grab my lock again
	spinlock_lock(&me.lock);
	/* run again */
	mutex_lock(pm);
}

/**
 * @@brief Wake up a waiting thread
 * if there is.
 */
void cond_signal(cond_t *cv)
{
	ENTRY_CK(cv);

	ENTRY_CK_VALID(cv);

	node_t *next = NULL;
	mutex_lock(&cv->qlock);
	if(!Q_EMPTY(cv->head)){
		Q_EJECT(cv->head,cv->tail,
				next);
		assert(next);
		mutex_unlock(&cv->qlock);
		MAKE_RUN(next);
	} else {
		mutex_unlock(&cv->qlock);
	}
}

/**
 * @@brief Wake up all waiting 
 * threads before the broadcast
 * time.
 */
void cond_broadcast(cond_t *cv)
{
	ENTRY_CK(cv);

	ENTRY_CK_VALID(cv);

	node_t *next = NULL;
	mutex_lock(&cv->qlock);
	while(!Q_EMPTY(cv->head)){
		Q_EJECT(cv->head,cv->tail,
				next);
		assert(next);
		MAKE_RUN(next);
	}
	mutex_unlock(&cv->qlock);
}

/**
 * @@brief Destroy a cond var
 * only when there is no one
 * waiting on the cond var.
 */
void cond_destroy(cond_t *cv)
{
	ENTRY_CK(cv);

	ENTRY_CK_VALID(cv);

	mutex_lock(&cv->qlock);
	if(Q_EMPTY(cv->head)){
		mutex_destroy(&cv->qlock);
		cv->valid = 0;
	} else {
		mutex_unlock(&cv->qlock);
	}
}
