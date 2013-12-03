/** @file traceback.c
 *  @brief The traceback library
 *  
 *  <B>Note</B> In this document, validity means accessibility, which
 *  implies an address is accessible to the program, in other words, 
 *  each byte of the multi-byte unit the address points to is accessible.

 *  <B>Intro</B> This file contains functions for the traceback library. 
 *  Once the library is called, the library will print a stack 
 *  trace of the caller program to the given file stream. 
 *
 *  This traceback library is to print as much information as 
 *  possible until one of the following <a name="end">end points</a>:
 *  <ul>
 *  <li>it reaches function main;</li> 
 *  <li>it reaches an invalid ebp or that the pointer above the ebp which 
 *  should point to the return address of the caller is invalid;</li> 
 *  <li>it reaches some point not conforming the calling convention.</li>
 *  </ul>
 *
 *  Thus, the traceback library can be used to debug a program.
 *
 *  <B>Implement</B> The library supposes:
 *  <ul>
 *  <li>the functions (functsym_t array) and args (argsym_t) are within 
 *  predefined length or end with zero-length name;</li>
 *  <li>the value of any property of functsym_t and argsym_t is valid;</li>
 *  <li>it will be used by Linux IA32 program (due to the <a href="#align">
 *  alignment</a>)</li>
 *  <li>the value of an string array argument should end with a NULL, or the
 *  output may be not correct.</li>
 *  <li>support multiple processes running currently, not multi-thread 
 *  program</li>
 *  </ul>
 *  The library just lists UNKNOWN types and functions without processing 
 *  any more. 
 *
 *  <B>Trace back</B> The library traces back a stack according to the 
 *  calling convention. The convention is that the return address
 *  pointed to the next instruction of the caller is below the 
 *  arguments and above the old ebp pointed by callee's ebp. The 
 *  procedure follows: 
 *  <ol>
 *  <li>traceback gets the most recent caller's ebp (ebp1) and return 
 *  address (ret1) by using fp, the parameter is passed to traceback;</li> 
 *  <li>get_funct gets the second most recent caller's ebp and return 
 *  address by using ebp1;</li>
 *  <li>get_funct recursively applies step 2 util it reaches one of the 
 *  above <a href="end">end point</a>.</li>
 *  </ol>
 *  
 *  <B>Identify function</B> Once given a return address, get_funct goes 
 *  through functions which is an array of functsym_t sorted by 
 *  start address to identify the corresponding function. As it is hard
 *  to determine the end of the last function and the last function is
 *  usually not important enough to traceback, the design treats the 
 *  return address higher than the start address of last item in funcions
 *  as an unknown function.
 *
 *  <B>Print Format</B> Once given an identified function and the corresponding 
 *  ebp,it is not hard to print the function as the format 
 *  <code>
 *  Function name(type name=value[, type name=value])[, in] 
 *  </code>
 *  Functions print_function(), print_args(), print_arg(), print_str(), 
 *  print_str_arr() and print_char() are to deal with format issue. 
 *
 *  <B>Error handling</B> Once there is an error, traceback library will 
 *  flush the backtrace remained in the memory, close resources and deal 
 *  with the error using error handling functions 
 *  from help.h.
 *
 *  <B>User Interface</B> Print information like stacktrace, outbound, 
 *  unaligned, unprintable and infinite loop to the given file stream. 
 *  Print unix error like file open/close/delete/write error, application 
 *  error like bad file stream and being interrupted to stderr.
 *
 *  <B>Risks</B> Due to overflow, weird-version assembly code (without 
 *  updating ebp) and so on, bugs like invalid pointers, alignment, sprintf 
 *  overflow and infinite loops will arise.
 *  
 *  <B>Invalid pointers</B> OS allocates memory in VM by creating map of 
 *  integral mutiples of pagesize, however, there is no existing system 
 *  call to check the protect of a page. Thus, the function is_valid_unit() 
 *  hacks existing system call write() to check whether an address is 
 *  accessable to the process since write() is much more elegant than 
 *  other calls like mmap(), msync(), mprotect(). When writing 1 byte 
 *  from a buffer to a regular file, the write() will return a failure 
 *  status with an errno (EFALT) when the buffer is outside accessible 
 *  space. 
 *
 *  Once the address of any byte within a multi-byte unit is invalid
 *  , print the unit's address instead of its value. Here, the unit can
 *  be char type, int type, float type, double type, char* type, char** type,
 *  void* type.
 *
 *  For the <a name="charstar">char* type</a>(a string), if there is a 
 *  charactor within the predefined length or '\\0' is invalid, print 
 *  the value of char* instead of the string it points to.
 *  
 *  The char** type(a string array) needs two steps:
 *	<ol>
 *	<li>Its value(the pointer to a char*) should be valid and aligned, otherwise,
 *	print its value when it is the first one in the string array, otherwise treat it
 *	as the end of the string array;</li>
 *  <li>follow the <a href="#charstar">char* type</a>;</li>
 *  </ol>
 *
 *  <B><a name="align">Alignment</a></B> The library follows the alignment 
 *  convention of Linux IA32 and the alignment is epitomised by is_align().
 *
 *  <B>Sprintf overflow</B> Only print those printable (call isprint with 
 *  the char and return 1) and endable (contain '\\0') strings, or strings 
 *  printable within predefined length; otherwise, print the address of the 
 *  string instead. Also, use snprintf instead of sprintf.
 *
 *  <B>Infinite loops</B> As it is hard to detect an infinite loop, the current 
 *  solution is to schedule a 10 seconds alarm, whose signal handler will 
 *  exit the process when the alarm is due before the process terminates. 
 *  Meanwhile, the handler will print traces remained in str and close 
 *  resource. 
 *
 *  <B>Leakage</B> Most significant leakage is a temp file used by write().
 *  To solve this problem, the solution is to install signal handlers
 *  to SIGINT, SIGTERM, SIGABRT, SIGQUIT to close fd and delte file.
 *
 *  @author Yue Xing (yuexing)
 *  @bug
 *  <ol>
 *  <li>The way to deal with infinite loop is not perfect, for some
 *  program which takes more than 10 seconds to run, this will
 *  terminate abruptly;</li>
 *  </ol>
 */

#include "traceback_internal.h"
#include "traceback_internal2.h"
#include "help.h"

/**
 * @brief Prints stack trace to filestream fp.
 *
 * This function installs signal handlers, schedules an
 * alarm used to stop infinit loop, initializes global 
 * variables, and starts tracing back the stack. After
 * that closes all the resources.
 *
 * @param fp The file stream to which the stack trace should
 * be printed.
 */
void traceback(FILE *fp)
{

	Signal(SIGALRM, &sigalrm_handler);
	Signal(SIGINT, &sig_handler);
	Signal(SIGQUIT, &sig_handler);
	Signal(SIGABRT, &sig_handler);
	Signal(SIGTERM, &sig_handler);

	/* schedule an alarm to stop infinit loop */
	alarm(10);

	/* initialize file stream pointer */ 
	fptr = fp;

	/* open temp file */
	tmp_fd = mkstemp(tmp_file_tpl);
	if (tmp_fd == -1) {
		unix_error("open temp file.");
	}

	/* empty the str for print */
	*str = '\0'; 

	/* start tracing back the stack*/
	void **fpp = (void **)&fp;
	void *ret = *(fpp - 1);
	void *ebp = *(fpp - 2);

	get_funct(ret, ebp, str);

	/* close resource */
	close_res();

	/* Do I need to exit with 0 ? */
}

/**
 * @brief Closes resource in case of leakage
 */
static void close_res() 
{
	/* close fd and delete file */
	if(close(tmp_fd)) {
		if (errno != EINTR) {
			unix_error("close temp file error.");
		}
	}
	if(unlink(tmp_file_tpl)) {
		unix_error("delete temp file error.");
	}
}

/**
 * @brief Prints the backtrace remained in str and 
 * closes the resources.
 *
 * Call this function when the program terminates
 * abnormally.
 */
static void tb_exit()
{
	close_res();

	if (fptr) {
		fprintf(fptr, "%s\n", str);
	} else {
		app_error("invalid file stream fp.");
	}

	exit(-1);
}

/**
 * @brief Starts tracing back the stack and printing recursively
 * until it reaches a <a href="#end">point</a>.
 * 
 * Go through functions until:
 * <ul>
 * <li>maximum length;</li>
 * <li>item with zero-length name</li> 
 * <li>
 * <ol>
 * <li>ret < functs[i].addr;</li>
 * <li>ret == functs[i].addr.</li> 
 * </ol>
 * </li>
 * </li>
 * If the function corresponding to the ret address is not in the 
 * functions, print <code>"Function %p(...), in\n"</code>
 * , otherwise, call print_function() to print the function.
 *
 * After printing, empty the str for the next print, get the 
 * ebp and ret of the caller of this current function by 
 * following the calling convention, and call itself with 
 * caller's ebp and ret.
 *
 * @param ret The return address or the next instruction of
 * the caller
 * @param ebp The ebp of the caller
 * @param str The string to which to concatenate the output
 */
static void get_funct(void *ret, void *ebp, char *str) 
{
	if (ret == ebp) {
		strcat(str, "FATAL: return address and ebp is the same.");
		tb_exit();
	}

	strcpy(format,  "Function %p(...), in\n");
	
	const functsym_t *pfunct = NULL;
	int i = 0;
	for (; i < FUNCTS_MAX_NUM; i++) {
		
		if (strlen(functions[i].name) == 0) {
			/* end of functions */
			break;
		}

		if (functions[i].addr > ret) {
			if (i > 0)
				pfunct = &(functions[i-1]);
			break;
		} else if (functions[i].addr == ret) {
			pfunct = &(functions[i]);
			break;
		}
	} 
	
	/* fill str with output */
	if (!pfunct) {
		snprintf(str, BUF_SIZE, format, ret);
	} else {
		print_function(pfunct, ebp, str);
	}

	/* print out str */
	if (fptr) {
		fprintf(fptr, "%s", str);
	} else {
		tb_exit();
	}

	/* end with main */
	if (pfunct && is_main(pfunct)) {
		return;
	}

	/* empty the str for next print */
	*str = '\0';
	*format = '\0';

	/* go to the next function */
	if (is_valid_unit((void *)((void **)ebp + 1), TYPE_PTR)){
		ret = *((void **)ebp + 1);
		if (!is_valid_unit(ret, TYPE_PTR)) {
			strcat(format, "FATAL: the return address (%p); out of bound");
			snprintf(str, BUF_SIZE, format, ret);
			tb_exit();
		}
	} else {
		strcat(format, "FATAL: pointer above ebp (%p); out of bound");
		snprintf(str, BUF_SIZE, format, ebp);
		tb_exit();
	}

	if (is_valid_unit(ebp, TYPE_PTR)) {
		ebp = *(void **)ebp;
		get_funct(ret, ebp, str);
	} else {
		strcat(format, "FATAL: ebp (%p) out of bound");
		snprintf(str, BUF_SIZE, format, ebp);
		tb_exit();
	}
}

/**
 * @brief Prints function. In case of function main, print as <code> 
 * Function funct_name(Args)\\n</code> otherwise, print as <code>Function 
 * funct_name(Args), in\\n</code>.
 *
 * @param pfunct Pointer to the functsym_t to be printed
 * @param ebp Pointer to the ebp of a running instance 
 *  of the function
 * @param strfunct concatenate output to this string
 */
static void print_function(const functsym_t *pfunct, 
		void *ebp, char *strfunct) {

	strcat(strfunct, "Function ");

	print_str(pfunct->name, strfunct, TYPE_FUNCT);

	strcat(strfunct, "(");

	print_args(pfunct->args, ebp, strfunct);

	if (!is_main(pfunct)) {
		strcat(strfunct, "), in\n");
	} else {
		strcat(strfunct, ")\n");
	}
}

/**
 * @brief Prints arguments as <code>void</code> or <code>arg[, arg]</code>
 *
 * Go through args until
 * <ol>
 * <li>maximum length;</li>
 * <li>argument with zero-length name.</li>
 * </ol>
 * Print all arguments if there exist; otherwise print "void".
 *
 * @param args Pointer to an argsym_t array
 * @param ebp Pointer to the ebp based on which to extract value of argument
 * @param strargs concatenate output to this string
 */
static void print_args(const argsym_t *args, void *ebp, char *strargs) {
	const argsym_t *arg = args;

	/* print first arg or void */
	if (strlen(arg->name) == 0) {
		strcat(strargs, "void");
		return;
	} else {
		print_arg(arg, ebp, strargs);
	}

	/* print remain args */
	int i = 1;
	for (; i < ARGS_MAX_NUM; i++) {

		arg = args + i;

		/* the argv terminates */
		if (strlen(arg->name) == 0) {
			break;
		}
		
		strcat(strargs, ", ");
		print_arg(arg, ebp, strargs);
	}
} 

/**
 * @brief Prints argument as <code>type arg_name=arg_val</code>
 *
 * Get the address of the argument through ebp and offset
 * of the argument, and test whether the address of the type
 * is aligned and each byte of the multi-byte unit is valid. 
 * if so, print as:
 * <dl>
 * <dt>int</dt> <dd><code>"int name=%d, "</code></dd> 
 * <dt>float</dt> <dd><code>"float name=%f, "</code></dd> 
 * <dt>double</dt> <dd><code>"double name=%lf, "</code></dd> 
 * <dt>char</dt> <dd><code>"char name=%c, "</code> or <code>"char name=%d, "
 * </code></dd>
 * <dt>char *</dt> <dd><code>"char *name=%s, "</code></dd> 
 * <dt>char**</dt> <dd><code>"char **name={%s,%s,%s,...}, "</code></dd> 
 * <dt>void*</dt> <dd><code>"void *name=0v%lx, "</code></dd> 
 * <dt>UNKNOWN</dt> <dd><code>"UNKNOWN name=%p, "</code></dd>
 * </dl>
 * otherwise, print the address with information instead.
 *
 * @param arg Pointer to the argsym_t to be printed
 * @param ebp Pointer to the ebp based on which to extract 
 * value of argument
 * @param strarg concatenate output to this string
 */
static void print_arg(const argsym_t *arg, void *ebp, char *strarg) {
	
	void *addr = ebp + arg->offset;

	switch(arg->type) {
		case TYPE_CHAR:
			/* addr points to a char type */
			strcat(strarg, "char ");
			print_str(arg->name, strarg, TYPE_ARG);
			strcat(strarg, "=");
			print_char((char *)(addr), strarg);
			break;
		case TYPE_INT:
			/* addr points to an int type */
			strcat(strarg, "int ");
			print_str(arg->name, strarg, TYPE_ARG);
			strcpy(format, strarg);
			if (is_valid_unit(addr, TYPE_INT)) {
				if (!is_align(addr, TYPE_INT)){
					/* not aligned */
					strcat(format, "=%p%s");
					snprintf(strarg, BUF_SIZE, format, 
							addr, msg_not_align);
				} else {
					strcat(format, "=%d");
					snprintf(strarg, BUF_SIZE, format, 
							*(int *)(addr));
				}
			} else {
				/* invalid */
				strcat(format, "=%p%s");
				snprintf(strarg, BUF_SIZE, format, addr, 
						msg_out_bound);
			}
			break;
		case TYPE_FLOAT:
			/* addr points to a float type */
			strcat(strarg, "float ");
			print_str(arg->name, strarg, TYPE_ARG);
			strcpy(format, strarg);
			if (is_valid_unit(addr, TYPE_FLOAT)) {
				if (!is_align(addr, TYPE_FLOAT)){
					/* not aligned */
					strcat(format, "=%p%s");
					snprintf(strarg, BUF_SIZE, format, 
							addr, msg_not_align);
				} else {
					strcat(format, "=%f");
					snprintf(strarg, BUF_SIZE, format, 
							*(float *)(addr));
				}
			} else {
				/* invalid */
				strcat(format, "=%p%s");
				snprintf(strarg, BUF_SIZE, format, addr, 
						msg_out_bound);
			}
			break;
		case TYPE_DOUBLE:
			/* addr points to a double type */
			strcat(strarg, "double ");
			print_str(arg->name, strarg, TYPE_ARG);
			strcpy(format, strarg);
			if (is_valid_unit(addr, TYPE_DOUBLE)) {
				if (!is_align(addr, TYPE_DOUBLE)){
					/* not aligned */
					strcat(format, "=%p%s");
					snprintf(strarg, BUF_SIZE, format, 
							addr, msg_not_align);
				} else {
					strcat(format, "=%lf");
					snprintf(strarg, BUF_SIZE, format, 
							*(double *)(addr));
				}
			} else {
				/* invalid */
				strcat(format, "=%p%s");
				snprintf(strarg, BUF_SIZE, format, addr, 
						msg_out_bound);
			}
			break;
		case TYPE_STRING:
			/* addr points to a char* type */
			strcat(strarg, "char *");
			print_str(arg->name, strarg, TYPE_ARG);
			strcat(strarg, "=");
			strcpy(format, strarg);
			if (is_valid_unit(addr, TYPE_PTR)) {
				if (!is_align(addr, TYPE_PTR)){
					/* not aligned */
					strcat(format, "=%p%s");
					snprintf(strarg, BUF_SIZE, format, 
							addr, msg_not_align);
				} else {
					print_str(*(char **)(addr), strarg, 
							TYPE_STR);
				}
			} else {
				/* invalid */
				strcat(format, "=%p%s");
				snprintf(strarg, BUF_SIZE, format, addr, 
						msg_out_bound);
			}
			break;
		case TYPE_STRING_ARRAY:
			/* addr points to a char** type */
			strcat(strarg, "char **");
			print_str(arg->name, strarg, TYPE_ARG);
			strcpy(format, strarg);
			if (is_valid_unit(addr, TYPE_PTR)) {
				if (!is_align(addr, TYPE_PTR)){
					/* not aligned */
					strcat(format, "=%p%s");
					snprintf(strarg, BUF_SIZE, format, 
							addr, msg_not_align);
				} else {
					strcat(strarg, "={");
					print_str_arr(*(char ***)(addr), strarg);
					strcat(strarg, "}");
				}
			} else {
				/* invalid */
				strcat(format, "=%p%s");
				snprintf(strarg, BUF_SIZE, format, addr, 
						msg_out_bound);
			}
			break;
		case TYPE_VOIDSTAR:
			/* addr points to a void* type */
			strcat(strarg, "void *");
			print_str(arg->name, strarg, TYPE_ARG);
			strcpy(format, strarg);
			if (is_valid_unit(addr, TYPE_PTR)) {
				if (!is_align(addr, TYPE_PTR)){
					/* not aligned */
					strcat(format, "=%p%s");
					snprintf(strarg, BUF_SIZE, format, 
							addr, msg_not_align);
				} else {
					strcat(format, "=0v%lx");
					snprintf(strarg, BUF_SIZE, format ,
							(long)(*(void **)(addr)));
				}
			} else {
				/* invalid */
				strcat(format, "=%p%s");
				snprintf(strarg, BUF_SIZE, format, addr, 
						msg_out_bound);
			}
			break;
		case TYPE_UNKNOWN:
			strcat(strarg, "UNKNOWN");
			print_str(arg->name, strarg, TYPE_ARG);
			strcpy(format, strarg);
			strcat(format, "=%p");
			snprintf(strarg, BUF_SIZE, format, addr);
			break;
	}
}

/**
 * @brief Prints the string.
 *
 * When the string is the name of a function or an argument,
 * print it as long as it is printable and endable; when the string
 * is the value of argument and it is valid and printable within predefined 
 * length or '\\0', print the string in predefined length. If the 
 * above conditions can't be satisfied, print the address instead.
 *
 * @param pstr Pointer to the string to be printed
 * @param str The string to be contatenated with output
 * @param type The type of the string, a value of TYPE_STR, 
 * TYPE_FUNCT, TYPE_ARG.
 */
static void print_str(const char *pstr, char *str, int type) {

	strcpy(format, str);

	/* message to put error information */
	char msg[STR_MAX_SIZE];

	if (type == TYPE_STR) {
		/* the value of the string argument */
		if (is_print_strvalue(pstr, msg)) {
			if (strlen(pstr) > STR_MAX_SIZE) {
				strcat(format, "\"%.25s...\"");
			} else {
				strcat(format, "\"%s\"");
			}
			snprintf(str, BUF_SIZE, format, pstr);
		} else {
			/* address and msg */
			strcat(format, "%p%s");
			snprintf(str, BUF_SIZE, format, pstr, msg);
		}
	} else {
		/* the name of a function or an argument */
		if (is_print_name(pstr, type, msg)) {
			strcat(format, "%s");
			snprintf(str, BUF_SIZE, format, pstr);
		} else {
			/* address and msg */
			strcat(format, "%p%s");
			snprintf(str, BUF_SIZE, format, pstr, msg);
		}
	}

}

/**
 * @brief Prints the char as '\%d' (unprintable) or '%c' if 
 * accessible (valid); otherwise, print the address.
 *
 * @param pchar Pointer to the char to be printed
 * @param str The string to be contatenated with output
 */
static void print_char(char *pchar, char *str) {

	strcpy(format, str);

	if (is_valid_unit((void *)pchar, TYPE_CHAR)) {
		/* valid */
		if (isprint(*pchar)) {
			strcat(format, "'%c'");
		} else {
			/* unprintable */
			strcat(format, "'\\%d'");
		}
		snprintf(str, BUF_SIZE, format, *pchar);
	} else {
		/* invalid char */
		strcat(format, "%p%s");
		snprintf(str, BUF_SIZE, format, pchar, msg_out_bound);
	}

}

/**
 * @brief Prints a string array argument as <code>"s1"[, "s2"]</code> 
 * within defined length, if there are more, add ", ...". Once the array 
 * is empty, print nothing.
 *
 * Empty means the pointer to the first item is invalid, or not aligned,
 * or the first item is NULL.
 *
 * @param pstrarr The string array to be printed
 * @param str The string to be contatenated with output
 */
static void print_str_arr(char **pstrarr, char *str) {

	char **pstrr = pstrarr;

	if (!is_valid_unit(pstrr, TYPE_PTR) 
			|| !is_align(pstrr, TYPE_PTR)|| !(*pstrr))
		/* The array is empty */
		return;
	else {
		print_str(*pstrr, str, TYPE_STR);
	}
		
	int i = 1;
	for (; i < STR_MAX_NUM; i++) {
		pstrr = pstrarr + i;

		if (!is_valid_unit(pstrr, TYPE_PTR)) {
			/* treat outbound as the end of string array */
			return;
		} 
		
		if (!(*pstrr)){
			/* end with a null pointer */
			return;
		}

		strcat(str, ", ");
		print_str(*pstrr, str, TYPE_STR);
	}
	
	if (is_valid_unit(pstrarr + i, TYPE_PTR) 
			&& (*(pstrarr + i))) {
		strcat(str, ", ...");
	}
}

/**
 * @brief Tests whether the name of a function or a argument is endable 
 * and printable. Endable means the string contains '\\0'. Printable means
 * there is no unprintable charactor within '\\0' and predefined length.
 *
 * @param str The string to be detected
 * @param type The type of the string, a value of TYPE_STR, 
 * TYPE_FUNCT, TYPE_ARG.
 * @param msg The error msg useful when test fails
 *
 * @return 0 when the name can be printed, otherwise, 1.
 */
static int is_print_name(const char *str, int type, char *msg) {
	int len = 0;
	strcpy(msg, msg_not_print);

	switch(type) {
		case TYPE_FUNCT:
			len = FUNCTS_MAX_NAME;
			break;
		case TYPE_ARG:
			len = ARGS_MAX_NAME;
			break;
		default:
			return 1;
	}


	int i = 0;
	/* loop ends with '\\0' and length */
	for (; *(str + i) != '\0' && i < len; i++ ) {
		
		if (!isprint(*(str + i))) {
			/* there is an unprintable charactor */
			return 0;
		}
	}

	if (*(str + i) != '\0') {
		/* does not contain '\\0' */
	 	return 0;
	}

	return 1;
}

/**
 * @brief Tests whether the value of a string argument is valid and 
 * printable. A valid string means each char is valid within its 
 * predefind length or '\\0'. Printable means the string does not contain 
 * any unprintable char within its predefined length or '\\0'.
 *
 * @param str The string to be detected
 * @param msg The error msg useful when the test fails
 * @return 0 when the value can be printed, otherwise, 1.
 */
static int is_print_strvalue(const char *str, char *msg){

	int i = 0;
	int printable = 1;
	const char *strr;

	/* loop ends with '\\0' and length */
	for (; i < STR_MAX_SIZE; i++ ) {

		strr = str + i;

		if (!is_valid_unit((void *)strr, TYPE_CHAR)) {
			/* invalid */
			strcpy(msg, msg_out_bound);
			return 0;
		}

		if (*(strr) == '\0') {
			/* end */
			break;
		}

		if (printable && !isprint(*(strr))) {
			strcpy(msg, msg_not_print);
			printable = 0;
		}
	}

	if (printable)
		return 1;
	else 
		/* not printable */
		return 0;
}

/**
 * @brief Tests whether the type which should be aligned is aligned.
 *
 * The alignment follows Linux IA32. It is that the lowest 2 bits of 
 * the address of int, float, double should be 00 and the address of
 * any other type suffers no restriction.
 *
 * @param addr The address from which to start detection 
 * @param type One of the types like TYPE_INT, TYPE_FLOAT, TYPE_DOUBLE. 
 * @return 1 if aligned, otherwise, 0.
 */
static int is_align(const void *addr, int type) {
	switch(type) {
		case TYPE_INT:
		case TYPE_FLOAT:
		case TYPE_DOUBLE:
			/* the lowest 2 bits of address should be 00 */
			if ((((unsigned long)addr)&0x3) == 0L)
				return 1;
			else
				return 0;
			break;
	}
	/* no restriction */
	return 1;
}

/**
 * @brief Tests whether a multi-byte unit is valid by using its size (len).
 * In other words, the function will test memory of 
 * [addr-len+1, addr] is valid.
 *
 * @param addr The address from which to start detection 
 * @param type One of the types like TYPE_INT, TYPE_FLOAT, etc. 
 * @return 1 if any byte of the unit is accessible, otherwise, 0.
 */
static int is_valid_unit(const void *addr, int type) {

	int len;
	switch(type){
		case TYPE_CHAR:
			len = CHAR_SIZE;
			break;
		case TYPE_INT:
			len = INT_SIZE;
			break;
		case TYPE_FLOAT:
			len = FLOAT_SIZE;
			break;
		case TYPE_DOUBLE:
			len = DOUBLE_SIZE;
			break;
		case TYPE_PTR:
			len = PTR_SIZE;
			break;
		default:
			len = 0;
			break;
	}
	char *addrr;
	int i =0;
	for (; i < len; i++) {
		addrr = (char *)addr - i;
		if (write(tmp_fd, (char *)addrr, 1) == -1) {
			if (errno == EFAULT) {
				return 0;
			} else if (errno != EINTR) {
				fprintf(stderr, "write error: %s\n", 
						strerror(errno));
				tb_exit();
			}
		}
	}

	return 1;
}

/**
 * @brief Tests whether the function is the main function according to
 * its name.
 *
 * @param funct Pointer to the function to be detected
 * @return 1 if the name of the function is "main", otherwise 0.
 */
static int is_main(const functsym_t *funct) {
	if (!strcmp(funct->name, "main")) {
		return 1;
	} else {
		return 0;
	}
} 

/**
 * @brief Executes when the scheduled alarm is due.
 * This handler is used to terminate infinite loop.
 *
 * @param sig specifies the signal processed
 */
void sigalrm_handler (int sig) {
	strcat(str, "\nFatal: there may be a infinite loop.");
	tb_exit();
}

/**
 * @brief Executes when such signals as SIGINT, SIGQUIT,
 * SIGABRT, SIGTERM are received
 *
 * @param sig specifies the signal processed
 */
void sig_handler (int sig) {
	fprintf(stderr, "Interrupted.\n");
	tb_exit();
}

