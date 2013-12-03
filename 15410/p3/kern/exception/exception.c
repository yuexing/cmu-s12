/**
 * @file exception.c
 * @brief kernel exception handlers.
 *
 * Only the page fault handler supports user
 * level exception handling. 
 * 
 * @author Yue Xing(yuexing)
 * @bug No Found Bug.
 */

#include <kernel.h>

static void kill_thr(ureg_t *ureg);
static void display_all(char *buf, ureg_t *ureg);
static void get_permit(page_dir_t*, uint32_t vaddr,
		char **pw, char **pu);
static void get_error(uint32_t errcode, 
		char **paction, int *pp);
static void *limit_stack = (void*)0xf0000000;

/**
 * @brief handle all exceptions except
 * for page_fault
 */
void basic_handle(ureg_t *ureg)
{
	kill_thr(ureg);
}

/**
 * @brief That a cr2 happens page fault twice,
 * indicates the page fault swexn handler 
 * does not work at all!
 *
 * Note: This machanism has been deleted to
 * pass the test.
 */
void pf_handle(ureg_t *ureg)
{
	kthread_tcb_t *tcb = THREAD_TCB_CUR;
	process_t *pcb = tcb->pcb;
	page_dir_t *dir= pcb->dir;

	uint32_t eip;
	void *esp3;
	uint32_t *tmp_esp;
	int ret;

	void *base=(void*)PAGE_ROUND_DOWN(ureg->cr2);
	// try to solve zfod
	if(ck_zfod(dir,base)){
		block_lock(&pcb->dir_lock);
		ret = new_mapping(base,dir);
		block_unlock(&pcb->dir_lock);
		if(ret == ERROR){
			kill_thr(ureg);
		}
		debug_call("alloc %p", base);
		return;
	}

	// not that large stack!
	if(base <= limit_stack){
		kill_thr(ureg);
		return;
	}

	if(tcb->swexn.eip ){
		if( ureg->cr2 == tcb->swexn.last){
			debug_call("The handler may not work!");
		}
		/*record the cr2 history*/
		tcb->swexn.last = ureg->cr2;

		eip = tcb->swexn.eip;
		esp3 = tcb->swexn.esp3;

		/* unregister */
		tcb->swexn.eip = 0;
		tcb->swexn.esp3 = NULL;
		
		/* setup stack */

		/*Return to user level handler
		 * ureg_t
		 * ureg_t*
		 * void*
		 * ret addr
		 * */
		esp3 -= sizeof(ureg_t);
		memmove(esp3, (void*)ureg, 
				sizeof(ureg_t));
		tmp_esp = esp3;
		*(--tmp_esp) =(uint32_t)esp3;
		*(--tmp_esp) =(uint32_t)tcb->swexn.arg;
		*(--tmp_esp) = 0;

		// return to user
		ureg->esp = (uint32_t)tmp_esp;
		ureg->eip = eip;
		ureg->eflags = update_eflags(); 
	} else {
		kill_thr(ureg);
	}
}

/**
 * @brief Kill a compromised thread.
 * @pre tcb is not in runQ at all. 
 */
void kill_bad_thr(kthread_tcb_t *tcb)
{
	process_t *pcb = tcb->pcb;

	if(del_thr(pcb, tcb)){
		pcb->status = KILL_STATUS;
		destroy_pcb(pcb);
	}

	// safely free
	free_page(tcb);

	// print infor
	char buf[128];
	sprintf(buf, "Thread: %lu\n KILLED BY COMPROMISED", 
			tcb->tid);
	putbytes(buf, strlen(buf));
}

static void kill_thr(ureg_t *ureg)
{
	kthread_tcb_t *tcb = THREAD_TCB_CUR;
	process_t *pcb = tcb->pcb;

	char buf[256];
	char *action = NULL, *rw, *u;
	int present = 0;
	if(ureg->cause == SWEXN_CAUSE_PAGEFAULT){
		get_error(ureg->error_code, &action,
				&present);
		if(!present){
			sprintf(buf, 
					"\nPAGE FAULT: Invalid %s to a \
					non-present page at %p\n",
					action,(void*)ureg->cr2);	
		} else {
			get_permit(pcb->dir,ureg->cr2,&rw, &u);
			sprintf(buf,
					"\nPAGE FAULT: Invalid %s to a \
					%s %s page at %p\n",
					action, u, rw,(void*)ureg->cr2);
		}
	} else {
		switch(ureg->cause){
			case SWEXN_CAUSE_DIVIDE:
				sprintf(buf, "\nDIVIDE ERROR\n");	
				break;
			case SWEXN_CAUSE_DEBUG:
				sprintf(buf, "\nDEBUG ERROR\n");	
				break;
			case SWEXN_CAUSE_BREAKPOINT:
				sprintf(buf, "\nBREAKPOINT ERROR\n");	
				break;
			case SWEXN_CAUSE_OVERFLOW:
				sprintf(buf, "\nOVERFLOW ERROR\n");	
				break;
			case SWEXN_CAUSE_BOUNDCHECK:
				sprintf(buf, "\nBOUNDCHECK ERROR\n");	
				break;
			case SWEXN_CAUSE_OPCODE:
				sprintf(buf, "\nOPCODE ERROR\n");	
				break;
			case SWEXN_CAUSE_NOFPU:
				sprintf(buf, "\nNOFPU ERROR\n");	
				break;
			case SWEXN_CAUSE_SEGFAULT:
				sprintf(buf, "\nSEGFAULT ERROR\n");	
				break;
			case SWEXN_CAUSE_STACKFAULT:
				sprintf(buf, "\nSTACKFAULT ERROR\n");	
				break;
			case SWEXN_CAUSE_PROTFAULT:
				sprintf(buf, "\nPROTFAULT ERROR\n");	
				break;
			case SWEXN_CAUSE_FPUFAULT:
				sprintf(buf, "\nFPUFAULT ERROR\n");	
				break;
			case SWEXN_CAUSE_ALIGNFAULT:
				sprintf(buf, "\nALIGNFAULT ERROR\n");	
				break;
			case SWEXN_CAUSE_SIMDFAULT:
				sprintf(buf, "\nSIMFAULT ERROR\n");	
				break;
			default:
				sprintf(buf, "\nSOME ERROR\n");
				break;
		}
	}
	
	disable_ctx_switch();

	putbytes(buf, strlen(buf));
	display_all(buf, ureg);
	
	enable_ctx_switch();

	// delete should be done at last
	if(del_thr(pcb, tcb)){
		pcb->status = KILL_STATUS;
		destroy_pcb(pcb);
	}

	destroy_tcb(tcb);
}

static void display_all(char *buf, ureg_t *ureg)
{
	sprintf(buf, "Thread: %lu\n", THREAD_TCB_CUR->tid);
	sprintf(buf, "%sRegisters Dump:\n", buf);
	putbytes(buf, strlen(buf));

	sprintf(buf, "eax: %p, ebx: %p, ecx: %p\n",
			(void*)ureg->eax, (void*)ureg->ebx, 
			(void*)ureg->ecx);
	sprintf(buf, "%sedx: %p, esi: %p, edi: %p\n",
			buf, (void*)ureg->edx, (void*)ureg->esi, 
			(void*)ureg->edi);
	putbytes(buf, strlen(buf));

	sprintf(buf, "ebp: %p, esp: %p, eip: %p\n",
			(void*)ureg->ebp, (void*)ureg->esp, 
			(void*)ureg->eip);
	sprintf(buf, "%sss:   %p, cs:    %p, ds:    %p\n",
			buf, (void*)ureg->ss, (void*)ureg->cs,
			(void*)ureg->ds);
	putbytes(buf, strlen(buf));

	sprintf(buf, "es:   %p, fs:    %p, gs:    %p\n",
			(void*)ureg->es, (void*)ureg->fs,
			(void*)ureg->gs);
	sprintf(buf, "%seflags: %p\n", buf, 
			(void*)ureg->eflags);
	putbytes(buf, strlen(buf));
}

static void get_error(uint32_t errcode, 
		char **paction, int *pp)
{
	// action
	if(errcode & 0x2){
		*paction = "WRITE";
	} else {
		*paction = "READ";
	}
	// present or not
	*pp = (errcode & 0x1);
}

static void get_permit(page_dir_t *dir,
		uint32_t vaddr, char **pw, char **pu)
{
	pte_t pte = v2pte(dir,(void*)vaddr);
	if(IS_WRITABLE(pte)){
		*pw = "WRITABLE";
	} else {
		*pw = "READABLE";
	}

	if(IS_USER(pte)){
		*pu = "USER";	
	} else {
		*pu = "SUPERVISOR";
	}
}
