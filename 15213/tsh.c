/* 
 * tsh - A tiny shell program with job control
 *
 * Yue Xing (yuexing@andrew.cmu.edu)
 */
#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <ctype.h>
#include <signal.h>
#include <sys/types.h>
#include <fcntl.h>
#include <sys/wait.h>
#include <errno.h>

/* Misc manifest constants */
#define MAXLINE    1024   /* max line size */
#define MAXARGS     128   /* max args on a command line */
#define MAXJOBS      16   /* max jobs at any point in time */
#define MAXJID    1<<16   /* max job ID */

/* Job states */
#define UNDEF         0   /* undefined */
#define FG            1   /* running in foreground */
#define BG            2   /* running in background */
#define ST            3   /* stopped */

/* 
 * Jobs states: FG (foreground), BG (background), ST (stopped)
 * Job state transitions and enabling actions:
 *     FG -> ST  : ctrl-z
 *     ST -> FG  : fg command
 *     ST -> BG  : bg command
 *     BG -> FG  : fg command
 * At most 1 job can be in the FG state.
 */

/* Parsing states */
#define ST_NORMAL   0x0   /* next token is an argument */
#define ST_INFILE   0x1   /* next token is the input file */
#define ST_OUTFILE  0x2   /* next token is the output file */


/* Global variables */
extern char **environ;      /* defined in libc */
char prompt[] = "tsh> ";    /* command line prompt (DO NOT CHANGE) */
int verbose = 0;            /* if true, print additional output */
int nextjid = 1;            /* next job ID to allocate */
char sbuf[MAXLINE];         /* for composing sprintf messages */

struct job_t {              /* The job struct */
	pid_t pid;              /* job PID */
	int jid;                /* job ID [1, 2, ...] */
	int state;              /* UNDEF, BG, FG, or ST */
	char cmdline[MAXLINE];  /* command line */
};
struct job_t job_list[MAXJOBS]; /* The job list */

struct cmdline_tokens {
	int argc;               /* Number of arguments */
	char *argv[MAXARGS];    /* The arguments list */
	char *infile;           /* The input file */
	char *outfile;          /* The output file */
	enum builtins_t {       /* Indicates if argv[0] is a builtin command */
		BUILTIN_NONE,
		BUILTIN_QUIT,
		BUILTIN_JOBS,
		BUILTIN_BG,
		BUILTIN_FG} builtins;
};
/* End global variables */


/* Function prototypes */
void eval(char *cmdline);

void sigchld_handler(int sig);
void sigtstp_handler(int sig);
void sigint_handler(int sig);

/* Here are helper routines that we've provided for you */
int parseline(const char *cmdline, struct cmdline_tokens *tok); 
void sigquit_handler(int sig);

void clearjob(struct job_t *job);
void initjobs(struct job_t *job_list);
int maxjid(struct job_t *job_list); 
int addjob(struct job_t *job_list, pid_t pid, int state, char *cmdline);
int deletejob(struct job_t *job_list, pid_t pid); 
pid_t fgpid(struct job_t *job_list);
struct job_t *getjobpid(struct job_t *job_list, pid_t pid);
struct job_t *getjobjid(struct job_t *job_list, int jid); 
int pid2jid(pid_t pid); 
void listjobs(struct job_t *job_list, int output_fd);

void usage(void);
void unix_error(char *msg);
void app_error(char *msg);
typedef void handler_t(int);
handler_t *Signal(int signum, handler_t *handler);



/*
 * main - The shell's main routine 
 */
	int 
main(int argc, char **argv) 
{
	char c;
	char cmdline[MAXLINE];    /* cmdline for fgets */
	int emit_prompt = 1; /* emit prompt (default) */

	/* Redirect stderr to stdout (so that driver will get all output
	 * on the pipe connected to stdout) */
	dup2(1, 2);

	/* Parse the command line */
	while ((c = getopt(argc, argv, "hvp")) != EOF) {
		switch (c) {
			case 'h':             /* print help message */
				usage();
				break;
			case 'v':             /* emit additional diagnostic info */
				verbose = 1;
				break;
			case 'p':             /* don't print a prompt */
				emit_prompt = 0;  /* handy for automatic testing */
				break;
			default:
				usage();
		}
	}

	/* Install the signal handlers */

	/* These are the ones you will need to implement */
	Signal(SIGINT,  sigint_handler);   /* ctrl-c */
	Signal(SIGTSTP, sigtstp_handler);  /* ctrl-z */
	Signal(SIGCHLD, sigchld_handler);  /* Terminated or stopped child */
	Signal(SIGTTIN, SIG_IGN);
	Signal(SIGTTOU, SIG_IGN);

	/* This one provides a clean way to kill the shell */
	Signal(SIGQUIT, sigquit_handler); 

	/* Initialize the job list */
	initjobs(job_list);


	/* Execute the shell's read/eval loop */
	while (1) {

		if (emit_prompt) {
			printf("%s", prompt);
			fflush(stdout);
		}
		if ((fgets(cmdline, MAXLINE, stdin) == NULL) && ferror(stdin)){
			printf("pid : %d\n", getpid());
			unix_error("fgets error");
		}
		if (feof(stdin)) { 
			/* End of file (ctrl-d) */
			printf ("\n");
			fflush(stdout);
			fflush(stderr);
			exit(0);
		}

		/* Remove the trailing newline */
		cmdline[strlen(cmdline)-1] = '\0';
		/* Evaluate the command line */
		eval(cmdline);

		fflush(stdout);
		fflush(stdout);
	} 

	exit(0); /* control never reaches here */
}

/* 
 * eval - Evaluate the command line that the user has just typed in
 * 
 * If the user has requested a built-in command (quit, jobs, bg or fg)
 * then execute it immediately. Otherwise, fork a child process and
 * run the job in the context of the child. If the job is running in
 * the foreground, wait for it to terminate and then return.  Note:
 * each child process must have a unique process group ID so that our
 * background children don't receive SIGINT (SIGTSTP) from the kernel
 * when we type ctrl-c (ctrl-z) at the keyboard.  
 */
	void 
eval(char *cmdline) 
{
	int bg;              /* should the job run in bg or fg? */
	struct cmdline_tokens tok;

	/* Parse command line */
	bg = parseline(cmdline, &tok); 

	if (bg == -1) return;               /* parsing error */
	if (tok.argv[0] == NULL)  return;   /* ignore empty lines */

	if (tok.builtins == BUILTIN_NONE){
		/* fork a child to execve non-builtin program */
		int pid, jid;

		/*
		 * these exist a race btw parent and child
		 * apply explicit mask
		 */
		sigset_t mask;
		sigemptyset(&mask);
		sigaddset(&mask, SIGCHLD);
		sigaddset(&mask, SIGINT);
		sigaddset(&mask, SIGTSTP);
		sigprocmask(SIG_BLOCK, &mask, NULL);//block SIGCHLD

		if((pid = fork()) == 0){
			int in_fd, out_fd; /*IO redirect*/

			/* redirect I/O first */
			if(tok.infile != NULL){
				if((in_fd = open(tok.infile, O_RDONLY)) == -1){
					perror("Input Error");
					return;
				}
				if(dup2(in_fd, 0) < 0){
					perror("Dup Error");
					return;
				}
			}

			if(tok.outfile != NULL){
				if((out_fd = open(tok.outfile, O_CREAT | O_WRONLY, S_IRUSR | S_IWUSR | S_IRGRP | S_IWGRP)) == -1){
					perror("Output Error");
					return;
				}
				if(dup2(out_fd, 1) < 0){
					perror("Dup Error");
					return;
				}
			}
			/* process group */
			setpgid(0, 0);

			/* set signal handler  to default */
			sigprocmask(SIG_UNBLOCK, &mask, NULL);
			Signal(SIGINT,  SIG_DFL);   /* ctrl-c */
			Signal(SIGTSTP, SIG_DFL);  /* ctrl-z */
			Signal(SIGCHLD, SIG_DFL);  /* Terminated or stopped child */
			Signal(SIGTTIN, SIG_DFL);
			Signal(SIGTTOU, SIG_DFL);

			if(execve(tok.argv[0], tok.argv, environ) == -1){
				fprintf(stderr, "%s: Command not found.\n", tok.argv[0]);
				exit(EXIT_FAILURE);
			}

		}
		if(pid == -1){
			unix_error("fork error");
		}
		/* FG 1, bg 0
		 * BG 2, bg 1
		 * bg + 1, can be treated as status
		 * status change : ST, EXIT will be processed by handler or parents
		 */
		addjob(job_list, pid, bg + 1, cmdline);

		sigprocmask(SIG_UNBLOCK, &mask, NULL);
		jid = pid2jid(pid);
		/* parent wait for fg job to terminate */
		if (bg == 0){
			while(fgpid(job_list) != 0)
				;
		}else{
			fprintf(stdout, "[%d] (%d) %s\n", jid, pid, cmdline);
		}

	}else if(tok.builtins == BUILTIN_QUIT){
		int pid = getpid();
		if(verbose){
			fprintf(stdout, "clear all the jobs and quit\n");
		}

		kill(pid, SIGQUIT); 
	}else if(tok.builtins == BUILTIN_JOBS){
		/* should be able to redirect jobs */
		int out_fd = STDOUT_FILENO;
		if(tok.outfile != NULL){
			out_fd = open(tok.outfile, O_CREAT | O_WRONLY, S_IRUSR | S_IWUSR | S_IRGRP | S_IWGRP);
			if(out_fd == -1){
				perror("jobs output error");
				return;
			}
		}
		listjobs(job_list, out_fd);
	}else if(tok.builtins == BUILTIN_BG){
		struct job_t *stjob;
		int jid, pid;
		/*
		 * argc should be 2
		 * argv can be cast to integer by using atoi
		 * the job should be stopped for restarting as background
		 */
		if(tok.argc != 2){
			fprintf(stderr, "wrong input");
			return;
		}else if(*(tok.argv[1]) == '%'){
			jid = atoi((tok.argv[1]) + 1);
			stjob = getjobjid(job_list, jid);
		}else{
			pid = atoi((tok.argv[1]));
			stjob = getjobpid(job_list, pid);
		}
		if(stjob == NULL){
			fprintf(stderr, "No Such Job\n");
		}
		if(stjob->state != ST){
			fprintf(stderr, "[%d] %d is running now\n", stjob->jid, stjob->pid);
			return;
		}
		fprintf(stdout, "[%d] (%d) %s", stjob->jid, stjob->pid, stjob->cmdline);
		/* send SIGCONT */
		kill(-(stjob->pid), SIGCONT);
		/* set BG */
		stjob->state = BG;
	}else if(tok.builtins == BUILTIN_FG){
		struct job_t *stjob;
		int pid, jid;
		/*
		 * argc should be 2
		 * argv can be cast to integer by using atoi
		 * the job should be stopped for restarting as foreground
		 */
		if(tok.argc != 2){
			fprintf(stderr, "wrong input");
			return;
		}else if(*(tok.argv[1]) == '%'){
			jid = atoi((tok.argv[1]) + 1);
			stjob = getjobjid(job_list, jid);
		}else{
			pid = atoi(tok.argv[1]);
			stjob = getjobpid(job_list, pid);
		}
		if(stjob == NULL){
			fprintf(stderr, "No Such Job\n");
		}

		if(stjob->state != ST){
			fprintf(stderr, "[%d] %d is running now\n", stjob->jid, stjob->pid);
			return;
		}
		/* send SIGCONT */
		kill(-(stjob->pid), SIGCONT);
		/* set FG */
		stjob->state = FG;
		/* wait for terminate */
		while(fgpid(job_list) != 0)
			;
	}else{
		fprintf(stderr ,"uncaught cmd: %s\n", cmdline);
		return;
	}
	return;
}

/* 
 * parseline - Parse the command line and build the argv array.
 * 
 * Parameters:
 *   cmdline:  The command line, in the form:
 *
 *                command [arguments...] [< infile] [> oufile] [&]
 *
 *   tok:      Pointer to a cmdline_tokens structure. The elements of this
 *             structure will be populated with the parsed tokens. Characters 
 *             enclosed in single or double quotes are treated as a single
 *             argument. 
 * Returns:
 *   1:        if the user has requested a BG job
 *   0:        if the user has requested a FG job  
 *  -1:        if cmdline is incorrectly formatted
 * 
 * Note:       The string elements of tok (e.g., argv[], infile, outfile) 
 *             are statically allocated inside parseline() and will be 
 *             overwritten the next time this function is invoked.
 */
	int 
parseline(const char *cmdline, struct cmdline_tokens *tok) 
{

	static char array[MAXLINE];          /* holds local copy of command line */
	const char delims[10] = " \t\r\n";   /* argument delimiters (white-space) */
	char *buf = array;                   /* ptr that traverses command line */
	char *next;                          /* ptr to the end of the current arg */
	char *endbuf;                        /* ptr to the end of the cmdline string */
	int is_bg;                           /* background job? */

	int parsing_state;                   /* indicates if the next token is the
											input or output file */

	if (cmdline == NULL) {
		(void) fprintf(stderr, "Error: command line is NULL\n");
		return -1;
	}

	(void) strncpy(buf, cmdline, MAXLINE);
	endbuf = buf + strlen(buf);

	tok->infile = NULL;
	tok->outfile = NULL;

	/* Build the argv list */
	parsing_state = ST_NORMAL;
	tok->argc = 0;

	while (buf < endbuf) {
		/* Skip the white-spaces */
		buf += strspn (buf, delims);
		if (buf >= endbuf) break;

		/* Check for I/O redirection specifiers */
		if (*buf == '<') {
			if (tok->infile) {
				(void) fprintf(stderr, "Error: Ambiguous I/O redirection\n");
				return -1;
			}
			parsing_state |= ST_INFILE;
			buf++;
			continue;
		}
		if (*buf == '>') {
			if (tok->outfile) {
				(void) fprintf(stderr, "Error: Ambiguous I/O redirection\n");
				return -1;
			}
			parsing_state |= ST_OUTFILE;
			buf ++;
			continue;
		}

		if (*buf == '\'' || *buf == '\"') {
			/* Detect quoted tokens */
			buf++;
			next = strchr (buf, *(buf-1));
		} else {
			/* Find next delimiter */
			next = buf + strcspn (buf, delims);
		}

		if (next == NULL) {
			/* Returned by strchr(); this means that the closing
			   quote was not found. */
			(void) fprintf (stderr, "Error: unmatched %c.\n", *(buf-1));
			return -1;
		}

		/* Terminate the token */
		*next = '\0';

		/* Record the token as either the next argument or the input/output file */
		switch (parsing_state) {
			case ST_NORMAL:
				tok->argv[tok->argc++] = buf;
				break;
			case ST_INFILE:
				tok->infile = buf;
				break;
			case ST_OUTFILE:
				tok->outfile = buf;
				break;
			default:
				(void) fprintf(stderr, "Error: Ambiguous I/O redirection\n");
				return -1;
		}
		parsing_state = ST_NORMAL;

		/* Check if argv is full */
		if (tok->argc >= MAXARGS-1) break;

		buf = next + 1;
	}

	if (parsing_state != ST_NORMAL) {
		(void) fprintf(stderr, "Error: must provide file name for redirection\n");
		return -1;
	}

	/* The argument list must end with a NULL pointer */
	tok->argv[tok->argc] = NULL;

	if (tok->argc == 0)  /* ignore blank line */
		return 1;

	if (!strcmp(tok->argv[0], "quit")) {                 /* quit command */
		tok->builtins = BUILTIN_QUIT;
	} else if (!strcmp(tok->argv[0], "jobs")) {          /* jobs command */
		tok->builtins = BUILTIN_JOBS;
	} else if (!strcmp(tok->argv[0], "bg")) {            /* bg command */
		tok->builtins = BUILTIN_BG;
	} else if (!strcmp(tok->argv[0], "fg")) {            /* fg command */
		tok->builtins = BUILTIN_FG;
	} else {
		tok->builtins = BUILTIN_NONE;
	}

	/* Should the job run in the background? */
	if ((is_bg = (*tok->argv[tok->argc-1] == '&')) != 0)
		tok->argv[--tok->argc] = NULL;

	return is_bg;
}


/*****************
 * Signal handlers
 *****************/

/* 
 * sigchld_handler - The kernel sends a SIGCHLD to the shell whenever
 *     a child job terminates (becomes a zombie), or stops because it
 *     received a SIGSTOP, SIGTSTP, SIGTTIN or SIGTTOU signal. The 
 *     handler reaps all available zombie children, but doesn't wait 
 *     for any other currently running children to terminate.  
 */
	void 
sigchld_handler(int sig) 
{
	int child_status;
	pid_t wpid;
	/**
	 * 
	 * signals can block
	 * signals are not queued
	 *
	 * waitpid:
	 * if -1, wait for any child
	 * if WNOHANG, do not wait
	 * return:
	 * on success, wpid > 0
	 * with WNOHANG, one or more children exist, have not yet changed state, wpid = 0
	 * on error, wpid = -1
	 */ 
	while(((wpid = waitpid(-1, &child_status, WNOHANG | WUNTRACED)) > 0)){

		struct job_t *job = getjobpid(job_list, wpid);

		if(job == NULL){
			app_error("sigchld getjobpid error");
		}

		if(WIFEXITED(child_status)){
			/* if child terminated normally */
			if(verbose){	
				fprintf(stdout, "[%d] (%d) %s deleted\n", job->jid, job->pid, job->cmdline);
			}
			deletejob(job_list, wpid);
		}else if(WIFSTOPPED(child_status)){
			/* if is stopped by delivery of a signal */
			job->state = ST;
			if(verbose){}
			fprintf(stdout, "Job [%d] (%d) stopped by signal %d\n", job->jid, job->pid, WSTOPSIG(child_status));
		}else if(WIFSIGNALED(child_status)){
			/* if is terminated by a signal */
			fprintf(stdout, "Job [%d] (%d) terminated by signal %d\n", job->jid, job->pid, WTERMSIG(child_status));
			deletejob(job_list, wpid);
		}
	}

	if (wpid == -1 && errno != ECHILD){
		unix_error("sigchld wait error");
	}
	return;
}

/* 
 * sigint_handler - The kernel sends a SIGINT to the shell whenver the
 *    user types ctrl-c at the keyboard.  Catch it and send it along
 *    to the foreground job.  
 */
	void 
sigint_handler(int sig) 
{
	/* foreground job */
	int fid = fgpid(job_list);

	/* 0 if no such job */
	if(fid == 0){
		return;
	} 
	/* catch and send forward */
	if(kill(-fid, SIGINT) == -1){
		unix_error("error sigint.");
	}
	return;
}


/*
 * sigtstp_handler - The kernel sends a SIGTSTP to the shell whenever
 *     the user types ctrl-z at the keyboard. Catch it and suspend the
 *     foreground job by sending it a SIGTSTP.  
 */
	void 
sigtstp_handler(int sig) 
{
	/* foreground job */
	int fid = fgpid(job_list);
	/* 0 if no such job */
	if(fid == 0){
		return;
	}
	/* catch and send forward */
	if(kill(-fid, SIGTSTP) == -1)
		unix_error("error sigtstp.");
	return;
}

/*********************
 * End signal handlers
 *********************/

/***********************************************
 * Helper routines that manipulate the job list
 **********************************************/

/* clearjob - Clear the entries in a job struct */
void 
clearjob(struct job_t *job) {
	job->pid = 0;
	job->jid = 0;
	job->state = UNDEF;
	job->cmdline[0] = '\0';
}

/* initjobs - Initialize the job list */
void 
initjobs(struct job_t *job_list) {
	int i;

	for (i = 0; i < MAXJOBS; i++)
		clearjob(&job_list[i]);
}

/* maxjid - Returns largest allocated job ID */
	int 
maxjid(struct job_t *job_list) 
{
	int i, max=0;

	for (i = 0; i < MAXJOBS; i++)
		if (job_list[i].jid > max)
			max = job_list[i].jid;
	return max;
}

/* addjob - Add a job to the job list */
	int 
addjob(struct job_t *job_list, pid_t pid, int state, char *cmdline) 
{
	int i;

	if (pid < 1)
		return 0;

	for (i = 0; i < MAXJOBS; i++) {
		if (job_list[i].pid == 0) {
			job_list[i].pid = pid;
			job_list[i].state = state;
			job_list[i].jid = nextjid++;
			if (nextjid > MAXJOBS)
				nextjid = 1;
			strcpy(job_list[i].cmdline, cmdline);
			if(verbose){
				printf("Added job [%d] %d %d %s\n", job_list[i].jid, job_list[i].pid, getpgid(job_list[i].pid), job_list[i].cmdline);
			}
			return 1;
		}
	}
	printf("Tried to create too many jobs\n");
	return 0;
}

/* deletejob - Delete a job whose PID=pid from the job list */
	int 
deletejob(struct job_t *job_list, pid_t pid) 
{
	int i;

	if (pid < 1)
		return 0;

	for (i = 0; i < MAXJOBS; i++) {
		if (job_list[i].pid == pid) {
			clearjob(&job_list[i]);
			nextjid = maxjid(job_list)+1;
			return 1;
		}
	}
	return 0;
}

/* fgpid - Return PID of current foreground job, 0 if no such job */
pid_t 
fgpid(struct job_t *job_list) {
	int i;

	for (i = 0; i < MAXJOBS; i++)
		if (job_list[i].state == FG)
			return job_list[i].pid;
	return 0;
}

/* getjobpid  - Find a job (by PID) on the job list */
struct job_t 
*getjobpid(struct job_t *job_list, pid_t pid) {
	int i;

	if (pid < 1)
		return NULL;
	for (i = 0; i < MAXJOBS; i++)
		if (job_list[i].pid == pid)
			return &job_list[i];
	return NULL;
}

/* getjobjid  - Find a job (by JID) on the job list */
struct job_t *getjobjid(struct job_t *job_list, int jid) 
{
	int i;

	if (jid < 1)
		return NULL;
	for (i = 0; i < MAXJOBS; i++)
		if (job_list[i].jid == jid)
			return &job_list[i];
	return NULL;
}

/* pid2jid - Map process ID to job ID */
	int 
pid2jid(pid_t pid) 
{
	int i;

	if (pid < 1)
		return 0;
	for (i = 0; i < MAXJOBS; i++)
		if (job_list[i].pid == pid) {
			return job_list[i].jid;
		}
	return 0;
}

/* listjobs - Print the job list */
	void 
listjobs(struct job_t *job_list, int output_fd) 
{
	int i;
	char buf[MAXLINE];

	for (i = 0; i < MAXJOBS; i++) {
		memset(buf, '\0', MAXLINE);
		if (job_list[i].pid != 0) {
			sprintf(buf, "[%d] (%d) ", job_list[i].jid, job_list[i].pid);
			if(write(output_fd, buf, strlen(buf)) < 0) {
				fprintf(stderr, "Error writing to output file\n");
				exit(1);
			}
			memset(buf, '\0', MAXLINE);
			switch (job_list[i].state) {
				case BG:
					sprintf(buf, "Running    ");
					break;
				case FG:
					sprintf(buf, "Foreground ");
					break;
				case ST:
					sprintf(buf, "Stopped    ");
					break;
				default:
					sprintf(buf, "listjobs: Internal error: job[%d].state=%d ",
							i, job_list[i].state);
			}
			if(write(output_fd, buf, strlen(buf)) < 0) {
				fprintf(stderr, "Error writing to output file\n");
				exit(1);
			}
			memset(buf, '\0', MAXLINE);
			sprintf(buf, "%s\n", job_list[i].cmdline);
			if(write(output_fd, buf, strlen(buf)) < 0) {
				fprintf(stderr, "Error writing to output file\n");
				exit(1);
			}
		}
	}
	if(output_fd != STDOUT_FILENO)
		close(output_fd);
}
/******************************
 * end job list helper routines
 ******************************/


/***********************
 * Other helper routines
 ***********************/

/*
 * usage - print a help message
 */
	void 
usage(void) 
{
	printf("Usage: shell [-hvp]\n");
	printf("   -h   print this message\n");
	printf("   -v   print additional diagnostic information\n");
	printf("   -p   do not emit a command prompt\n");
	exit(1);
}

/*
 * unix_error - unix-style error routine
 */
	void 
unix_error(char *msg)
{
	fprintf(stdout, "%s: %s\n", msg, strerror(errno));
	exit(1);
}

/*
 * app_error - application-style error routine
 */
	void 
app_error(char *msg)
{
	fprintf(stdout, "%s\n", msg);
	exit(1);
}

/*
 * Signal - wrapper for the sigaction function
 */
	handler_t 
*Signal(int signum, handler_t *handler) 
{
	struct sigaction action, old_action;

	action.sa_handler = handler;  
	sigemptyset(&action.sa_mask); /* block sigs of type being handled */
	action.sa_flags = SA_RESTART; /* restart syscalls if possible */

	if (sigaction(signum, &action, &old_action) < 0)
		unix_error("Signal error");
	return (old_action.sa_handler);
}

/*
 * sigquit_handler - The driver program can gracefully terminate the
 *    child shell by sending it a SIGQUIT signal.
 */
	void 
sigquit_handler(int sig) 
{
	printf("Terminating after receipt of SIGQUIT signal\n");
	exit(1);
}

