/**
 * @file vm_type.h
 * @brief data structs for virtual memory
 * @author Yue Xing(yuexing)
 * @bug No Found bugs
 */

#ifndef _VM_TYPE_H
#define _VM_TYPE_H

#define TABLE_SIZE	(1<<10)

/* page directory entry*/
typedef uint32_t pde_t;

/* page table entry */
typedef uint32_t pte_t;

/* a page directory */
typedef struct {
	pde_t pdes[TABLE_SIZE];
} page_dir_t;

/* a page table */
typedef struct {
	pte_t ptes[TABLE_SIZE];
	/* when cnt==0
	 * the table will be freed*/
	uint32_t cnt; 
} page_table_t;

#endif
