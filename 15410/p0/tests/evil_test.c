/** @file evil_test.c
 *
 *  Test code for the traceback function
 *
 *  This test calls a function foo() with an unterminated string.
 *  foo() in turn calls traceback.
 *
 *  @author Michael Ashley-Rollman (mpa)
 */


#include "traceback.h"
#include <errno.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>

void foo(char *str) {
  traceback(stderr);
}

int main() {
  void *addr;
  char *str;
  int i;

  /* get the current brk point */
  addr = sbrk(0);

  /* create a new brk point that is higher than the current
     and also page aligned */
  addr = (void*)(((int)addr & 0xfffff000)+ 0x2000);
  
  /* set the new brk to addr */
  if (brk(addr) != 0) {
    fprintf(stderr, "brk() failed: %s\n", strerror(errno));
    return -1;
  }

  str = ((char *)addr) - 10;

  /* fill in the string up to the brk point */
  for(i = 0; i < 10; i++)
    str[i] = 'f';

  /* call foo and cross our fingers */
  foo(str);

  return 0;
}
