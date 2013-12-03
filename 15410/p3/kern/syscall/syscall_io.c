/**
 * @file syscall_io.c
 * @brief implementation of io related 
 * syscalls. 
 * @author Yue Xing(yuexing)
 * @bug No Found Bugs
 */

#include <kernel.h>

static block_t input_lock;
static block_t output_lock;

int init_mod_syscall_io()
{
	int ret = SUCCESS;
	ret |= block_init(&input_lock);
	ret |= block_init(&output_lock);
	return ret;
}

void kgetchar(ureg_t* ureg)
{
	/* for p3 */
	debug_call("getchar!");
	ureg->eax = SUCCESS;
	return ;
}

/**
 * @brief int readline(int len, char *buf)
 */
void kreadline(ureg_t* ureg)
{
	/* extract args */
	void **esi = (void**)ureg->esi;
	int len = (int)esi[0];
	char *buf = (char*)esi[1];

	/* ck args */
	if(len < 0){
		ureg->eax = ERROR;
		return;
	}

	if(!len){
		ureg->eax = SUCCESS;
		return;
	}

	if(ck_addr(buf, len, 1) == ERROR){
		ureg->eax = ERROR;
		return;
	}
	
	/* readline part */
	
	/* only one can access */
	block_lock(&input_lock);

	/* switch */
	disable_ctx_switch();
	wait_line();
	/* NOT add to any queue */
	block_and_switch(NULL, NULL, NULL);
	enable_ctx_switch();
	ureg->eax = copy_line(len, buf);

	block_unlock(&input_lock);
}

/**
 * @brief int print(int len, char *buf)
 */
void kprint(ureg_t* ureg)
{
	/* extract args */
	void **esi = (void**)ureg->esi;
	int len = (int)esi[0];
	char *buf = (char*)esi[1];

	/* ck args */
	if(len < 0){
		ureg->eax = ERROR;
		return;
	}

	if(!len){
		ureg->eax = SUCCESS;
		return;
	}

	if(ck_addr(buf, len, 0) == ERROR){
		ureg->eax = ERROR;
		return;
	}

	/* putbytes */
	block_lock(&output_lock);
	putbytes(buf, len);
	block_unlock(&output_lock);
	ureg->eax = SUCCESS;
	return;
}

/**
 * @brief int set_term_color(int color)
 */ 
void kset_term_color(ureg_t* ureg)
{
	int color = (int)ureg->esi;

	ureg->eax = set_term_color(color);
	return;
}

/**
 * @brief int set_cursor_pos(int ,int)
 */
void kset_cursor_pos(ureg_t* ureg)
{
	int *esi = (int*)ureg->esi;
	int row = esi[0], col = esi[1];

	block_lock(&output_lock);
	ureg->eax = set_cursor(row, col);
	block_unlock(&output_lock);
	return;
}

/**
 * @brief int get_curor_pos(int*, int*)
 */
void kget_cursor_pos(ureg_t* ureg)
{
	int **esi = (int**)ureg->esi;
	int *row = esi[0], *col = esi[1];

	/* ckarg */
	if(ck_addr(row, WORD_SIZE, 1) 
			== ERROR){
		ureg->eax = ERROR;
		return;
	}

	if(ck_addr(col, WORD_SIZE, 1)
		 	== ERROR){
		ureg->eax = ERROR;
		return;
	}

	block_lock(&output_lock);
	get_cursor(row, col);
	block_unlock(&output_lock);
	ureg->eax = SUCCESS;
	return;
}
