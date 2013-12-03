#include <syscall_int.h>

.globl readline

readline:

/* save %esi */
push %esi

/* make system call packet */
lea 8(%esp), %esi

/* INT */
INT $READLINE_INT

/* restore %esi */
pop %esi

/* ret */
ret

