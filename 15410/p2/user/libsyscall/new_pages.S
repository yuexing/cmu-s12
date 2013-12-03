#include <syscall_int.h>

.globl new_pages

new_pages:

/* save %esi */
push %esi

/* construct a system call packet */
lea 8(%esp), %esi

/* INT */
INT $NEW_PAGES_INT

/* restore %esi */
pop %esi

/* ret */
ret
