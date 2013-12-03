/**
 * @file kb.h
 * @brief declaration of kb-driver
 * @author Yue Xing(yuexing)
 * @bug No Found Bugs
 */

#ifndef _KB_H
#define _KB_H

void kb_init(void);

int copy_line(int len, char *buf);
char readchar();
void wait_line();
void wait_char();

#endif
