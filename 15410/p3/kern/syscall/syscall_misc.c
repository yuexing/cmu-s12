/**
 * @file syscall_misc.c
 * @brief
 * @author Yue Xing(yuexing.h)
 * @bug No Found Bugs
 */

#include <kernel.h>

void kmisbehave(ureg_t* ureg)
{
	/* p3 */
	return;
}

/**
 * @brief int ls(int, char*)
 */
void kls(ureg_t* ureg)
{
	/* extract args */
	void **esi = (void**)ureg->esi;
	int size = (int)esi[0];
	char *buf = (char*)esi[1];

	/* ck arg */
	if(size < ls_size){
		goto err;
	}

	if(ck_addr(buf, size, 1)
		== ERROR){
		goto err;
	}
	
	/* memmove now */
	memmove(buf,ls_names,ls_size);

	ureg->eax = ls_n;
	return;
err:
	ureg->eax = ERROR;
	return;
}
