#include <syscall_int.h>

.globl misbehave

misbehave:

/* save %esi */
push %esi

/* pass parameter */
mov 8(%esp), %esi

/* INT */
INT $MISBEHAVE_INT

/* restore %esi */
pop %esi

/* ret */
ret
