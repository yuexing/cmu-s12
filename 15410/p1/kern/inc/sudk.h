/**
 * @file sokudu module. It include a solver, a loader and a checker.
 *
 * @author Yue Xing(yuexing)
 * @bugs Not Found
 */

#ifndef _SKD_H
#define _SKD_H

int solve_sudk(char sudoku[SU_GRID_SIZE][SU_GRID_SIZE], char *slvd_sdk, int level);
const char *load_sudk(char level, unsigned int base);
int is_solved(char skd[SU_GRID_SIZE][SU_GRID_SIZE]);

#endif /* _SKD_H */
