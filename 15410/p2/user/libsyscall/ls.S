#include <syscall_int.h>

.globl ls

ls:

/* save %esi*/
push %esi

/* make system call package */
lea 8(%esp), %esi

/* INT */
INT $LS_INT

/* restore %esi */
pop %esi

/* ret */
ret
