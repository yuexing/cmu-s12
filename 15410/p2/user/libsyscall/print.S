#include <syscall_int.h>

.globl print

print:

/* save %esi */
push %esi

/* construct a system call packet */
lea 8(%esp), %esi

/* INT */
INT $PRINT_INT

/* restore %esi */
pop %esi

/* ret */
ret
