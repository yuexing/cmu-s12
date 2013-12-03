/** @file console.h
 *  @brief Function prototypes for the console driver.
 *
 *  This contains the prototypes and global variables for the console
 *  driver
 *
 *  @author Yue Xing(yuexing)
 *  @bug No known bugs.
 */

#ifndef _CONSOLE_H
#define _CONSOLE_H

int putbyte( char ch );

void putbytes(const char* s, int len);

int set_term_color(int color);

void get_term_color(int* color);

int set_cursor(int row, int col);

void get_cursor(int* row, int* col);

void hide_cursor();

void show_cursor();

void clear_console();

void draw_char(int row, int col, int ch, int color);

char get_char(int row, int col);

#endif /* _CONSOLE_H */
