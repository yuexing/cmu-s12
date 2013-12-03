#include <syscall_int.h>

.globl get_ticks

get_ticks:

/* INT */
INT $GET_TICKS_INT

/* ret */
ret
