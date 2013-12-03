#include <syscall_int.h>

.globl gettid

gettid:

/* INT */
INT $GETTID_INT

/* ret */
ret
