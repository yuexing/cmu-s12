/**
 * @file vq.h
 * @brief This file encapsulates 
 * the common operations
 * related to ll queue.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#ifndef _VQ_H
#define _VQ_H

/**
 * @brief This is an element of a 
 * queue, used for storing a 
 * waiting thread.
 */
typedef struct node{
	int tid;
	/*for deschedule*/
	int reject;
	spinlock_t lock;
	struct node* next;
} node_t;


/* linked-list queue */
#define Q_INIT(head, tail)\
	do{\
		head = tail = 0;\
	}while(0)

/* add a node to the front of the
 * queue*/
#define Q_INJECT(head, tail, node)\
	do{\
		if(!head){\
			tail = node;\
			(node)->next=0;\
		} else {\
			(node)->next = head;\
		}\
		head = node;\
	}while(0)

/* append a node to the tail of the queue*/
#define Q_PUSH(head, tail, node)\
	do{\
		(node)->next = 0;\
		if(!head){\
			head = (node);\
		} else {\
			(tail)->next = (node);\
		}\
		tail = (node);\
	}while(0)

/* eject the front to cur */
#define Q_EJECT(head, tail, cur)\
	do{\
		if(head){\
			cur = head;\
			if(head == tail)\
				head = tail = 0;\
			else {\
				head = (cur)->next;\
			}\
			(cur)->next = 0;\
		}\
	}while(0)

/* find a node from the queue which has attr with value*/
#define Q_FIND(head, tail, node, attr, value)\
	do{\
		for(node=head;node;node=node->next){\
			if(node->attr == (value)){\
				break;\
			}\
		}\
	}while(0);

#define Q_EMPTY(head)	((head)==0)

#define Q_DEL(head, tail, prev, target)\
	do {\
	if(target==head\
				&& target==tail){\
			head = tail = 0;\
		} else if (target==head){\
			head = target->next;\
		} else if (target==tail){\
			tail = prev;\
			prev->next = 0;\
		} else {\
			prev->next = target->next;\
		}\
		target->next = 0;\
	} while(0);
#endif
