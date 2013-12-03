/**
 * @file vm.h
 * @brief prototypes and macros for virtual memory
 * @author Yue Xing(yuexing)
 * @bug No Found bugs
 */

#ifndef _VM_H
#define _VM_H

#define PRESENT_BIT		0x1
#define RW_BIT			0x2
#define USR_BIT			0x4
#define GLO_BIT			(1<<8)

/* used for new_pages and 
 * remove_pages. The two bits
 * indicate the start and the end
 * of a pages area.*/
#define PAGES_START_BIT	(1<<9)
#define PAGES_END_BIT	(1<<10)
#define PAGES_NEW		(1<<11)

#define OFFSET_MASK			(PAGE_SIZE-1)
#define ADDR_MASK			(~OFFSET_MASK)
#define PAGE_ROUND_DOWN(a) 	(((uint32_t)(a))&ADDR_MASK)
#define PAGE_ROUND_UP(a)	((((uint32_t)(a))+OFFSET_MASK)&ADDR_MASK)
#define PAGES(a)		(((uint32_t)a)%PAGE_SIZE?((uint32_t)a)/PAGE_SIZE+1:((uint32_t)a)/PAGE_SIZE)
#define TABLES(a)	((a)%PAGES_PER_TABLE?(a)/PAGES_PER_TABLE+1:(a)/PAGES_PER_TABLE)

/* present manipulations */
#define SET_PRESENT(a)		((a)=((uint32_t)(a))|PRESENT_BIT)
#define SET_UNAVAIL(a)		((a)=((uint32_t)(a))&(~PRESENT_BIT))
#define IS_PRESENT(a)		(((uint32_t)(a))&PRESENT_BIT)

/* read/write manipulations */
#define SET_WRITABLE(a)		((a)=((uint32_t)(a))|RW_BIT)
#define SET_READABLE(a)		((a)=((uint32_t)(a))&(~RW_BIT))
#define IS_WRITABLE(a)		(((uint32_t)(a))&RW_BIT)

/* user/supervisor manipulations */
#define SET_USR(a)			((a)=((uint32_t)(a))|USR_BIT)
#define SET_SUPER(a)		((a)=((uint32_t)(a))&(~USR_BIT))
#define IS_USER(a)			(((uint32_t)(a))&USR_BIT)

/* new page bit*/
#define SET_NEW(a)			((a)=((uint32_t)(a))|PAGES_NEW)
#define IS_NEW(a)			(((uint32_t)(a))&PAGES_NEW)

/* global page */
#define SET_GLO(a)			((a)=((uint32_t)(a))|GLO_BIT)

/* set p, w, u */
#define SET_PWU(a,w,u)\
	do{\
	SET_PRESENT(a);\
	if(w) SET_WRITABLE(a);\
	if(u) SET_USR(a);\
	}while(0)

/* set p, w, u, g, n */
#define SET_PWUGN(a,w,u,g,n)\
	do{\
	SET_PRESENT(a);\
	if(w) SET_WRITABLE(a);\
	if(u) SET_USR(a);\
	if(g) SET_GLO(a);\
	if(n) SET_NEW(a);\
	}while(0)

/* start and end */
#define SET_START(a)		((a)=((uint32_t)(a))|PAGES_START_BIT)
#define CLR_START(a)		((a)=((uint32_t)(a))&(~PAGES_START_BIT))
#define IS_START(a)			(((uint32_t)(a))&PAGES_START_BIT)

#define SET_END(a)			((a)=((uint32_t)(a))|PAGES_END_BIT)
#define CLR_END(a)			((a)=((uint32_t)(a))&(~PAGES_END_BIT))
#define IS_END(a)			(((uint32_t)(a))&(PAGES_END_BIT))

/* size */
#define PAGES_PER_TABLE		(1<<10)
#define SIZE_PER_TABLE		(PAGE_SIZE*PAGES_PER_TABLE)
#define NUM_KPDES			(USER_MEM_START/SIZE_PER_TABLE)

/* transform from v to pde->pte */
#define ADR_PDE(a)			(((uint32_t)a)/SIZE_PER_TABLE)
#define ADR_PTE(a)			(((uint32_t)a)%SIZE_PER_TABLE/PAGE_SIZE)

/* table */
#define PDE_TBL(a)	((page_table_t*)PAGE_ROUND_DOWN(a))


/* functions */
int init_mod_vm();

void destroy_dir(page_dir_t*,process_t*);

void install_kpdes(page_dir_t*);

int cnt_user_ptes(page_dir_t*);

void load_user_frms(int, void **, page_dir_t*);

page_dir_t *clone_dir(page_dir_t*,void**);

int ck_mapping(void *vaddr, page_dir_t *dir);

int map_vp(void*, void*, page_dir_t*, uint32_t, uint32_t);

int map_vp_tbl(void*,void*,void*,page_dir_t*,uint32_t,uint32_t);

void free_v(void*, page_dir_t*);

int new_mapping(void*,page_dir_t*);

int new_nmapping(void* start, int len, page_dir_t *dir);

int free_nmapping(void *start, page_dir_t*);

void*get_vaddr(int pde_idx, int pte_idx);

void*get_paddr(page_dir_t *dir, void *vaddr);

pte_t v2pte(page_dir_t *dir, void *vaddr);

int ck_zfod(page_dir_t*,void*);

void *new_page();

page_table_t *new_tbl();

void free_page(void*);

void free_tbl(page_table_t*);

void free_dir(page_dir_t*);

void clr_dir(page_dir_t*);

void enable_paging();

void invl_tlb(void*);
#endif
