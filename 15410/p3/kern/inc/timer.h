/**
 * @file timer.h
 * @brief declaration of timer-driver
 * @author Yue Xing(yuexing)
 * @bug No Found Bugs
 */

#ifndef _TIMER_H_
#define _TIMER_H_


typedef void tickback_t(uint32_t); 

void timer_init(void);

void install_timer_callback(tickback_t*);

uint32_t tick();

#endif 
