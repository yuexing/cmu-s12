/**
 * @file block.h
 * @brief prototype for block-style lock functionality.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug.
 */

#ifndef _BLOCK_H
#define _BLOCK_H

int block_init(block_t *block);
void block_lock(block_t *block);
void block_unlock(block_t *block);
void block_cond_unlock(block_t *block);
void block_unlock_vanish(block_t *block);

#endif
