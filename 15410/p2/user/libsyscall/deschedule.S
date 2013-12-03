#include <syscall_int.h>

.globl deschedule

deschedule:

/* save %esi */
push %esi

/* pass parameter */
mov 8(%esp), %esi

/* INT */
INT $DESCHEDULE_INT

/* restore %esi */
pop %esi

/* ret */
ret
