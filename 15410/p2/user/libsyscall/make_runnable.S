#include <syscall_int.h>

.globl make_runnable

make_runnable:

/* save %esi */
push %esi

/* pass parameter */
mov 8(%esp), %esi

/* INT */
INT $MAKE_RUNNABLE_INT

/* restore %esi */
pop %esi

/* ret */
ret
