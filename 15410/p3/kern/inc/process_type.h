/**
 * @file process_type.h
 * @brief defination of the data structures for a process
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#ifndef _PROCESS_TYPE_H
#define _PROCESS_TYPE_H

struct process{
	
	/* page directory*/
	page_dir_t *dir;
	block_t dir_lock; 

	/*thread list,number,and lock*/
	pcb_node_t *thr_head;
	pcb_node_t *thr_tail;
	int thr_cnt;
	block_t thr_lock;

	/*exit status*/
	int status;

	/*the main thread*/
	uint32_t main_tid;

	/* parent process which creates me*/
	process_t *parent;

	/* number of child processes that have not
	 * been waited. The value should only be changed
	 * during fork and wait, and has nothing to do 
	 * with vanish */
	int unwaited_cnt;
	block_t unwaited_cnt_lock;

	/* running children list */
	process_t *running_head;
	process_t *running_tail;
	block_t running_lock;

	/* zombie children list */
	process_t *zombie_head;
	process_t *zombie_tail;
	block_t zombie_lock;
	kcond_t zombie_cv;

	/* sibling processes, this will help double ll */
	process_t *next;
	process_t *prev;
};
#endif
