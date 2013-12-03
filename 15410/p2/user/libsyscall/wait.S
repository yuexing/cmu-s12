#include <syscall_int.h>

.globl wait

wait:

/* save %esi */
push %esi

/* pass parameter */
mov 8(%esp), %esi

/* INT */
INT $WAIT_INT

/* restore %esi */
pop %esi

/* ret */
ret
