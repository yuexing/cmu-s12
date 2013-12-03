#include <syscall_int.h>

.globl remove_pages

remove_pages:

/* save %esi */
push %esi

/* pass parameter */
mov 8(%esp), %esi

/* INT */
INT $REMOVE_PAGES_INT

/* restore %esi */
pop %esi

/* ret */
ret
