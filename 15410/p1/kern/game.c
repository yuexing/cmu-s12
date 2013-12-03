/** @file game.c
 *  @brief A kernel with timer, keyboard, console support
 *
 *  This file contains the kernel's main() function.
 *
 *  It sets up the drivers and starts the game.
 *
 *  @author Yue Xing(yuexing)
 *  @bug No known bugs.
 */

#include <p1kern.h>

/* libc includes. */
#include <stdio.h>
#include <simics.h>                 /* lprintf() */
#include <malloc.h>

/* multiboot header file */
#include <multiboot.h>              /* boot_info */

/* memory includes. */
#include <lmm.h>                    /* lmm_remove_free() */

/* x86 specific includes */
#include <x86/seg.h>                /* install_user_segs() */
#include <x86/interrupt_defines.h>  /* interrupt_setup() */
#include <x86/asm.h>                /* enable_interrupts() */
#include <string.h>
#include <assert.h>                 /* panic */
#include "inc/screen.h"

/** @brief Kernel entrypoint.
 *  
 *  This is the entrypoint for the kernel.  It simply sets up the
 *  drivers and passes control off to game_run().
 *
 * @return Does not return
 */
int kernel_main(mbinfo_t *mbinfo, int argc, char **argv, char **envp)
{
    /*
     * Initialize time and device-driver library.
     */
    if(handler_install(tick_backup) < 0) {
		panic("handler_install works unproperly.");
	}

	/* enable interrupts as they are disabled now */
	enable_interrupts();

	game_run();

    return 0;
}
