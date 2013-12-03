/**
 * @file skd.c
 * @brief sodoku module. Include a complete solver,
 * and a sudoku loader and checker. The solve can solve
 * all the sudoku in DB in user mode as I apply 2 level
 * recursion. Since we are in kernel mode, it is hard 
 * for it to solve 9th level and some 8th level. 
 *
 * @author Yue Xing(yuexing)
 * @bugs 
 * Not going well with 9th, and some 8th.
 * Thus show "can not solve to user" :(
 */
#include <string.h>
#include <stdio.h>
#include <sudoku.h>
#include <sudokudb.h>
#include <mt19937int.h>
#include "inc/defines.h"


#define DB_UNIT_SIZE	(SU_GRID_AREA + 1)
#define SUDK_BASE			'1'
#define SUDK_NONE			'0'
#define CHECK_ROW			0
#define CHECK_COL			1
#define CHECK_BOX			2
#define SURE_ITERATIONS		50
#define UNSURE_ITERATIONS	10
#define GUESS_COUNT			1

typedef char needed_t[SU_GRID_SIZE][SU_GRID_SIZE][SU_GRID_SIZE];

static int is_solved_type(sudoku_t skd, int fix_index, int type);
static char get_val(sudoku_t skd, int fix_index, int var_index, int type);
static int square_to_col(int square, int idx); 
static int square_to_row(int square, int idx);
static int coord_to_idx(int row, int col);
static int coord_to_square(int row, int col); 
static void reduce_need(needed_t need, int r, int c, int i);
static void eliminate(needed_t need, int r, int c, int i);
static void	put_val(sudoku_t sudoku, needed_t need, 
		int r, int c, int i);
static void try_place(sudoku_t sudoku, needed_t need, 
		int r, int c, int i);
static int complete_solve(sudoku_t sudoku, int level);
static int solve_known (sudoku_t sudoku, needed_t need);
static int solve_unknown(sudoku_t sudoku, needed_t need, int count, int level);
static void disjoint(needed_t need, int r, int c);
static void get_str(needed_t need, int r, int c, char *str);


/**
 * @brief solve sudoku with complete solver
 *
 * @param skd the sudoku to solve
 * @param slvd	the solved skd will be put into slvd
 */
int solve_sudk(sudoku_t skd, char *slvd, int level)
{
	int done = complete_solve(skd, level);
	memcpy(slvd, skd, SU_GRID_AREA);
	return done;
}

/**
 * @brief Finds a sokudu from database. It starts with
 * a random number, <code>start = (start + 1) % db_size</code>
 * and finds the first sokudu which matches the given level. 
 *
 * The random number is generated based on the base passed in,
 * 
 * It will never return null if the database is proper.
 *
 * @param level the level according to which return a sokudu
 * @param base based on which we generate a random number
 * @return points to the sokudu which matches level
 */
const char *load_sudk(char level, unsigned int base)
{
	sgenrand(base);
	unsigned long rand = genrand();
	int rand_i = (int)(rand % MAX_SUDOKUS);
	int i = (rand_i + 1) % MAX_SUDOKUS;
	const char *pc;
	
	for (; i < MAX_SUDOKUS; i = (i+1) % MAX_SUDOKUS){
		pc = sudokudb + i * DB_UNIT_SIZE;
		if (*pc == level) {
			return pc + 1;
		}

		if (i == rand_i) {
			/* prevent infinite loop */
			return NULL;
		}
	}
	return NULL;
}

/**
 * @brief Checks whether a given sokudu has been solved.
 *
 * @param skd The sokudu waiting to be decided.
 * @return 1 if the given sokudu has been solved, 0, otherwise.
 */
int is_solved(sudoku_t skd)
{
	int i = 0;
	for (; i < SU_GRID_SIZE; i++){
		if (!is_solved_type(skd, i, CHECK_ROW) 
				|| !is_solved_type(skd, i, CHECK_COL)
				|| !is_solved_type(skd, i, CHECK_BOX)){
			return 0;
		}
	}
	return 1;
}

/**
 * @brief test whether a row/col/box has been solved. 
 * @param skd The sokudu waiting to be decided.
 * @param fix_index Indicates which row/col/box to check.
 * @param type One of CHECK_ROW, CHECK_COL, CHECK_BOX
 *
 * @return 1 if solved, otherwise 0.
 */
static int is_solved_type(sudoku_t skd, int fix_index, int type) 
{
	int i = 0;
	char val;
	int mask = 0;
	for (; i < SU_GRID_SIZE; i++){
		val = get_val(skd, fix_index, i, type);
		if (val == CHAR_SPACE)
			/* not filled cell */
			return 0;
		if (((0x1 << (val - '0')) && mask) != 0)
			/* duplicate cell */
			return 0;
		else
			mask = mask || (0x1 << (val - '0'));
	}

	return 1;
}

/**
 * @brief Return the value in the cell of (fix_index, var_index)
 * according to the type.
 *
 * @param skd The sokudu from which to get a number.
 * @param fix_index Indicates the row/col/box the function uses
 * to get number .
 * @param var_index Indicates the cell in the row/col/box the
 * function uses to get number.
 * @param type One of CHECK_ROW, CHECK_COL, CHECK_BOX
 *
 * @return The value of the cell.
 */
static char get_val(sudoku_t skd, int fix_index, int var_index, int type)
{
	int row, col;
	switch (type) {
		case CHECK_ROW:
			return skd[fix_index][var_index];
		case CHECK_COL:
			return skd[var_index][fix_index];
		case CHECK_BOX:
			row = square_to_row(fix_index, var_index);
			col = square_to_col(fix_index, var_index);
			return skd[row][col];
	}

	return 0;
}

/**
 * @brief a complete sudoku_solver
 *
 * @param sudoku The sudoku to solve
 * @param level The play level
 */
static int complete_solve(sudoku_t sudoku, int level)
{
	/* generate a need first */
	needed_t need;
	int r = 0, c = 0, i = 0, val = -1;
	char ch;
	for(r = 0; r < SU_GRID_SIZE; r++) {
		for(c = 0; c < SU_GRID_SIZE; c++){

			val = -1;
			ch = sudoku[r][c];
			if (ch != CHAR_SPACE) {
				/* there is a value !*/
				val = ch - SUDK_BASE;
			}

			for(i = 0; i < SU_GRID_SIZE; i++){
				if (val != -1){
					need[r][c][i] = SUDK_NONE;
				} else {
					/* put 123456789 */
					need[r][c][i] = SUDK_BASE + i;
				}
			}
			
		}
	}

	/* reduce need */
	for(r = 0; r < SU_GRID_SIZE; r++){
		for(c = 0; c < SU_GRID_SIZE; c++){
			ch = sudoku[r][c];

			/* reduce need as the value has been decided */
			if(ch != CHAR_SPACE){
				reduce_need(need, r, c, ch - SUDK_BASE);
			} 
		}
	}

	/* solve with certainty */
	for(i = 0; i < SURE_ITERATIONS; i++){
		if(solve_known(sudoku, need)){
			return 1;
		}
	}

	/* solve with uncertainty */
	if(solve_unknown(sudoku, need, GUESS_COUNT, level)){
		return 1;
	}

	return 0;
}

/**
 * @brief solve with uncertainty
 *
 * @param sudoku the sudoku wait to be solved
 * @param need  the matrix representing the need of a cell
 * @param level used to control recursion
 */
static int solve_unknown(sudoku_t sudoku, needed_t need, int count, int level)
{
	if(level == 0) 
		/* can not be solved*/
		return 0;

	int k, r, c, i;
	needed_t need_unsure;
	sudoku_t sudoku_unsure;
	for(r = 0; r < SU_GRID_SIZE; r++){
		for(c = 0; c < SU_GRID_SIZE; c++){
			if(sudoku[r][c] == CHAR_SPACE){
				for(i = 0; i < SU_GRID_SIZE; i++){
					if(need[r][c][i] == SUDK_NONE)
						continue;

					/* try this i */
					memcpy(sudoku_unsure, sudoku, SU_GRID_AREA);
					memcpy(need_unsure, need, SU_GRID_AREA * SU_GRID_SIZE);
					put_val(sudoku_unsure, need_unsure, r, c, i);
					/* with one certain value to see whether the sudoku can be solved*/
					for(k = 0; k < UNSURE_ITERATIONS; k++){
						if(solve_known(sudoku_unsure, need_unsure)){
							memcpy(sudoku, sudoku_unsure, SU_GRID_AREA);
							return 1;
						}
					}

					if(level < 8 
							&&solve_unknown(sudoku_unsure, 
								need_unsure, 
								count--, 
								level)){
						memcpy(sudoku, sudoku_unsure, 
								SU_GRID_AREA);
						return 1;
					} /* NOTE: if I am processing level 9, 
						 I will suffer an overflow */
				} 
			}
		}
	}

	return 0;
}

/**
 * @brief solve with certainty
 *
 * @param sudoku the sudoku wait to be solved
 * @param need  the matrix representing the need of a cell
 */
static int solve_known(sudoku_t sudoku, needed_t need)
{
	int r, c, i;
	int done = 1;
	
	/* eliminate probability */
	for(r = 0; r < SU_GRID_SIZE; r++){
		for(c = 0; c < SU_GRID_SIZE; c++){
			if(sudoku[r][c] != CHAR_SPACE)
				continue;

			/* apply disjoint-subset */
			disjoint(need, r, c);

			for(i =0; i < SU_GRID_SIZE; i++){

				if(need[r][c][i] == SUDK_BASE + i){
					/* apply single-cell, and single-box */
					eliminate(need, r, c, i);
				}
			}
		}
	}

	/* place */
	for(r = 0; r < SU_GRID_SIZE; r++){
		for(c = 0; c < SU_GRID_SIZE; c++){
			for(i =0; i < SU_GRID_SIZE; i++){

				if(sudoku[r][c] == CHAR_SPACE 
						&& need[r][c][i] == SUDK_BASE + i){
					try_place(sudoku, need, r, c, i);
				}

				/* if it is still not filled */
				if(sudoku[r][c] == CHAR_SPACE){
					done = 0;
				}
			}
		}
	}
	return done;
}

/**
 * @brief try to place SUDK_BASE + i at (r, c)
 *
 * @param sudoku the sudoku wait to be solved
 * @param need  the matrix representing the need of a cell
 * @param r 	the row
 * @param c		the col
 * @param i		can indicate the value to put
 */
static void try_place(sudoku_t sudoku, needed_t need, 
		int r, int c, int i)
{
	int box = coord_to_square(r, c);
	int j, row, col;

	int cellneed = 0, rowneed = 0, colneed = 0, boxneed = 0;

	for(j = 0; j < SU_GRID_SIZE; j++){
		row = square_to_row(box, j);
		col = square_to_col(box, j);

		if(need[r][c][j] != SUDK_NONE && j != i){
			cellneed++;
		}
		if(need[r][j][i] == SUDK_BASE + i && j != c){
			colneed++;
		}
		if(need[j][c][i] == SUDK_BASE + i && j != r){
			rowneed++;
		}
		if(need[row][col][i] == SUDK_BASE + i 
				&& j != coord_to_idx(r, c)){
			boxneed++;
		}
	}

	if(!cellneed || !rowneed || !colneed || !boxneed){
		put_val(sudoku, need, r, c, i);
	} else {
		//try can not be put here, or it will affect other's certainty
	}
}

/**
 * @brief put ith value into sudoku
 */
static void put_val(sudoku_t sudoku, needed_t need,
		int r, int c, int i)
{
	// put into sudoku
	sudoku[r][c] = SUDK_BASE + i;

	int box = coord_to_square(r, c);
	int j, row, col;
	for(j = 0; j < SU_GRID_SIZE; j++){
		row = square_to_row(box, j);
		col = square_to_col(box, j);
		/* remove possibility which is like reduce_need */
		need[r][c][j] = SUDK_NONE;
		need[r][j][i] = SUDK_NONE;
		need[j][c][i] = SUDK_NONE;
		need[row][col][i] = SUDK_NONE;
	}

}

/**
 * @brief
 * Disjoint subset: Look in a row, a column, or a box, if there
 * are x number of cells containing x possible numbers, and the
 * possible numbers are the same.
 *
 * Actually this one is not working well with our DB.
 *
 * @param r	the row
 * @param c the col
 */
static void disjoint(needed_t need, int r, int c)
{
	char val[10], tmp[10];
	int i, j, rowsame = 0, colsame = 0, boxsame = 0, 
		row, col, count = 0;
	get_str(need, r, c, val);
	
	int box = coord_to_square(r, c);
	/* account the value in the (r, c)
	 * the cells in a row/col/box that has the same value with
	 * (r, c).
	 * */
	int cell = 0, rowindex = 0, colindex = 0, boxindex = 0;

	for(i = 0; i < SU_GRID_SIZE; i++){
		/* count */
		if(*(val + i) != SUDK_NONE){
			cell |= (1 << i);		
			count++;
		}

		/* row */
		get_str(need, r, i, tmp);
		if(!strcmp(val, tmp)){
			//equal
			rowindex |= (1 << i); 
			rowsame++;
		}

		/* col */
		get_str(need, i, c, tmp);
		if(!strcmp(val, tmp)){
			//equal
			colindex |= (1 << i);
			colsame++;
		}

		/* box */
		row = square_to_row(box, i);
		col = square_to_col(box, i);
		get_str(need, row, col, tmp);
		if(!strcmp(val, tmp)){
			//equal
			boxindex |= (1 << i);
			boxsame++;
		}
	}

	if(count < 2 || (rowsame != count
				&& colsame != count
				&& boxsame != count)){
		return;
	}

	for(i = 0; i < SU_GRID_SIZE; i++){
		if(((cell >> i) & 0x1) == 0){
			/* no possibility */
			continue;
		}

		/* use the possibility to eliminate the other cells 
		 * not satisfying the possibility
		 * */
		for(j = 0; j < SU_GRID_SIZE; j++){
			if(rowsame == count && ((rowindex >> j) && 0x1) == 0){
				need[r][j][i] = SUDK_NONE;
			}
			if(colsame == count && ((colindex >> j) && 0x1) == 0){
				need[j][c][i] = SUDK_NONE;
			}
			if(boxsame == count && ((boxindex >> j) && 0x1) == 0){
				row = square_to_row(box, j);
				col = square_to_col(box, j);
				need[row][col][i] = SUDK_NONE;
			}
		}
	}

}

/**
 * @brief return the need in the cell (r, c) as a string
 *
 * @param need  the matrix representing the need of a cell
 * @param r		the row
 * @param c		the column
 * @param str	fill the need into the string
 */
static void get_str(needed_t need, int r, int c, char *str)
{
	sprintf(str, "%9.9s", need[r][c]);
}

/**
 * @brief 
 *
 * Single cell:
 * If a cell contains a value that is the only one
 * in its row, then the value can be eliminate from the rest
 * of column and box, to which the cell belongs.
 * The same for a column and a box.

 * Single Box: 
 * Look at the rows and columns. If a number is only present
 * in a spesific box, we can remove the number from the other
 * rows in the specific box.
 *
 * Look at the box, if a number is only present in a row of 
 * the specific box, we can remove the number from the other
 * cells in the same row out of the box. The same for column.
 *
 * @param need  the matrix representing the need of a cell
 * @param r		the row
 * @param c		the column
 * @param i		indicates the value
 */
static void eliminate(needed_t need, int r, int c, int i)
{
	int colneed = 1, rowneed = 1, boxrowneed = 1, boxcolneed = 1;
	int rowsingle = 1, colsingle = 1, boxsingle =1;
	int box = coord_to_square(r, c);
	int j, row, col;

	for(j = 0; j < SU_GRID_SIZE; j++){
		row = square_to_row(box, j);
		col = square_to_col(box, j);

		/* is (a cell) single in a row ?*/
		if(rowsingle && c != j && need[r][j][i] == SUDK_BASE + i){
			rowsingle = 0;
		}

		/* is (a cell) single in a col ?*/
		if(colsingle && r != j && need[j][c][i] == SUDK_BASE + i){
			colsingle = 0;
		}

		/* is (a cell) single in a box ?*/
		if(boxsingle){
			if(j != coord_to_idx(r, c)
					&& need[row][col][i] == SUDK_BASE + i){
				boxsingle = 0;
			}
		}

		/* is (cells in a box) needed in a row ?*/
		if(rowneed && coord_to_square(r, j) != box
				&& need[r][j][i] == SUDK_BASE + i){
			rowneed = 0;
		}

		/* is (cells in a box) needed in a col ?*/
		if(colneed && coord_to_square(j, c) != box
				&& need[j][c][i] == SUDK_BASE + i){
			colneed = 0;
		}

		/* is (cells in a row of a box) needed in a box ?*/
		if(boxrowneed && row != r && 
				need[row][col][i] == SUDK_BASE + i){
			boxrowneed = 0;
		}

		/* is (cells in a col of a box) needed in a box ?*/
		if(boxcolneed && col != c && 
				need[row][col][i] == SUDK_BASE + i){
			boxcolneed = 0;
		}

	}

	/* if it is single, then remove need from 
	 * other related cells in row, col, and box.
	 **/
	if(rowsingle || colsingle || boxsingle){
		for(j = 0; j < SU_GRID_SIZE; j++){
			if(j != i){
				need[r][c][j] = SUDK_NONE;
			}
			if(j != c){
				need[r][j][i] = SUDK_NONE;
			}
			if(j != r){
				need[j][c][i] = SUDK_NONE;
			}
			if(j != coord_to_idx(r, c)){
				need[square_to_row(box, j)][square_to_col(box, j)][i] 
					= SUDK_NONE;
			}
		}
	}

	if(rowneed){
		/* remove need from other rows in the specific box */
		for(j = 0; j < SU_GRID_SIZE; j++){
			row = square_to_row(box, j);
			col = square_to_col(box, j);
			if(row != r){
				need[row][col][i] = SUDK_NONE; 
			}
		}
	}

	if(colneed){
		/* remove need from other cols in the specific box */
		for(j = 0; j < SU_GRID_SIZE; j++){
			row = square_to_row(box, j);
			col = square_to_col(box, j);
			if(col != c){
				need[row][col][i] = SUDK_NONE; 
			}
		}
	}

	if(boxrowneed){
		/* remove need from other cells in the same row 
		 * but out of specific box */
		for(j = 0; j < SU_GRID_SIZE; j++){
			if(coord_to_square(r, j) != box){
				need[r][j][i] = SUDK_NONE;
			}
		}
	}

	if(boxcolneed){
		/* remove need from other cells in the same col
		 * but out of specific box */
		for(j = 0; j < SU_GRID_SIZE; j++){
			if(coord_to_square(j, c) != box){
				need[j][c][i] = SUDK_NONE;
			}
		}
	}
}

/**
 * @brief remove the need for ch of any cell in the row/col/box 
 * which (row, col) is in
 *
 * @prev (row, col) of sudoku contains a certain value
 * @param r the row
 * @param c the col
 * @param i indicate which one is being reduced
 */
static void reduce_need(needed_t need, int r, int c, int i)
{
	int box = coord_to_square(r, c);
	int j;

	for(j = 0;j < SU_GRID_SIZE; j++){
		need[r][j][i] = SUDK_NONE;
		need[j][c][i] = SUDK_NONE;
		need[square_to_row(box, j)][square_to_col(box, j)][i] 
			= SUDK_NONE; 
	}
}


/* Convert from row/col to a square index */
static int coord_to_square(int row, int col) 
{
  return col / SU_BOX_SIZE + row / SU_BOX_SIZE * SU_BOX_SIZE;
}

/* Convert from row/col to an index within a square */
static int coord_to_idx(int row, int col) 
{
  return col % SU_BOX_SIZE + row % SU_BOX_SIZE * SU_BOX_SIZE;
}

/* Convert from a square number and index in that square to a row number */
static int square_to_row(int square, int idx) 
{
  return idx / SU_BOX_SIZE + square / SU_BOX_SIZE * SU_BOX_SIZE;
}

/* Convert from a square number and index in that square to a column number */
static int square_to_col(int square, int idx) 
{
  return idx % SU_BOX_SIZE + square % SU_BOX_SIZE * SU_BOX_SIZE;
}
