/**
 * @file handler_type.h
 * @brief Data structures needed by
 * the handler.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug.
 */

#ifndef _HANDLER_TYPE_H
#define _HANDLER_TYPE_H

/**
 * @brief A smaller ureg which is 
 * used in mode switch.
 */
typedef struct {
	uint32_t eip;
	uint32_t cs;
	uint32_t eflags;	uint32_t esp;
	uint32_t ss;
}iret_reg_t;

typedef void (*handler_t)(ureg_t*);

#endif
