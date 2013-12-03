#include <stdio.h>
#include "traceback.h"

void bar(int x, int y)
{
  int z;
  z = x + y;
  traceback(stdout);
}

void foo() {
  bar (5,17);
}

int main (int argc, char **argv)
{
  foo();
  return 0;
}
