/**
 * @file syscall_process.c
 * @brief Implementation of process-related
 * syscalls.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#include <kernel.h>

/**
 * @brief int fork().
 * 1. only fork for uni-thread process;
 * 2. allocate rsrcs needed at once;
 * 3. roll-back on error;
 */
void kfork(ureg_t* ureg, void *saved_esp)
{
	int thr_cnt = -1;
	process_t *cur_pcb = THREAD_PCB_CUR;

	block_lock(&cur_pcb->thr_lock);
	thr_cnt = cur_pcb->thr_cnt;
	block_unlock(&cur_pcb->thr_lock);
	
	if(thr_cnt > 1){
		goto err;
	}

	// Create a pcb
	process_t *pcb =(process_t*)
		malloc(sizeof(process_t));
	if(!pcb){
		goto err;
	}

	// Create a new dir by clone
	// including:
	// install kernel pdes
	// clone all user space pages
	// create kernel stack
	void *kstk = NULL;
	page_dir_t *dir 
		= clone_dir(cur_pcb->dir, &kstk);
	if(!dir){
		goto dir_error;
	}

	// Initialize a new thread
	assert(kstk);
	kthread_tcb_t *tcb =
		(kthread_tcb_t*)kstk;
	int tid=init_tcb(tcb, ureg, saved_esp);

	// cp swexn 
	cp_swexn(THREAD_TCB_CUR, tcb);

	// associate
	init_pcb(pcb,tcb,dir);
	add_child(cur_pcb, pcb);

	//sim_reg_child(dir, cur_pcb->dir);

	// prepare to schedule
	disable_ctx_switch();
	runq_push((kthread_tcb_t*)kstk);
	enable_ctx_switch();

	ureg->eax = tid;
	return;

dir_error:
	free(pcb);
err:
	ureg->eax = ERROR;
	return;
}

/**
 * @brief exec(char*, char**)
 * we exec as follows:
 * 1. only allow uni-thread process;
 * 2. copy argv before load new program;
 * 3. reuse or release rsrc when loading new
 * program;
 * 4. roll back once there is an error.
 */
void kexec(ureg_t* ureg)
{
	int thr_cnt = -1;
	kthread_tcb_t *cur_tcb
		= THREAD_TCB_CUR;
	process_t *cur_pcb = cur_tcb->pcb;
	page_dir_t *cur_dir = cur_pcb->dir;

	block_lock(&cur_pcb->thr_lock);
	thr_cnt = cur_pcb->thr_cnt;
	block_unlock(&cur_pcb->thr_lock);

	/* not allow multi-thread */
	if(thr_cnt > 1){
		goto err;
	}

	/* extract args */
	void **esi = (void**)ureg->esi;
	char *execname = (char*)esi[0];
	char **argv = (char**)esi[1];
	int argc = 0;

	/* ck args */
	if(ck_str(execname)==ERROR){
		goto err;
	}

	if((argc = ck_str_arr(argv))
			==ERROR){
		goto err;
	}

	if(exist(execname)==ERROR){
		goto err;
	}

	/* load program */
	uint32_t esp = load_program(execname,argv,
			argc,cur_dir,&(cur_tcb->eip));

	//sim_reg_process(cur_dir, execname);

	if(esp == ERROR){
		goto err;
	}

	/* set up iret*/
	ureg->eip = cur_tcb->eip;
	ureg->esp = esp;
	ureg->eax = SUCCESS;
	return;
err:
	ureg->eax = ERROR;
	return;
}

void kexit(ureg_t* ureg)
{
	/* p3 */
	return;
}

/**
 * @brief int wait(int*)
 * wait for a child process, return tid,
 * set exit status.
 */
void kwait(ureg_t* ureg)
{
	int *pstatus = (int*)ureg->esi;
	
	/* ck args */
	if(pstatus&&ck_addr((void*)
		pstatus,WORD_SIZE, 1)
			== ERROR){
		goto err;
	}
	
	kthread_tcb_t *tcb = THREAD_TCB_CUR;
	process_t *cur = tcb->pcb;
	process_t *zombie = NULL;

	/*only the main thread can wait*/
	if(tcb->tid!=cur->main_tid){
		goto err;
	}

	/* update unwaited count */
	block_lock(&cur->unwaited_cnt_lock);
	if(!cur->unwaited_cnt){
		block_unlock(&cur->unwaited_cnt_lock);
		goto err;
	}
	cur->unwaited_cnt--;
	block_unlock(&cur->unwaited_cnt_lock);

	/* wait until signal */
	block_lock(&cur->zombie_lock);
	debug_call("(%lu),wait zombie", tcb->tid);
	while(Q_EMPTY(cur->zombie_head)){
		kcond_wait(&cur->zombie_cv, &cur->zombie_lock);
	}
	debug_call("(%lu),get zombie", tcb->tid);
	zombie = get_zombie(cur);
	block_unlock(&cur->zombie_lock);

	/* status */
	if(pstatus){
		*pstatus = zombie->status;
	}
	ureg->eax = zombie->main_tid;

	/* Reap */
	reap_child(zombie);
	return;
err:
	ureg->eax=ERROR;
	return;
}

/**
 * @brief vanish()
 *
 * If the cur thread is the last one in the
 * process, vanish the process as well.
 */
void kvanish(ureg_t* ureg)
{
	kthread_tcb_t *tcb = 
		THREAD_TCB_CUR;
	process_t *pcb = 
		tcb->pcb;

	/* destroy the process */
	if(del_thr(pcb,tcb)){
		destroy_pcb(pcb);
	}
	
	/*distroy the thread and switch */
	destroy_tcb(tcb);
}

/**
 * @brief 
 */
void kthread_fork(ureg_t* ureg, void *saved_esp)
{
	void *kstk = new_page();
	if(!kstk){
		goto err;
	}

	int tid=init_tcb(kstk,ureg,saved_esp);
	
	/* associate */
	add_thr(THREAD_PCB_CUR,
			(kthread_tcb_t*)kstk);

	/* prepare to schedule */
	disable_ctx_switch();
	runq_push((kthread_tcb_t*)kstk);
	enable_ctx_switch();

	ureg->eax = tid;
	return;
err:
	ureg->eax = ERROR;
	return;
}

void ktask_vanish(ureg_t* ureg)
{
	/* p3 */
	return;
}

/**
 * @brief set_status(int)
 */
void kset_status(ureg_t* ureg)
{
	THREAD_PCB_CUR->status = ureg->esi;
}
