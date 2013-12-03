/**
 * @file sem.c
 * @brief This file contains the 
 * implementation of the sem.
 *
 * A semaphore is implemented on
 * the top of mutex lock and cond
 * var.
 *
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */
#include <libthread.h>

/**
 * @brief Initialize the semaphore.
 *
 * @pre A semaphore should be 
 * initialized once and only once.
 */
int sem_init(sem_t *sem, int cnt)
{
	ENTRY_CK_RET(sem);

	if(cnt < 0){
		return ERROR;
	}

	sem->valid = 1;
	sem->count = cnt;
	int ret = cond_init(&sem->cv);
	ret |= mutex_init(&sem->lock);
	return ret;
}

/**
 * @brief Wait allows a thread to
 * decrement the semaphore value.
 * Once the value is less than 0,
 * the thread has to cond_wait().
 */
void sem_wait(sem_t *sem)
{
	ENTRY_CK(sem);

	ENTRY_CK_VALID(sem);

	mutex_lock(&sem->lock);
	sem->count--;
	if(sem->count < 0){
		cond_wait(&sem->cv, 
				&sem->lock);
	}
	mutex_unlock(&sem->lock);
}

/**
 * @brief Increment the semaphore
 * value. If the value is less than
 * or equal to 0, which indicate
 * there must be some thread(s)
 * waiting for cond_signal().
 */
void sem_signal(sem_t *sem)
{
	ENTRY_CK(sem);

	ENTRY_CK_VALID(sem);

	mutex_lock(&sem->lock);
	sem->count++;
	if(sem->count <= 0){
		cond_signal(&sem->cv);
	}
	mutex_unlock(&sem->lock);
}

/**
 * @brief Destroy a semaphore
 * only when no waiting thread.
 */
void sem_destroy(sem_t *sem)
{
	ENTRY_CK(sem);

	ENTRY_CK_VALID(sem);

	mutex_lock(&sem->lock);
	if(sem->count < 0){
		mutex_unlock(&sem->lock);
		return;
	}

	cond_destroy(&sem->cv);
	mutex_destroy(&sem->lock);
	sem->valid = 0;
}
