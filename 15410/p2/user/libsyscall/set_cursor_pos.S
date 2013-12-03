#include <syscall_int.h>

.globl set_cursor_pos

set_cursor_pos:

/* save %esi */
push %esi

/* make system call packet */
lea 8(%esp), %esi

/* INT */
INT $SET_CURSOR_POS_INT

/* restore %esi */
pop %esi

/* ret */
ret

