/** @file kb_driver.c 
 *
 *  @brief Implementation of keyboard-driver library.
 *
 *  @author Yue Xing(yuexing)
 *  @bug Not Found.
 */

#include <asm.h>
#include <keyhelp.h>
#include "inc/defines.h"

static char key_buf[KEY_BUF_SIZE]; /* the keyboard buffer */
static int key_buf_front = 0;/* the position for this get */
static int key_buf_tail = 0; /* the position for next put */

/**
 * @brief The keyboard interrupt handler function. It read a byte
 * from the KEYBOARD_PORT and put the byte into the buffer. The
 * buffer is implemented as a queue. When the buffer is full, there
 * is not effect.
 *
 */
void kb_handler() 
{
	int next = (key_buf_tail + 1) % KEY_BUF_SIZE;

	if (next == key_buf_front ){
		/* the buf is full */
		return;
	}

	key_buf[key_buf_tail] = inb(KEYBOARD_PORT);

	/* update key_buf_tail */
	key_buf_tail = next;
}

/** 
 * @brief Returns the next character in the keyboard buffer
 *
 * This function does not block if there are no characters in 
 * the keyboard buffer
 *
 * @return The next character in the keyboard buffer, or -1 if
 * the keyboard buffer is currently empty
 **/
int readchar(void) 
{
	disable_interrupts();

	/* argumented character */
	int kh;

	for (; key_buf_front != key_buf_tail; 
		key_buf_front = (key_buf_front + 1) % KEY_BUF_SIZE) {

		kh = process_scancode(key_buf[key_buf_front]);

		if (KH_HASDATA(kh) && KH_ISMAKE(kh)) {
			/* if there exists a character */
			key_buf_front = 
				(key_buf_front + 1) % KEY_BUF_SIZE;

			enable_interrupts();
			return KH_GETCHAR(kh);
		}
	}

	enable_interrupts();
	return -1;
}

