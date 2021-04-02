	.file	"hello.c"
	.text
	.globl	echo
	.type	echo, @function
echo:
.LFB11:
	.cfi_startproc
	subq	$24, %rsp
	.cfi_def_cfa_offset 32
	leaq	12(%rsp), %rdi
	movl	$0, %eax
	call	gets
	leaq	12(%rsp), %rdi
	call	puts
	addq	$24, %rsp
	.cfi_def_cfa_offset 8
	ret
	.cfi_endproc
.LFE11:
	.size	echo, .-echo
	.ident	"GCC: (Uos 8.3.0.3-3+rebuild) 8.3.0"
	.section	.note.GNU-stack,"",@progbits
