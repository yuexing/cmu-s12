/**
 * @file screen_internal.h
 * @brief This file should be included in screen
 * module itself. Others which want to call screen
 * module, should include screen.h.
 */
#ifndef _SCREEN_INTERNAL_H
#define _SCREEN_INTERNAL_H

#include <sudoku.h>
#include <video_defines.h>
#include <stdio.h>
#include <p1kern.h>
#include <string.h>
#include <asm.h>
#include <keyhelp.h>
#include "inc/sudk.h"
#include "inc/defines.h"

#define TERM_COLOR		FGND_WHITE | BGND_BLACK

/* the type of sudoku matrix, this decide whether there
 * is a need to backup it for reset or solve*/
#define SUDK_NEW		0
#define SUDK_RESTORE 	1
#define SUDK_OTHER		2

/* the position to draw on the screen */
#define CENTR_ROW			10
#define CENTR_COL			40
#define TITLE_ROW			0
#define MENU_ROW			1
#define MENU_COL			1
#define CONTENT_ROW			5
#define CONTENT_COL			5
#define TIME_ROW			CENTR_ROW
#define TIME_COL			60
#define TIME_DIGIT_COL		66
#define LEVEL_ROW			78
#define LEVEL_COL			CONTENT_COL
#define STATUS_ROW			79
#define STATUS_COL			CONTENT_COL
#define CURSOR_BASE_ROW		CONTENT_ROW	
#define CURSOR_BASE_COL		CONTENT_COL

/* the property of char and cursor */
#define CHAR_COLOR			FGND_GREEN | BGND_BLACK

/* strings drawn on the screen */
#define AUTHOR_NAME		"Author's Name: Yue Xing(yuexing@andrew)"
#define TITLE			"Sudoku"
#define MENU_INSTR		"(i)Instrction  "
#define MENU_NEW		"(n)New Game  "
#define MENU_SLV		"(x)Solve  "
#define MENU_RST		"(r)Reset  "
#define MENU_INFO		"(t)Info  "
#define MENU_DONE		"(d)Done  "
#define MENU_BACK		"(b)BACK To Game "
#define TITLE_TM_STRT	"Time: 0.0"
#define TITLE_TM		"%4d.%d"
#define TITLE_TM_SIZE	6
#define TITLE_HIGH		"Highest Score: %d"
#define LEVEL_SEL		"Enter difficulty (0-9, 0 is easy): "
#define GRID_ROW		"+------+------+------+"
#define GRID_COL		"|"
#define TITLE_SLVD		"This is our solution:)"
#define ERR_UNSLVD		"Sorry, can not solve:("
#define ERR_NO_LVL		"There is none for this level, please select another level."
#define ERR_CMD			"Error command"
#define	STATUS_FIRST	"Welcome to Sokudu!"
#define STATUS_WIN		"Congratulations! You have solved the puzzle!"
#define STATUS_FAIL		"What's a pity! You did not solve it!"
#define STATUS_SLV		"Solving...."

/* status of the game */
#define GAME_PREP		0x1 /* type "new"*/
#define GAME			0x2 /* after level selection*/
#define GAME_OVER		0x10
#define INFO			0x4
#define INSTR			0x8
#define ALLOW_BACK		(status==0x6 || status==0xA || status==0xE)
#define IS_GAME_PREP	status==GAME_PREP
#define IS_GAME			status==GAME
#define IS_IN_GAME 		((status & 0x2) == 0x2)
#define PREP_GAME		status=GAME_PREP
#define START_GAME		status=GAME 
#define END_GAME		status=GAME_OVER
#define START_TIME		is_time_run = 1
#define END_TIME		is_time_run = 0	
#define IS_TIME_STOP	is_time_run==0

/* commands */
#define CMD_NEW			'n'
#define CMD_BACK		'b'
#define CMD_INFO		't'
#define CMD_INSTR		'i'
#define CMD_RST			'r'
#define CMD_SLV			'x'
#define CMD_DONE		'd'
#define CMD_UP			'j'
#define CMD_DOWN		'k'
#define CMD_LEFT		'h'
#define CMD_RIGHT		'l'
#define CMD_1			'1'
#define CMD_2			'2'
#define CMD_3			'3'
#define CMD_4			'4'
#define CMD_5			'5'
#define CMD_6			'6'
#define CMD_7			'7'
#define CMD_8			'8'
#define CMD_9			'9'
#define CMD_0			'0'
#define CMD_END			0x0
#define CMD_DEL			'\b'

/* the base values */
#define SCR_BASE		10
#define TM_BASE			3000 /* in 100ms, thus 5 min */

#define PUT_SPACE(r, c)		draw_char(r, c, CHAR_SPACE, TERM_COLOR)
#define PUT_CHAR(r, c, ch)	draw_char(r, c, ch, TERM_COLOR)
#define COPY_TIME(t)		disable_interrupts();t=time;enable_interrupts()
#define ZERO_TIME		    set_cursor(TIME_ROW, TIME_COL);printf(TITLE_TM_STRT)

static void show_game_screen();
static void show_instr_screen();
static void show_info_screen();
static void show_title();
static void show_menu(char *menu_str);
static void show_grid(const char *sudoku_str, int type);
static void show_time();
static void show_level_sel();
static void hide_line();
static void show_status(char *);
static void reset_time();
static char get_val(int row, int col);
static void put_val(char c);
static void clear_val();
static void cursor_right();
static void cursor_left();
static void cursor_down();
static void cursor_up();
static void put_cursor();
static void check_game();
static int new_game(char c);
static void get_solution();
static void wait_cmd();

static char *instr[] = {"1. View highest score in Info by typing t;",
	  "2. For more instruction, type i;",
	  "3. Sudoku is to fill the grid so that every row/col/box contains 1 to 9;",
	  "4. You can use h(Left), j(Up), k(Down), l(Right), CTRL+H(DEL);",
	  "5. The game stops when time is due or you type d(Done)/x(Solve);",
	  "6. The score is based on the time you spent solving the puzzle;",
	  "7. If you spend no more than half of the time limit, you will earn full points; else, the more time you spend, the less you earn;",
		   CMD_END};

static int high_scr = 0,
		   cursor_row = 0,
		   cursor_col = 0,
		   sudk_row = 0,
		   sudk_col = 0;

int time_elapse = 0; /* a signal from timer driver
								 that 100ms has elapsed*/

int time_ten_msed = 0; /* the number of 10ms, it increases to 10
								   and then will be reset to 0 */

static int time = 0; /* time with unit of 100ms,
			 the time is updated in game module,
		 but displayed in screen module.*/

static int level = 0; /* The current game level. */

static char status = 0;/* The status of the game */ 

static int is_time_run = 0; /* update time or not. This state is 1 after 
selecting a level and before typing 'solve', 'done' or time due.*/

static const char *orig_sudk = 0x0; /* this is used for reset */
static char user_sudk[SU_GRID_SIZE][SU_GRID_SIZE]; /* for user's input */
static char backup_sudk[SU_GRID_SIZE][SU_GRID_SIZE]; /* used for solve */
/* the time limit for each level in the unit of 100ms */
static char slvd_sudk[SU_GRID_AREA];
static int time_limits[] = {TM_BASE, TM_BASE * 2, 
	TM_BASE * 3, TM_BASE * 4, TM_BASE * 5, 
	TM_BASE * 6, TM_BASE * 7, TM_BASE * 8, 
	TM_BASE * 9, TM_BASE * 10};

#endif /* _SCREEN_INTERNAL_H */
