/* asm.S */
#include <interrupt_defines.h>

.global asm_timer_wrapper
asm_timer_wrapper:
	pusha
	call timer_handler /*call c handler */
	movb $INT_ACK_CURRENT, %al
	movw $INT_CTL_PORT, %dx
	out %al, %dx
	popa
	IRET

.global asm_kb_wrapper
asm_kb_wrapper:
	pusha
	call kb_handler /*call c handler */
	movb $INT_ACK_CURRENT, %al
	movw $INT_CTL_PORT, %dx
	out %al, %dx
	popa
	IRET
	
