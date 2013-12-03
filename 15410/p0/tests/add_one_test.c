/** @file add_one_test.c
 *  @brief a test program for the add_one functions
 *
 *  This program provides a brief demonstration of the usage of the
 *  add_one functions.
 *
 *  @author Michael Ashley-Rollman(mpa)
 */

#include <stdio.h>
#include "add_one.h"

int main()
{
  printf("add_one1(7) = %d\n", add_one1(7));

  printf("add_one2(7) = %d\n", add_one2(7));

  return 0;
}
