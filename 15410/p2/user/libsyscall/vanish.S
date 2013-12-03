#include <syscall_int.h>

.globl vanish

vanish:

/* INT */
INT $VANISH_INT

/* ret */
ret
