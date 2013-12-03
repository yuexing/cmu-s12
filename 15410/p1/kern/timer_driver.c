/** @file timer_driver.c 
 *
 *  @brief Implementation of timer-driver library.
 *
 *  As the internal rate of the PC timer is 1193182HZ, 
 *  and the driver configures the timer to generate interrupts 
 *  every 11931 cycles, which is treated as 10ms. 11931 cycles 
 *  actually is 9.999 ms, which is 0.001ms less than 10ms. And
 *  this 0.001ms implies the driver is 0.001ms faster than the
 *  PC time.
 *
 *  Thus, the driver obey the following rules:
 *  <OL>
 *  <LI>Every 10000, number of ticks should not change. The 
 *  reason is that the 0.001 ms has made another 10ms.
 *  In this case, do not call the callback function.</LI>
 *  <LI>The variable which stores the number of ticks
 *  will suffer overflow. The variable will be reset 
 *  to avoid this.</LI>
 *  </OL>
 *
 *  The application just need treating every tick as an another 
 *  10ms.
 *
 *  @author Yue Xing(yuexing)
 *  @bug Not Found.
 */

#include <x86/asm.h>
#include <limits.h>
#include <simics.h>                 /* lprintf() */
#include <string.h>
#include <timer_defines.h>
#include <interrupt_defines.h>
#include "inc/defines.h"

typedef void tickback_t(unsigned int); /* define type of 
									callback function*/

static int numTicks = 0;
static tickback_t *tickback = 0x0;
/**
 * @brief Initializes the timer by setting its mode and the number
 * of timer cycles between interrupts.
 */
void timer_init()
{
	outb(TIMER_MODE_IO_PORT, TIMER_SQUARE_WAVE);

	short cycles = TIMER_INTR_RATE;

	/* lower */
	outb(TIMER_PERIOD_IO_PORT, cycles);

	/* higher */
	outb(TIMER_PERIOD_IO_PORT, cycles >> HIGHER_SHORT_SHIFT);
}

/**
 * @brief Timer interrupt handler. It calls the regiested 
 * callback function with numTicks if the callback function
 * exists. 
 *
 * If the numTicks is multiple of 10000, there is no 
 * need to bother the user, since the driver 0.001ms faster
 * every tick.
 *
 * */
void timer_handler()
{
	if (numTicks && numTicks % TIMER_COMPLETE_RATE == 0){
		// wait for PC timer now	
		// no need to bother user
	} else {
		++numTicks;
		if(tickback) {
			tickback(numTicks);
		}
	}
}

/**
 * @brief Installs an application-provided clock-tick callback
 * function each time the timer interrupt fires. This callback
 * function will be passed an unsigned integer numTicks 
 * containing the total number of timer interrupts the handler
 * has caught since the application began running.
 * The callback function should run quickly which
 * implies it should return long before the next timer 
 * interrupt will arrive.
 */
void install_timer_callback(tickback_t *tback)
{
	tickback = tback;
}
