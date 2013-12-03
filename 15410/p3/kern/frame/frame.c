/**
 * @file frame.c
 * @brief implementation for physical memory
 * manipulation only tracking the user frames.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#include <kernel.h>

/* bitset related vars */
static uint8_t *bitset;
/* how many user frames */
static uint32_t ubit;
/* how big the bitset is*/
static uint32_t uset;
/* next bit */
static uint32_t next_bit;
/* lock */
static block_t lock;
/* internel help methods*/
static void *alloc_frame();
static void free_frame(void *);

#define UNIT_SIZE		8
#define UNIT_MAX		((1<<UNIT_SIZE)-1)

#define IDX(a)			(a/UNIT_SIZE)
#define OFF(a)			(a%UNIT_SIZE)

#define SET(idx,off)	(bitset[idx]|=(1<<off))
#define CLR(idx,off)	(bitset[idx]&=(~(1<<off)))
#define IS_SET(idx,off) (bitset[idx]&(1<<off))

#define UPDATE(a)		(a=(++a==ubit)?0:a)

#define ADR_FRM(a)		((((uint32_t)a)-USER_MEM_START)>>PAGE_SHIFT)
#define	FRM_ADR(a)		((a<<PAGE_SHIFT)+USER_MEM_START)

/**
 * @brief Initialize the frame sub-module.
 */
int init_mod_frame()
{
	block_init(&lock);
	ubit = machine_phys_frames()
		- (USER_MEM_START >> PAGE_SHIFT);

	uset = (ubit + UNIT_SIZE - 1)/UNIT_SIZE;

	// chg to smalloc
	bitset = smalloc(uset);
	if(!bitset){
		return ERROR;
	}	
	return SUCCESS;
}


/**
 * @brief Alloc cnt frames and put 
 * the paddr into paddrs. Rollback
 * on failure.
 */
int alloc_nframes(int cnt, void **paddrs)
{
	int i = 0,j, ret = SUCCESS;

	block_lock(&lock);
	for(; i < cnt; i++){
		if(!(paddrs[i]
				=alloc_frame())){
			break;
		}
	}
	if(i != cnt){
		/* roll back */
		for (j = 0; j < i; j++){
			free_frame(paddrs[j]);
			paddrs[j]=0;
		}
		ret = ERROR;
	}
	block_unlock(&lock);
	return ret;
}

/**
 * @brief Free cnt frames in paddrs.
 */
void free_nframes(int cnt, void **paddrs)
{
	int i = 0;
	block_lock(&lock);
	for(; i < cnt; i++){
		free_frame(paddrs[i]);
		paddrs[i]=0;
	}
	block_unlock(&lock);
}

/**
 * @brief Alloc a frame by applying
 * next fit.
 * 
 * @prev The whole process is 
 * exclusive guaranteed by lock.
 *
 * @post Remember to memset 0.
 *
 * @return NULL if no unused frame. 
 * */
static void *alloc_frame()
{
	void *ret = NULL;
	uint32_t idx, off;
	int i = 0;

	for (; i < ubit; i++){
		idx = IDX(next_bit), 
		off = OFF(next_bit);

		if(!IS_SET(idx, off)){
			SET(idx, off);
			ret = (void*)FRM_ADR(next_bit);
			UPDATE(next_bit);
			return ret;
		}
		UPDATE(next_bit);
	}
	return NULL;
}

/**
 * @brief Free a frame corresponding
 * to the paddr.
 */
static void free_frame(void *paddr)
{
	uint32_t frame=ADR_FRM(paddr),
			idx = IDX(frame), 
			off= OFF(frame);
	CLR(idx, off);
}
