/** @file console.c 
 *
 *  @brief A simplified implementation 
 *  for P3.
 *
 *  @author Yue Xing(yuexing)
 *  @bug Not Found.
 */
#include <kernel.h>

/* get the addr mapping memory for (r,l)*/
#define GET_MEM(r,l)((void*)(CONSOLE_MEM_BASE)+(CONSOLE_WIDTH*(r)+(l))*2)

/* get the offset on the screen for (r,l)*/
#define GET_SCR_OFFSET(r,l)	(CONSOLE_WIDTH*(r)+(l))

#define ASSERT_POS(r,l)\
	do{\
	assert(row >= 0);\
	assert(row < CONSOLE_HEIGHT);\
	assert(col >= 0);\
	assert(col < CONSOLE_WIDTH);\
	}while(0)


static int is_cursor_hide = 0; /* whether the cursor is hidden */
static int cursor_row = 0; /* the row of the logical cursor */
static int cursor_col = 0; /* the column of the logical cursor */
static int term_color = FGND_WHITE | BGND_BLACK; /* white foreground with black background */
static void set_scr_cursor(int, int);
static void scroll_up(int);
static void set_crtc_cursor(int, int);
static int ck_pos(int row, int col);

/**
 * @brief Scroll up the screen by 1 line.
 *
 * @param offset The offset to scroll up or down.
 * @pre for kernel in P3, support 1 is ok.
 */
static void scroll_up(int offset) {
	assert(offset == 1);
	// memmov
	int size=2*((CONSOLE_HEIGHT-1)*CONSOLE_WIDTH);
	memmove(GET_MEM(0,0),GET_MEM(1,0),size);

	// clear the last line
	char *start = GET_MEM(CONSOLE_HEIGHT-1,0);
	int i = 0;
	for(; i < CONSOLE_WIDTH; i++){
		start[i * 2] = ' ';	
	}
}

/**
 * @brief Updates crtc's cursor, and reflect on the 
 * screen.
 * 
 * The input row and col should within the screen.
 *
 * @param row The row to put cursor at.
 * @param col The column to put cursor at.
 */
static void set_crtc_cursor(int row, int col) {
	short cursor_offset = (short) GET_SCR_OFFSET
		(row, col);

	/* higher 1 byte */
	outb(CRTC_IDX_REG, CRTC_CURSOR_MSB_IDX);
	outb(CRTC_DATA_REG, cursor_offset >> 8);

	/* lower 1 byte */
	outb(CRTC_IDX_REG, CRTC_CURSOR_LSB_IDX);
	outb(CRTC_DATA_REG, cursor_offset);
}

/**
 * @brief Prints character ch at the current location of the cursor.
 * 
 * @param ch The character to print on the screen.
 * @return The printed charater, '\n', '\b', or '\r'; otherwise, -1.
 */
int putbyte(char ch) {
	switch (ch) {
		case '\n':
			set_scr_cursor(cursor_row+1, 0);
			return ch;
		case '\r':
			set_scr_cursor(cursor_row, 0);
			return ch;
		case '\b':
			cursor_col=(--cursor_col<0?0:cursor_col);
			set_scr_cursor(cursor_row, cursor_col);
			draw_char(cursor_row, cursor_col,' ', term_color);
			return ch;
		default:
			if (isprint(ch)) {
				/* only print printable charater */
				draw_char(cursor_row,cursor_col,ch,term_color);
				set_scr_cursor(cursor_row,cursor_col+1);
				return ch;
			} else {
				/* unprintable char */
				return -1;
			}
	}
}

/**
 * @brief Prints the character array s, starting at current 
 * location of the cursor. 
 * The function processes charater just like putbyte().
 *
 * @param s The character array to print on the screen.
 * @param len The length limit.
 */
void putbytes(const char *s, int len) {
	int i = 0;
	while (i < len) {
		putbyte(s[i++]);
	}
}

/**
 * @brief Changes the foreground and background color of future 
 * characters printed on the console.
 *
 * @param color The new color
 * @return 1 when successfully having changed color, otherwise, 0.
 */
int set_term_color(int color) {
	if (color >> 8)
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
void get_term_color(int *color) {
	if (!color)
		return;
	*color = term_color;
}

/**
 * @brief Sets the cursor to the position (row, col). 
 *
 * @param row The row to set cursor to.
 * @param col The column to set cursor to.
 */
int set_cursor(int row, int col)
{
	if(ck_pos(row,col)==ERROR)
		return ERROR;

	cursor_row = row;
	cursor_col = col;

	if (!is_cursor_hide) {
		set_crtc_cursor(row, col);
	}
	return SUCCESS;
}

/**
 * @brief Sets the cursor to the position (row, col). 
 *
 * @param row The row to set cursor to.
 * @param col The column to set cursor to.
 */
static void set_scr_cursor(int row, int col) {
	if (col >= CONSOLE_WIDTH) {
		col = 0;
		row = row + 1;
	}

	if (row >= CONSOLE_HEIGHT) {
		scroll_up(row - (CONSOLE_HEIGHT - 1));
		row = CONSOLE_HEIGHT - 1;
	}

	/* update global status */
	cursor_row = row;
	cursor_col = col;

	if (!is_cursor_hide) {
		set_crtc_cursor(row, col);
	}
}

/**
 * @brief Writes the current position of the cursor into the arguments
 * row and col.
 *
 * @param row The address to which the current cursor row will be written.
 * @param col The address to which the current cursor column 
 * will be written.
 */
void get_cursor(int *row, int *col) {
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
 */
void hide_cursor() {
	if (is_cursor_hide)
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
void show_cursor() {
	if (!is_cursor_hide)
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
 *
 * This method is suggested to be called the first time
 * the system start using the screen.
 */
void clear_console() {
	char* start = (char*)CONSOLE_MEM_BASE;
	char* limit = GET_MEM(CONSOLE_HEIGHT-1, CONSOLE_WIDTH-1);

	while (start < limit) {
		start[0] = ' ';
		start += 2;
	}

	set_scr_cursor(0, 0);
}

/**
 * @brief Prints character ch with the specified color at 
 * position(row, col). If any argument is invalid, the 
 * function has no effect.
 *
 * @param row The row to draw the character.
 * @param row The column to draw the character.
 * @param ch The character to draw.
 * @param color The color with which the character will be drawn.
 */
void draw_char(int row, int col, int ch, int color) {
	ASSERT_POS(row, col);

	char *char_pos = GET_MEM(row, col);
	char_pos[0] = ch;
	char_pos[1] = color;
}

/**
 * @brief Returns the character displayed at position (row, col).
 * 
 * @param row Row of the character. 
 * @param col Column of the character.
 *
 * @return The character at (row, col).
 */
char get_char(int row, int col) {
	if(ck_pos(row,col)==ERROR)
		return ERROR;

	char *char_pos = GET_MEM(row, col);
	return char_pos[0];
}

static int ck_pos(int row, int col)
{
	if (row < 0 || row >= CONSOLE_HEIGHT
			|| col < 0 || col >= CONSOLE_WIDTH) {
		return ERROR;
	}
	return SUCCESS;
}
