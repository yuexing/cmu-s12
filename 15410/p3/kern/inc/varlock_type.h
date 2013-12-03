/**
 * @file varlock_type.h
 * @brief Data structure for the varlock.
 *
 * A varlock applies value assignment instead of
 * xchg. Thus there is a limited usage for varlock.
 *
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */
#ifndef _VARLOCK_TYPE_H
#define _VARLOCK_TYPE_H

typedef struct {
	uint32_t lock;	
} varlock_t;

#endif
