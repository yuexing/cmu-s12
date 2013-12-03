/**
 * @file ctx_switch.h
 * @brief prototype for the ctx_switch function.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */
/**
 * @brief pusha and store the esp to old_esp.
 * restore esp as new_esp and popa.
 */
void ctx_switch(void **old_esp, void *new_esp);
