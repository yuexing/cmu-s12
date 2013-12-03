/** 
 * @file handler.c
 * @brief The handler controller which takes care
 * of handler installation.
 *
 * @author Yue Xing (yuexing)
 * @bugs Not Found
 */
#include <kernel.h>

#define ENTRY_SIZE	8
#define IDT_INTR	0x8e			/*10001110*/
#define IDT_TRAP	0x8f			/*10001111*/	
#define IDT_SYSCALL	0xef			/*11101111*/

typedef struct {
	uint16_t base_lo; /* lower 16 bits of the base */
	uint16_t segsel; /* kernel segsel */
	uint8_t always0; /* 0 - 7 are 0*/
	uint8_t flags; /* for interrupt/trap/syscall */
	uint16_t base_hi;
} idt_gate_t;

static void _idt_set(uint8_t num, uint32_t base, uint8_t flags);

/**
 * @brief install an interrupt handler in idt.
 * Remember to use this for 14, 32, 33.
 * 
 * @param num
 * The offset of the entry to be set in the IDT
 * 
 * @param base
 * the address of the handler function
 */
static void idt_set_interrupt(uint8_t num, uint32_t base) {
	_idt_set(num, base, IDT_INTR);
}

/**
 * @brief install a system call trap handler in idt.
 * 
 * @param num
 * The offset of the entry to be set in the IDT
 * 
 * @param base
 * The address of the handler function
 */
static void idt_set_syscall(uint8_t num, uint32_t base) {
	_idt_set(num, base, IDT_SYSCALL);
}

/**
 * @brief install a trap handler in idt.
 * This is always used for interrupt.
 * 
 * @param num
 * The offset of the entry to be set in the IDT
 * 
 * @param base
 * The address of the handler function
 */
static void idt_set_trap(uint8_t num, uint32_t base) {
	_idt_set(num, base, IDT_TRAP);
}

/**
 * @brief Installs a handler at the offset in the IDT.
 * 
 * @param num
 *  The offset of the entry to be set in the IDT
 * 
 * @param base
 * The address of the handler function
 * 
 * @param flags
 * The type of the handler. For interrupt gate, IDT_INTR is used;
 * For trap gate, IDT_TRAP is used;
 * For the software interrupt IDT_SYSCALL is used
 */
static void _idt_set(uint8_t num, uint32_t base, uint8_t flags) {
	idt_gate_t e_idt;

	e_idt.base_lo = (uint16_t) base;
	e_idt.segsel = SEGSEL_KERNEL_CS;
	e_idt.always0 = 0;
	e_idt.flags = flags;
	e_idt.base_hi = (uint16_t) (base >> 16);

	/* install to idt */
	char *idt = (char *) idt_base();
	char *idt_offset = idt + ENTRY_SIZE * num;
	memcpy((void*) idt_offset, (void *) &e_idt, sizeof (idt_gate_t));
}

static handler_t handlers[128];

/**
 * @brief 
 * Put the address of an interrupt handler into the array handlers[].
 * The address stored at the offset X of the array is the address of the
 * corresponding interrupt handler routine for IDT X.
 */
void idt_routine_install(uint8_t num, handler_t base) {
	handlers[num] = base;
}

/**
 * @brief install syscall handlers into idt.
 */
static void syscall_init(void) 
{
	idt_routine_install(EXEC_INT, kexec);
	idt_routine_install(WAIT_INT, kwait);
	idt_routine_install(YIELD_INT, kyield);
	idt_routine_install(DESCHEDULE_INT, kdeschedule);
	idt_routine_install(MAKE_RUNNABLE_INT, kmake_runnable);
	idt_routine_install(GETTID_INT, kgettid);
	idt_routine_install(NEW_PAGES_INT, knew_pages);
	idt_routine_install(REMOVE_PAGES_INT, kremove_pages);
	idt_routine_install(SLEEP_INT, ksleep);
	idt_routine_install(GETCHAR_INT, kgetchar);
	idt_routine_install(READLINE_INT, kreadline);
	idt_routine_install(PRINT_INT, kprint);
	idt_routine_install(SET_TERM_COLOR_INT, kset_term_color);
	idt_routine_install(SET_CURSOR_POS_INT, kset_cursor_pos);
	idt_routine_install(GET_CURSOR_POS_INT, kget_cursor_pos);
	idt_routine_install(GET_TICKS_INT, kget_ticks);
	idt_routine_install(MISBEHAVE_INT, kmisbehave);
	idt_routine_install(HALT_INT, khalt);
	idt_routine_install(LS_INT, kls);
	idt_routine_install(TASK_VANISH_INT, ktask_vanish);
	idt_routine_install(SET_STATUS_INT, kset_status);
	idt_routine_install(VANISH_INT, kvanish);
	idt_routine_install(SWEXN_INT, kswexn);
}


/**
 * @brief install exception handlers into idt
 */
static void exception_init(void)
{
	idt_routine_install(SWEXN_CAUSE_DIVIDE, basic_handle);
	idt_routine_install(SWEXN_CAUSE_DEBUG, basic_handle);
	idt_routine_install(SWEXN_CAUSE_BREAKPOINT, basic_handle);
	idt_routine_install(SWEXN_CAUSE_OVERFLOW, basic_handle);
	idt_routine_install(SWEXN_CAUSE_BOUNDCHECK, basic_handle);
	idt_routine_install(SWEXN_CAUSE_OPCODE, basic_handle);
	idt_routine_install(SWEXN_CAUSE_NOFPU, basic_handle);
	idt_routine_install(SWEXN_CAUSE_SEGFAULT, basic_handle);
	idt_routine_install(SWEXN_CAUSE_STACKFAULT, basic_handle);
	idt_routine_install(SWEXN_CAUSE_PROTFAULT, basic_handle);
	idt_routine_install(SWEXN_CAUSE_FPUFAULT, basic_handle);
	idt_routine_install(SWEXN_CAUSE_ALIGNFAULT, basic_handle);
	idt_routine_install(SWEXN_CAUSE_SIMDFAULT, basic_handle);
	/* page fault */
	idt_routine_install(SWEXN_CAUSE_PAGEFAULT, pf_handle);
}

/** 
 * @brief The driver-library initialization function
 * Install the interrupt handlers in the IDT table
 * Install timer, kb, syscall software routines
 *
 * @param tickback 
 * Pointer to clock-tick callback function
 *   
 * @return A negative error code on error, or 0 on success
 **/
int init_mod_handler() {
	timer_init();
	kb_init();
	syscall_init();
	exception_init();

	idt_set_trap(0, (uint32_t) handle_no_errorcode0);
	idt_set_trap(1, (uint32_t) handle_no_errorcode1);
	idt_set_trap(2, (uint32_t) handle_no_errorcode2);
	idt_set_trap(3, (uint32_t) handle_no_errorcode3);
	idt_set_trap(4, (uint32_t) handle_no_errorcode4);
	idt_set_trap(5, (uint32_t) handle_no_errorcode5);
	idt_set_trap(6, (uint32_t) handle_no_errorcode6);
	idt_set_trap(7, (uint32_t) handle_no_errorcode7);
	/* 8 has been installed */
	idt_set_trap(9, (uint32_t) handle_no_errorcode9);
	idt_set_trap(10, (uint32_t) handle_errorcode10);
	idt_set_trap(11, (uint32_t) handle_errorcode11);
	idt_set_trap(12, (uint32_t) handle_errorcode12);
	idt_set_trap(13, (uint32_t) handle_errorcode13);
	idt_set_trap(14, (uint32_t) handle_errorcode14);
	idt_set_trap(15, (uint32_t) handle_no_errorcode15);
	idt_set_trap(16, (uint32_t) handle_no_errorcode16);
	idt_set_trap(17, (uint32_t) handle_errorcode17);
	idt_set_trap(18, (uint32_t) handle_no_errorcode18);
	idt_set_trap(19, (uint32_t) handle_no_errorcode19);
	idt_set_trap(20, (uint32_t) handle_no_errorcode20);
	idt_set_trap(21, (uint32_t) handle_no_errorcode21);
	idt_set_trap(22, (uint32_t) handle_no_errorcode22);
	idt_set_trap(23, (uint32_t) handle_no_errorcode23);
	idt_set_trap(24, (uint32_t) handle_no_errorcode24);
	idt_set_trap(25, (uint32_t) handle_no_errorcode25);
	idt_set_trap(26, (uint32_t) handle_no_errorcode26);
	idt_set_trap(27, (uint32_t) handle_no_errorcode27);
	idt_set_trap(28, (uint32_t) handle_no_errorcode28);
	idt_set_trap(29, (uint32_t) handle_no_errorcode29);
	idt_set_trap(30, (uint32_t) handle_no_errorcode30);
	idt_set_trap(31, (uint32_t) handle_no_errorcode31);
	idt_set_interrupt(32, (uint32_t) handle_no_errorcode32);
	idt_set_interrupt(33, (uint32_t) handle_no_errorcode33);
	idt_set_trap(38, (uint32_t) handle_no_errorcode38);

	/* syscall */
	idt_set_syscall(65, (uint32_t) handle_fork);

	idt_set_syscall(66, (uint32_t) handle_no_errorcode66);
	idt_set_syscall(67, (uint32_t) handle_no_errorcode67);
	idt_set_syscall(68, (uint32_t) handle_no_errorcode68);
	idt_set_syscall(69, (uint32_t) handle_no_errorcode69);
	idt_set_syscall(70, (uint32_t) handle_no_errorcode70);
	idt_set_syscall(71, (uint32_t) handle_no_errorcode71);
	idt_set_syscall(72, (uint32_t) handle_no_errorcode72);
	idt_set_syscall(73, (uint32_t) handle_no_errorcode73);
	idt_set_syscall(74, (uint32_t) handle_no_errorcode74);
	idt_set_syscall(75, (uint32_t) handle_no_errorcode75);
	idt_set_syscall(76, (uint32_t) handle_no_errorcode76);
	idt_set_syscall(77, (uint32_t) handle_no_errorcode77);
	idt_set_syscall(78, (uint32_t) handle_no_errorcode78);
	idt_set_syscall(79, (uint32_t) handle_no_errorcode79);
	idt_set_syscall(80, (uint32_t) handle_no_errorcode80);
	idt_set_syscall(81, (uint32_t) handle_no_errorcode81);

	idt_set_syscall(82, (uint32_t) handle_thread_fork);

	idt_set_syscall(83, (uint32_t) handle_no_errorcode83);
	idt_set_syscall(84, (uint32_t) handle_no_errorcode84);
	idt_set_syscall(85, (uint32_t) handle_no_errorcode85);
	idt_set_syscall(86, (uint32_t) handle_no_errorcode86);
	idt_set_syscall(87, (uint32_t) handle_no_errorcode87);
	idt_set_syscall(89, (uint32_t) handle_no_errorcode89);
	idt_set_syscall(96, (uint32_t) handle_no_errorcode96);
	idt_set_syscall(116, (uint32_t) handle_no_errorcode116);

	return 0;
}

/**
 * @brief the general interrupt 
 * handler.
 * The interrupt handler, whose 
 * entry address stored at the 
 * offset in the handler[] 
 * specified by "ureg.cause", will 
 * be called.
 * 
 * @param ureg
 */
void handle_interrupt(ureg_t ureg) 
{
	uint32_t cause = ureg.cause;

	if (handlers[cause] != 0) {
		handlers[cause](&ureg);
	}
}

/**
 * @brief update the current eflag 
 * values to what we want.
 */
uint32_t update_eflags()
{
	uint32_t eflags = get_eflags();
	
	/* interrupt on*/
	eflags |= EFL_IF;
	/* bit 1 on */
	eflags |= EFL_RESV1;
	/* alignment check off */
	eflags &= (~EFL_AC); 
	/* IOPL bits off,only 0 */
	eflags &= (~EFL_IOPL_RING1);
	eflags &= (~EFL_IOPL_RING2);

	return eflags;
}

/**
 * @brief ck the validity of eflags.
 */
int valid_eflags(uint32_t eflags)
{
	/* interrupt on*/
	if(!(eflags & EFL_IF)){
		return 0;
	}

	/* bit 1 on */
	if(!(eflags & EFL_RESV1)){
		return 0;
	}

	/* alignment check off */
	if (eflags & EFL_AC){
		return 0;
	} 
	/* IOPL bits off */
	if(eflags & EFL_IOPL_RING1){
		return 0;
	}

	if(eflags & EFL_IOPL_RING2){
		return 0;
	}

	return 1;
}

void update_iret(iret_reg_t* reg, kthread_tcb_t *tcb)
{
	reg->eip = tcb->eip;
	reg->cs = SEGSEL_USER_CS;
	reg->eflags = update_eflags();
	reg->esp = (uint32_t)tcb->esp;
	reg->ss = SEGSEL_USER_DS;
}
