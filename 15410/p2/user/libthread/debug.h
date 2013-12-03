/**
 * @file debug.h
 * @brief support for development
 * @author Yue Xing (yuexing)
 * @bug No Found Bug
 */
#ifndef DEBUG_H
#define DEBUG_H

#define DEBUG_VM
#define DEBUG_PROC
#define DEBUG_THR
#define DEBUG_CALL

#ifdef DEBUG_VM
#define debug_vm(...) lprintf(__VA_ARGS__)
#else
#define debug_vm(...)
#endif

#ifdef DEBUG_PROC
#define debug_proc(...) lprintf(__VA_ARGS__)
#else
#define debug_proc(...)
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

#endif
