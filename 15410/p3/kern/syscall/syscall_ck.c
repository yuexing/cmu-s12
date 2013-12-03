/**
 * @file syscall_ck.c
 * @brief implementaion of ck for 
 * args passed into syscalls.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug.
 */

#include <kernel.h>

static int ck_page(void *addr, uint32_t w);

/**
 * @brief ck whethre a memory area starting 
 * with addr with length as len is 
 * accessible .
 */
int ck_addr(void *addr, uint32_t len, uint32_t w)
{
	void *start
		=(void*)PAGE_ROUND_DOWN(addr);
	void *end
		=(void*)PAGE_ROUND_DOWN(addr+len);
	if(!end){
		// overflow, definitely not right
		return ERROR;
	}
	do {
		if(ck_page(start, w)==ERROR){
			return ERROR;
		}
		UP_PAGE(start);
	} while (start <= end);

	return SUCCESS;
}

/**
 * @brief check the readablity of every pointer
 * until NULL, also check the string pointed
 * to.
 *
 * @return the length of arr on SUCCESS
 */
int ck_str_arr(char **arr)
{
	int i = 0;
	for(; arr[i]; i++){
		if(ck_addr(arr[i], WORD_SIZE, 0)
				== ERROR){
			return ERROR;
		}
		if(ck_str(arr[i])
				== ERROR){
			return ERROR;
		}
	}
	return i;
}

/**
 * @brief check the readablity of a string.
 */
int ck_str(char *str)
{
	int i = 0;
	for(; str[i]; i++){
		if(ck_addr((void*)str+i,1,0)
				== ERROR){
			return ERROR;
		}
	}
	return SUCCESS;
}

/**
 * @brief ck the availability for a
 * single page.
 */
static int ck_page(void *addr, uint32_t w)
{
	uint32_t pde_idx = ADR_PDE(addr);
	uint32_t pte_idx = ADR_PTE(addr);

	page_dir_t *dir 
		= THREAD_DIR_CUR;
	
	if(!IS_PRESENT(dir->pdes[pde_idx])
	|| !IS_USER(dir->pdes[pde_idx])
||(w&&!IS_WRITABLE(dir->pdes[pde_idx]))){
		return ERROR;
	}

	page_table_t *tbl 
= (page_table_t*)PAGE_ROUND_DOWN(dir->pdes[pde_idx]); 

	if(!IS_PRESENT(tbl->ptes[pte_idx])
	|| !IS_USER(tbl->ptes[pte_idx])
||(w&&!IS_WRITABLE(tbl->ptes[pte_idx]))){
		return ERROR;
	}
	return SUCCESS;
}
