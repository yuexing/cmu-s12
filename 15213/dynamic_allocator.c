/*
 * mm.c
 *
 * author : yuexing@andrew.cmu.edu
 * version: 10.0
 * date: 04/13/2012
 *
 * NOTICE:
 * if you find today is so late that I have to suffer a penalty,
 * please judge the perior versions as they are all 96 :)
 *
 * this file is for memory allocator.
 * the allocator:
 * 1) applies segregated list for free blocks;
 * 2) a free block consists of header, prev, next, footer; at least 4 * 4 bytes;
 * 3) a allocated block consists of header and payload;
 * 4) the header records size, prev ALLOC status, self ALLOC status,
 *    the footer of free blocks also records these stuff;
 */
#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "mm.h"
#include "memlib.h"

/******************************************************
 * CORE functions:
 *
 * coalesce happens when there exist two adjacent free blocks,
 * specifically, after free, place called by realloc, realloc, and extend_heap;
 *
 * place is called by malloc methods to split a free block;
 *
 * place1 is a kind of place called by realloc
 *
 * find_fit goes into a proper rank list and search for a better free block;
 * 
 * extend_heap happens when there's no FREE block big enough for request
 * ****************************************************/
static void *coalesce(void *bp);
static void place(void *bp, unsigned int size);
static void place1(void *bp, unsigned int size);
static void *extend_heap(unsigned int size);
static void *find_fit(int asize);


/**
 * RANK CLASS functions:
 *
 * get_rank returns the corresponding rank of a certain size;
 */
static int get_rank(unsigned int size);


/********************************************
 * RANK LIST functions:
 *
 * ********************************************/
static void addfree(void *bp, unsigned int n);
static void delfree(void *bp, unsigned int n);


/* If you want debugging output, use the following macro.  When you hand
 * in, remove the #define DEBUG line. */
#define DEBUG
#ifdef DEBUG
# define dbg_printf(...) printf(__VA_ARGS__)
#else
# define dbg_printf(...)
#endif


/* do not change the following! */
#ifdef DRIVER
/* create aliases for driver tests */
#define malloc mm_malloc
#define free mm_free
#define realloc mm_realloc
#define calloc mm_calloc
#endif /* def DRIVER */

/****************************
 * rank
 *
 * ***************************/
#define RANK_ONE		0
#define RANK_TWO		1
#define RANK_THR		2
#define RANK_FOR		3
#define RANK_OVER		4
/**
 * 0 - 512
 * 521 - 1024
 * 1024 - 4096
 * 4096 - 
 */
#define RANK_ONE_EXP	9
#define RANK_TWO_EXP	9
#define RANK_THR_EXP	10
#define RANK_FOR_EXP	12
#define RANK_SIZE  		4
#define INIT_SIZE  		(RANK_SIZE + 2) /* need padding: 13 * 8*/
#define RANKP(n, lo)	(lo + n * WSIZE)
#define RADDR(p, lo)    ((void *)((*(unsigned int*)p == 0) ? (unsigned long)0x0 : ((unsigned long)(*(unsigned int*)p) + lo)))

/* calculate next, prev, rank addr  */
#define PADDRP(bp)      (bp)
#define NADDRP(bp)      ((char *)bp + WSIZE)

/* cast an addr to 4-byte unsigned int */
#define ADDR(p, lo)     ((unsigned int)((size_t)p - (size_t)lo))

/* single word (4) or double word (8) alignment */
#define ALIGNMENT       8

/* rounds up to the nearest multiple of ALIGNMENT */
#define ALIGN(p)       (((size_t)(p) + (ALIGNMENT-1)) & ~0x7L)

/* for semantic use size_t */
#define SIZE_T_SIZE    (ALIGN(sizeof(size_t)))

/* Basic constants and macros */
#define WSIZE 			4	/* the size of the heap will never be greater than or equal to 2^32 bytes */
#define DSIZE 			8	/* double word size */
#define CHUNKSIZE 		320	/* extend heap by this amount*/
#define THRESHOD_EXP 	4
#define THRESHOD		16
#define MAX_FIND		4

#define MAX(x, y) 		((x) > (y) ? (x) : (y))

/* Pack a size and allocated bit into a word */
#define PACK(size, alloc) ((size) | (alloc))

/* read and write a word at addr p */
#define GET(p) 		(*(unsigned int *)(p))
#define PUT(p, val) (*(unsigned int *)(p) = (val))

/* read the size and allocated, PREV allocated field from address p */
#define GET_SIZE(p) (GET(p) & ~0x7)
#define GET_ALLOC(p) (GET(p) & 0x1)
#define SET_ALLOC(p) (*(unsigned int*)(p) |= 0x1)
#define SET_FREE(p)	 (*(unsigned int*)(p) &= 0xfffffffe)
#define GET_PALLOC(p) (GET(p) & 0x2)
#define SET_PALLOC(p)	(*(unsigned int*)(p) |= 0x2)
#define SET_PFREE(p)	(*(unsigned int*)(p) &= 0xfffffffd)

/* given block ptr bp, compute addr of its header and footer */
#define HDRP(bp) ((char *)(bp) - WSIZE)
#define FTRP(bp) ((char *)(bp) + GET_SIZE(HDRP(bp)) - DSIZE)

/* given block ptr bp, compute addr of the next and prev blocks */
#define NEXT_BLKP(bp) ((char *)(bp) + GET_SIZE(((char *)(bp) - WSIZE)))
#define PREV_BLKP(bp) ((char *)(bp) - GET_SIZE(((char *)(bp) - DSIZE)))

/* ***************************************
 * GLOBAL vars
 * 
 * cbp is the current addr affected by malloc, realloc, and free
 ****************************************/
static char *mem_lo, *cbp;

/******************************************************
 * CORE functions 
 *******************************************************/

/*
 * Initialize: return -1 on error, 0 on success.
 *
 * padding & epilogue(0, 0x3)
 * 1) align
 * 2) to avoid coalesce to padding
 */
int mm_init(void) {
	/* create the initial empty heap */
	if((mem_lo = mem_sbrk(INIT_SIZE * WSIZE)) == (void *)-1)
		return -1;

	/* 4 * RANK */
	bzero(mem_lo, 5 * WSIZE); /* padding */
	PUT(mem_lo + 5 * WSIZE, PACK(0, 0x3)); /* epilogue header */

	/* extend the empty heap with a free block of CHUNKSIZE bytes */
	if((cbp = extend_heap(CHUNKSIZE)) == NULL)
		return -1;

    return 0;
}


/**
 * malloc
 *
 * returns a ptr to an allocated block payload of at least size 
 * types.
 * payload ptr should be aligned to 8 bytes
 * should lie within the heap
 * should not overlap with any other allocated chunk
 */
void *malloc (size_t size) {
	
	if(size == 0)
		return NULL;
	
	unsigned int asize = ALIGN(size + WSIZE);

	/* the least free block size */
	if(asize >> THRESHOD_EXP == 0){
		asize = THRESHOD; 
	}

	char *bp;

	if((bp = find_fit(asize)) == NULL){
	 	unsigned int esize = MAX(asize, CHUNKSIZE);
		if((bp = extend_heap(esize)) == NULL){
			return NULL;
		}
	}

	place(bp, asize);

	/* mm_checkheap */
	cbp = bp;
    return bp;
}

/*
 * free
 * frees the block pointed by ptr and returns nothing.
 * only work with ptr returned by malloc calloc or relloc
 */
void free (void *ptr) {

	if(ptr == NULL)
		return;

	void *hd = HDRP(ptr);
	SET_FREE(hd);
	PUT(FTRP(ptr), *(unsigned int*)hd);

	/* mm_checkheap */
	cbp = coalesce(ptr);
}

/*
 * realloc
 * returns a pointer to an allocated region of at least size bytes
 * if oldptr is NULL, acts as malloc
 * if size is 0, acts as free
 * modified:
 * coalesce PREV and NEXT if possible
 * coalesce NEXT if possbile
 * return oldptr if possible
 * naive	
 *
 * all the old data should be copyed to new area(memmove)
 */
void *realloc(void *oldptr, size_t size) {

	void *newptr;
	unsigned int oldsize, asize;

  	/* If oldptr is NULL, then this is just malloc. */
	if(oldptr == NULL)
		return malloc(size);

  	/* If size == 0 then this is just free, and we return NULL. */
	if(size == 0){
		free(oldptr);
		return NULL;
	}

	asize = ALIGN(size + WSIZE);
	oldsize = GET_SIZE(HDRP(oldptr));

	/* the least free block size */
	if(asize >> THRESHOD_EXP == 0){
		asize = THRESHOD; 
	}

	/* coalesce all if possible */ 
	unsigned int prev_alloc = GET_PALLOC(HDRP(oldptr));
	void *hd = HDRP(oldptr), 
		 *next = NEXT_BLKP(oldptr), 
		 *nexthd = HDRP(next), *prev, *prevhd;
	unsigned int next_alloc = GET_ALLOC(nexthd), 
				 nsize = GET_SIZE(nexthd), psize, nnsize, rank;

	if(!prev_alloc && !next_alloc){

		prev = PREV_BLKP(oldptr);
		prevhd = HDRP(prev);
		psize = GET_SIZE(prevhd);

		if((nnsize = psize + oldsize + nsize) >= asize){
			/* del NEXT */
			rank = get_rank(nsize);
			delfree(next, rank);

			/* del PREV*/
			rank = get_rank(psize);
			delfree(prev, rank);

			/* set & cpy*/
			prev_alloc = GET_PALLOC(prevhd);
			PUT(prevhd, PACK(nnsize,  prev_alloc | 1));
		
			if(size < oldsize)
				oldsize = size;
			memmove(prev, oldptr, oldsize);

			place1(prev, asize);
		
			cbp = prev;
			return prev;
		}
	}else if(prev_alloc && !next_alloc 
			&& (nnsize = nsize + oldsize) >= asize){
		/* coalesce next if possible */
		/* del NEXT */
		rank = get_rank(nsize);
		delfree(next, rank);

		/* set & place */
		PUT(hd, PACK(nnsize, 0x3));
		
		place1(oldptr, asize);

		cbp = oldptr;
		return oldptr;
	}

	
	if(oldsize >= asize){
		/* return oldptr if possible */
		place1(oldptr, asize);
		cbp = oldptr;
		return oldptr;
	}

	newptr = malloc(size);

	/* If realloc() fails the original block is left untouched  */
	if(!newptr) {
		return 0;
	}

	/* Copy the old data. */
	if(size < oldsize) oldsize = size;
	memcpy(newptr, oldptr, oldsize);

	/* Free the old block. */
	free(oldptr);

	cbp = newptr;
	return newptr;
}

/*
 * calloc - you may want to look at mm-naive.c
 * This function is not tested by mdriver, but it is
 * needed to run the traces.
 */
void *calloc (size_t nmemb, size_t size) {
    void *ptr;
	size_t asize = nmemb * size;
	if((ptr = malloc(asize)) == NULL){
		return NULL;
	}else{
		bzero(ptr, (asize));
		return ptr;
	}
}

/*******************************
 * RANK CLASS functions
 *******************************/
/**
 * return the corresponding rank of the size
 * apply right shift for efficiency
 */
static inline int get_rank(unsigned int size){
	if((size >> RANK_ONE_EXP == 0) || ((size >> RANK_ONE_EXP) > 0 && (size >>  RANK_TWO_EXP) == 0))
			return RANK_ONE;
	else if((size >> RANK_TWO_EXP) > 0 && (size >>  RANK_THR_EXP) == 0)
			return RANK_TWO;
	else if((size >> RANK_THR_EXP) > 0 && (size >>  RANK_FOR_EXP) == 0)
			return RANK_THR;
	else if(size >> RANK_FOR_EXP > 0)
		return RANK_FOR;
	else{	
		fprintf(stderr, "get rank error with size : %u\n", size);
		exit(-1);
	}
}

/**
 * place a block of size at bp
 */
static inline void place(void *bp, unsigned int size){
	void *hd = HDRP(bp);
	unsigned int csize = GET_SIZE(hd),
				 crank = get_rank(csize),
				 rsize = csize - size;

	/* del from rank */
	delfree(bp, crank);

	if(rsize >> THRESHOD_EXP > 0){
		/* big enough for a FREE blk */
		unsigned int prev_alloc = GET_PALLOC(hd);
		PUT(hd, PACK(size, prev_alloc | 0x1));
		bp = NEXT_BLKP(bp);

		/* add to rank*/
		crank = get_rank(rsize);
		addfree(bp, crank);

		PUT(HDRP(bp), PACK(rsize, 0x2));
		PUT(FTRP(bp), PACK(rsize, 0x2));
	}else{
		SET_ALLOC(hd);
		bp = NEXT_BLKP(bp);
		SET_PALLOC(HDRP(bp));
	}
}


/**
 * place a block of size at bp
 * for realloc
 */
static inline void place1(void *bp, unsigned int size){
	void *hd = HDRP(bp);
	unsigned int csize = GET_SIZE(hd),
				 rsize = csize - size;

	if(rsize >> THRESHOD_EXP > 0){
		/* big enough for a FREE blk */
		unsigned int prev_alloc = GET_PALLOC(hd), crank;
		PUT(hd, PACK(size, prev_alloc | 0x1));

		/* add to rank*/
		bp = NEXT_BLKP(bp);
		crank = get_rank(rsize);
		addfree(bp, crank);

		PUT(HDRP(bp), PACK(rsize, 0x2));
		PUT(FTRP(bp), PACK(rsize, 0x2));

		/* notify real NEXT blk*/
		bp = NEXT_BLKP(bp);
		SET_PALLOC(HDRP(bp));
	}else{
		SET_ALLOC(hd);
		bp = NEXT_BLKP(bp);
		SET_PALLOC(HDRP(bp));
	}
}


/**
 * modified FIRST fit search
 *
 * start from rank list n;
 * until find the first big enough in rank list n';
 * go through n' to find MAX_FIND big enough blocks to select the best one and return;
 *
 * if the size is just equal, quick return;
 */
static inline void *find_fit(int asize){
	/* ... */
	 int i = get_rank(asize),
		 bpsize,
		 tmpsize,
		 bestsize = (1 << 30),
		 inrank = 0,
		 count;
	void *bp, *hd, *bestp = NULL;

	for(; i < RANK_OVER && !inrank; i++){
		count = 0;
		bp = RADDR(RANKP(i, mem_lo), mem_lo);
		while(bp && count < MAX_FIND){
			hd = HDRP(bp);
			bpsize = GET_SIZE(hd);
			tmpsize = bpsize - asize;
			if(tmpsize == 0){
				return bp;
			}
			if(tmpsize > 0){
				if((tmpsize >> THRESHOD_EXP) == 0){
					return bp;
				}
				if(tmpsize < bestsize){
					bestsize = tmpsize;
					bestp = bp;
				}
				count++;
				inrank = 1;
			}

			bp = RADDR(NADDRP(bp), mem_lo); 
		}
	}

	return bestp;
}

/**
 * size must have been aligened 
 */
static inline void *extend_heap(unsigned int size){
	char *bp;
	
	if((bp = mem_sbrk(size)) == (void *)-1){
		return NULL;
	}

	unsigned int prev_alloc = GET_PALLOC(bp - WSIZE);

	PUT(HDRP(bp), PACK(size, prev_alloc)); /* Free block header */
	PUT(FTRP(bp), PACK(size, prev_alloc)); /* Free block footer */

	/* set PREV 0x2
	 * */
	PUT(HDRP(NEXT_BLKP(bp)), PACK(0, 0x1)); /* Recover epilogue header */

	/* Coalesce if the previous block was free */
	return coalesce(bp);
}

/**
 * coalesce with PREV or NEXT
 * if possible
 * notify NEXT here for efficiency
 */
static inline void *coalesce(void *bp){

	void *hd = HDRP(bp), 
		 *next = NEXT_BLKP(bp),
		 *nexthd = HDRP(next);

	unsigned int prev_alloc = GET_PALLOC(hd),
				 next_alloc = GET_ALLOC(nexthd),
				 size = GET_SIZE(hd),
				 tsize,
				 rank;

	if(prev_alloc && next_alloc){ 
		/* nofity NEXT */
		SET_PFREE(nexthd);

		/* Add ME*/
		rank = get_rank(size);
		addfree(bp, rank);
		return bp;
	}else if(prev_alloc && !next_alloc){
		/* del NEXT */
		tsize = GET_SIZE(nexthd);
		rank = get_rank(tsize);
		delfree(next, rank);
		
		/* Add ME*/
		size += tsize;
		rank = get_rank(size);
		addfree(bp, rank);

		PUT(hd, PACK(size, 0x2)); /* sequence matters */
		PUT(FTRP(bp), PACK(size, 0x2));
	}else if(!prev_alloc && next_alloc){
		/* notify NEXT */
		SET_PFREE(nexthd);
		
		/* del PREV */
		bp = PREV_BLKP(bp);
		hd = HDRP(bp);
		prev_alloc = GET_PALLOC(hd);
		tsize = GET_SIZE(hd);
		rank = get_rank(tsize);
		delfree(bp, rank);

		/* add PREV */
		size += tsize;
		rank = get_rank(size);
		addfree(bp, rank);

		PUT(hd, PACK(size, prev_alloc));
		PUT(FTRP(bp), PACK(size, prev_alloc));
	}else{
		/* del NEXT */
		tsize = GET_SIZE(nexthd);
		size += tsize;
		rank = get_rank(tsize);
		delfree(next, rank);

		/* del PREV */
		bp = PREV_BLKP(bp);
		hd = HDRP(bp);
		prev_alloc = GET_PALLOC(hd);
		tsize = GET_SIZE(hd);
		rank = get_rank(tsize);
		delfree(bp, rank);

		/* add PREV */
		size += tsize;
		rank = get_rank(size);
		addfree(bp, rank);

		PUT(hd, PACK(size, prev_alloc));
		PUT(FTRP(bp), PACK(size, prev_alloc));
	}

	return bp;
}
/********************************************
 * RANK LIST functions:
 *
 * ********************************************/

/**
 * add a free block to a rank
 * LIFO
 */
static inline void addfree(void *bp, unsigned int n){
	
	/* get */
	void *ranklist = RANKP(n, mem_lo) ;
	void *free_listp = RADDR(ranklist, mem_lo);

	/* set bp's prev ->  0 */
	PUT(PADDRP(bp), 0);
	/* set bp's next -> free_listp */
	PUT(NADDRP(bp), ADDR(free_listp, mem_lo));

	/* update free_listp */
	if(free_listp != NULL){
		/* 
		 * set free_listp's prev -> bp
		 */
		PUT(PADDRP(free_listp), ADDR(bp, mem_lo));
	}

	free_listp = bp;
	/* put */
	PUT(ranklist, ADDR(free_listp, mem_lo));

}

/**
 * remove a free block from a rank
 */
static inline void delfree(void *bp, unsigned int n){

	/* get */
	void *ranklist = RANKP(n, mem_lo);
	void *free_listp = RADDR(ranklist, mem_lo);

	if(bp == free_listp){
		bp = RADDR(NADDRP(bp), mem_lo);
		
		/* here can be null */
		if(bp != NULL){
			PUT(PADDRP(bp), 0);
		}
		
		free_listp = bp;

		/* put */
		PUT(ranklist, ADDR(free_listp, mem_lo));
	}else{
		void *prev = RADDR(PADDRP(bp), mem_lo);
		void *next = RADDR(NADDRP(bp), mem_lo);
		PUT(NADDRP(prev), ADDR(next, mem_lo));
		if(next != NULL){
			PUT(PADDRP(next), ADDR(prev, mem_lo));
		}
	}

}

/**************************************
 * CHECK heap functions
 *
 *************************************/
/*
 * Return whether the pointer is in the heap.
 * May be useful for debugging.
 */
static int in_heap(const void *p) {
    return p <= mem_heap_hi() && p >= mem_heap_lo();
}

/*
 * Return whether the pointer is aligned.
 * May be useful for debugging.
 */
static int aligned(const void *p) {
    return (size_t)ALIGN(p) == (size_t)p;
}


/*
 * mm_checkheap
 */
void mm_checkheap(int verbose) {
	
	if(verbose == 1){
		/* illustrate the cur bp by init, malloc, realloc, free*/
		fprintf(stdout, "*************************\n");
		fprintf(stdout, "current bp:\n");

		if(!in_heap(cbp)){
			printf("out of bound: %p -> %p\n", mem_heap_lo(), mem_heap_hi());
		}
		if(!aligned(cbp)){
			printf("not aligned\n");
		}

		/* prev ALLOC, ALLOC, ptr, size(head), size(foot) */
		fprintf(stdout, "%s, %s, %p, %d, %d\n", 
				(GET_PALLOC(HDRP(cbp)) ? "A" : "F"),
				(GET_ALLOC(HDRP(cbp)) ? "A" : "F"),
				cbp,
				(GET_SIZE(HDRP(cbp))),
				((GET_ALLOC(HDRP(cbp)) == 0 ? (GET_SIZE(FTRP(cbp))) : 0))
				);
	}

	else if(verbose == 2){
		/* illustrate the heap */
		void *bp = mem_lo;
		unsigned char isfree = 0;

		fprintf(stdout, "*************************\n");
		fprintf(stdout, "rank class lists:\n");
		fprintf(stdout, "%d, %p, %p\n", RANK_ONE, RANKP(RANK_ONE, mem_lo), RADDR(RANKP(RANK_ONE, mem_lo), mem_lo));
		fprintf(stdout, "%d, %p, %p\n", RANK_TWO, RANKP(RANK_TWO, mem_lo), RADDR(RANKP(RANK_TWO, mem_lo), mem_lo));
		fprintf(stdout, "%d, %p, %p\n", RANK_THR, RANKP(RANK_THR, mem_lo), RADDR(RANKP(RANK_THR, mem_lo), mem_lo));
		fprintf(stdout, "%d, %p, %p\n", RANK_FOR, RANKP(RANK_FOR, mem_lo), RADDR(RANKP(RANK_FOR, mem_lo), mem_lo));

		/* padding */
		bp +=  INIT_SIZE * WSIZE;

		/* blocks */
		fprintf(stdout, "*************************\n");
		fprintf(stdout, "blocks: prev ALLOC, ALLOC, ptr, size(head), size(foot)\n");
		while(GET_SIZE(HDRP(bp))){
			fprintf(stdout, "%s, %s, %p, %d, %d\n", 
					(GET_PALLOC(HDRP(bp)) ? "A" : "F"),
					(GET_ALLOC(HDRP(bp)) ? "A" : "F"),
					bp,
					(GET_SIZE(HDRP(bp))),
					((GET_ALLOC(HDRP(bp)) == 0 ? (GET_SIZE(FTRP(bp))) : 0))
				   );
			/* free block foot and head consistency */
			if((GET_ALLOC(HDRP(bp))) == 0 && GET_SIZE((HDRP(bp))) != GET_SIZE((FTRP(bp)))){
				fprintf(stderr, "%p head and foot not match\n", bp);
				break;
			}
			
			/* adjacent free blocks */
			if(isfree & !GET_ALLOC(HDRP(bp))){
				fprintf(stderr, "%p should have been coalesced\n", bp);
				break;
			}

			/* out of heap */
			if(!in_heap(bp)){
				fprintf(stderr, "%p out of bound: %p -> %p\n", bp, mem_heap_lo(), mem_heap_hi());
				break;
			}

            /* aligned */
			if(!aligned(bp)){
				fprintf(stderr, "%p not aligned\n", bp);
				break;
			}

			/* update isfree */
			if(GET_ALLOC(HDRP(bp)) == 0){
				isfree = 1;	
			}else{
				isfree = 0;
			}

			bp = NEXT_BLKP(bp);
		}

		/* epilogue */
		fprintf(stdout, "*************************\n");
		fprintf(stdout, "epilogue: prev ALLOC, ALLOC, ptr, size(head)\n");
		fprintf(stdout, "%s, %s, %p, %d\n", 
				(GET_PALLOC(HDRP(bp)) ? "A" : "F"),
				(GET_ALLOC(HDRP(bp)) ? "A" : "F"),
				bp,
				(GET_SIZE(HDRP(bp)))
			   );


	}
	else if(verbose == 3){
		/* illustrate rank class */
		fprintf(stdout, "*************************\n");
		fprintf(stdout, "rank classes:\n");
	  
		unsigned int rank = RANK_ONE;
		void *bp, *prev;
		while(rank < RANK_OVER){
			fprintf(stdout, "rank %d:\n", rank);
			bp = RADDR(RANKP(rank, mem_lo), mem_lo);
			prev = NULL;
			while(bp){
				fprintf(stdout, "%s, %s, %p, %d, %d\n", 
						(GET_PALLOC(HDRP(bp)) ? "A" : "F"),
						(GET_ALLOC(HDRP(bp)) ? "A" : "F"),
						bp,
						(GET_SIZE(HDRP(bp))),
						((GET_ALLOC(HDRP(bp)) == 0 ? (GET_SIZE(FTRP(bp))) : 0))
					   );

				/* should in list ?*/
				if(GET_ALLOC(bp)){
					fprintf(stderr, "%p is allocated\n", bp);
				}

				/* foot and head consistency */
				if(GET_SIZE((HDRP(bp))) != GET_SIZE((FTRP(bp)))){
					fprintf(stderr, "%p head and foot not match\n", bp);
					break;
				}

				/* prev -> next and next -> prev */
				if(RADDR(PADDRP(bp), mem_lo) != prev){
					fprintf(stderr, "%p's prev should not be %p, %p actually\n", bp, RADDR(PADDRP(bp), mem_lo), prev);
					break;
				}

				/* out of heap */
				if(!in_heap(bp)){
					fprintf(stderr, "%p out of bound: %p -> %p\n", bp, mem_heap_lo(), mem_heap_hi());
					break;
				}

				/* update */
				prev = bp;
				bp = RADDR(NADDRP(bp), mem_lo);		
			}
			rank++;
		}
	}
}
