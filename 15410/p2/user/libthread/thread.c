/**
 * @file thread.c
 * @brief Implementation of the
 * thread library.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#include <thr_internals.h>

static libthr_t libthr;
static thread_tcb_t *new_tcb(void *esp);
static void *alloc_stk();
static void thr_autostack(void*, ureg_t*);
static void hack_main();
/* The two get is an encapsulation for special 
 * process for main thread */
static inline thread_tcb_t *get_tcb(void *);
static inline void *get_base(void *);

/**
 * @brief specifies the amount of 
 * stack space which will be 
 * available for use by each thread
 * created with thr_create.
 */
int thr_init(unsigned int size)
{
	void *esp = get_esp();

	// init libthr
	libthr.size=PAGE_RD_UP(size);
	libthr.size_pages=
		libthr.size/PAGE_SIZE;
	libthr.boundary
		=(void*)(PAGE_RD_DN(esp)
				-(libthr.size-PAGE_SIZE));

	Q_INIT(libthr.hhead,
			libthr.htail);

	Q_INIT(libthr.thead,
			libthr.ttail);

	if(mutex_init(&libthr.block)
			== ERROR)
		goto err;
	if(mutex_init(&libthr.hlock)
			== ERROR)
		goto err;
	if(mutex_init(&libthr.tlock)
			== ERROR)
		goto err;
	
	debug_thr("main_esp: %p, size: %lu, bound: %p",
			esp, libthr.size, libthr.boundary);

	// deal with main thread
	void *top = (void*)PAGE_RD_DN(esp);

	// we need to track main
	// thread for join, exit
	// gettid.
	thread_tcb_t *tcb
		= libthr.main 
		= new_tcb(esp);

	if(!tcb){
		goto tcb_err;
	}

	// install thread-version autostack
	if(swexn(ESP3(tcb), thr_autostack, NULL, NULL)
			== ERROR){
		goto swexn_err;
	}
	
	// hack the return addr
	// of main thread, thus it
	// can call thr_exit and
	// be joined
	hack_main();
	tcb->tid = gettid();

	// no func and arg
	// main thread: 
	// no need lock here
	tcb->ready = 1;
	Q_PUSH(libthr.thead,
			libthr.ttail,
			tcb);
	return SUCCESS;

swexn_err:
	free(tcb);
tcb_err:
	free_stk(top);
err:
	return ERROR;
}

/**
 * @brief Allocate a stack and 
 * then invoke the thread_fork in 
 * an appropriate way.
 * the stack pointer should be 
 * 4-byte aligned.
 */
int thr_create(void*(*func)(void*),
		void *args)
{
	void *base = alloc_stk();
	void *top = (void*)PAGE_RD_DN(base);
	void *esp = base - 4;

	stk_tcb_t *stk_tcb
		= (stk_tcb_t*)base;

	thread_tcb_t *tcb
		= stk_tcb->tcb 
		= new_tcb(base);

	if(!tcb){
		goto tcb_err;
	}

	// fill with func and arg
	tcb->func = func;
	tcb->arg = args;

	// fork here
	int tid = -1;
	if((tid = thr_fork(esp))
			==ERROR){
		goto fork_err;
	}

	// add to queue
	mutex_lock(&tcb->lock);
		tcb->tid = tid;
	tcb->ready = 1;
	cond_signal(&tcb->cv);
	mutex_unlock(&tcb->lock);

	mutex_lock(&libthr.tlock);
	Q_PUSH(libthr.thead,
			libthr.ttail,
			tcb);
	mutex_unlock(&libthr.tlock);

	debug_thr("create: (%d), alloc: %p->%p", 
			tcb->tid, base, ((stk_tcb_t*)base)->tcb);
	// on success return tid
	return tid;

fork_err:
	free(tcb);
tcb_err:
	free_stk(top);
	return ERROR;
}

/**
 * @brief This function 'clean up' after a 
 * thread. Wait until the target thread exit
 * if it does not. A thread can be joined only
 * once.
 */
int thr_join(int tid, void **statusp)
{
	thread_tcb_t *prev = NULL, *target = NULL;
	mutex_lock(&libthr.tlock);
	// search
	for(prev=target=libthr.thead; target; 
			prev = target, target= target->next){
		if(target->tid == tid){
			break;
		}
	}
	if(!target){
		// joined or not exist
		mutex_unlock(&libthr.tlock);
		debug_thr("join (%d) not exist!", tid);
		return ERROR;
	}

	mutex_lock(&target->lock);
	if(!target->exited){
		// not exited
		// release the target's lock for its vanish
		// it is not safe to grab target's lock in
		// cond_wait, since the target may have been
		// reaped by others, which indicates the lock
		// is not valid at all.
		mutex_unlock(&target->lock);
		debug_thr("join (%d) WAIT!", tid);
		cond_wait(&target->cv, &libthr.tlock);
		// search again
		for(target=libthr.thead;
				target; target= target->next){
			if(target->tid == tid){
				break;
			}
		}
		if(!target){
			// joined
			mutex_unlock(&libthr.tlock);
			debug_thr("join (%d) JOINED!", tid);
			return ERROR;
		} 
		// target zombie, REAP it!
		mutex_lock(&target->lock);
		assert(target->exited);
	}
	// join
	if(statusp){
		*statusp = target->status;
	}
	// reap
	Q_DEL(libthr.thead, libthr.ttail,
			prev, target);
	FREE_TCB(target);

	mutex_unlock(&libthr.tlock);
	debug_thr("join (%d) REAPED!", tid);
	return SUCCESS;
}

/**
 * @brief Exits the thread with exit status.
 */
void thr_exit(void* status)
{
	void *esp = get_esp();
	thread_tcb_t *tcb = get_tcb(esp);
	assert(tcb);

	debug_thr("exit: (%d)", tcb->tid);

	tcb->status = status;
	/* It's ok to use esp3, since
	 * I am vanishing now*/
	switch_stack(ESP3(tcb), tcb);
}

/**
 * @brief Returns the thread ID of the current
 * thread.
 */
int thr_getid(void)
{
	void *esp = get_esp();
	thread_tcb_t *tcb = get_tcb(esp);
	assert(tcb);

	return tcb->tid;
}

/**
 * @brief Defers execution of the invoking thread
 * in favor the thread with ID as tid.
 */ 
int thr_yield(int tid)
{
	return yield(tid);
}

/**
 * @brief create a brand new tcb.
 * base is enough for this function, as we
 * know the first allocated stack is PAGE_SIZE.
 *
 * @pre base is assumed as to be with the first
 * page of the stack.
 */
static thread_tcb_t *new_tcb(void *base)
{
	void *top = (void*)PAGE_RD_DN(base);
	void *limit = (void*)PAGE_RD_UP(base-
			libthr.size);

	thread_tcb_t *tcb= malloc(
			sizeof(thread_tcb_t));
	if(!tcb){
		goto err;	
	}

	tcb->tid = -1;
	tcb->ready = 0;
	tcb->func = tcb->arg = NULL;
	tcb->top = top;
	tcb->limit = limit;
	tcb->status = 0;
	tcb->exited = 0;

	if(mutex_init(&tcb->lock)
			== ERROR){
		goto err_lock;
	}

	if(cond_init(&tcb->cv)
			== ERROR){
		goto err_lock;
	}

	return tcb;

err_lock:
	free(tcb);
err:
	return NULL;
}

/**
 * @brief the brand new thread 
 * will run this function
 */
void after_thr_fork()
{
	void *esp = get_esp();
	thread_tcb_t *tcb = get_tcb(esp);

	mutex_lock(&tcb->lock);
	// IF is enough
	if(!tcb->ready){
		cond_wait(&tcb->cv, &tcb->lock);
	}
	assert(tcb->ready);
	mutex_unlock(&tcb->lock);

	// install thread-version autostack
	if(swexn(ESP3(tcb), thr_autostack, NULL, NULL)
			== ERROR){
		goto err;
	}

	// the child has been added,
	// and setted tid
	debug_thr("after_thr_fork (%d): %p", 
			tcb->tid, tcb->arg);
	thr_exit(tcb->func(tcb->arg));
err:
	assert(0);
}

/**
 * @brief after switch stack to the 
 * esp3, the thread still holds its
 * lock.
 */
void after_switch_stack(thread_tcb_t *tcb)
{
	// free its stack now
	free_stk(tcb->top);

	/* broadcast the death */
	mutex_lock(&tcb->lock);
	tcb->exited = 1;
	cond_broadcast(&tcb->cv);

	debug_thr("after switch stk (%d)", tcb->tid);
	/* vanish safely */
	unlock_vanish(&tcb->lock);
}

/**
 * @brief Free stack by remove_pages
 * and adding to hole list.
 * @pre Top is PAGE_SIZE aligned
 * and used for new_pages.
 */
void free_stk(void *top)
{
	void *base = get_base(top);

	for(; top < base; top += PAGE_SIZE){
		remove_pages(top);
	}

	hole_node_t *node
		= malloc(sizeof(hole_node_t));

	assert(node);

	node->base = base;
	node->next = NULL;

	mutex_lock(&libthr.hlock);
	Q_PUSH(libthr.hhead, libthr.htail,
			node);
	mutex_unlock(&libthr.hlock);
}

/**
 * @brief find a hole in free list
 * if there is, otherwise alloc
 * by new_pages
 *
 * @return the base of the new stack,
 * which is 4-byte aligned.
 */
static void *alloc_stk()
{
	void *base = NULL, *tmp = NULL;

	/*find a hole*/
	hole_node_t *node = NULL;
	mutex_lock(&libthr.hlock);	
	if(!Q_EMPTY(libthr.hhead)){
		Q_EJECT(libthr.hhead,
				libthr.htail,
				node);
		mutex_unlock(&libthr.hlock);

		/* node is found! */
		assert(node);
		base = node->base;
	} else {
		mutex_unlock(&libthr.hlock);

		/*decrement boundary*/
		mutex_lock(&libthr.block);
		base = libthr.boundary-1;
		libthr.boundary-=libthr.size;
		mutex_unlock(&libthr.block);

		// no enough memory?
		base = (void*)WORD_RD_DN(base);
	}

	// alloc a PAGE_SIZE 0-filled stack
	tmp = (void*)PAGE_RD_DN(base);
	new_pages(tmp,PAGE_SIZE);
	memset(tmp,0, PAGE_SIZE);

	// return the base
	return base;
}

/**
 * @brief To get main is a little different.
 */
static inline thread_tcb_t *get_tcb(void *esp)
{
	thread_tcb_t *tcb = NULL;
	void *base = NULL;
	if(libthr.main && esp >= (libthr.main)->limit){
		/* main thread */
		tcb = libthr.main;
	} else {
		base = BASE(esp);
		tcb = ((stk_tcb_t*)base)->tcb;
	}

	return tcb;
}

static inline void *get_base(void *esp)
{
	return BASE(esp);
}

/**
 * @brief thread autostack is like single-thread
 * process autostack, except that it needs updating
 * top and guaranteeing limit.
 */
static void thr_autostack(void *arg, ureg_t *ureg)
{
	void *esp = (void*)ureg->esp;

	thread_tcb_t *tcb = get_tcb(esp);
	assert(tcb);

	void *limit = tcb->limit;
	void *cr2 = NULL;

	if(ureg->cause == SWEXN_CAUSE_PAGEFAULT
			&& IS_NON_PRESENT(ureg->error_code)){

		cr2 = (void*)PAGE_RD_DN(ureg->cr2);	

		if(cr2 < limit){
			debug_thr("cr2: %p", cr2);
			panic ("FATAL: Ask for too much space!\n");
		}

		if(new_pages(cr2, PAGE_SIZE)){
			/* when new_page fail we panic */
			panic ("FATAL: Stack Growth Failed!\n");
		}

		memset(cr2, 0, PAGE_SIZE);

		if(tcb->top > cr2){
			tcb->top = cr2;
		}
		debug_thr("autogrow (%d): top:%p, cr2:%p", 
				tcb->tid, tcb->top, cr2);
	} 
	/* else always re-install */
	swexn(ESP3(tcb), thr_autostack, 0, ureg);
}

/**
 * @brief hack the return addr of main
 * thread to thr_exit.
 */
static void hack_main()
{
	void **ebp = get_ebp();
	while(*ebp != _main_ebp){
		ebp = (void**)*ebp;
	}
	*(ebp+1) = thr_exit;
}
