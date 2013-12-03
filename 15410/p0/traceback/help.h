/** @file help.h
 *  @brief Function prototype for helper functions.
 *
 *  This file contains both functions to deal with errors either 
 *  from unix or application, and wapper functions for signal.
 *
 *  @author Yue Xing(yuexing)
 *  @bug No Known bugs
 */

#ifndef __help_h_
#define __help_h_

#include "string.h"
#include "stdlib.h"
#include "stdio.h"
#include "errno.h"
#include "signal.h"
#include "unistd.h"

typedef void handler_t(int); /* define the type of signal handler */

void unix_error(char *msg);
void app_error(char *msg);

handler_t *Signal(int signum, handler_t *handler);



#endif /* help.h */
