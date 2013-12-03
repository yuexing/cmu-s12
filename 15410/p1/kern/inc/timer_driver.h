/**
 * @file timer_driver.h
 * @brief  defines functionality of a timer driver
 * @author Yue Xing (yuexing)
 * @bugs Not Found
 */
#ifndef _TIMER_DRIVER_H
#define _TIMER_DRIVER_H

void timer_init();

void install_timer_callback(void (*tickback_t)(unsigned int));

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
 * of the previous interrupt from timer is "done".  
 * */
void asm_timer_wrapper();

#endif /* _TIMER_DRIVER_H */
