.globl ctx_switch

ctx_switch:
// save general-r
pusha
mov 36(%esp), %eax
mov %esp, (%eax)
// restore general-r
mov 40(%esp), %esp
popa
// the ret addr
ret
