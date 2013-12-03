/**
 * @file autostack.
 * @brief handle stack auto-growth. Called
 * int crt0.c before launch _main.
 * @author Yue Xing(yuexing)
 * @bug Rules needs to differiate stack from
 * other area [heap]
 */
#include <thr_internals.h>

static char esp3[ESP3_SIZE];
#define AESP3				((void*)esp3+ESP3_SIZE)

void *_main_ebp;

static void autostack(void *arg, ureg_t *ureg);

/**
 * @brief Register the autostack handler.
 */
void install_autostack(void *stack_high, void *stack_low)
{
	_main_ebp = *((void**)get_ebp());

	int ret = 
		swexn(AESP3, autostack, NULL, NULL);
	assert(!ret);
}

/**
 * @brief This function only handles automatic
 * stack growth.
 */
static void autostack(void *arg, ureg_t *ureg)
{
	void *base = NULL;

	if(ureg->cause == SWEXN_CAUSE_PAGEFAULT
			&& IS_NON_PRESENT(ureg->error_code)){

		base = (void*)PAGE_RD_DN(ureg->cr2);	

		if(new_pages(base, PAGE_SIZE)){
			/* when new_page fail we panic */
			panic ("FATAL: Stack Growth Failed!\n");
		}

		lprintf("AUTO: %p", base);
		memset(base, 0, PAGE_SIZE);
	} 
	/* always re-install */
	int ret = swexn(AESP3, autostack, 0, ureg);
	lprintf("%d", ret);
}
