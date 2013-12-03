#include <syscall_int.h>

.globl exec

exec:

/* save %esi */
push %esi

/* construct a system call packet */
lea 8(%esp), %esi

/* INT */
INT $EXEC_INT

/* restore %esi */
pop %esi

/* ret */
ret
