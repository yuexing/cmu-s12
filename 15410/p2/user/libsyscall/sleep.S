#include <syscall_int.h>

.globl sleep

sleep:

/* save %esi */
push %esi

/* pass parameter */
mov 8(%esp), %esi

/* INT */
INT $SLEEP_INT

/* restore %esi */
pop %esi

/* ret */
ret

