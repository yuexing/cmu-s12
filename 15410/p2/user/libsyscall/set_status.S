#include <syscall_int.h>

.globl set_status

set_status:

/* save %esi */
push %esi

/* pass parameter */
mov 8(%esp), %esi

/* INT */
INT $SET_STATUS_INT

/* restore %esi */
pop %esi

/* ret */
ret
