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

#include <kernel.h>

#define TIMER_INTR_RATE		11931
#define TIMER_COMPLETE_RATE 10000

static uint32_t numTicks = 0;
static tickback_t *tickback = 0x0;

static void timer_handler(ureg_t *reg);

/**
 * @brief Initializes the timer by setting its mode and the number
 * of timer cycles between interrupts.
 */
void timer_init()
{
	idt_routine_install(TIMER_IDT_ENTRY, timer_handler);

	short cycles = TIMER_INTR_RATE;
	outb(TIMER_MODE_IO_PORT, TIMER_SQUARE_WAVE);
	outb(TIMER_PERIOD_IO_PORT, cycles);
	outb(TIMER_PERIOD_IO_PORT, cycles >> 8);
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
 * @param the param leave with no use now.
 * can be useful in context switch 
 * */
static void timer_handler(ureg_t *reg)
{
	numTicks++;
	outb(INT_CTL_PORT, INT_ACK_CURRENT);

	if (numTicks && numTicks % TIMER_COMPLETE_RATE == 0){
		// wait for PC timer now	
		// no need to bother user
	} else {
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


uint32_t tick()
{
	return numTicks;
}
