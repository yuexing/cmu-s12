#include <syscall_int.h>

.global get_esp

get_esp:
lea 4(%esp), %eax
ret

.global thr_fork

thr_fork:

// put the dedicated esp into 
// callee-save ebx
push %ebx
mov 8(%esp), %ebx

INT $THREAD_FORK_INT

// parent?
test %eax, %eax
jne .parent

// child!
mov %ebx, %esp
call after_thr_fork

.parent:
pop %ebx
ret

.global switch_stack

switch_stack:
// save tcb
mov 8(%esp), %ecx

// run on esp3
mov 4(%esp), %esp

// after_switch_stack(tcb)
push %ecx
call after_switch_stack

// never return

.global unlock_vanish

unlock_vanish:

pop %ecx

// mutex_unlock(&tcb->lock)
call mutex_unlock

// DO NOT use stack
INT $VANISH_INT


.global get_ebp

get_ebp:

mov %ebp, %eax
ret
