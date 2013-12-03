/**
 * @file mutex.c
 * @brief The implementation of mutex.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug.
 */
#include <libthread.h>
/**
 * @brief Initialize a mutex by 
 * simply initializing its queue, 
 * spinlock, and giving it a lock.
 *
 * @pre A mutex should be 
 * initialized once and only once.
 */
int mutex_init(mutex_t *pm)
{
	ENTRY_CK_RET(pm);

	pm->valid = 1;
	pm->lock = 1;
	pm->holder = -1;
	Q_INIT(pm->head, pm->tail);
	return spinlock_init(&pm->qlock);
}

/**
 * @brief By leveraging spinlock,
 * a thread can atomically check
 * the mutex status.
 *
 * If a thread fail to get the lock,
 * it adds itself in the mutex's 
 * queue,unlock spinlock, and 
 * deschedule itself.
 *
 * @pre The mutex should have been
 * initialized.
 */
void mutex_lock(mutex_t *pm)
{
	ENTRY_CK(pm);

	ENTRY_CK_VALID(pm);

	node_t me;
	me.tid = gettid();
	me.reject = 0;
	me.next = NULL;

	spinlock_lock(&pm->qlock);
	if(pm->lock){
		pm->lock = 0;
		pm->holder = me.tid;
		spinlock_unlock(&pm->qlock);
	}else {
		Q_PUSH(pm->head,pm->tail,
				&me);
		// init the lock before who will
		// wake me up
		spinlock_init(&me.lock);
		spinlock_unlock(&pm->qlock);
		/* deschedule */
		deschedule(&me.reject);
		// wakes up and wait for
		// make_runnable has been
		// done!
		spinlock_lock(&me.lock);
	}
}

/**
 * @brief Unlock by atomically checking
 * the mutex's queue.
 *
 * If there exist thread waiting in
 * the queue, wake up the first one
 * without releasing the lock.
 */
void mutex_unlock(mutex_t *pm)
{
	ENTRY_CK(pm);

	ENTRY_CK_VALID(pm);

	ENTRY_CK_HOLDER(pm);

	node_t *node = NULL;

	spinlock_lock(&pm->qlock);
	if(Q_EMPTY(pm->head)){
		pm->holder = -1;
		pm->lock = 1;
		spinlock_unlock(&pm->qlock);
	} else {
		Q_EJECT(pm->head, pm->tail, node);
		assert(node);
		pm->holder = node->tid;
		spinlock_unlock(&pm->qlock);
		MAKE_RUN(node);
	}
}

/**
 * @brief destroy a mutex.
 * No rsrc to release.
 *
 * @pre The usage of mutex after
 * destruction is undefined.
 */
void mutex_destroy(mutex_t *pm)
{
	ENTRY_CK(pm);

	ENTRY_CK_VALID(pm);

	ENTRY_CK_HOLDER(pm);

	if(Q_EMPTY(pm->head)){
		pm->valid = 0;
	}
}
