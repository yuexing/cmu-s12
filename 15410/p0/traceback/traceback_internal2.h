/** @file traceback_internal2.h
 *  @brief Function prototype and global variables for traceback 
 *  library
 *
 *  The content of this file should go into traceback_internal.h,
 *  only be included by the files that form the traceback library.
 *
 *  @author Yue Xing(yuexing)
 *  @bug No Known bugs
 */

#ifndef __traceback_internal2_h_
#define __traceback_internal2_h_

#include "string.h"
#include "stdlib.h"
#include "stdio.h"
#include "ctype.h"
#include "sys/types.h"
#include "fcntl.h"

/* type definitions */
#define TYPE_PTR	7
#define TYPE_FUNCT	8
#define TYPE_ARG	9
#define TYPE_STR	10

/* type info definitions */
#define STR_MAX_NUM  3 /* The maximum number of strings to print for a
						  string array argument */
#define STR_MAX_SIZE 25 /* The maximum number of charactors to print 
						   for a string argument */
#define BUF_SIZE	16 + FUNCTS_MAX_NAME\
	+ ARGS_MAX_NUM * (STR_MAX_SIZE + ARGS_MAX_NAME + 3) /* The buffer 
								size for the output of a function */

#define CHAR_SIZE	sizeof(char) /* size of a charactor */
#define INT_SIZE 	sizeof(int) /* size of an integer*/
#define FLOAT_SIZE	sizeof(float) /* size of a float */
#define DOUBLE_SIZE	sizeof(double) /* size of a double */
#define PTR_SIZE	sizeof(void *) /* size of a pointer */

/* Function prototype */
static void get_funct(void *ret, void *ebp, char *str);
static void print_function (const functsym_t *pfunct, void *ebp, 
		char *strfunct);
static void print_args(const argsym_t *args, void *ebp, char *strargs);
static void print_arg(const argsym_t *arg, void *ebp, char *strarg);
static void print_str(const char *pstr, char *str, int type);
static void print_char(char *pChar, char *str);
static void print_str_arr(char **pstrarr, char *str);
static int is_print_name(const char *str, int type, char *msg);
static int is_print_strvalue(const char *str, char *msg);
static int is_valid_unit(const void *addr, int type);
static int is_align(const void *addr, int type);
static int is_main(const functsym_t *funct);
static void tb_exit();
static void close_res();
static void sigalrm_handler(int sig);
static void sig_handler(int sig);

/* Global variables */
static char tmp_file_tpl[10] = ".tbXXXXXX"; /* The temp file to be 
									writed to detect invalid address */
static int tmp_fd; /* The file descriptor of temp file*/
static FILE *fptr;	/* Point to the given file stream */
static char format[BUF_SIZE]; /* The format string for a function */
static char str[BUF_SIZE]; /* The output string for a function */
static char *msg_out_bound = "<out of bound>"; /* The message printed out 
							with invalid address */
static char *msg_not_align = "<not aligned>"; /* The message printed out 
				the address of a type which should be aligned but not */
static char *msg_not_print = "<unprintable>"; /* The message printed out 
							with unprintable charactor or string */
#endif /* traceback_internal2.h */


