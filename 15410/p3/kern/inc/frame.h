/**
 * @file frame.h
 * @brief prototype of physical memory manipulation.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#ifndef _FRAME_H
#define _FRAME_H

int init_mod_frame();
int alloc_nframes(int cnt, void**);
void free_nframes(int cnt, void**);

#endif
