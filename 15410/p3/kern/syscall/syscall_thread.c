/**
 * @file syscall_thread.c
 * @brief implementaion of thread-
 * related syscalls.
 * @author Yue Xing(yuexing)
 * @bug No Bug Found
 */

#include <kernel.h>

/**
 * get_ticks
 */
void kget_ticks(ureg_t* ureg)
{
	ureg->eax = tick();
}


/**
 * @brief int yield(int)
 */
void kyield(ureg_t* ureg)
{
	int tid = (int)ureg->esi;
	/* ck */
	if(tid < -1){
		goto err;
	}

	disable_ctx_switch();
	kthread_tcb_t *thr 
		= get_runnable(tid);
	if(!thr){
		enable_ctx_switch();
		goto err;
	}
	
	yield_to(thr);

	enable_ctx_switch();
	goto succ;
err:
	ureg->eax = ERROR;
	return;
succ:
	ureg->eax = SUCCESS;
	return;
}

/**
 * @brief int deschedule(int*)
 */
void kdeschedule(ureg_t* ureg)
{
	/* extract arg */
	int *reject = (int*)ureg->esi;

	/* ck */
	if(ck_addr(reject, WORD_SIZE, 0)
			== ERROR){
		goto err;
	}

	/* atomically ck and dischdule*/
	disable_ctx_switch();
	if(*reject){
		enable_ctx_switch();
		goto succ;
	}
	block();
	/* back to life */
	enable_ctx_switch();
	goto succ;

err:
	ureg->eax = ERROR;
	return;
succ:
	ureg->eax = SUCCESS;
	return;
}

/**
 * @brief int make_runnable(int)
 */
void kmake_runnable(ureg_t* ureg)
{
	int tid = (int)ureg->esi;
	if(tid < 0){
		goto err;
	}

	disable_ctx_switch();
	kthread_tcb_t *thr 
		= get_blocked(tid);
	if(!thr){
		enable_ctx_switch();
		goto err;
	}
	unblock_no_switch(NULL,NULL,thr);
	enable_ctx_switch();
	goto succ;
err:
	ureg->eax = ERROR;
	return;
succ:
	ureg->eax = SUCCESS;
	return;
}

/**
 * @brief int gettid();
 */
void kgettid(ureg_t* ureg)
{
	ureg->eax
		= THREAD_TCB_CUR->tid;
	return;
}

/**
 * int sleep(int)
 */
void ksleep(ureg_t* ureg)
{
	int ticks=(int)ureg->esi;
	/* ck arg */
	if(ticks < 0){
		goto err;
	}
	if(!ticks){
		goto succ;
	}
	/* set up ticks */
	kthread_tcb_t *cur
		= THREAD_TCB_CUR;
	ticks += tick();
	cur->ticks = ticks;
	/* sleep */
	disable_ctx_switch();
	sleep();
	enable_ctx_switch();
	goto succ;
err:
	ureg->eax = ERROR;
	return;
succ:
	ureg->eax = SUCCESS;
	return;
}

/**
 * @brief int swexn(void*, 
 * swexn_handler_t, void*, ureg_t)
 */
void kswexn(ureg_t* ureg)
{
	/* extract args */
	void **esi = (void**)ureg->esi;
	void *esp3= esi[0];
	uint32_t eip=(uint32_t)esi[1];
	void *arg = esi[2];
	ureg_t *newureg=(ureg_t*)esi[3];

	kthread_tcb_t *cur = THREAD_TCB_CUR;

	/* ck args */
	if(!esp3 || !eip){
		cur->swexn.esp3 = NULL;
		cur->swexn.eip = 0;
		goto ck_reg;
	}

	// esp3 points to an addr one word higher
	// than the first addr that the kernel
	// should use to push values onto the 
	// exception stack.
	if(ck_addr(esp3-WORD_SIZE, WORD_SIZE, 1)
			==ERROR){
		goto err;
	}

	if(ck_addr((void*)eip, WORD_SIZE, 0)
			==ERROR){
		goto err;
	}

	if(newureg&& 
		ck_addr(newureg, sizeof(ureg_t), 0)
		== ERROR){
		goto err;
	}

	// ck more about ureg, especially the eflag
	// The ureg should not crash the kernel
	if(newureg&&
			!valid_eflags(newureg->eflags)){
		goto err;
	}

	/* install */
	cur->swexn.esp3 = esp3;
	cur->swexn.eip = eip;
	cur->swexn.arg = arg;

ck_reg:
	/* whether or not a handler is being regi
	 * or de-regi, if the newureg is non-0,
	 * the kernel is requested to adopt the
	 * specified values before returning to
	 * user space. */
	if(newureg){
		memmove(ureg, newureg, sizeof(ureg_t));
	}
	goto succ;

err:
	ureg->eax = ERROR;
	return;
succ:
	ureg->eax = SUCCESS;
	return;
}
