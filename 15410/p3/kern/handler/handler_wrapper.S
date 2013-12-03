# The interrupt handler wrapper should generate an ureg_t for furthure use.
# More details about ureg_t, please refer to spec/Uregs.h
# Such interrupts as 10, 11, 12, 13, 14, 17 have error_code.
# Such interrupts as 14 have cr2.

#include <seg.h>


.global handle_fork

handle_fork:

push $0		#dummy errorcode

pusha
push %gs
push %fs
push %es
push %ds

mov $SEGSEL_KERNEL_DS, %eax
mov %eax, %gs
mov %eax, %fs
mov %eax, %es
mov %eax, %ds

push $0      #dummy cr2
push 0x41    #set cause no.

mov %esp, %eax
push %eax
call kfork_wrapper #kfork_wrapper(ureg_t*)
pop %eax

add $8, %esp   #skip cr2 and cause no.
pop %ds
pop %es
pop %fs
pop %gs
popa
add $4, %esp  #skip error code
iret

.global handle_thread_fork

handle_thread_fork:

push $0		#dummy errorcode

pusha
push %gs
push %fs
push %es
push %ds

mov $SEGSEL_KERNEL_DS, %eax
mov %eax, %gs
mov %eax, %fs
mov %eax, %es
mov %eax, %ds

push $0      #dummy cr2
push 0x52    #set cause no.

mov %esp, %eax
push %eax
call kfork_thread_wrapper #kfork_thread_wrapper(ureg_t*)
pop %eax

add $8, %esp   #skip cr2 and cause no.
pop %ds
pop %es
pop %fs
pop %gs
popa
add $4, %esp  #skip error code
iret

.macro handle_no_errorcode no

.global handle_no_errorcode\no

handle_no_errorcode\no:

push $0		#dummy errorcode

pusha
push %gs
push %fs
push %es
push %ds

mov $SEGSEL_KERNEL_DS, %eax
mov %eax, %gs
mov %eax, %fs
mov %eax, %es
mov %eax, %ds

push $0      #dummy cr2
push $\no    #set cause no.

call handle_interrupt

add $8, %esp   #skip cr2 and cause no.
pop %ds
pop %es
pop %fs
pop %gs
popa
add $4, %esp  #skip error code
iret
.endm

/* IRQ and PIC, sending acknowledge has been done in handler */
handle_no_errorcode 0
handle_no_errorcode 1
handle_no_errorcode 2
handle_no_errorcode 3
handle_no_errorcode 4
handle_no_errorcode 5
handle_no_errorcode 6
handle_no_errorcode 7
handle_no_errorcode 9
handle_no_errorcode 15    
handle_no_errorcode 16
handle_no_errorcode 18
handle_no_errorcode 19
handle_no_errorcode 20
handle_no_errorcode 21
handle_no_errorcode 22
handle_no_errorcode 23
handle_no_errorcode 24
handle_no_errorcode 25
handle_no_errorcode 26
handle_no_errorcode 27
handle_no_errorcode 28
handle_no_errorcode 29
handle_no_errorcode 30
handle_no_errorcode 31
handle_no_errorcode 32
handle_no_errorcode 33
handle_no_errorcode 38

/* system call */
/* fork 0x41 */
handle_no_errorcode 66
handle_no_errorcode 67
handle_no_errorcode 68
handle_no_errorcode 69
handle_no_errorcode 70
handle_no_errorcode 71
handle_no_errorcode 72
handle_no_errorcode 73
handle_no_errorcode 74
handle_no_errorcode 75
handle_no_errorcode 76
handle_no_errorcode 77
handle_no_errorcode 78
handle_no_errorcode 79
handle_no_errorcode 80
handle_no_errorcode 81
/* thread_fork 0x52 */
handle_no_errorcode 83
handle_no_errorcode 84
handle_no_errorcode 85
handle_no_errorcode 86
handle_no_errorcode 87
handle_no_errorcode 89
handle_no_errorcode 96
handle_no_errorcode 116

.macro handle_errorcode no

.globl handle_errorcode\no

handle_errorcode\no:

pusha 
push %gs
push %fs
push %es
push %ds

mov $SEGSEL_KERNEL_DS, %eax
mov %eax, %gs
mov %eax, %fs
mov %eax, %es
mov %eax, %ds

push $0			# dummy cr2
push $\no		# this is the cause

call handle_interrupt

add $8, %esp 	# skip cr2 and cause
pop %ds
pop %es
pop %fs
pop %gs
popa
add $4, %esp  #skip error code
iret

.endm

//Delcare handlers for the INTs that has no errorcodes
/* 8 has been installed */
handle_errorcode 10
handle_errorcode 11
handle_errorcode 12
handle_errorcode 13
handle_errorcode 17

//INT 14
.globl handle_errorcode14

handle_errorcode14:

pusha 
push %gs
push %fs
push %es
push %ds 

mov $SEGSEL_KERNEL_DS, %eax
mov %eax, %gs
mov %eax, %fs
mov %eax, %es
mov %eax, %ds

mov %cr2, %eax
push %eax						# cr2 can not be pushed
push $14						# this is the cause

call handle_interrupt

add $8, %esp # skip cr2 and cause
pop %ds
pop %es
pop %fs
pop %gs
popa
add $4, %esp  #skip error code
iret

// mode switch function
// it takes the address of a iret_reg
.global switch_to_user

switch_to_user:
// set segment
mov $SEGSEL_USER_DS, %eax
mov %eax, %gs
mov %eax, %fs
mov %eax, %es
mov %eax, %ds
// set up esp for iret
mov 4(%esp), %eax
mov %eax, %esp
// to user mode
iret

.global before_run

before_run:
call check_pending
call enable_ctx_switch
// set segment
mov $SEGSEL_USER_DS, %eax
mov %eax, %gs
mov %eax, %fs
mov %eax, %es
mov %eax, %ds
iret

.global get_cs
get_cs:
mov %cs, %eax
ret
