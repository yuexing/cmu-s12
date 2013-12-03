#include <syscall_int.h>

.globl yield

yield:

/* save %esi */
push %esi

/* pass parameter */
mov 8(%esp), %esi

/* INT */
INT $YIELD_INT

/* restore %esi */
pop %esi

/* ret */
ret
