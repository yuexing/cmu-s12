/**
 * @file kb_driver.h
 * @brief  defines functionality of a keyboard driver.
 * @author Yue Xing (yuexing)
 * @bugs Not Found
 */
#ifndef _KB_DRIVER_H
#define _KB_DRIVER_H

/**
 * @brief wrapper timer interrupt handler
 *
 * it saves registers %eax, %ebx, %ecx, %edx, %edi, %esi, %ebp
 * and calls the timer handler, and then restores the registers.
 *
 * Also, it tells PIC the recent interrupt the PIC delivered
 * has been processed and ends with IRET. 
 *
 * Sending the acknowledge to the PIC is to indicate that handling 
 * of the previous interrupt from keyboard is "done". And the PIC can 
 * send a new keyboard interrupt.
 *
 */
void asm_kb_wrapper();

#endif /* _KB_DRIVER_H */
