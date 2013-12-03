/**
 * Copyright to yuexing@andrew.cmu.edu
 * All right reserved.
 *
 * file: cachesim.c
 * To simulate a cache by displaying the hits, misses, and evicts.
 *
 * modified date : Feb 24
 * modified: to make sense, set_mask, tag_mask, mem_addr, set_id, tag_id should 
 * be unsigned long as they can be really long.
 */

#include <getopt.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <string.h>
#include "cachelab-tools.h"
/**
 * a line has a valid bit( no bit in c, so use char to save space, 
 * however there is alignment, so change to integer ultimately).
 * a count to document LUR
 */
typedef struct{
	int valid ;
	unsigned long tag ;
	int last ;

}Line;
/*
 * a set has several Line decided by E
 */
typedef struct{
	Line* lines ;
	int count;
}Set;

/**
 * global variables used by all functions
 * s, b, E is defined by user
 * m is a word size, also the length of memory address
 * t = m - s - b
 * set_mask is to extract bits used for set
 * tag_mask is to extract bits used fot tag
 * sets is for cache sets
 *
 */

int s = 0, b = 0, E = 0, t = 0;
//is used for malloc, so can not be larger than Maximum integer
int set_size = 0, block_size = 0;
const int  m = sizeof(long) * 8;
unsigned long set_mask = 0, tag_mask = 0;
FILE *trace = NULL;
Set *sets = NULL;
int hits, misses, evicts;
/**
 * according to the lab instruction
 * ignore the fetch_size this time
 * TODO: consider fetch_size
 */
void cache(unsigned long mem_addr);

int main(int argc, char **argv){
	int c;
	char *cvalue = NULL;

	while((c = getopt(argc, argv, "b:s:E:t:")) != -1){
		switch(c){
			case 's':
				cvalue = optarg;
				s = atoi(cvalue);
				if(s < 0 && s > m ){
					printf("s should between 0 and word length\n");
					exit(-1);
				}
				set_size = 1 <<	s;		
				break;
			case 'E':
				cvalue = optarg;
				E = atoi(cvalue);
				if(E < 1){
					printf("E should larger than 0\n");
					exit(-1);
				}
				break;
			case 'b':
				cvalue = optarg;
				b = atoi(cvalue);
				block_size = 1 << b;	
				if(b < 0 && b > m){
					printf("b should between 0 and word length\n");
					exit(-1);
				}
				break;
			case 't':
				cvalue = optarg;
				trace = fopen(cvalue, "r");
				if(trace == NULL){
					printf( "%s open error!\n", cvalue);
					exit(-1);
				}
				break;
			case 'v':
				cvalue = optarg;
				//TODO: add later if necessary
				break;
			default:
				break;
		}//switch	
	}//while
	
	//tag size in an address
	t = m - s - b;
	if(t < 0 ){
		printf("t should larger than or equal to  0\n");
		exit(-1);

	}
	//set_mask to extract set
	int i = b, j;
	while(i < s + b){
		set_mask |= (1L << i);
		i++;
	}

	//tag_mask to extract tag
	tag_mask = (1L << 63 >> (t-1));
	
	//sets= [set], set -> [lines], line -> block(block_size)
	sets = (Set *)malloc(set_size * sizeof(Set));
	if(sets == NULL){
		fprintf(stderr, "malloc error");
	}
	i = 0;
	while (i < set_size){
		Set *set = (sets + i);
		set->lines = (Line *)malloc(E * sizeof(Line));
		if(set->lines == NULL){
			fprintf(stderr, "malloc error");
		}	
		set->count = 0;
		j = 0;
		while( j < E){
			Line *line = (set->lines + j);
			line->valid = 0;
			line->tag = 0;
			line->last = 0;
			j++;
		}
		i++;
	}

	//read file
	char *pos, line[20];
	unsigned long mem_addr = 0;
	int fetch_size = 0;
	while (fgets(line, 20, trace) != NULL){
		pos = strchr(line, ',');
		if(pos == NULL){
			//the trace usally have some lines containing only '\n'
			break;
		}
		if(*line == 'I'){
			//we do not care instruction
			continue;
		}else{
			*pos = '\0';
                        if(*(line + 1) == 'M'){
				*(line+1) = '0';
				*(line+2) = 'x';
				mem_addr = strtoul(line + 1, NULL, 0);
				fetch_size = atoi(pos + 1);
				//cache work!
				cache(mem_addr);
				cache(mem_addr);
				continue;
			}
			*(line+1) = '0';
			*(line+2) = 'x';
			mem_addr = strtoul(line + 1, NULL, 0);
			fetch_size = atoi(pos + 1);
			//cache work!
			cache(mem_addr);
			
		}
	}
	printCachesimResults(hits, misses, evicts);
	//free	
	while (i < set_size){
		Set *set = (sets + i);
		free(set->lines);
	}
	free(sets);
	return 0;	
}
/**
 * cache - Evaluate a memory address with the cache and count hits, misses and 
 * evicts.
 *
 * hit only happens when the line is valid and the tag of the line matches the 
 * the tag of the memory address.
 * miss happens when a cold miss or a evict.
 * evict apply the LUR.
 *
 * Parameters:
 * 	mem_addr : memory address
 */
void cache(unsigned long mem_addr){
	unsigned long set_id, tag_id;
	set_id = (set_mask & mem_addr) >> b;
	tag_id = tag_mask & mem_addr;
	
	Set *set = sets + set_id;
	//should be a pointer
	Line *line;
	int i = 0, lur_line = 0, lur_last = set->lines->last;
	
	while(i < E){
		line = set->lines + i;
		if(line->valid == 0){
			//init & miss
			misses++;
			line->tag = tag_id;
			line->valid = 1;
			set->count++;
			line->last = set->count;
			return;
		}else if(line->tag == tag_id){
			//hit
			hits++;
			set->count++;
			line->last = set->count;
			return;
		}else if(line->last < lur_last){
			//unhit
			lur_last = line->last;
			lur_line = i;
		
		}
		i++;
	}
	
	//evict here
	evicts++;
        misses++;
        line = set->lines + lur_line;
	line->tag = tag_id;
	set->count++;
	line->last = set->count;
	return;
}
