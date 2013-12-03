#include <syscall_int.h>

.globl set_term_color

set_term_color:

/* save %esi */
push %esi

/* pass parameter */
mov 8(%esp), %esi

/* INT */
INT $SET_TERM_COLOR_INT

/* restore %esi */
pop %esi

/* ret */
ret
