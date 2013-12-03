/**
 * @file vq.h
 * @brief This file encapsulates the common operations
 * related to queue, list, bit-set.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#ifndef _VQ_H
#define _VQ_H

/* double linked-list queue */
#define Q_INIT(head, tail)\
	do{\
		head = tail = 0;\
	}while(0)

/* add a node to the front of the
 * queue*/
#define Q_INJECT(head, tail, node)\
	do{\
		(node)->prev = 0;\
		if(!head){\
			tail = node;\
			(node)->next=0;\
		} else {\
			(node)->next = head;\
			(head)->prev = node;\
		}\
		head = node;\
	}while(0)

/* append a node to the tail of the queue*/
#define Q_PUSH(head, tail, node)\
	do{\
		(node)->next = 0;\
		if(!head){\
			head = (node);\
			(node)->prev =0;\
		} else {\
			(node)->prev = tail;\
			(tail)->next = (node);\
		}\
		tail = (node);\
	}while(0)

/* append a queue to the tail of a queue
 * */
#define Q_PUSH_ALL(dest_head, dest_tail, src_head,\
		src_tail)\
do{\
	if(src_head){\
		(src_tail)->next = 0;\
		if(!dest_head){\
			dest_head = src_head;\
			(src_head)->prev = 0;\
		} else {\
			(src_head)->prev = dest_tail;\
			(dest_tail)->next = src_head;\
		}\
		dest_tail = src_tail;\
	}\
}while(0)

/* eject the front to cur */
#define Q_EJECT(head, tail, cur)\
	do{\
		if(head){\
			assert(tail);\
			cur = head;\
			if(head == tail)\
				head = tail = 0;\
			else {\
				head = (cur)->next;\
				(head)->prev = 0;\
			}\
			(cur)->next = 0;\
		}\
	}while(0)

/* free a node from queue */
#define Q_DEL(head, tail, node)\
	do{\
		if(head){\
			if(head == node && tail == node){\
				head = tail = 0;\
			} else {\
				if(tail == node){\
					tail=(node)->prev;\
					(tail)->next = 0;\
				}else if(head == node){\
					head=(node)->next;\
					(head)->prev = 0;\
				}else {\
					(node)->prev->next =(node)->next;\
					(node)->next->prev =(node)->prev;\
				}\
			}\
		}\
		(node)->prev=(node)->next=0;\
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

#endif
