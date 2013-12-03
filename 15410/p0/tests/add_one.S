/*
 * As you can see, you can perform c style comments in .S files.	
 * You may also use precompiler directives like #include and #define
 */


/* define the add_one1 and add_one2 labels so that they can be called from
 * other files (.c or .S) */
.global add_one1
.global add_one2

/* Method 1 saves off all of the callee save registers
 * and then adds one to the argument and returns it
 */
add_one1:
	/* make sure we save all of the callee save registers */
	pushl	%ebp		
	movl	%esp,%ebp	
	pushl	%edi
	pushl	%ebx
	pushl	%esi
	
        movl	8(%ebp),%eax	/* get the argument */
        addl	$1,%eax		/* add one to it */

	/* restore the callee save registers */
	popl	%esi
	popl	%ebx
	popl	%edi
	movl	%ebp,%esp
	popl	%ebp
	ret

/* Method 2 doesn't use any callee save registers or the stack
 * so it chooses not to save them. Notice the difference in how
 * we need to get the argument for the function. Figure out why
 * it is different for the different functions and convince yourself
 * that it is right.
 */
add_one2:
	movl	4(%esp),%eax	/* get the argument */
	addl	$1,%eax		/* add one to it and return it */
	ret


