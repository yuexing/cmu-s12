/** @file console_driver.c 
 *
 *  @brief Implementation of console-driver library.
 *
 *  @author Yue Xing(yuexing)
 *  @bug Not Found.
 */
#include <asm.h>
#include <p1kern.h>
#include <stdio.h>
#include <simics.h>                 /* lprintf() */
#include <ctype.h>
#include <string.h>
#include "inc/defines.h"

static int is_cursor_hide = 1;	/* whether the cursor is hidden */
static int cursor_row = 0;		/* the row of the logical cursor */
static int cursor_col = 0;		/* the column of the logical cursor */
static int term_color = FGND_WHITE | BGND_BLACK; 	/* white foreground with black background */
static void scroll_screen(int);
static void set_crtc_cursor(int, int);

/**
 * @brief Scroll the screen with offset rows. When the offset is positive,
 * it implies scroll up. When the offset is negtive, it implies scroll 
 * down. In project1, it only allow positive value as I did not design 
 * a buffer.
 *
 * @param offset The offset to scroll up or down.
 */
static void scroll_screen(int offset) {
	void *offset_addr = (void *)(CONSOLE_MEM_BASE + offset * CONSOLE_WIDTH * 2);
	int size = (CONSOLE_HEIGHT - offset)* CONSOLE_WIDTH * 2;

	memmove((void *)CONSOLE_MEM_BASE, offset_addr, size);
}

/**
 * @brief Updates crtc's cursor, which will reflect on the screen and 
 * has nothing to do with the global cursor variable.
 * 
 * The input row and col should within the screen.
 *
 * @param row The row to put cursor at.
 * @param col The column to put cursor at.
 */
static void set_crtc_cursor(int row, int col) {

	short cursor_offset = (short)(CONSOLE_WIDTH * row + col);
	
	/* higher 1 byte */
	outb(CRTC_IDX_REG, CRTC_CURSOR_MSB_IDX);
	outb(CRTC_DATA_REG, cursor_offset >> HIGHER_SHORT_SHIFT);

	/* lower 1 byte */
	outb(CRTC_IDX_REG, CRTC_CURSOR_LSB_IDX);
	outb(CRTC_DATA_REG, cursor_offset);
}


/**
 * @brief Prints character ch at the current location of the cursor.
 * 
 * There are several cases related to the character:
 * <UL>
 * <LI>the cursor is always at next input.</LI>
 * <LI>'\n', the cursor should be moved to the first col of the next 
 * line. </LI>
 * <LI>'\r', the cursor should be moved to the first col of the current
 * line. </LI>
 * <LI>'\b', remove the last character. If the current cursor is the first
 * col, move it to the last col of the previous line.</LI>
 * <LI>Unprintable character, do not print.</LI>
 * <UL>
 *
 * @param ch The character to print on the screen.
 * @return The printed charater, '\n', '\b', or '\r'; otherwise, -1.
 */
int putbyte( char ch )
{
	if (ch == '\n') {
		set_cursor(cursor_row + 1, 0);
		return ch;
	} else if (ch == '\r') {
		set_cursor(cursor_row, 0);
		return ch;
	} else if (ch == '\b') {
		if (cursor_col == 0) {
			set_cursor(cursor_row - 1, CONSOLE_WIDTH - 1);
			draw_char(cursor_row - 1, CONSOLE_WIDTH - 1, 0, term_color);
		} else {
			set_cursor(cursor_row, cursor_col - 1);
			draw_char(cursor_row, cursor_col - 1, 0, term_color);
		}
		return ch;
	} else {
		if (isprint(ch)) {
			/* only print printable charater */
			draw_char(cursor_row, cursor_col, ch, term_color);
			set_cursor(cursor_row, cursor_col + 1);
			return ch;
		}
	}	
	/* unprintable character */
	return -1; 
}

/**
 * @brief Prints the character array s, starting at current location of 
 * the cursor. 
 * The function processes charater just like putbyte().
 *
 * @param s The character array to print on the screen.
 * @param len The length limit.
 */
void putbytes( const char *s, int len )
{
	const char *sch;
	int i = 0;
	for (; i < len; i++) {
		sch = s + i;

		putbyte(*sch);
	}
}

/**
 * @brief Changes the foreground and background color of future 
 * characters printed on the console.
 *
 * @param color The new color
 * @return 1 when successfully having changed color, otherwise, 0.
 */
int set_term_color( int color )
{
	if (color > 0x80)
		return -1;

	term_color = color;
	return 0;
}

/**
 * @brief Writes the current foreground and background color of 
 * characters printed on the console into the argumetn color.
 *
 * @param color The address to which the current color information 
 * will be written.
 */
void get_term_color( int *color )
{
	if (!color)
		return;
	*color = term_color;
}

/**
 * @brief Sets the cursor to the position (row, col). 
 * The <a href="video_conv">convention</a> is the same to draw_char.
 *
 * If the cursor is currently hidden, a call to set_cursor 
 * must not show the cursor.
 *
 * If the row and col exceeds the range, the function will scroll 
 * up the screen accordingly.
 *
 * @param row The row to set cursor to.
 * @param col The column to set cursor to.
 *
 * @return 1 when the function successfully executes, otherwise 0.
 */
int set_cursor( int row, int col )
{
	if(row < 0 || col < 0) {
		/* no effect to invalid input */
		return -1;
	}

	if(col >= CONSOLE_WIDTH) {
		col = 0;
		row = row + 1;
	}

	if(row >= CONSOLE_HEIGHT){
		/* scroll screen */
		scroll_screen(row - (CONSOLE_HEIGHT - 1));

		row = CONSOLE_HEIGHT - 1;
	}

	/* update global status */
	cursor_row = row;
	cursor_col = col;

	if (!is_cursor_hide) {
		/* update the cursor on screen only when the cursor is not hide*/
		set_crtc_cursor(row, col);
	}
	return 0;
}

/**
 * @brief Writes the current position of the cursor into the arguments
 * row and col.
 *
 * @param row The address to which the current cursor row will be written.
 * @param col The address to which the current cursor column 
 * will be written.
 */
void get_cursor( int *row, int *col )
{
	if (!row || !col)
		return;
	*row = cursor_row;
	*col = cursor_col;
}

/**
 * @brief Causes the cursor to become invisible, without changing 
 * its location  as global cursor variables. Subsequent calls to 
 * putbyte or putbytes must not cause the cursor to become visible 
 * again. If the cursor is already invisible, the function has no effect.
 *
 */
void hide_cursor()
{
	if(is_cursor_hide)
		return;

	int row = CONSOLE_WIDTH;
	int col = CONSOLE_HEIGHT;

	set_crtc_cursor(row, col);
	is_cursor_hide = 1;
}

/**
 * @brief Causes the cursor to become visible without changing its location
 * stored in the global cursor variables. If the cursor is already visible,
 * the function has no effect.
 */
void show_cursor()
{
	if(!is_cursor_hide)
		return;

	int row = cursor_row;
	int col = cursor_col;

	set_crtc_cursor(row, col);
	is_cursor_hide = 0;
}

/**
 * @brief Clears the entire console and resets the cursor 
 * to the home position.
 * If the cursor is currently hidden it should stay hidden.
 */
void clear_console()
{
	bzero((void *)CONSOLE_MEM_BASE,
			CONSOLE_HEIGHT * CONSOLE_WIDTH * 2);

	set_cursor(0, 0);
}

/**
 * @brief Prints character ch with the specified color at 
 * position(row, col). If any argument
 * is invalid, the function has no effect.
 *
 * The <a name="video_conv">convention</a> is that we will 
 * number from 0, thus if you want to draw on the third charactor
 * of the second line, you have to call draw_char with (1,2).
 *
 * If the row or the col exceeds the screen, draw_char will 
 * scroll up accordingly.
 *
 * @param row The row to draw the charactor.
 * @param row The column to draw the charactor.
 * @param ch The character to draw.
 * @param color The color with which the character will be drawn.
 */
void draw_char( int row, int col, int ch, int color )
{
	if(row < 0 || col < 0 || !isprint(ch)) {
		/* can not draw due to invalid input */
		return;
	}

	if(col >= CONSOLE_WIDTH) {
		col = 0;
		row = row + 1;
	}

	if(row >= CONSOLE_HEIGHT){
		/* scroll screen */
		scroll_screen(row - (CONSOLE_HEIGHT - 1));

		row = CONSOLE_HEIGHT - 1;
	}

	unsigned long char_pos = CONSOLE_MEM_BASE 
		+ 2 * (row * CONSOLE_WIDTH + col);
	*(char *)char_pos = ch;
	*(char *)(char_pos + 1) = color;
}

/**
 * @brief Returns the character displayed at position (row, col).
 *
 * The <a href="video_conv">convention</a> is the same with draw_char.
 * 
 * @param row Row of the characoter. 
 * @param col Column of the character.
 *
 * @return The character at (row, col).
 */
char get_char( int row, int col )
{
	if(row < 0 || row >= CONSOLE_HEIGHT 
			|| col < 0 || col >= CONSOLE_WIDTH) {
		return 0;
	}

	unsigned long char_pos = CONSOLE_MEM_BASE 
		+ 2 * (row * CONSOLE_WIDTH + col);
	return *(char *)char_pos;
}

