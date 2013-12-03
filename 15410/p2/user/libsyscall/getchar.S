#include <syscall_int.h>

.globl getchar

getchar:

/* INT */
INT $GETCHAR_INT

/* ret */
ret
