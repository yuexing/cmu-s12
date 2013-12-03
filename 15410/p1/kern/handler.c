/** 
 * @file handler.c
 * @brief The handler controller which takes care
 * of handler installation.
 * @author Yue Xing (yuexing)
 * @bugs Not Found
 */

#include <asm.h>
#include <x86/seg.h>                /* install_user_segs() */
#include <ctype.h>
#include <string.h>
#include <timer_defines.h>
#include <keyhelp.h>
#include "inc/defines.h"
#include "inc/timer_driver.h"
#include "inc/kb_driver.h"

#define TRAP_GATE_LOWER	0x8f00
#define ENTRY_SIZE	8

typedef void handler_t(void);

/**
 * @brief according to handout, we use trap gate for the interrupts in 
 * this projects. the difference btw an interrupt gate and a trap gate is 
 * that when the processor accesses an interrupt handler through an 
 * interrupt gate, it clears a flag in the processor's registers to defer 
 * all further interrupts until the current handler returns. Note:
 * <UL>
 * <LI>a single interrupt source will not signal a new interrupt to the 
 * processoruntil the processor has indicated the handling of the previous 
 * interrupt from that source is 'done'. </LI>
 * <LI>It should allow that one interrupt handler to be interrupted by a 
 * different interrupt handler, as long as they do not share data struct
 * </LI>
 * <LI>An entry of IDT is 64-bit long with two consecutive 32-bit words, and
 * the first represents the least significant 32 bits of gate.</LI>
 * </UL>
 */
typedef struct {
	unsigned short offset;
	unsigned short segsel;
	unsigned short lower;
	unsigned short offset2;
}trap_gate_t;

/**
 * @brief Installs a handler at the offset in the IDT.
 *
 * @param phandler the handler to install
 * @param offset the position in the IDT to install the handler
 */
void install_handler(handler_t *phandler, int offset)
{
	trap_gate_t e_idt;
	e_idt.offset = (unsigned short)((unsigned long)phandler);
	e_idt.lower = TRAP_GATE_LOWER;
	e_idt.segsel = SEGSEL_KERNEL_CS;
	e_idt.offset2 = (unsigned short)(((unsigned long)phandler)
			>> HIGHER_INT_SHIFT);

	/* install to idt */
	void *idt = idt_base();
	void *idt_offset = (void *)((char *)idt + ENTRY_SIZE * offset);
	memcpy(idt_offset, (void *)&e_idt, sizeof(trap_gate_t));
}

/** 
 * @brief The driver-library initialization function
 *
 * Installs the timer and keyboard interrupt handler, and enable interrupts.
 *
 * @param tickback Pointer to clock-tick callback function
 *   
 * @return A negative error code on error, or 0 on success
 **/
int handler_install(void (*tickback)(unsigned int)) 
{
	timer_init();
	install_timer_callback(tickback);
	install_handler(asm_timer_wrapper, TIMER_IDT_ENTRY);
	install_handler(asm_kb_wrapper, KEY_IDT_ENTRY);

	return 0;
}
