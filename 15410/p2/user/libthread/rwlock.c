/**
 * @file rwlock.c
 * @brief implementation of the rwlock.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug.
 */

#include <libthread.h>

int rwlock_init(rwlock_t *rwlock)
{
	ENTRY_CK_RET(rwlock);

	rwlock->writer = -1;

	rwlock->readers
		= rwlock->waiting_readers
		= rwlock->waiting_writers
		= 0;

	rwlock->valid = 1;
	rwlock->turn = RWLOCK_READ;

	if(cond_init(&rwlock->rcv) 
		== ERROR)
		return ERROR;
	if(cond_init(&rwlock->wcv)
			== ERROR)
		return ERROR;
	if(mutex_init(&rwlock->lock)
			== ERROR)
		return ERROR;
	
	return SUCCESS;
}

/**
 * @brief lock the rwlock according to
 * certain type.
 */
void rwlock_lock(rwlock_t *rwlock,
		int type)
{
	ENTRY_CK(rwlock);

	ENTRY_CK_VALID(rwlock);

	if((type != RWLOCK_READ
				&& type != RWLOCK_WRITE)){
		return;
	}

	mutex_lock(&rwlock->lock);
	if(type == RWLOCK_READ){
		if(rwlock->writer >0 
				|| rwlock->waiting_writers > 0){
			/* there is a writer or 
			 * earlier waiting writers*/
			rwlock->waiting_readers++;
			cond_wait(&rwlock->rcv, &rwlock->lock);
			rwlock->waiting_readers--;
		}
		rwlock->readers++;
	} else {
		if(rwlock->readers >0 
				|| rwlock->writer > 0){
			/* there are readers or a writer*/
			rwlock->waiting_writers++;
			cond_wait(&rwlock->wcv, &rwlock->lock);
			rwlock->waiting_writers--;
		}
		rwlock->writer = gettid();
	}
	mutex_unlock(&rwlock->lock);
}

/**
 * @brief unlock the rwlock.
 */
void rwlock_unlock(rwlock_t *rwlock)
{
	ENTRY_CK(rwlock);

	ENTRY_CK_VALID(rwlock);

	mutex_lock(&rwlock->lock);

	// release the lock
	if(rwlock->writer == gettid()){
		rwlock->writer = -1;
	} else {
		rwlock->readers--;
	}

	// last reader and the writer wake up others
	if((rwlock->readers == 0)
			|| (rwlock->writer == -1)){
		// here take turns to broadcast waiting readers
		// or signal a waiting writer
		if(rwlock->waiting_writers > 0
				&& rwlock->waiting_readers > 0){
			if(rwlock->turn == RWLOCK_READ){
				cond_broadcast(&rwlock->rcv);
				rwlock->turn = RWLOCK_WRITE;
			} else {
				cond_signal(&rwlock->wcv);
				rwlock->turn = RWLOCK_READ;
			}
		} else if (rwlock->waiting_readers > 0){
			// flush all readers
			cond_broadcast(&rwlock->rcv);
		} else if (rwlock->waiting_writers > 0){
			// signal a writer
			cond_signal(&rwlock->wcv);
		}
	}
	mutex_unlock(&rwlock->lock);
}

/**
 * @brief A writer decides to be a reader.
 * After the call no one holds WRITE lock,
 * more than one holds READ lock. The waiting
 * writers continue waiting.
 */
void rwlock_downgrade(rwlock_t *rwlock)
{
	ENTRY_CK(rwlock);

	ENTRY_CK_VALID(rwlock);

	assert(rwlock->writer = gettid());

	mutex_lock(&rwlock->lock);
	// unlock WRITE and lock READ
	rwlock->writer = -1;
	rwlock->readers++;
	rwlock->turn = RWLOCK_WRITE;
	// flush all readers
	cond_broadcast(&rwlock->rcv);
	mutex_unlock(&rwlock->lock);
	return;
}

/**
 * @brief Deactivate the lock.
 */
void rwlock_destroy(rwlock_t *rwlock)
{
	ENTRY_CK(rwlock);

	ENTRY_CK_VALID(rwlock);

	/* make sure no reader/writer */
	if(!rwlock->readers 
			|| (rwlock->readers-1) == 0
			|| (rwlock->writer == gettid())){

		/* make sure no waiting thread*/
		if(!rwlock->waiting_readers
				&& !rwlock->waiting_writers){
			cond_destroy(&rwlock->rcv);
			cond_destroy(&rwlock->wcv);
			mutex_destroy(&rwlock->lock);

			rwlock->valid = 0;
		}
	}
}
