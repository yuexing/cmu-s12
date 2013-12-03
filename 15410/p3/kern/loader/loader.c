/**
 * @file loader.c
 * @brief implementation of the loader.
 * Note, the program format this kernel supports
 * is as follows:
 * bss    <--- w/r
 * data
 * rodata <--- read-only
 * text
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */

#include <kernel.h>

int ls_size;
int ls_n;
char *ls_names;

static int get_names();
static int cnt_elf_ptes(simple_elf_t*, void**, void**);

/**
 * @brief init the loader module.
 */
int init_mod_loader()
{
	return get_names();
}

/**
 * Copies data from a file into a buffer.
 *
 * @param filename   the name of the file to copy data from
 * @param offset     the location in the file to begin copying from
 * @param size       the number of bytes to be copied
 * @param buf        the buffer to copy the data into
 *
 * @return returns the number of bytes copied on succes; -1 on failure
 */
int getbytes(const char *filename, int offset, 
		int size, char *buf )
{
	int i = 0;
	for(; i < exec2obj_userapp_count
			&& i < MAX_NUM_APP_ENTRIES; i++){
		if(!strcmp(filename, 
					exec2obj_userapp_TOC[i].execname)){
			memmove(buf, exec2obj_userapp_TOC[i].execbytes
					+ offset, (size_t)size);
			return size;
		}
	}
	return ERROR;
}

/**
 * @brief Test whether an exec exist.
 * @return ERROR if not, index if yes.
 */
int exist(const char *filename)
{
	int i = 0;
	for(; i < exec2obj_userapp_count
			&& i < MAX_NUM_APP_ENTRIES; i++){
		if(!strcmp(filename, 
					exec2obj_userapp_TOC[i].execname)){
			return i;
		}
	}
	return ERROR;
}

/**
 * @brief Initialize ls_size and
 * ls_names.
 */
static int get_names()
{
	ls_size = 0;
	int i = 0, j = 0;
	/* calculate size */
	for(;i<exec2obj_userapp_count;
			i++){
		ls_size += 
			strlen(exec2obj_userapp_TOC[i].execname) + 1;
	}
	ls_n = i;
	ls_size++;

	/* cp names */
	ls_names=(char*)malloc(ls_size);
	if(!ls_names){
		return ERROR;
	}

	char *cp = ls_names;
	for(i=0;i<exec2obj_userapp_count;
			i++){
		j = strlen(exec2obj_userapp_TOC[i].execname) + 1;
		strcpy(cp, exec2obj_userapp_TOC[i].execname);
		cp += j;
	}
	cp[0] = 0;

	assert(ls_size == (cp-ls_names+1));

	return SUCCESS;
}

/**
 * calculate the highest read/write part addr
 * bss    <--- w/r
 * data
 * rodata <--- read-only
 * text
 */
static int cnt_elf_ptes(simple_elf_t* elf, 
		void** rhigh, void** whigh)
{

	uint32_t high = 0;
	if(elf->e_rodatstart>0){
		*rhigh= (void*)(elf->e_rodatstart+elf->e_rodatlen);
		high = (elf->e_rodatstart+elf->e_rodatlen);
	} else {
		*rhigh = (void*)(elf->e_txtstart+elf->e_txtlen);
		high = (elf->e_txtstart+elf->e_txtlen);
	}

	if(elf->e_bssstart>0){
		*whigh = (void*)(elf->e_bssstart+elf->e_bsslen);
		high = (elf->e_bssstart+elf->e_bsslen);
	} else if(elf->e_datstart>0){
		*whigh = (void*)(elf->e_datstart+elf->e_datlen);
		high = (elf->e_datstart+elf->e_datlen);
	} else {
		/* no data and bss at all*/
		*whigh = 0;
	}

	high -= USER_MEM_START;
	return PAGES(high);
}

/**
 * @brief Load the program 
 * according to fname to the 
 * process's page dir.
 *
 * Rollback on ERROR.
 *
 * @return esp/ERROR
 */
int load_program(const char *fname,
		char **argv, int argc,
		page_dir_t *dir, uint32_t* pentry)
{
	simple_elf_t elf;

	// Initialize a elf struct
	if(elf_load_helper(&elf, fname)==ELF_NOTELF){
		goto err;
	}	

	// set up entry
	*pentry = elf.e_entry;

	// exec starting byte
	int i = exist(fname);
	assert(i != ERROR);
	void *exec=(void*)exec2obj_userapp_TOC[i].execbytes;

	// calculate ptes
	int ptes 
		= cnt_user_ptes(dir);

	// calculate highest addr
	void *rhigh = NULL, *whigh = NULL;
	int ptes_need = 
		cnt_elf_ptes(&elf, &rhigh, &whigh);

	// calculate _main arguments
	int abytes = 0;
	for(i=0;i<argc;i++){
		abytes += (strlen(argv[i]) + 1);
	}
	abytes = WORD_RD_UP(abytes) + 
		(argc + 1 + 4) * WORD_SIZE;

	// allocate a kernel area for args
	// before remap.
	void *aregion = 
		memalign(WORD_SIZE,abytes);
	if(!aregion){
		goto err;
	}
	// start is for assert
	void *aregion_start = aregion;
	aregion += abytes;

	// alloc for arrinfo
	uint32_t *arrinfo = NULL, len;
	if(argc){
		arrinfo=(uint32_t*)
			malloc(argc*WORD_SIZE);
		if(!arrinfo){
			goto arrinfo_err;
		}
	}

	// pages for mapping 
	int apages = PAGES(abytes);

	// THE PTES we need!
	int tbls_need = TABLES(ptes_need)
		+ TABLES(apages);
	ptes_need += apages;

	// The page tables we need!
	void **tbls = malloc(tbls_need*WORD_SIZE);
	if(!tbls){
		goto tbl_err;
	}
	int j;
	for(i=0;i<tbls_need;i++){
		tbls[i] = new_tbl();
		if(!tbls[i]){
			// rollback 
			for(j = 0; j < i; j++){
				free_tbl(tbls[j]);
			}
			goto newtbl_err;
		}
	}

	// load all frames
	void **frames = malloc(ptes*WORD_SIZE);
	if(ptes && !frames){
		goto frame_err;
	}
	load_user_frms(ptes,frames,dir);
	debug_load("old ptes: %d", ptes);

	// allocate/free frame rsrcs
	if(ptes_need > ptes){
		frames = realloc(frames,
				ptes_need*WORD_SIZE);
		if(!frames){
			goto alloc_err;
		}

		if((alloc_nframes(
						ptes_need-ptes,
						frames + ptes))
				== ERROR){
			goto alloc_err;
		}
	} else if(ptes_need < ptes){
		free_nframes(ptes-ptes_need, frames);
		frames = frames + (ptes-ptes_need);
	}
	debug_load("new ptes:%d, new tbls:%d",ptes_need,
			tbls_need);

	// filling the argregion now.
	debug_call("argc:%d, abytes:%d, apages:%d",
			argc, abytes, apages);
	void *esp = (void*)USER_STACK;
	void *esp_arr = esp;
	char *arr = (char*)aregion;

	// cp strings 
	for(i=argc-1;i>-1; i--){
		len = strlen(argv[i]) + 1;
		arr-=len;
		esp_arr-=len;
		strcpy(arr, argv[i]);
		// arrinfo stores the addr
		arrinfo[i]=(uint32_t)esp_arr;
	}
	// finish cp strings
	aregion = (void*)WORD_RD_DN(arr);
	esp = (void*)WORD_RD_DN(esp_arr);
	// assert
	assert(aregion-(void*)arr==esp-esp_arr);

	int *iArgs = (int*)aregion;
	// cp NULL-ended argvs
	*(--iArgs) = 0;
	esp -= 4;
	if(argc){
		for(i=argc-1;i>-1;i--){
			*(--iArgs) = arrinfo[i]; 
			esp -= 4;
		}
		// Free arrinfo
		assert(*((int*)arrinfo-1)==(argc+1)*WORD_SIZE);
		free(arrinfo);
		arrinfo = NULL;
	}
	// update argv 
	argv=(char**)esp;
	// _main(argc, argv, hi, lo)
	esp -= 4 * WORD_SIZE;
	*(--iArgs) = PAGE_ROUND_DOWN(esp);
	*(--iArgs) = USER_STACK;
	*(--iArgs) = (int)argv;
	*(--iArgs) = argc;
	aregion = (void*)iArgs;
	assert(aregion == aregion_start);

	// clr previous mapping
	clr_dir(dir);

	// we have all frames and tables
	// and a clean page table
	// now do the map vp
	void *start =(void*)USER_MEM_START;
	int f = 0, t = 0;
	/* read */
	do{
		if(map_vp_tbl(start,tbls[t],frames[f++],dir,0,1)){
			t++;
		}
		start += PAGE_SIZE;		
	}while(start <= rhigh);

	/* write */
	if(whigh){
		do{
			if(map_vp_tbl(start,tbls[t],frames[f++],dir,1,1)){
				t++;
			}
			start += PAGE_SIZE;		
		}while(start <= whigh);
	}

	/* user stack */
	start =(void*)PAGE_ROUND_DOWN(USER_STACK);
	for(i=0;i<apages;i++){
		if(map_vp_tbl(start,tbls[t],frames[f++],dir,1,1)){
			t++;
		}
		start-=PAGE_SIZE;
	}
	assert(f==ptes_need);
	assert(t==tbls_need);

	/* free frames*/
	if(ptes_need < ptes){
		frames = 
			frames-(ptes-ptes_need);
	}

	// check malloc
	assert(*((int*)tbls-1)==(tbls_need+1)*WORD_SIZE);
	if(ptes_need > ptes)
		assert(*((int*)frames-1)==(ptes_need+1)*WORD_SIZE);
	else 
		assert(*((int*)frames-1)==(ptes+1)*WORD_SIZE);

	free(frames);
	free(tbls);

	// useful when load(char*)
	set_cr3((uint32_t)dir);

	// we have done the mapping
	// now do the memmove
	memmove((void*)elf.e_txtstart,
			exec+elf.e_txtoff, elf.e_txtlen);
	memmove((void*)elf.e_datstart,
			exec+elf.e_datoff, elf.e_datlen);
	memmove((void*)elf.e_rodatstart,
			exec+elf.e_rodatoff, elf.e_rodatlen);
	memmove(esp, aregion, abytes);
	// assert
	assert(*(int*)esp == argc);

	// free aregion now
	free(aregion);

	/* return esp */
	esp -= 4;
	return (int)esp;

alloc_err:
	if(frames) free(frames);
frame_err:
	if(tbls_need){
		for(i=0;i<tbls_need;i++)
			free_tbl(tbls[i]);
	}
newtbl_err:
	if(tbls)free(tbls);
tbl_err:
	if(arrinfo) free(arrinfo);
arrinfo_err:
	free(aregion);
err:
	return ERROR;
}
