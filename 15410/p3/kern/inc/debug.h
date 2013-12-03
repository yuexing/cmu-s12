/**
 * @file debug.h
 * @brief support for development
 * @author Yue Xing (yuexing)
 * @bug No Found Bug
 */
#ifndef DEBUG_H
#define DEBUG_H

#define DEBUG_VM1
#define DEBUG_PROC1
#define DEBUG_THR1
#define DEBUG_CALL1
#define DEBUG_LOAD1
#define DEBUG_LOCK1

#ifdef DEBUG_VM
#define debug_vm(...) lprintf(__VA_ARGS__)
#else
#define debug_vm(...)
#endif

#ifdef DEBUG_VM
#define debug_mem(...) lprintf(__VA_ARGS__)
#else
#define debug_mem(...)
#endif

#ifdef DEBUG_PROC
#define debug_proc(...) lprintf(__VA_ARGS__)
#else
#define debug_proc(...)
#endif

#ifdef DEBUG_LOCK
#define debug_lock(...) lprintf(__VA_ARGS__)
#else
#define debug_lock(...)
#endif

#ifdef DEBUG_THR
#define debug_thr(...) lprintf(__VA_ARGS__)
#else
#define debug_thr(...)
#endif

#ifdef DEBUG_CALL
#define debug_call(...) lprintf(__VA_ARGS__)
#else
#define debug_call(...)
#endif

#ifdef DEBUG_LOAD
#define debug_load(...) lprintf(__VA_ARGS__)
#else
#define debug_load(...)
#endif

#endif
