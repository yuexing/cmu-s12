/**
  * wrapper of XCHG
  */
.global xchg

xchg:
mov 8(%esp), %eax
mov 4(%esp), %ecx
xchg (%ecx), %eax

ret
