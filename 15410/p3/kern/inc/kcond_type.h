/**
 * @file kcond_type.h
 * @brief Data structs for kernel level cond var.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#ifndef _KCOND_TYPE_H
#define _KCOND_TYPE_H

typedef struct {
	schedule_node_t *head;
	schedule_node_t *tail;
} kcond_t;

#endif
