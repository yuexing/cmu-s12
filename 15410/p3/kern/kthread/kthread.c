/**
 * @file kthread.c
 * @brief implementation of kernel threads.
 *
 * A kernel thread is modeled as a kernel stack and
 * a thread control block. They two together take
 * a PAGE_SIZE.
 *
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#include <kernel.h>

static uint32_t next_tid = 1;
static block_t tid_lock;

static inline uint32_t get_next_tid();

/**
 * @brief Initialize the thread sub-module.
 */
int init_mod_kthread()
{
	return block_init(&tid_lock);
}

/**
 * @brief This function is called after fork
 * to create a new tcb much more like the
 * old one.
 *
 * @return tid
 */
uint32_t init_tcb(void *new_kstk, 
		ureg_t *old_ureg, void *old_esp)
{
	kthread_tcb_t *cur_tcb, *new_tcb;

	cur_tcb = THREAD_TCB_CUR;
	new_tcb = (kthread_tcb_t*)new_kstk;

	/* cp tcb and kstk */
	memmove(new_tcb, cur_tcb, PAGE_SIZE);
	/* find the new_ureg*/
	ureg_t *new_ureg = new_kstk + (
			(void*)old_ureg-(void*)cur_tcb);
	new_ureg->eax = 0;
	/* find the new_esp*/
	void *new_esp = new_kstk + (
			old_esp - (void*)cur_tcb);
	/* set up esp for switch_to */
	new_tcb->esp = new_esp;
	/* fill */
	return fill_tcb(new_tcb);
}

/**
 * @brief destroy a tcb.
 * This process of destruction is like the
 * destruction is P2, including switching
 * stack, free previous area and vanish.
 */
void destroy_tcb(kthread_tcb_t *tcb)
{
	terminate(tcb, tmp_dir, &tmp_dir_lock);
}

/**
 * @brief a mixture of fork and 
 * exec. Create a pcb, tcb, dir
 * and Load program on that dir.
 */
kthread_tcb_t *load(char* fname)
{
	// Create a dir
	page_dir_t *dir = 
		(page_dir_t*)new_page();
	if(!dir){
		goto dir_err;
	}

	// install kpdes
	install_kpdes(dir);

	// Create a kstk for thread
	void *kstk = new_page();
	if(!kstk){
		goto kstk_err;
	}

	// Cast to a thread 
	kthread_tcb_t *tcb
		=(kthread_tcb_t*)kstk;

	// Create a pcb
	process_t *pcb =(process_t*)
		malloc(sizeof(process_t));
	if(!pcb){
		goto pcb_err;
	}

	// Load program
	if((tcb->esp=(void*)
				load_program(fname, NULL, 
			0, dir, &(tcb->eip)))
			==(void*)ERROR){
		goto load_err;
	}

	fill_tcb(tcb);
	init_pcb(pcb,tcb,dir);

	sim_reg_process(dir, fname);

	return tcb;

load_err:
	free(pcb);
pcb_err:
	free_page(kstk);
kstk_err:
	free_page(dir);
dir_err:
	return NULL;
}

/**
 * @brief fill a tcb with default values.
 * called by init_tcb() during fork and 
 * load() during load.
 *
 * @return tid.
 */
uint32_t fill_tcb(kthread_tcb_t *tcb)
{
	tcb->tid = get_next_tid();
	/* canary */
	tcb->canary = CANARY;
	/* nodes */
	tcb->pcb_node.next=tcb->pcb_node.prev
		=NULL;
	tcb->schedule_node.next
		=tcb->schedule_node.prev=NULL;
	tcb->pcb_node.tcb=tcb->schedule_node.tcb
		= tcb;
	/* swexn */
	tcb->swexn.esp3 = NULL;
	tcb->swexn.eip = 0;
	tcb->swexn.last = -1;
	/* state */
	tcb->state = RUNNABLE;

	return tcb->tid;
}

/**
 * @brief copy swexn if there is a need.
 */
void cp_swexn(kthread_tcb_t *old, kthread_tcb_t* new)
{
	new->swexn = old->swexn;	
}

/**
 * @brief Get the next available tid.
 */
static inline uint32_t get_next_tid()
{
	uint32_t tid = -1;

	block_lock(&tid_lock);
	tid = next_tid++;
	block_unlock(&tid_lock);

	return tid;
}

/**
 * @brief Set up the stack for tcb's first
 * run on ctx_switch. 
 * The stack includes a pusha area, a ret addr,
 * and an iret area.
 * For the current kernel, this is only used
 * for 'idle_thr'. What is more, 'idle_thr'
 * should have never run because of 'init'.
 */
void prepare_ctx_switch(kthread_tcb_t *tcb)
{
	uint32_t *esp = KSTK(tcb);
	// set up iret area
	iret_reg_t iret_reg;
	update_iret(&iret_reg, tcb);
	esp = (void*)esp - sizeof(iret_reg_t);
	memmove((void*)esp, (void*)&iret_reg,
			sizeof(iret_reg_t));
	// set up a ret addr
	*(--esp) = (uint32_t)
		before_run;
	// set up a pusha area
	// will the ebp matter?
	esp -= 8;
	tcb->esp = esp;	
	return;
}

/**
 * @brief Prepare the iret regs, esp0,
 * cr3 to switch to user mode and run
 * tcb (HERE is INIT).
 */
void run(kthread_tcb_t *tcb)
{
	// iret regs
	iret_reg_t iret_reg;
	update_iret(&iret_reg, tcb);
	// set_esp0
	uint32_t esp0=
		(uint32_t)KSTK(tcb);
	set_esp0(esp0);
	// set_cr3
	set_cr3((uint32_t)tcb->pcb->dir);
	// switch to user
	switch_to_user(&iret_reg);
}
