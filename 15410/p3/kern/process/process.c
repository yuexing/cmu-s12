/**
 * @file process.c
 * @brief implementation of process prototypes which 
 * are defined in inc/process.h.
 *
 * A process possesses its own information, thread list
 * , and children list. 
 * When a process dies, there is a need to collect its
 * resources, and move its children to init process.
 *
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#include <kernel.h>

static void move_running_to_init(process_t *parent);
static void move_zombie_to_init(process_t *parent);
static block_t globl_lock;

/**
 * @brief Initialize the process sub-module.
 */
int init_mod_proc()
{
	return block_init(&globl_lock);	
}

/**
 * @brief Initialize the brand new pcb.
 *
 * @param pcb
 * @param thr The main thread for the process
 * @param dir The page dir.
 */
void init_pcb(process_t *pcb, 
	kthread_tcb_t *tcb, page_dir_t *dir)
{
	/* paging */
	pcb->dir = dir;
	block_init(&pcb->dir_lock);

	/* status */
	pcb->status = 0;

	/* main thread */
	pcb->main_tid = tcb->tid;

	/* thr_list */
	block_init(&pcb->thr_lock);
	pcb->thr_cnt = 0;
	Q_INIT(pcb->thr_head, pcb->thr_tail);
	add_thr(pcb, tcb);

	/* running, zombie children */
	Q_INIT(pcb->running_head, pcb->running_tail);
	block_init(&pcb->running_lock);
	Q_INIT(pcb->zombie_head, pcb->zombie_tail);
	block_init(&pcb->zombie_lock);
	kcond_init(&pcb->zombie_cv);

	/* waited cnt*/
	block_init(&pcb->unwaited_cnt_lock);
	pcb->unwaited_cnt = 0;

	/* sibling */
	pcb->next = pcb->prev = NULL;
}

/**
 * @brief Destroy a pcb by deallocating its resources 
 * and move its children to Init process.
 *
 * @param pcb The process to destroy
 */
void destroy_pcb(process_t *pcb)
{
	/* turn off the current paging
	 * and use the global page table
	 * instead.
	 */
	destroy_dir(pcb->dir,pcb);
	
	// sequence matters here!
	move_running_to_init(pcb);
	move_zombie_to_init(pcb);

	/* move myself now */
	add_zombie(pcb);
}

/**
 * @brief Append a thread to a process
 *
 * @param pcb
 * @param thr
 */
void add_thr(process_t *pcb, kthread_tcb_t *tcb)
{
	/* Append */
	block_lock(&pcb->thr_lock);
	Q_PUSH(pcb->thr_head, pcb->thr_tail, 
		&(tcb->pcb_node));
	pcb->thr_cnt++;
	block_unlock(&pcb->thr_lock);
	/* associate */
	tcb->pcb = pcb;
}

/**
 * @brief remove a thread from a process
 *
 * @param pcb
 * @param thr
 * @return 1 if the thread is the last one in the 
 * process. Otherwise, return 0.
 */
int del_thr(process_t *pcb, kthread_tcb_t *tcb)
{
	int isLast = 0;
	block_lock(&pcb->thr_lock);
	if(!(--pcb->thr_cnt)){
		isLast = 1;
	}
	Q_DEL(pcb->thr_head, pcb->thr_tail, 
		&(tcb->pcb_node));
	if(isLast) assert(!pcb->thr_head);

	block_unlock(&pcb->thr_lock);
	return isLast;
}

/**
 * @brief Append a child process to its parent process's child list
 */
void add_child(process_t *parent, process_t *child)
{
	/* count */
	block_lock(&parent->unwaited_cnt_lock);
	parent->unwaited_cnt++;
	block_unlock(&parent->unwaited_cnt_lock);

	/* add to running children q */
	block_lock(&parent->running_lock);
	Q_PUSH(parent->running_head, parent->running_tail, child);
	block_unlock(&parent->running_lock);

	/* associate */
	child->parent = parent;
}

/**
 * @brief Remove a child from running q and add it to 
 * the zombie q.
 * Race: 'parent' field can change to 'init' when it
 * parent die.
 */
void add_zombie(process_t *child)
{
	block_lock(&globl_lock);

	process_t *parent = child->parent;
	debug_call("add %lu->%lu", child->main_tid,
			parent->main_tid);
	if(parent == proc_init){
		block_unlock(&globl_lock);
	}
	// Remove from run q
	block_lock(&parent->running_lock);
	Q_DEL(parent->running_head,parent->running_tail,child);
	block_unlock(&parent->running_lock);
	// add to zombie q
	block_lock(&parent->zombie_lock);
	Q_PUSH(parent->zombie_head,parent->zombie_tail,child);
	// signal parent
	kcond_signal(&parent->zombie_cv);
	block_unlock(&parent->zombie_lock);
	if(parent != proc_init){
		block_unlock(&globl_lock);
	}
}

void reap_child(process_t *child)
{
	free(child);
}

/**
 * @brief get a zombie child.
 */
process_t *get_zombie(process_t *proc)
{
	process_t *ret = NULL;
	assert(!Q_EMPTY(proc->zombie_head));
	Q_EJECT(proc->zombie_head, proc->zombie_tail, ret);
	return ret;
}

/**
 * @brief Move the running children to init if there
 * are.
 */
static void move_running_to_init(process_t *parent)
{
	if(Q_EMPTY(parent->running_head)){
		return;
	}

	// update the unwaited_cnt
	// the cnt of parent will not change any more
	// as the process is vanishing
	block_lock(&proc_init->unwaited_cnt_lock);
	proc_init->unwaited_cnt += 
		parent->unwaited_cnt;
	block_unlock(&proc_init->unwaited_cnt_lock);

	// parent appends all running chilren
	// to init process, and chg the 'parent'
	// field. If there is ctx_switch, a 
	// vanishing child may add_zombie() to
	// the parent. This may cause:
	// 1) proc_init->unwaited_cnt is incorrect;
	// 2) the child is added to the 'gone' 
	// parent;
	block_lock(&globl_lock);

	block_lock(&proc_init->running_lock);
	block_lock(&parent->running_lock);
	if(parent->running_head){
		// append all
		Q_PUSH_ALL(proc_init->running_head,
				proc_init->running_tail, 
				parent->running_head,
				parent->running_tail);
		// update associate
		process_t *cur = parent->running_head;
		while(cur != parent->running_tail){
			cur->parent = proc_init;
			cur = cur->next;
		}
		parent->running_tail->parent 
			= proc_init;
		// clr myself
		parent->running_tail = parent->running_head
			= NULL;
	}
	block_unlock(&parent->running_lock);
	block_unlock(&proc_init->running_lock);

	block_unlock(&globl_lock);
}

/**
 * @brief Move the running children to init if there
 * are.
 * The parent is vanishing, thus the zombie queue of
 * parent will not chg at all.
 */
static void move_zombie_to_init(process_t *parent)
{
	if(Q_EMPTY(parent->zombie_head)){
		return;
	}

	block_lock(&proc_init->zombie_lock);
	block_lock(&parent->zombie_lock);
	if(parent->zombie_head){
		// append all
		Q_PUSH_ALL(proc_init->zombie_head,
				proc_init->zombie_tail, 
				parent->zombie_head,
				parent->zombie_tail);
		// update associate
		process_t *cur = parent->zombie_head;
		while(cur != parent->zombie_tail){
			cur->parent = proc_init;
			cur = cur->next;
		}
		parent->zombie_tail->parent
			= proc_init;
		// clr myself
		parent->zombie_head = parent->zombie_tail
			= NULL;
		// signal init
		kcond_signal(&proc_init->zombie_cv);
	}
	block_unlock(&parent->zombie_lock);
	block_unlock(&proc_init->zombie_lock);
}
