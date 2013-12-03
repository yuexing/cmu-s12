/**
 * @file kernel_defines.h
 * @brief This file collects all the declarations.
 * @author Yue Xing(yuexing)
 * @bug No found bugs
 */

#ifndef _KERNEL_DEFINES_H
#define _KERNEL_DEFINES_H

#include <limits.h>

#define MAX_LENGTH 1024
#define SUCCESS	0
#define ERROR	-1
#define WORD_SIZE	(sizeof(void*))
#define KSTK_OFFSET	(PAGE_SIZE-WORD_SIZE)
#define KILL_STATUS	-2
#define	WORD_MASK	(WORD_SIZE-1) 
#define WORD_RD_UP(a)((((uint32_t)a)+WORD_MASK)&(~WORD_MASK))
#define WORD_RD_DN(a)(((uint32_t)a)&(~WORD_MASK))
#define UP_PAGE(a)	(a=(a+=PAGE_SIZE)?a:(void*)UINT_MAX)

/**************************************
 * 410kern/x86
 * ************************************/
#include <multiboot.h>  
#include <seg.h>
#include <asm.h>
#include <cr.h>
#include <eflags.h>
#include <keyhelp.h>
#include <page.h> // PAGE_SHIFT, PAGE_SIZE
#include <timer_defines.h>
#include <video_defines.h>

/***************************************
 * Spec
 * ************************************/
#include <syscall_int.h>
#include <ureg.h>
#include <common_kern.h>

/***************************************
 * 410kern/String
 * ************************************/
#include <string.h>

/***************************************
 * 410kern/Stdlib
 * ************************************/
#include <ctype.h>
#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>

/***************************************
 * 410kern/malloc
 * ************************************/
#include <malloc_internal.h>

/***************************************
 * 410kern/elf
 * ************************************/
#include <elf_410.h>

/***************************************
 * 410kern/Simics
 * ************************************/
#include <simics.h>

/***************************************
 * 410kern/Elf
 * ************************************/

/***************************************
 * 410kern/Inc
 * ************************************/
#include <assert.h>
#include <malloc.h>
#include <stddef.h>
#include <stdint.h>
#include <types.h>
#include <exec2obj.h>
#include <interrupt_defines.h>	/* INT_CTL_PORT*/

/************************************
 * Data types
 * ************************************/
#include <vm_type.h>
#include <kthread_type.h>
#include <kcond_type.h>
#include <block_type.h>
#include <process_type.h>
#include <varlock_type.h>
#include <handler_type.h>

/************************************
 * Prototypes
 * ************************************/
//////// console
#include <console.h>

/////// timer
#include <timer.h>

/////// kb
#include <kb.h>

////// Exception
#include <exception.h>

/////// handler
#include <handler.h>

/////// debug
#include <debug.h>

/////// malloc
#include <kmalloc.h>

////// scheduler
#include <ctx_switch.h>
#include <scheduler.h>

////// process
#include <process.h>

/////// kthread
#include <kthread.h>

/////// locks
// block-style lock
#include <block.h>
// var assignment style lock
#include <varlock.h>
// cond var
#include <kcond.h>

/////// frames
#include <frame.h>

/////// vm
#include <vm.h>

/////// vq
#include <vq.h>

/////// ksyscall
#include <ksyscall.h>
#include <ksyscall_ck.h>

/////// loader
#include <loader.h>

/////// extern vars 
extern page_dir_t *tmp_dir;
extern block_t tmp_dir_lock;
extern kthread_tcb_t *thr_idle;
extern process_t *proc_init;
extern kthread_tcb_t *pending_thr;
extern void *zfod_frame;

#endif
