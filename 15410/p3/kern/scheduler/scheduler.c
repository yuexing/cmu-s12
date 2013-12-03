/**
 * @file scheduler.c
 * @brief The queue manipulation is a scheduler
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#include <kernel.h>

/* 3 types queues */
static schedule_node_t *run_head;
static schedule_node_t *run_tail;

static schedule_node_t *sleep_head;
static schedule_node_t *sleep_tail;

static schedule_node_t *block_head;
static schedule_node_t *block_tail;

static varlock_t ctx_lock;

static void from_to(schedule_node_t **from_head, 
schedule_node_t **from_tail, schedule_node_t **to_head, 
schedule_node_t **to_tail, kthread_tcb_t *pref, uint32_t tid);
static schedule_node_t *sleep2run();
static kthread_tcb_t *pop_tcb(schedule_node_t **head, 
		schedule_node_t **tail, uint32_t tid);
static void sleepq_adjust();

#define IS_RUN(a)	(a==&run_head)
#define IS_SLEEP(a)	(a==&sleep_head)
#define IS_BLOCK(a)	(a==&block_head)
#define IS_IDLE(a)	(a==thr_idle)

void tickback(uint32_t numTicks);

/**
 * @brief Initialize the scheduler module.
 */
int init_mod_scheduler()
{
	varlock_init(&ctx_lock);

	Q_INIT(run_head, run_tail);
	Q_INIT(sleep_head, sleep_tail);
	Q_INIT(block_head, block_tail);


	install_timer_callback(tickback);

	return SUCCESS;
}

/**
 * @brief Timer handler callback function
 * Try to put the current thread to the
 * run queue and schedule next runnable
 * thread.
 */
void tickback(uint32_t numTicks)
{
	if(!try_ctx_switch())
		return;

	sleepq_adjust();

	from_to(&run_head, &run_tail,
		&run_head, &run_tail, NULL, -1);

	enable_ctx_switch();
}

/* 
 * @brief disalbe ctx switch by acquiring
 * the lock.
 */
void disable_ctx_switch()
{
	varlock_lock(&ctx_lock);
}

/**
 * @brief enable ctx switch by releasing
 * the lock.
 */
void enable_ctx_switch()
{
	varlock_unlock(&ctx_lock);
}

/**
 * @brief try to ctx switch. This is used
 * by timer.
 *
 * @return 1 on success, 0 on failure.
 */
int try_ctx_switch()
{
	return varlock_trylock(&ctx_lock);
}


/**
 * @brief put the current thread into the
 * 'toQ'. 
 * Try to run 'pref', or the thread with 
 * 'tid' from the 'fromQ'. 
 * If both of the above two can not run, 
 * and the 'fromQ' is the run queue, run 
 * the next runnable thread. 
 * If 'toQ' is the sleep queue, insert the 
 * cur thread by increasing order.
 *
 * @prev pref should not be in any queue.
 *
 * @param from From this queue to 
 * schedule next thread.
 * @param to Put the current thread
 * to the queue.
 */
static void from_to(schedule_node_t **from_head, 
schedule_node_t **from_tail, schedule_node_t **to_head, 
schedule_node_t **to_tail, kthread_tcb_t *pref, uint32_t tid)
{
	kthread_tcb_t *cur=THREAD_TCB_CUR;
	if((void*)cur==(void*)tmp_dir){
		// let the vanishing thread die
		return;
	}

	kthread_tcb_t *next = NULL;
	if(pref){
		next = pref;
	} else {
		next = pop_tcb(from_head, from_tail, tid);
	}

	if(!next){
		if(IS_RUN(from_head)){
			next = thr_idle;
		} else {
			// no one to schedule
			return;
		}
	}

	if(cur == next){
		/* no need to switch */
		return;
	}

	if(to_head
		&&IS_RUN(to_head)&&IS_IDLE(next)){
		/* no need to switch */
		return;
	}

	if(!IS_VALID(next)){
		// next is not valid
		if(IS_IDLE(next)){
			// I wanna hlt!
			assert(0);
		} else {
			kill_bad_thr(next);
			return;
		}
	}

	schedule_node_t *node = &(cur->schedule_node),
					*tmp = NULL;

	// to_head is NULL because
	// we do not want to save
	if(to_head && !IS_IDLE(cur)){
		if(IS_SLEEP(to_head)){
			// sleep queue
			cur->state = SLEEP;
			if(!*to_head||(*to_head)->tcb->ticks 
					>= cur->ticks){
				// put in front
				Q_INJECT(*to_head, *to_tail, node);
			} else {
				for(tmp=*to_head;tmp&&
						tmp->tcb->ticks<cur->ticks; 
						tmp = tmp->next);
				if(tmp){
					// insert 
					tmp->prev->next = node;
					node->prev = tmp->prev;
					node->next = tmp;
					tmp->prev = node;
				} else {
					// push in tail
					Q_PUSH(*to_head,*to_tail, node);
				}
			}
		} else {
			// others
			if(IS_RUN(to_head)){
				cur->state = RUNNABLE;
			} else {
				cur->state = BLOCKED;
			}
			Q_PUSH(*to_head,*to_tail,node);
		}
	}
	switch_to(next);
}

/**
 * @brief put a thread to the tail 
 * of runnable queue.
 */
void runq_push(kthread_tcb_t* tcb)
{
	tcb->state = RUNNABLE;
	Q_PUSH(run_head, run_tail, 
			&(tcb->schedule_node));
}

/**
 * @brief put a thread to the front
 * of runnable queue.
 */
void runq_inject(kthread_tcb_t* tcb)
{
	tcb->state = RUNNABLE;
	Q_INJECT(run_head, run_tail, 
			&(tcb->schedule_node));
}

/**
 * @brief remove the front of the 
 * sleep queue and add it to the
 * front of runnable queue.
 */
static schedule_node_t *sleep2run()
{
	schedule_node_t *front = NULL;
	Q_EJECT(sleep_head, sleep_tail,
			front);

	assert(front->tcb->state
			== SLEEP);

	runq_inject(front->tcb);
	return sleep_head;
}

/**
 * @brief traverse the sleep queue and add
 * runnable thread to the run queue.
 */
static void sleepq_adjust()
{
	/* traverse */
	uint32_t time = tick();
	schedule_node_t *tmp = sleep_head;
	while(tmp&&tmp->tcb->ticks < time){
		tmp = sleep2run();
	}
}

/**
 * @brief Get the blocked thread.
 */
kthread_tcb_t *get_blocked(int tid)
{
	kthread_tcb_t *tcb
		= pop_tcb(&block_head,
				&block_tail,tid);
	if(!tcb){
		return NULL;
	}
	assert(tcb->state 
			== BLOCKED);
	return tcb;
}

/**
 * @brief Get the blocked thread.
 */
kthread_tcb_t *get_runnable(int tid)
{
	kthread_tcb_t *tcb
		= pop_tcb(&run_head,
				&run_tail,tid);
	if(!tcb && tid == -1){
		return thr_idle;
	}
	if(tcb){
		assert(tcb->state 
				== RUNNABLE);
	}
	return tcb;
}

/**
 * @brief get and del a thread from a 
 * queue by its tid. If the tid is
 * -1, get the front of the queue.
 *
 * The corresponding node will be deleted 
 * from the queue.
 *
 * @return NULL or a node
 */
static kthread_tcb_t *pop_tcb(schedule_node_t **head, 
		schedule_node_t **tail, uint32_t tid)
{
	schedule_node_t *node = NULL;

	if(tid == -1) {
		Q_EJECT(*head,*tail,node);
		if(!node){
			return NULL;
		}
	} else {
		Q_FIND(*head,*tail,node,tcb->tid,tid);
		if(!node){
			return NULL;
		}
		assert(node->tcb->tid==tid);
		Q_DEL(*head,*tail,node);
	}
	return node->tcb;
}

/**
 * @brief retrieve the next runnable
 * thread from run queue. 
 * If there is NULL, return thr_idle.
 */
kthread_tcb_t *get_next_runnable_thread()
{
	kthread_tcb_t *tcb = pop_tcb(
			&run_head, &run_tail, -1);
	if(!tcb){
		return thr_idle;
	}
	assert(tcb->state
			== RUNNABLE);
	return tcb;
}

/**
 * @brief add 'next' or the first of
 * the 'givenQ' to the tail
 * of the run queue. 
 * Currently used by locks and make_
 * runnable().
 */
void unblock_no_switch(schedule_node_t **head, 
		schedule_node_t **tail, kthread_tcb_t *next)
{
	schedule_node_t *node = NULL;
	if(next){
		assert(next->state
				== BLOCKED);
		runq_push(next);
	} else {
		assert(*head);
		Q_EJECT(*head, *tail, node);
		assert(node->tcb->state
				== BLOCKED);
		runq_push(node->tcb);
	}
}

/**
 * @brief try to run next. If next
 * is NULL or not runnable, run the
 * next thread from given queue.
 *
 * Currently only used by block_unlock
 * with context switch happening.
 *
 * @pre next Should Not Be in Any Queue.
 */
void unblock_and_switch(schedule_node_t **head, 
		schedule_node_t **tail, kthread_tcb_t *next)
{
	if(next){
		assert(next->state
				== BLOCKED);
		assert(!next->schedule_node.prev);
		assert(!next->schedule_node.next);
	}
	from_to(head,tail,&run_head,&run_tail,next,-1);
}

/**
 * @brief put the current thread to
 * the given queue. Try to schedule next
 * from runQ.
 *
 * Currently used by locks and 'next' is
 * the holder, and by readline.
 * */
void block_and_switch(schedule_node_t **head, 
		schedule_node_t **tail, kthread_tcb_t *next)
{
	if(next){
		if(next->state!=RUNNABLE) MAGIC_BREAK;
		assert(next->state
				== RUNNABLE);
		Q_DEL(run_head, run_tail, 
				&(next->schedule_node));
	}

	from_to(&run_head, &run_tail,
			head, tail, next, -1);
}

/**
 * @brief yield to thread 'pref'.
 */
void yield_to(kthread_tcb_t *pref)
{
	assert(pref);
	from_to(NULL, NULL, &run_head, 
			&run_tail, pref, -1);
}

/**
 * @brief put the current thread 
 * sleep.
 */
void sleep()
{
	from_to(&run_head, &run_tail,
			&sleep_head, &sleep_tail, 
			NULL,-1);
}

/**
 * @brief switch to next thread.
 */
void switch_to(kthread_tcb_t *next)
{
	next->state = RUNNING;
	uint32_t dir;
	uint32_t esp0;
	void *esp;
	void **save_esp;

	// next->esp, dir
	dir = (uint32_t)next->pcb->dir;
	esp = next->esp;

	// save_esp
	kthread_tcb_t *cur = THREAD_TCB_CUR;
	if((void*)cur == (void*)tmp_dir){
		// the vanishing thread use this addr
		save_esp = (void**)KSTK(cur);
	} else {
		save_esp = &(cur->esp);
	}

	// get_cr3() VS. dir
	if(get_cr3() != dir){
		set_cr3(dir);
	}

	// esp0
	esp0 = (uint32_t)KSTK(next);
	set_esp0(esp0);

	debug_thr("next:%d, %p", (int)next->tid, esp);
	ctx_switch(save_esp, esp);
	check_pending();
}

/**
 * @brief block the current thread.
 */
void block()
{	
	block_and_switch(&block_head,
			&block_tail, NULL);
}

/**
 * @brief check whether sth. interesting 
 * happens during the ctx switch.
 */
void check_pending()
{
	// get pending pcb
	if(!pending_thr){
		return;
	}

	kthread_tcb_t *next = pending_thr,
				  *cur = THREAD_TCB_CUR;
	// reset pending
	pending_thr = NULL;

	// update state
	runq_push(cur);
	next->state = RUNNING;

	// next can not vanish now!
	if(get_cr3() != (uint32_t)next->pcb->dir){
		set_cr3((uint32_t)next->pcb->dir);
	}

	//saved_esp
	void **esp = NULL;
	esp = &(cur->esp);

	//esp0
	uint32_t esp0 = (uint32_t)KSTK(next);
	set_esp0(esp0);

	ctx_switch(esp, next->esp);
}
