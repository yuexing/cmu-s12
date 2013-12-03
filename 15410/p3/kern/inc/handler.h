/**
 * @file handler.h
 * @brief The prototype for the handler.
 * The handler is in charge of idt registration
 * and software routine registration.
 * @author Yue Xing(yuexing)
 * @bug No Found Bug.
 */
#ifndef _HANDLER_H_
#define _HANDLER_H_

int init_mod_handler();

void idt_routine_install(uint8_t num, handler_t base);

void handle_interrupt(ureg_t ureg);

//Handlers with no error codes
extern void handle_no_errorcode0();
extern void handle_no_errorcode1();
extern void handle_no_errorcode2();
extern void handle_no_errorcode3();
extern void handle_no_errorcode4();
extern void handle_no_errorcode5();
extern void handle_no_errorcode6();
extern void handle_no_errorcode7();
//extern void handle_errorcode8();        /*Double Fault Exception*/
extern void handle_no_errorcode9();
extern void handle_errorcode10();
extern void handle_errorcode11(); 
extern void handle_errorcode12(); 
extern void handle_errorcode13(); 
extern void handle_errorcode14();
extern void handle_no_errorcode15();  
extern void handle_no_errorcode16();
extern void handle_errorcode17();
extern void handle_no_errorcode18();
extern void handle_no_errorcode19();
extern void handle_no_errorcode20();
extern void handle_no_errorcode21();
extern void handle_no_errorcode22();
extern void handle_no_errorcode23();
extern void handle_no_errorcode24();
extern void handle_no_errorcode25();
extern void handle_no_errorcode26();
extern void handle_no_errorcode27();
extern void handle_no_errorcode28();
extern void handle_no_errorcode29();
extern void handle_no_errorcode30();
extern void handle_no_errorcode31();
extern void handle_no_errorcode32();    /*timer*/
extern void handle_no_errorcode33();    /*kb*/
extern void handle_no_errorcode38();

void handle_fork();

extern void handle_no_errorcode66();
extern void handle_no_errorcode67();
extern void handle_no_errorcode68();
extern void handle_no_errorcode69();
extern void handle_no_errorcode70();
extern void handle_no_errorcode71();
extern void handle_no_errorcode72();
extern void handle_no_errorcode73();
extern void handle_no_errorcode74();
extern void handle_no_errorcode75();
extern void handle_no_errorcode76();
extern void handle_no_errorcode77();
extern void handle_no_errorcode78();    
extern void handle_no_errorcode79();   
extern void handle_no_errorcode80();   
extern void handle_no_errorcode81();   

void handle_thread_fork();

extern void handle_no_errorcode83();   
extern void handle_no_errorcode84();   
extern void handle_no_errorcode85();   
extern void handle_no_errorcode86();   
extern void handle_no_errorcode87();   
extern void handle_no_errorcode89();   
extern void handle_no_errorcode96();    /*vanish*/
extern void handle_no_errorcode116();

/**
 * @brief This function is to switch
 * from kernel mode to user mode.
 */
void switch_to_user(iret_reg_t*);

uint32_t update_eflags();

int valid_eflags(uint32_t eflags);

void update_iret(iret_reg_t* reg, kthread_tcb_t *tcb);

void before_run();

void after_fork();
#endif
