#include <stdio.h>
#include "traceback.h"

void bar(int x, int y)
{
  int z;
  z = x + y;
  traceback(stdout);
}

void foo(void *lookma) {
  bar (5,17);
}

int main (int argc, char **argv)
{
  foo( (void*) &argc ); // Avoid "is NULL 0x0 or (nil)?" issue
  return 0;
}
