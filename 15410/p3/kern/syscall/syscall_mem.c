/**
 * @file syscall_mem.c
 * @brief implementation of memory-related
 * syscalls
 * @author Yue Xing(yuexing)
 * @bug No Found Bugs
 */

#include <kernel.h>

/**
 * @brief int new_pages(void*, int)
 */
void knew_pages(ureg_t* ureg)
{
	void **esi = (void**)ureg->esi;
	void *base = esi[0];
	int len = (int)esi[1];
	
	/* ck args*/
	if(base!=(void*)PAGE_ROUND_DOWN(base)){
		goto err;
	}

	if(len <= 0){
		goto err;
	}

	if(len!=PAGE_ROUND_DOWN(len)){
		goto err;
	}

	if(!(base+(len - 1))){
		// overflow
		goto err;
	}

	process_t *pcb = THREAD_PCB_CUR;
	page_dir_t *dir = pcb->dir;
	
	block_lock(&pcb->dir_lock);
	ureg->eax = new_nmapping(base,len,dir);
	block_unlock(&pcb->dir_lock);
	
	return;
err:
	assert(0);
	ureg->eax = ERROR;
	return;
}

/**
 * @brief int remove_pages(void*)
 */
void kremove_pages(ureg_t* ureg)
{
	void *base = (void*)ureg->esi;

	/* ck args*/
	if(base!=(void*)PAGE_ROUND_DOWN(base)){
		ureg->eax = ERROR;
		return;
	}

	process_t *pcb = THREAD_PCB_CUR;
	page_dir_t *dir = pcb->dir;

	block_lock(&pcb->dir_lock);
	ureg->eax = free_nmapping(base,dir);
	block_unlock(&pcb->dir_lock);

	return;
}
