#include <syscall_int.h>

.globl swexn

swexn:

/* save %esi*/
push %esi

/* make system call package */
lea 8(%esp), %esi

/* INT */
INT $SWEXN_INT

/* restore %esi */
pop %esi

/* ret */
ret
