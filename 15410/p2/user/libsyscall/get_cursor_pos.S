#include <syscall_int.h>

.globl get_cursor_pos

get_cursor_pos:

/* save %esi */
push %esi

/* make system call packet */
lea 8(%esp), %esi

/* INT */
INT $GET_CURSOR_POS_INT

/* restore %esi */
pop %esi

/* ret */
ret

