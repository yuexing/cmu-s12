/** @file verify_test.c
 *
 * Test output format for the traceback function
 *
 * This test calls a few functions to test a bunch
 * of different outputs.
 *
 * @author Michael Ashley-Rollman(mpa)
 */

#include "traceback.h"

void f4(int i, float f)
{
  traceback(stdout);
}

void f3(char c, char *str)
{
  f4(5, 35.0);
}

void f2(void)
{
  f3('k', "test");
}

void f1(char ** array)
{
  f2();
}

int main()
{
  char *arg[] = {"foo", "bar", "baz", "bletch"};

  f1(arg);

  return 0;
}
