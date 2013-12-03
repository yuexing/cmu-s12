#include <syscall_int.h>

.globl fork

fork:

/* INT */
INT	$FORK_INT

/* ret */
ret
