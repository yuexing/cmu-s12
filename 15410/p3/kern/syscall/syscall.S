#include <page.h>
#define AOFFSET (PAGE_SIZE-4)

.global khalt

khalt:
	cli
	call sim_halt
	hlt

.globl terminate

terminate:
mov 4(%esp), %ebx # ebx = tcb
mov 8(%esp), %edi # edi = tmp_dir
mov 12(%esp), %esi #esi = &tmp_dir_lock

push %esi
call block_lock

add $AOFFSET, %edi
mov %edi, %esp

push %ebx
call free_page

push %esi
call block_unlock_vanish

// set the return address to after_work
// create a saved_esp area
// set up arguments: ureg, esp
.macro begin_wrapper
/* ret [4] */
push $after_fork
/* a [4 * 8 = 32 ]*/
pusha
/* void *esp [4]*/
mov %esp, %eax
push %eax
// ureg_t *ureg
mov 44(%esp), %eax
push %eax
.endm

// pop out everything
.macro end_wrapper
// pop ureg
pop %eax
// pop esp
pop %eax
popa
// pop after_fork
pop %eax
.endm

.global after_fork

after_fork:
call check_pending
call enable_ctx_switch
ret

.globl kfork_wrapper

kfork_wrapper:
begin_wrapper
call kfork
end_wrapper
ret

.globl kfork_thread_wrapper

kfork_thread_wrapper:
begin_wrapper
call kthread_fork
end_wrapper
ret
