/** @file alarming_test.c
 *
 *  Test code for the traceback function
 *
 *  This test calls traceback() from an alternate
 *  signal stack.
 *
 *  Don't try to understand the operation of sigaltstack()
 *  from the Linux man page since it is incoherent (at least
 *  through glibc 2.6).  This is better:
 *  http://www.opengroup.org/onlinepubs/000095399/functions/sigaltstack.html
 *
 *  @author Dave Eckhardt (de0u)
 */

#include "traceback.h"
#include <errno.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>

#include <signal.h>
#include <sys/mman.h>

#define MEGABYTE (1024*1024)

int work_done = 0;

void alarming(char *str, char *notstr) {

  traceback(stderr);

  work_done = 1;
}

void dingdong(int sig)
{
  char buf[512];

  snprintf(buf, sizeof (buf), "Signal %d hits!--More--", sig);
  alarming(buf, buf+MEGABYTE);
}

int main() {
  int stacksize;
  void *stackbase;
  stack_t st;
  struct sigaction sa;
  sigset_t all_signals;
  sigset_t no_signals;

  /* Ask for some memory (in theory, enough for four
   * "typical" signal stacks) at a place "convenient"
   * for the kernel.
   */
  stacksize = 4 * SIGSTKSZ;
  stackbase = mmap(NULL, stacksize, PROT_READ|PROT_WRITE|PROT_EXEC, MAP_ANONYMOUS|MAP_PRIVATE, -1, 0);

  if (stackbase == MAP_FAILED) {
    char buf[512];
    snprintf(buf, sizeof (buf), "mmap(NULL, %d, ...)", stacksize);
    perror(buf);
    exit(3);
  } else {
    printf("Alternate signal stack at 0x%x for %d bytes\n", (unsigned int) stackbase, stacksize);
  }

  /* Inform the kernel of our "alternate stack" on which
   * it should deliver certain signals (TBD).
   */
  st.ss_sp = stackbase;
  st.ss_size = stacksize;
  st.ss_flags = 0;

  if (sigaltstack(&st, NULL) < 0) {
    perror("sigaltstack()");
    exit(5);
  }

  /* Tell the kernel that we'd like SIGALRM to be delivered
   * on the alternate stack, with all other signals deferred.
   */
  sa.sa_handler = dingdong;
  sigfillset(&sa.sa_mask);
  sa.sa_flags = SA_ONSTACK;

  if (sigaction(SIGALRM, &sa, NULL) < 0) {
    perror("sigaction()");
    exit(7);
  }

  /* Arrange, in a robust way, to be twiddling our thumbs
   * when a SIGALRM is delivered.
   */
  sigfillset(&all_signals);
  sigemptyset(&no_signals);

  sigprocmask(SIG_BLOCK, &all_signals, NULL);
  alarm(2);
  sigsuspend(&no_signals);

  /* Ok, what happened? */

  if (work_done) {
    return 0;
  } else {
    fprintf(stderr, "Something went very wrong!\n");
    return 11;
  }
}
