/** @file help.c
 *  @brief Function implementation for helper functions defined in 
 *  help.h 
 *
 *  @author Yue Xing(yuexing)
 *  @bug No known bugs
 */

#include "help.h"

/**
 * @brief print error msg from application and errno description
 *
 * @param msg error msg from application
 */
void unix_error(char *msg) {
	fprintf(stderr, "%s: %s\n", msg, strerror(errno));
	exit(-1);
}

/**
 * @brief print error msg from application
 *
 * @param msg error msg from application
 */
void app_error(char *msg) {
	fprintf(stderr, "%s\n", msg);
	exit(-1);
}

/***************************************
 * Signal-handling functions
 * **************************************/

/**
 * @brief wrapper for the sigaction function
 *
 * @param signum specifies the signal and can be any valid signal 
 * except SIGKILL and SIGSTOP
 * @param handler a new value of signal handler
 *
 * @return the previous value of signal handler
 */
handler_t *Signal(int signum, handler_t *handler) {
	
	struct sigaction action, old_action;

	action.sa_handler = handler;
	sigemptyset(&action.sa_mask);/* block sigs of type being handled*/
	action.sa_flags = SA_RESTART;/* restart syscall if possible */

	if (sigaction(signum, &action, &old_action) < 0) {
		unix_error("Signal error");
	}

	return old_action.sa_handler;
}
