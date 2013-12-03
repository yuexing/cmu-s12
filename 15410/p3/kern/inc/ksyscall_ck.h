/**
 * @file ksyscall_ck.h
 * @brief prototypes for check args passed
 * into the syscall.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#ifndef _KSYSCALL_CK_H
#define _KSYSCALL_CK_H

int ck_addr(void *addr, uint32_t len, uint32_t w);

int ck_str_arr(char **arr);

int ck_str(char *str);

#endif
