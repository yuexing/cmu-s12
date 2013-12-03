#include "traceback_internal.h"
#include <stdio.h>


/** @file traceback_globals.c
 *  @brief global "functions" variable for traceback
 *
 *  This contains the actual definition of the global traceback
 *  variable. It is filled with the information necessary for
 *  symtabgen.pl to later fill the table in.
 *
 *  @author Michael Ashley-Rollman (mpa)
 *  @bug No known bugs.
 */

const functsym_t functions[FUNCTS_MAX_NUM] = 
  {{(void *)sizeof(functsym_t), 
    {(unsigned char)(FUNCTS_MAX_NUM % 256), 
     (unsigned char)(FUNCTS_MAX_NUM / 256),
     (char)ARGS_MAX_NUM,
     (char)sizeof(argsym_t),
     (char)FUNCTS_MAX_NAME,
     (char)ARGS_MAX_NAME }}};

