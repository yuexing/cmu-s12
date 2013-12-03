/**
 * @file vm.c
 * @brief The implementation of VM.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug
 */
#include <kernel.h>

static pde_t kpdes[NUM_KPDES];

static int init_kpdes();

static pde_t new_pde(uint32_t,uint32_t);

static pte_t get_pte(void*addr, 
		uint32_t w, uint32_t u, 
		uint32_t g, uint32_t n);

static void startnend(void *vaddr, 
		int st_or_e, page_dir_t*);
static int startorend(void *vaddr, 
		int st_or_e, page_dir_t*);

static int map_zfod(void*,page_dir_t*);

int init_mod_vm()
{
	return init_kpdes();
}

/**
 * @brief ck whether a vaddr has 
 * been mapped
 *
 * @return 1 if yes, 0 if no.
 */
int ck_mapping(void *vaddr, page_dir_t *dir)
{
	int pde_idx = ADR_PDE(vaddr);
	int pte_idx = ADR_PTE(vaddr);

	page_table_t *tbl = NULL;

	if(IS_PRESENT(dir->pdes[pde_idx])){
		tbl=PDE_TBL(dir->pdes[pde_idx]);
		if(IS_PRESENT(tbl->ptes[pte_idx])){
			return 1;
		}
	}
	return 0;
}

/**
 * @brief free mapping for vaddr in
 * VM. We do not care about PHY.
 * During the process, we will free
 * page table IF we CAN. 
 */
void free_v(void *vaddr, page_dir_t *dir)
{
	int pde_idx = ADR_PDE(vaddr);
	int pte_idx = ADR_PTE(vaddr);

	page_table_t *tbl=
	PDE_TBL(dir->pdes[pde_idx]);

	assert(tbl);
	assert(tbl->ptes[pte_idx]);

	tbl->ptes[pte_idx]=0;
	tbl->cnt--;
	if(!tbl->cnt){
		free_tbl(tbl);
		dir->pdes[pde_idx]=0;
	}

	/* flush tlb */
	invl_tlb(vaddr);
}

/**
 * @brief map btw vaddr and paddr,
 * add constrains.
 * During the process, create pde
 * and pte IF NEEDED.
 * 
 * @return ERROR on failure.
 */
int map_vp(void *vaddr, void *paddr,
	page_dir_t *dir, uint32_t w, 
	uint32_t u)
{
	// should be aligned
	assert(paddr==(void*)PAGE_ROUND_DOWN(paddr));

	int pde_idx = ADR_PDE(vaddr);
	int pte_idx = ADR_PTE(vaddr);
	int pde_tmp = 0;

	/* alloc a pde IF */
	if(!IS_PRESENT(dir->pdes[pde_idx])){
		pde_tmp = new_pde(1,1);

		if(pde_tmp == ERROR)
			return ERROR;

		dir->pdes[pde_idx]=pde_tmp;
	}

	page_table_t *tbl=
		PDE_TBL(dir->pdes[pde_idx]);

	/* cnt++ IF */
	if(!IS_PRESENT(tbl->ptes[pte_idx])){
		tbl->cnt++;
	} else { 
		debug_vm("M %p(v)->%p(old_p)->%p(p)"
			,vaddr,(void*)((tbl->ptes[pte_idx])&ADDR_MASK),
			paddr);
	}

	/* Map v->p */
	tbl->ptes[pte_idx]=get_pte(paddr,w,u,0,0);

	/* flush tlb */
	invl_tlb(vaddr);

	return SUCCESS;
}

/**
 * @return If the tbl has been consumed, return 1, else
 * 0.
 */
int map_vp_tbl(void *vaddr, void *tbl, void *paddr,
		page_dir_t *dir, uint32_t w, uint32_t u)
{
	assert(paddr==(void*)PAGE_ROUND_DOWN(paddr));
	assert(tbl==(void*)PAGE_ROUND_DOWN(tbl));

	int pde_idx = ADR_PDE(vaddr);
	int pte_idx = ADR_PTE(vaddr);
	int ret = 0;

	// use table IF
	if(!IS_PRESENT(dir->pdes[pde_idx])){
		dir->pdes[pde_idx]=(uint32_t)tbl;
		SET_PWU(dir->pdes[pde_idx],1,1);
		ret = 1;
	}

	page_table_t *ttbl
		= PDE_TBL(dir->pdes[pde_idx]);

	/* cnt++ IF */
	if(!IS_PRESENT(ttbl->ptes[pte_idx])){
		ttbl->cnt++;
	}else{  
		debug_vm("M %p(v)->%p(old_p)->%p(p)"
			,vaddr,(void*)((ttbl->ptes[pte_idx])&ADDR_MASK),
			paddr);
	}

	/* Map v->p */
	ttbl->ptes[pte_idx]=get_pte(paddr,w,u,0,0);

	/* flush tlb */
	invl_tlb(vaddr);

	return ret;
}

/**
 * @brief map a vaddr to 'read-only
 * zfod' frame.
 */
static int map_zfod(void *vaddr, page_dir_t *dir)
{
	return map_vp(vaddr,zfod_frame,dir,0,1);
}

/**
 * @brief map a frame to base
 * @pre base is 'zfod'
 */
int new_mapping(void *base, page_dir_t *dir)
{
	void *frame = NULL;

	if(alloc_nframes(1,&frame)==ERROR){
		return ERROR;
	}

	int st = startorend(base,1,dir),
		end = startorend(base,0,dir);

 	if(map_vp(base,frame,dir,1,1)
			== ERROR){
		free_nframes(1,&frame);
		return ERROR;
	}

	if(st){
		startnend(base,1,dir);
	} 
	if(end){
		startnend(base,0,dir);
	}
	memset(base,0,PAGE_SIZE);
	return SUCCESS;
}

/**
 * @brief destroy current paging.
 */
void destroy_dir(page_dir_t *dir, process_t *pcb)
{
	/* count frames */
	int ptes =	cnt_user_ptes(dir);

	void **frames = (void**)
		malloc(ptes*sizeof(void*));
	
	assert(frames);

	/* free frames at once */
	load_user_frms(ptes,frames, dir);

	free_nframes(ptes, frames);
	
	/* avoid leak */
	free(frames);

	/* set up cr3 */
	pcb->dir = tmp_dir;
	if(pcb == THREAD_PCB_CUR)
		set_cr3((uint32_t)tmp_dir);

	/* free the dir mapping */
	//sim_unreg_process(pcb->dir);
	free_dir(dir);
}

/**
 * @brief free ptes from the dir by
 * free tables.
 */
void clr_dir(page_dir_t *dir)
{
	int i = NUM_KPDES;
	page_table_t *tbl = NULL;
	/* free tables as we can */
	for(;i<TABLE_SIZE;i++){
		if(IS_PRESENT(
					dir->pdes[i])){
			tbl=PDE_TBL(
					dir->pdes[i]);
			free_tbl(tbl);
			dir->pdes[i] = 0;
		}
	}
}

/**
 * @brief free ptes from the dir by
 * free tables and the dir
 */
void free_dir(page_dir_t *dir)
{
	clr_dir(dir);
	free_page((void*)dir);
}

/**
 * @brief count the user space ptes
 */
int cnt_user_ptes(page_dir_t *dir)
{
	int i = NUM_KPDES, ptes = 0;
	page_table_t *tbl = NULL;

	for(; i < TABLE_SIZE; i++){
		if(!IS_PRESENT
				(dir->pdes[i]))
			continue;

		tbl=PDE_TBL
			(dir->pdes[i]);
		ptes += tbl->cnt;
	}
	return ptes;
}

/**
 * @brief Load paddr of frames into
 * paddrs. 
 * cnt is for assert.
 */
void load_user_frms(int cnt, void **paddrs, 
		page_dir_t* dir)
{
	if(!cnt) return;

	int i = NUM_KPDES, j, k, f=0;
	page_table_t *tbl = NULL;

	for(; i < TABLE_SIZE; i++){
		if(!IS_PRESENT
				(dir->pdes[i]))
			continue;

		/* process the pde */
		tbl=PDE_TBL
			(dir->pdes[i]);
		for(j=0,k=0;j<TABLE_SIZE
			&&k<tbl->cnt;j++){
			
			if(IS_PRESENT(
				tbl->ptes[j])){
				/* record */
				paddrs[f++]=(void*)
				PAGE_ROUND_DOWN(
					tbl->ptes[j]);
				k++;
			}
		}
	}
	assert(f==cnt);
}

/**
 * @brief clone the given dir to 
 * create a new dir. The new dir 
 * will have cloned all the user 
 * space pages, created a new kstk,
 * installed kpdes.
 *
 * @return NULL on some failure.
 */
page_dir_t *clone_dir(page_dir_t *old_dir,
		void **pkstk)
{
	int i = NUM_KPDES, j, k, f = 0, w;
	page_table_t *old_tbl = NULL;
	void *vaddr = NULL;

	// calculate user space ptes
	int ptes = cnt_user_ptes(old_dir);

	// alloc a new dir
	page_dir_t *dir
		=(page_dir_t*)new_page();
	if(!dir){
		goto dir_error;
	}

	// alloc a new kstk
	void *kstk=new_page();
	if(!kstk){
		goto kstk_error;
	}

	// alloc all frames(ptes)
	void **frames =
		(void**)malloc(ptes*sizeof(void*));
	if(!frames){
		goto frames_error;
	}

	if(alloc_nframes(ptes, frames)
			== ERROR){
		goto ptes_error;
	}

	// clone user space pages
	for(;i<TABLE_SIZE;i++){
		if(!IS_PRESENT(old_dir->pdes[i])){
			continue;
		}

		old_tbl=PDE_TBL(old_dir->pdes[i]);
		// copy every present frame
		j = 0, k = 0;
		while(j<old_tbl->cnt
				&&k<TABLE_SIZE){
			if(IS_PRESENT(old_tbl->ptes[k])){
				// vaddr for (old_dir,i,k)
				vaddr=get_vaddr(i,k);
				w = 0;
				if(IS_WRITABLE(old_tbl->ptes[k])){
					w = 1;
				}
				if(map_vp(vaddr,frames[f],dir,w,1)
						==ERROR){
					goto cp_error;
				}
				// map frame[f] to kstk to memmove.
				map_vp(kstk,frames[f],old_dir,1,1);
				// copy btw (old_dir,i,k) and kstk.
				memmove(kstk,vaddr,PAGE_SIZE);
				f++;
				j++;
			}
			k++;
		}
	}

	assert(f==ptes);

	// install kpdes
	install_kpdes(dir);

	//reset kstk
	map_vp(kstk,kstk,old_dir,1,0);
	memset(kstk,0,PAGE_SIZE);
	*pkstk = kstk;

	// free frames
	free(frames);

	// done!
	return dir;

cp_error:
	// free frames and tables
	free_nframes(ptes,frames);
	clr_dir(dir);
ptes_error:
	free(frames);
frames_error:
	free_page(kstk);
kstk_error:
	free_page(dir);
dir_error:
	return NULL;
}

/**
 * @brief add kernel pdes to the dir
 */
void install_kpdes(page_dir_t *dir)
{
	int i = 0;
	for(; i < NUM_KPDES; i++){
		dir->pdes[i] = kpdes[i];
	}
}

/**
 * @brief corresponds to new_pages. 
 * 'ZFOD' pages starting with 
 * START and ending with END.
 *
 * @prev start and len is page-
 * aligned
 */
int new_nmapping(void* start, int len, 
		page_dir_t *dir)
{
	void *alloced = start,
		 /* actually the END page 
		  * to mark. */
		 *end = start +(len-PAGE_SIZE);

	/* ck first */
	while(alloced <= end){
		if(ck_mapping(alloced,dir)){
			/* ERROR case */
			goto err;
		}
		alloced+=PAGE_SIZE;
	}

	/* map vp now */
	alloced = start;
	while(alloced <= end){
		if(map_zfod(alloced,dir)
				==ERROR){
			// rollback
			while(start < alloced){
				free_v(start, dir);
				start += PAGE_SIZE;
			}
			goto err;
		}
		alloced+=PAGE_SIZE;
	}

	/* mark start and end */
	startnend(start, 1, dir);
	startnend(end, 0, dir);

	return SUCCESS;

err:
	return ERROR;
}

/**
 * @brief corresponds to remove_pages.
 * Free pages starting with START
 * and ending with END.
 */
int free_nmapping(void *start, page_dir_t *dir)
{
	if(!startorend(start,1,dir)){
		return ERROR;
	}

	void *cked = start, *tmp;
	int ptes = 0;

	while(1){
		if(!ck_zfod(dir,cked)){
			// free these frames
			ptes++;
		}
		if(startorend(cked,0,dir)){
			break;
		}
		cked+=PAGE_SIZE;
	}

	// find the end
	void *end = cked;

	// alloc paddrs
	void **paddrs=(void**)
		malloc(ptes*sizeof(void*));
	if(!paddrs && ptes){
		return ERROR;
	}

	// free mapping and count paddrs
	int i = 0;
	cked = start;
	while(cked <= end){
		if((tmp=get_paddr(dir,cked))
				!=zfod_frame){
			paddrs[i] = tmp;
			i++;
		}
		free_v(cked,dir);
		cked+=PAGE_SIZE;
	}

	// assert
	assert(i==ptes);

	// free all frames
	free_nframes(ptes, paddrs);

	// free paddrs
	free(paddrs);
	return SUCCESS;
}

/**
 * @brief set start/end bit for 
 * vaddr.
 *
 * @param st_or_e 1 to set start 
 * bit, 0 to set end bit.
 */
static void startnend(void *vaddr, 
		int st_or_e, page_dir_t *dir)
{
	int pde_idx = ADR_PDE(vaddr);
	int pte_idx = ADR_PTE(vaddr);

	page_table_t *tbl
		=PDE_TBL(dir->pdes[pde_idx]);

	if(st_or_e){
		SET_START(tbl->ptes[pte_idx]);
	} else {
		SET_END(tbl->ptes[pte_idx]);
	}
}

/**
 * @brief ck start/end bit for 
 * vaddr.
 *
 * @param st_or_e 1 to ck start 
 * bit, 0 to ck end bit.
 */
static int startorend(void *vaddr, 
		int st_or_e, page_dir_t *dir)
{
	int pde_idx = ADR_PDE(vaddr);
	int pte_idx = ADR_PTE(vaddr);

	page_table_t *tbl
		=PDE_TBL(dir->pdes[pde_idx]);

	if(st_or_e){
		return IS_START(tbl->ptes[pte_idx]);
	} else {
		return IS_END(tbl->ptes[pte_idx]);
	}
}

/**
 * @brief Create a page_aligned 0-filled
 * page table.
 * return NULL when new resource available.
 */
page_table_t *new_tbl()
{
	void *tbl=
		smemalign(PAGE_SIZE,sizeof(page_table_t));
	if(tbl){
		memset((void*)tbl,0,sizeof(page_table_t));
	}
	return tbl;
}

/**
 * @brief free a page table. A 
 * new_tbl should be Freed in
 * this way.
 */
void free_tbl(page_table_t *tbl)
{
	sfree(tbl, sizeof(page_table_t));
}

/**
 * @brief Create a page_aligned 0-filled
 * PAGE_SIZE page.
 * return NULL when new resource available.
 */
void *new_page()
{
	void *page=
		smemalign(PAGE_SIZE,PAGE_SIZE);
	if(page){
		memset(page,0,PAGE_SIZE);
	}
	return page;
}

/**
 * @brief Free a page. A new_page
 * should be Freed in this way.
 */
void free_page(void *page)
{
	sfree(page, PAGE_SIZE);
}

/**
 * @brief Create a page table, and 
 * return the corresponding pde.
 */
static pde_t new_pde(uint32_t w, uint32_t u)
{
	pde_t pde = (pde_t)new_tbl();
	if(!pde){
		return ERROR;
	}
	SET_PWU(pde,w,u);
	return pde;
}

/**
 * @brief Get a pte corresponding to
 * the paddr and constrains.
 */
static pte_t get_pte(void*addr, uint32_t w, 
		uint32_t u, uint32_t g, uint32_t n)
{
	pte_t pte = PAGE_ROUND_DOWN(addr);
	SET_PWUGN(pte,w,u,g,n);
	return pte;
}

/**
 * @brief Initialize the kernel 
 * page tables into kpdes[], and 
 * they will be shared by all 
 * processes.
 */
static int init_kpdes()
{
	int i = 0, j = 0;
	void *start = 0;
	page_table_t *tmp_pt = NULL;
	for(; i < NUM_KPDES; i++){
		/* Create a page table
		 * give the pde more options*/
		kpdes[i] = new_pde(1,1);	
		if(kpdes[i] == ERROR){
			return ERROR;
		}
		tmp_pt=PDE_TBL(kpdes[i]);
		/* map start to start */
		for(j=0; j<TABLE_SIZE; 
			j++, start+=PAGE_SIZE){
			tmp_pt->ptes[j]=
				get_pte(start,1,0,1,0);
			tmp_pt->cnt++;
		}
		assert(tmp_pt->cnt
				== TABLE_SIZE);
	}
	assert(((uint32_t)start)==USER_MEM_START);
	return SUCCESS;
}

/**
 * @brief Get the pte according to vaddr.
 * @pre The vaddr is PRESENT.
 */
pte_t v2pte(page_dir_t *dir, void *vaddr)
{
	int pde_idx = ADR_PDE(vaddr);
	int pte_idx = ADR_PTE(vaddr);

	page_table_t *tbl
		=PDE_TBL(dir->pdes[pde_idx]);
	assert(tbl);
	return tbl->ptes[pte_idx];
}

/**
 * @brief Check if this vaddr is zfod.
 */
int ck_zfod(page_dir_t *dir, void *vaddr)
{
	int pde_idx = ADR_PDE(vaddr);
	int pte_idx = ADR_PTE(vaddr);

	page_table_t *tbl
		=PDE_TBL(dir->pdes[pde_idx]);
	if(!tbl){
		return 0;
	}
	pte_t pte = tbl->ptes[pte_idx];
	return (PAGE_ROUND_DOWN(pte)
			==(uint32_t)zfod_frame);
}

/**
 * @brief Get the paddr according to
 * vaddr.
 */
void*get_paddr(page_dir_t *dir, void *vaddr)
{
	int pde_idx = ADR_PDE(vaddr);
	int pte_idx = ADR_PTE(vaddr);

	page_table_t *tbl
		=PDE_TBL(dir->pdes[pde_idx]);

	return (void*)((PAGE_ROUND_DOWN(tbl->ptes[pte_idx])|((uint32_t)(vaddr)&OFFSET_MASK)));
}

/**
 * @brief Get the vaddr according to
 * (pde, pte). 
 */
void *get_vaddr(int pde_idx, int pte_idx)
{
	return (void*)(pde_idx * SIZE_PER_TABLE 
			+ (pte_idx << PAGE_SHIFT));
}

/**
 * @brief enable paging, enable global page.
 */
void enable_paging()
{
	uint32_t cr4 = get_cr4();
	cr4 |= CR4_PGE;
	set_cr4(cr4);

	uint32_t cr0 = get_cr0();
	cr0 |= CR0_PE;
	cr0 |= CR0_PG;
	set_cr0(cr0);
}
