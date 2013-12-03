#include <syscall_int.h>

.globl halt

halt:

/* INT */
INT	$HALT_INT

/* ret */
ret
