/** @file kb_driver.c 
 *
 *  @brief Implementation of the keyboard 
 *  driver. The keyboard driver is modeled
 *  as If there is a thread wait for input,
 *  then the char will be processed and 
 *  echoed. 
 *  If the thread waits for a char, then 
 *  once a char has been entered, the thread
 *  will be waken up.
 *  If the thread waits for a line, then
 *  once a '\n' has been entered, the thread
 *  will be waken up.
 *
 *  @author Yue Xing(yuexing)
 *  @bug Not Found.
 */

#include <kernel.h>

/* the keyboard buffer */
static char key_buf[MAX_LENGTH]; 

/* the position for this get */
static int key_buf_front = 0;

/* the position for next put */
static int key_buf_tail = 0; 

/* whether there is a thread waiting
 * for line */
static int is_wait = 0;
static int is_wait_line = 0;
static int is_wait_char = 0;

static kthread_tcb_t *wait_thr = NULL;

static void kb_handler();

static void push_char(char c);
static char pop_char();

/**
 * @brief install handler.
 */
void kb_init()
{
	idt_routine_install(KEY_IDT_ENTRY, kb_handler);	
}

char readchar()
{
	char c = pop_char();
	assert(c != ERROR);
	assert(key_buf_tail == key_buf_front);
	return c;
}

/**
 * @brief Copy the available chars to the 
 * buf as many as we can.
 */
int copy_line(int len, char *buf)
{
	int i = 0;
	for(; i<len && i<key_buf_tail; i++){
		buf[i] = key_buf[i];
	}
	/* Reset */
	key_buf_tail = key_buf_front;
	return i;
}

/**
 * @brief Declare wait for a line.
 * @prev only one thread can declare its 
 * wish.
 */
void wait_line()
{
	is_wait = 1;
	is_wait_line = 1;
	wait_thr = THREAD_TCB_CUR;
}

/**
 * @brief Declare wait for a char.
 * @prev only one thread can declare its 
 * wish.
 */
void wait_char()
{
	is_wait = 1;
	is_wait_char = 1;
	wait_thr = THREAD_TCB_CUR;
}

/**
 * @brief 
 */
static void kb_handler() 
{
	uint8_t raw = inb(KEYBOARD_PORT);
	outb(INT_CTL_PORT, INT_ACK_CURRENT);

	if(!is_wait){
		return;
	}
	
	kh_type key = process_scancode(raw);
	if(!(KH_HASDATA(key)&&KH_ISMAKE(key))){
		return;
	}

	char c = KH_GETCHAR(key);
	
	switch(c){
		case '\b':
			pop_char();
			putbyte(c);
			break;
		case '\n':
			push_char(c);
			putbyte(c);

			is_wait = 0;
			is_wait_char = 0;
			is_wait_line = 0;
			if(try_ctx_switch()){
				yield_to(wait_thr);
				enable_ctx_switch();
			} else {
				pending_thr = wait_thr;
			}
			break;
		default:
			push_char(c);
			putbyte(c);

			if(is_wait_char){
				is_wait = 0;
				is_wait_char = 0;
				if(try_ctx_switch()){
					yield_to(wait_thr);
					enable_ctx_switch();
				} else {
					pending_thr = wait_thr;
				}
			}
			break;
	}
}

/**
 * @brief insert a char into the buf.
 */
static void push_char(char c)
{
	if(key_buf_tail + 1 == MAX_LENGTH){
		/* full */
		return;
	}

	if(!isprint(c))
		return;

	key_buf[key_buf_tail++]=c;
}

/**
 * @brief pop a char from back of the buf.
 */
static char pop_char()
{
	if(key_buf_tail == key_buf_front){
		/* empty */
		return ERROR;
	} else {
		return key_buf[key_buf_tail--];
	}
}
