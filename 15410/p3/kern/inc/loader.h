/* 
 * @file: loader.h
 *
 * @brief prototypes for the user program
 * loader.
 *
 * @author Yue Xing(yuexing)
 * @bug No known bug
 */

#ifndef _LOADER_H
#define _LOADER_H

extern int ls_size;
extern int ls_n;
extern char *ls_names;

int init_mod_loader();

int getbytes( const char *filename, int offset, int size, char *buf );

int exist(const char *filename);

int load_program(const char *fname,		   char **arv, int argc,
	page_dir_t*, uint32_t*);

#endif /* _LOADER_H */
