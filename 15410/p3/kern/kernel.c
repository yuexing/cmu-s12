/** 
 * @file kernel.c
 * @brief An initial kernel.c
 * @author Yue Xing(yuexing)
 * @bug No Found Bug.
 */

#include <kernel.h>

void *zfod_frame;
page_dir_t *tmp_dir;
block_t tmp_dir_lock;
/* idle will not be stored
 * in the scheduler's run queue. */
kthread_tcb_t *thr_idle;
/* Init process */
process_t *proc_init;
/* The IO pending thread */
kthread_tcb_t *pending_thr;

/** 
 * @brief Kernel entrypoint.
 * @return Does not return
 */
int kernel_main(mbinfo_t *mbinfo, int argc, char **argv, char **envp)
{
	clear_console();

	int ret = SUCCESS;

	/* initialize modules */
	ret |= init_mod_malloc();
	ret |= init_mod_frame();
	ret |= init_mod_vm();
	ret |= init_mod_proc();
	ret |= init_mod_kthread();
	ret |= init_mod_scheduler();
	ret |= init_mod_loader();
	ret |= init_mod_syscall_io();

	assert(ret == SUCCESS);
	
	/* global tmp_dir */
	tmp_dir = 
		(page_dir_t*)new_page();
	debug_call("tmp_dir:%p", tmp_dir);
	zfod_frame=new_page();

	assert(tmp_dir);
	install_kpdes(tmp_dir);
	block_init(&tmp_dir_lock);

	/* turn on paging */
	set_cr3((uint32_t)tmp_dir);
	enable_paging();

	/* load idle */
	kthread_tcb_t *tcb
		= load("idle");
	assert(tcb);
	/* prepare for idle */
	thr_idle = tcb;
	prepare_ctx_switch(tcb);

	/* load init */
	tcb = load("init");
	assert(tcb);
	/* set up proc_init*/
	proc_init = tcb->pcb;

	ret |= init_mod_handler();
	assert(ret == SUCCESS);
	run(tcb);

	while (1) {
		continue;
	}

	return 0;
}
