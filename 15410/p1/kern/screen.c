/**
 * @file screen.c
 * @brief implementation of displaying all screens
 *
 * Each screen contains a title(Sudoku), a menu(New game, etc),
 * a content(a game, a time, a level selector, or other info), a 
 * status bar. The status bar is to give hint or feedback to user's
 * action.
 *
 * Almost apply set_cursor() and printf() to show a screen. For
 * the time, I use draw_char() instead as draw_char() will
 * not change the position of the cursor, which implies no competence
 * about cursor.
 *
 * Apply state machine to create only one while loop for the game,
 * which will save the stack:)
 *
 * @author Yue Xing
 * @bugs Not Found
 */

#include "inc/screen_internal.h"

/**
 * @brief set terminal color, show information screen,
 * and wait for command.
 */
void game_run(){
	/* set term color */
	set_term_color(TERM_COLOR);

   	/* show author */
	show_info_screen();

	/* wait for command */
	wait_cmd();
}

/** 
 * @brief Callback function, to be called by the timer interrupt handler
 * A tick means plus another 10ms,
 *
 **/

void tick_backup(unsigned int numTicks)
{
	if((++time_ten_msed % 10) == 0){
		time_ten_msed = 0;
		time_elapse = 1; /* control whether to print */
		time--; /* time to print on the screen */
	}
}

/**
 * @brief show info screen. The info screen displays 
 * the menu and the content. The menu contains 
 * instruction and new game. The content consists of
 * the game author's name and highest score.  
 *
 */
static void show_info_screen()
{
	clear_console();

	hide_cursor();

	show_title();

	/* show menu */
	char hint[] = {CMD_NEW, CMD_INSTR, CMD_BACK, CMD_END};

	if(!ALLOW_BACK){
		hint[2] = CMD_END;
	}
	show_menu(hint);

	/* center the content */
	int name_len = strlen(AUTHOR_NAME);
	int strt_x = CENTR_COL - name_len / 2;

	set_cursor(CENTR_ROW, strt_x);
	printf(AUTHOR_NAME);

	set_cursor(CENTR_ROW + 1, strt_x);
	printf(TITLE_HIGH, high_scr);

	show_status(STATUS_FIRST);
}

/**
 * @brief show instruction screen. The instruction
 * screen describes how to play the game. Its menu
 * contains info and new.
 *
 */
static void show_instr_screen()
{
	clear_console();

	hide_cursor();

	show_title();

	/* show menu */
	char hint[] = {CMD_NEW, CMD_INFO, CMD_BACK, CMD_END};

	if(!ALLOW_BACK){
		hint[2] = CMD_END;
	}
	show_menu(hint);

	char *instrr = *instr;

	/* instructions is an array which ends with CMD_END */
	int i = 0, y = CONTENT_ROW;
	for (; instrr != CMD_END; instrr = *(instr + i) ) {
		set_cursor(++y, CONTENT_COL);
		printf(instrr);
		i++;
	}

	show_status(STATUS_FIRST);
}

/**
 * @brief show game screen. The screen consists of
 * a menu, a grid, a timer and a level selecter.
 */
static void show_game_screen()
{
	clear_console();

	show_title();

	/* show menu */
	char hint[] = {CMD_NEW, CMD_INFO, CMD_INSTR, 
		CMD_SLV, CMD_RST, CMD_DONE, CMD_END};
	show_menu(hint);

	/* show timer 0.0 */
	ZERO_TIME;

	/* show cursor */
	show_cursor();


	if(ALLOW_BACK){
		/* back from other screens */
		memcpy(slvd_sudk, user_sudk, SU_GRID_AREA);
		show_grid(slvd_sudk, SUDK_RESTORE);
	} else {
		show_level_sel();
	}
}

/**
 * @brief show menus one by one on the MENU_ROW according
 * to the hint passed into the function.
 *
 * @param hint the hint is a collection of CMD_XX and ends with
 * CMD_END
 */
static void show_menu(char *hint)
{
	set_cursor(MENU_ROW, MENU_COL);
	for (; *hint != CMD_END; hint++){
		switch (*hint) {
			case CMD_NEW:
				printf(MENU_NEW);
				break;
			case CMD_INSTR:
				printf(MENU_INSTR);
				break;
			case CMD_INFO:
				printf(MENU_INFO);
				break;
			case CMD_RST:
				printf(MENU_RST);
				break;
			case CMD_DONE:
				printf(MENU_DONE);
				break;
			case CMD_SLV:
				printf(MENU_SLV);
				break;
			case CMD_BACK:
				printf(MENU_BACK);
				break;
		}
	}
}

/**
 * @brief reset time before a new game.
 * avoid interrupts when resetting, 
 * as the time_ten_msed and time_elapse are
 * shared with timer intterrupt handler.
 */
static void reset_time()
{
	disable_interrupts();
	time = time_limits[level];
	time_ten_msed = 0;
	time_elapse = 0;
	enable_interrupts();
}

/**
 * @brief show time at (TIME_DIGIT_COL, TIME_ROW).
 * 
 * Every 10 ticks(10 * 10ms = 100ms), the time_elapse
 * will be set to 1 by the timer interrupt handler.
 * When time_elapse is 1, the function is to print time.
 *
 * When time has decreased to less than 0, call check_game,
 * which will stop time.
 *
 * Apply limit and snprintf() to avoid overflow.
 */
static void show_time()
{
	int time_copy;

	disable_interrupts();
	if (!time_elapse){
		enable_interrupts();
		return;
	}
	time_copy = time; /* synchronize */
	time_elapse = 0;
	enable_interrupts(); 

	if(time_copy <= 0 || time_copy > (level + 1) * TM_BASE){
		/* as we may jump to other window, there
		 * are chances for less than 0 or overflow
		 * */
		ZERO_TIME;
		check_game();
	}

	if(IS_TIME_STOP || ALLOW_BACK){
		return;
	}

	int i = 0;
	char ch, tm_str[TITLE_TM_SIZE];
	snprintf(tm_str, TITLE_TM_SIZE, TITLE_TM, time_copy / 10, 
			time_copy % 10);
	for (; i < TITLE_TM_SIZE; i++){
		ch = *(tm_str + i);
		PUT_CHAR(TIME_ROW, TIME_DIGIT_COL + i, ch);
	}
}

/**
 * @brief show status at (STATUS_COL, STATUS_ROW).
 *
 * @param msg The message will display as status at the bottom, 
 * the message should be less than 80, or will be truncated.
 */
static void show_status(char *msg)
{
	/* clear */
	hide_line(STATUS_ROW);

	int i = 0, len = strlen(msg);
	char ch;
	for (; i < len && i < CONSOLE_WIDTH; i++){
		ch = *(msg + i);
		PUT_CHAR(STATUS_ROW, STATUS_COL + i, ch);
	}
}

/**
 * @brief clear the output of a row.
 *
 * @param row The row to be cleaned.
 */
static void hide_line(int row)
{
	int i = 0;
	for (; i < CONSOLE_WIDTH; i++){
		PUT_SPACE(row, i);
	}
}

/**
 * @brief show level selector at (LEVEL_COL, LEVEL_ROW).
 */
static void show_level_sel()
{
	/* clear */
	hide_line(LEVEL_ROW);


	/* print */
	set_cursor(LEVEL_ROW, LEVEL_COL);
	printf(LEVEL_SEL);	
}

/**
 * @brief show title in the middle
 */
static void show_title()
{
	/* center the content */
	int ttle_len = strlen(TITLE);
	int strt_x = CENTR_COL - ttle_len / 2;

	set_cursor(TITLE_ROW, strt_x);
	printf(TITLE);
}

/**
 * @brief draw 9 * 9 grid.
 * If we are loading a new grid, show_grid will backup 
 * the pointer of the grid into a char* as well as its
 * value into two matrixes. 
 * 
 * The user_sudk is to store user's value, while the 
 * backup_sudk is for the complete solver solve the 
 * sukudo.
 *
 * @param sudoku_str points to the sudoku to be loaded
 * @param type one of TYPE_NEW, and TYPE_OTHER
 */
static void show_grid(const char *sudoku_str, int type) 
{
	if(!sudoku_str)
		return;

	if (type == SUDK_NEW) {
		orig_sudk = sudoku_str;

		memcpy(backup_sudk, orig_sudk, SU_GRID_AREA);
		memcpy(user_sudk, orig_sudk, SU_GRID_AREA);
	}

	/* prepare to print */
	set_cursor(CONTENT_ROW, CONTENT_COL);

	int i = 0, j = 0, y = CONTENT_ROW, x = 0, 
		first_space = 0;
	char ch;

	for(; i < SU_GRID_SIZE; i++){

		if(i % 3 == 0){
			/* every 3 rows there is a line*/
			printf(GRID_ROW);
			set_cursor(++y, CONTENT_COL);
		}

		for(j = 0; j < SU_GRID_SIZE; j++){

			if (j % 3 == 0) {
				/* every 3 cols there is a line */
				printf(GRID_COL);
			}

			/* print on the screen */
			ch =  *(sudoku_str + i * SU_GRID_SIZE + j);
			if(type == SUDK_RESTORE){
				if(ch != get_val(i, j)){
					/* user's data with different color */
					x = CONTENT_COL + j*2 + j/3 + 1;
					draw_char(y, x, ch, CHAR_COLOR);		
					draw_char(y, x + 1, CHAR_SPACE, CHAR_COLOR);		
					set_cursor(y, x + 2);
				} else{
					printf("%c ", ch);
				}
			}else{
				printf("%c ", ch);
			}

			if (type == SUDK_NEW) {

				/* put logical cursor here */
				if (ch == ' ' && !first_space){
					sudk_row = i,
							 sudk_col = j;
					first_space = 1;
				}
			}
		}

		/* the last col line */
		printf(GRID_COL);

		/* next row */
		set_cursor(++y, CONTENT_COL);
	}

	/* the last row line*/
	printf(GRID_ROW);

	/* set to first cell waiting to fill */
	put_cursor();
}

/**
 * @brief wait for one of the commands to come about.
 * Some commands are only allowed in some state. And
 * some commands can change state.
 */
static void wait_cmd()
{
	unsigned char c;

	do {
		if(IS_IN_GAME){
			/* update time */
			show_time();
		}

		c = readchar();
		if (c == -1) {
			continue;
		}

		switch(c){
			case CMD_INSTR:
				if(IS_IN_GAME){
					status |= INSTR;
				} else {
					status = INSTR;
				}
				show_instr_screen();
				break;
			case CMD_INFO:
				if(IS_IN_GAME){
					status |= INFO;
				} else {
					status = INFO;
				}
				show_info_screen();
				break;
			case CMD_NEW:
				PREP_GAME;
				show_game_screen();
				break;
			case CMD_BACK:
				if(ALLOW_BACK){
					show_game_screen();
					status = status & ~INFO;
					status = status & ~INSTR;
				}
				break;
			case CMD_DONE:
				if(IS_GAME)
					check_game();
				break;
			case CMD_SLV:
				if(IS_GAME)
					get_solution();
				break;
			case CMD_RST:
				if(IS_GAME){
					memcpy(user_sudk, orig_sudk, SU_GRID_AREA);
					show_grid(orig_sudk, SUDK_OTHER);
				}
				break;
			case CMD_LEFT:
				if(IS_GAME)
					cursor_left();
				break;
			case CMD_UP:
				if(IS_GAME)
					cursor_up();
				break;
			case CMD_DOWN:
				if(IS_GAME)
					cursor_down();
				break;
			case CMD_RIGHT:
				if(IS_GAME)
					cursor_right();
				break;
			case CMD_DEL:
				if(IS_GAME)
					clear_val();
				break;
			case CMD_0:
			case CMD_1:
			case CMD_2:
			case CMD_3:
			case CMD_4:
			case CMD_5:
			case CMD_6:
			case CMD_7:
			case CMD_8:
			case CMD_9:
				if(IS_GAME_PREP){ 
					new_game(c);
				}else if(IS_GAME){
					put_val(c);	
				}
				break;
			case KHE_ARROW_UP:
				if(IS_GAME)
					cursor_up();
				break;
			case KHE_ARROW_LEFT:
				if(IS_GAME)
					cursor_left();
			case KHE_ARROW_RIGHT:
				if(IS_GAME)
					cursor_right();
			case KHE_ARROW_DOWN:
				if(IS_GAME)
					cursor_down();
		}

	}while(1);
}

/**
 * @brief set status for a new game with level c
 *
 * @param the level for the new game
 *
 * @return 1 if the new game is ready; otherwise, 0.
 */
static int new_game(char c)
{
	/* here do not care about synchronization */
	const char* skd = load_sudk(c, time);

	if (!skd) {
		/* DB has none for this level */
		show_status(ERR_NO_LVL);
		return 0;
	} else {
		level = c - CMD_0;
		hide_line(STATUS_ROW);
		show_grid(skd, SUDK_NEW);
		reset_time(c);

		START_TIME;
		START_GAME;

		return 1;
	}
}

/**
 * @brief check whether the sudoku has been solved
 * by using the user_sudk. Show status and wait for
 * farthur command.
 * If it has been solved, calculate the score.
 */
static void check_game()
{
	END_TIME;
	END_GAME;

	int scr = 0, time_limit = 0, time_copy;
	COPY_TIME(time_copy);

	if (is_solved(user_sudk)){
		/* add score here */
		scr = SCR_BASE * level;
		time_limit = TM_BASE * level;

		if (time_copy < time_limit /10){
			/* avoid too low */
			time_copy = time_limit / 10;
		}

		if (time_copy < time_limit /2){
			/* use more that a half */
			scr *= (time_copy / time_limit);
		}

		if (high_scr < scr){
			high_scr = scr;
		}

		show_status(STATUS_WIN);
	} else {
		show_status(STATUS_FAIL);
	}
}

/**
 * @brief get solution for the backup_sudk and load the solution
 * into slvd_sudk.
 *
 */
static void get_solution()
{
	END_TIME;
	END_GAME;

	int solved = 0;

	/* change status line */
	show_status(STATUS_SLV);
	/* solve and show */
	solved = solve_sudk(backup_sudk, slvd_sudk, level);
	show_grid(slvd_sudk, SUDK_OTHER);

	if(solved){
		show_status(TITLE_SLVD);
	} else {
		show_status(ERR_UNSLVD);
	}
}

/**
 * @brief move down cursor as a response to KHE_ARROW_UP
 */
static void cursor_up()
{
	if( sudk_row > 0){
		sudk_row -= 1;
		put_cursor();
	}
}

/**
 * @brief move down cursor as a response to KHE_ARROW_DOWN
 */
static void cursor_down()
{
	if( sudk_row < SU_GRID_SIZE - 1){
		sudk_row += 1;
		put_cursor();
	}
}

/**
 * @brief move down cursor as a response to KHE_ARROW_LFET
 */
static void cursor_left()
{
	if( sudk_col > 0){
		sudk_col -= 1;
		put_cursor();
	}
}

/**
 * @brief move down cursor as a response to KHE_ARROW_RIGHT
 */
static void cursor_right()
{
	if( sudk_col < SU_GRID_SIZE - 1){
		sudk_col += 1;
		put_cursor();
	}
}

/**
 * @brief return value of orig_sudk [row][col]
 *
 * @param row
 * @param col
 * @return char The value of orig_sudk[row][col]
 */
static char get_val(int row, int col)
{
	int offset = row * SU_GRID_SIZE + col;
	return *(orig_sudk + offset);
}

/**
 * @brief update (cursor_row, cursor_col) according 
 * to (sudk_row, sudk_col).
 */
static void put_cursor()
{
	/* calculate cursor */
	cursor_row = CURSOR_BASE_ROW 
		+ (sudk_row / 3 + 1) /* the row deliminate line */
		+ sudk_row;
	cursor_col = CURSOR_BASE_COL
		+ (sudk_col / 3 + 1) /* the col deliminate line */
		+ sudk_col * 2; /* every value take 2 cols now */

	set_cursor(cursor_row, cursor_col);
}

/**
 * @brief put the char at (cursor_row, cursor_col)
 */
static void put_val(char c) 
{
	if (get_val(sudk_row, sudk_col) == CHAR_SPACE){
		user_sudk[sudk_row][sudk_col] = c;
		draw_char(cursor_row, cursor_col, c, CHAR_COLOR);
	}
}

/**
 * @brief delete char only happen when there is an
 * input before cursor.
 */
static void clear_val()
{
	if (get_val(cursor_row, cursor_col) == CHAR_SPACE){
		user_sudk[sudk_row][sudk_col] = CHAR_SPACE;
		PUT_SPACE(cursor_row, cursor_col);
	}
}
