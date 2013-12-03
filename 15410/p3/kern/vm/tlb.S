.global invl_tlb

invl_tlb:
mov 4(%esp), %eax
invlpg (%eax)
ret
