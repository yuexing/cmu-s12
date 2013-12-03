#include <syscall_int.h>

.globl task_vanish

task_vanish:

/* save %esi */
push %esi

/* put parameter */
mov 8(%esp), %esi

/* INT */
INT $TASK_VANISH_INT

/* pop %esi */
pop %esi

/* ret */
ret
