
./main.o:     file format elf64-x86-64


Disassembly of section .init:

0000000000401000 <_init>:
  401000:	48 83 ec 08          	sub    $0x8,%rsp
  401004:	48 8b 05 ed 2f 00 00 	mov    0x2fed(%rip),%rax        # 403ff8 <__gmon_start__>
  40100b:	48 85 c0             	test   %rax,%rax
  40100e:	74 02                	je     401012 <_init+0x12>
  401010:	ff d0                	callq  *%rax
  401012:	48 83 c4 08          	add    $0x8,%rsp
  401016:	c3                   	retq   

Disassembly of section .plt:

0000000000401020 <.plt>:
  401020:	ff 35 e2 2f 00 00    	pushq  0x2fe2(%rip)        # 404008 <_GLOBAL_OFFSET_TABLE_+0x8>
  401026:	ff 25 e4 2f 00 00    	jmpq   *0x2fe4(%rip)        # 404010 <_GLOBAL_OFFSET_TABLE_+0x10>
  40102c:	0f 1f 40 00          	nopl   0x0(%rax)

0000000000401030 <pthread_create@plt>:
  401030:	ff 25 e2 2f 00 00    	jmpq   *0x2fe2(%rip)        # 404018 <pthread_create@GLIBC_2.2.5>
  401036:	68 00 00 00 00       	pushq  $0x0
  40103b:	e9 e0 ff ff ff       	jmpq   401020 <.plt>

0000000000401040 <pthread_cond_wait@plt>:
  401040:	ff 25 da 2f 00 00    	jmpq   *0x2fda(%rip)        # 404020 <pthread_cond_wait@GLIBC_2.3.2>
  401046:	68 01 00 00 00       	pushq  $0x1
  40104b:	e9 d0 ff ff ff       	jmpq   401020 <.plt>

0000000000401050 <printf@plt>:
  401050:	ff 25 d2 2f 00 00    	jmpq   *0x2fd2(%rip)        # 404028 <printf@GLIBC_2.2.5>
  401056:	68 02 00 00 00       	pushq  $0x2
  40105b:	e9 c0 ff ff ff       	jmpq   401020 <.plt>

0000000000401060 <__assert_fail@plt>:
  401060:	ff 25 ca 2f 00 00    	jmpq   *0x2fca(%rip)        # 404030 <__assert_fail@GLIBC_2.2.5>
  401066:	68 03 00 00 00       	pushq  $0x3
  40106b:	e9 b0 ff ff ff       	jmpq   401020 <.plt>

0000000000401070 <pthread_cond_signal@plt>:
  401070:	ff 25 c2 2f 00 00    	jmpq   *0x2fc2(%rip)        # 404038 <pthread_cond_signal@GLIBC_2.3.2>
  401076:	68 04 00 00 00       	pushq  $0x4
  40107b:	e9 a0 ff ff ff       	jmpq   401020 <.plt>

0000000000401080 <pthread_mutex_unlock@plt>:
  401080:	ff 25 ba 2f 00 00    	jmpq   *0x2fba(%rip)        # 404040 <pthread_mutex_unlock@GLIBC_2.2.5>
  401086:	68 05 00 00 00       	pushq  $0x5
  40108b:	e9 90 ff ff ff       	jmpq   401020 <.plt>

0000000000401090 <pthread_join@plt>:
  401090:	ff 25 b2 2f 00 00    	jmpq   *0x2fb2(%rip)        # 404048 <pthread_join@GLIBC_2.2.5>
  401096:	68 06 00 00 00       	pushq  $0x6
  40109b:	e9 80 ff ff ff       	jmpq   401020 <.plt>

00000000004010a0 <pthread_mutex_lock@plt>:
  4010a0:	ff 25 aa 2f 00 00    	jmpq   *0x2faa(%rip)        # 404050 <pthread_mutex_lock@GLIBC_2.2.5>
  4010a6:	68 07 00 00 00       	pushq  $0x7
  4010ab:	e9 70 ff ff ff       	jmpq   401020 <.plt>

Disassembly of section .text:

00000000004010b0 <_start>:
  4010b0:	31 ed                	xor    %ebp,%ebp
  4010b2:	49 89 d1             	mov    %rdx,%r9
  4010b5:	5e                   	pop    %rsi
  4010b6:	48 89 e2             	mov    %rsp,%rdx
  4010b9:	48 83 e4 f0          	and    $0xfffffffffffffff0,%rsp
  4010bd:	50                   	push   %rax
  4010be:	54                   	push   %rsp
  4010bf:	49 c7 c0 b0 13 40 00 	mov    $0x4013b0,%r8
  4010c6:	48 c7 c1 50 13 40 00 	mov    $0x401350,%rcx
  4010cd:	48 c7 c7 c2 12 40 00 	mov    $0x4012c2,%rdi
  4010d4:	ff 15 16 2f 00 00    	callq  *0x2f16(%rip)        # 403ff0 <__libc_start_main@GLIBC_2.2.5>
  4010da:	f4                   	hlt    
  4010db:	0f 1f 44 00 00       	nopl   0x0(%rax,%rax,1)

00000000004010e0 <_dl_relocate_static_pie>:
  4010e0:	c3                   	retq   
  4010e1:	66 2e 0f 1f 84 00 00 	nopw   %cs:0x0(%rax,%rax,1)
  4010e8:	00 00 00 
  4010eb:	0f 1f 44 00 00       	nopl   0x0(%rax,%rax,1)

00000000004010f0 <deregister_tm_clones>:
  4010f0:	b8 68 40 40 00       	mov    $0x404068,%eax
  4010f5:	48 3d 68 40 40 00    	cmp    $0x404068,%rax
  4010fb:	74 13                	je     401110 <deregister_tm_clones+0x20>
  4010fd:	b8 00 00 00 00       	mov    $0x0,%eax
  401102:	48 85 c0             	test   %rax,%rax
  401105:	74 09                	je     401110 <deregister_tm_clones+0x20>
  401107:	bf 68 40 40 00       	mov    $0x404068,%edi
  40110c:	ff e0                	jmpq   *%rax
  40110e:	66 90                	xchg   %ax,%ax
  401110:	c3                   	retq   
  401111:	66 66 2e 0f 1f 84 00 	data16 nopw %cs:0x0(%rax,%rax,1)
  401118:	00 00 00 00 
  40111c:	0f 1f 40 00          	nopl   0x0(%rax)

0000000000401120 <register_tm_clones>:
  401120:	be 68 40 40 00       	mov    $0x404068,%esi
  401125:	48 81 ee 68 40 40 00 	sub    $0x404068,%rsi
  40112c:	48 c1 fe 03          	sar    $0x3,%rsi
  401130:	48 89 f0             	mov    %rsi,%rax
  401133:	48 c1 e8 3f          	shr    $0x3f,%rax
  401137:	48 01 c6             	add    %rax,%rsi
  40113a:	48 d1 fe             	sar    %rsi
  40113d:	74 11                	je     401150 <register_tm_clones+0x30>
  40113f:	b8 00 00 00 00       	mov    $0x0,%eax
  401144:	48 85 c0             	test   %rax,%rax
  401147:	74 07                	je     401150 <register_tm_clones+0x30>
  401149:	bf 68 40 40 00       	mov    $0x404068,%edi
  40114e:	ff e0                	jmpq   *%rax
  401150:	c3                   	retq   
  401151:	66 66 2e 0f 1f 84 00 	data16 nopw %cs:0x0(%rax,%rax,1)
  401158:	00 00 00 00 
  40115c:	0f 1f 40 00          	nopl   0x0(%rax)

0000000000401160 <__do_global_dtors_aux>:
  401160:	80 3d 19 2f 00 00 00 	cmpb   $0x0,0x2f19(%rip)        # 404080 <completed.7325>
  401167:	75 17                	jne    401180 <__do_global_dtors_aux+0x20>
  401169:	55                   	push   %rbp
  40116a:	48 89 e5             	mov    %rsp,%rbp
  40116d:	e8 7e ff ff ff       	callq  4010f0 <deregister_tm_clones>
  401172:	c6 05 07 2f 00 00 01 	movb   $0x1,0x2f07(%rip)        # 404080 <completed.7325>
  401179:	5d                   	pop    %rbp
  40117a:	c3                   	retq   
  40117b:	0f 1f 44 00 00       	nopl   0x0(%rax,%rax,1)
  401180:	c3                   	retq   
  401181:	66 66 2e 0f 1f 84 00 	data16 nopw %cs:0x0(%rax,%rax,1)
  401188:	00 00 00 00 
  40118c:	0f 1f 40 00          	nopl   0x0(%rax)

0000000000401190 <frame_dummy>:
  401190:	eb 8e                	jmp    401120 <register_tm_clones>

0000000000401192 <put>:
  401192:	83 3d 6f 2f 00 00 00 	cmpl   $0x0,0x2f6f(%rip)        # 404108 <count>
  401199:	75 11                	jne    4011ac <put+0x1a>
  40119b:	c7 05 63 2f 00 00 01 	movl   $0x1,0x2f63(%rip)        # 404108 <count>
  4011a2:	00 00 00 
  4011a5:	89 3d 61 2f 00 00    	mov    %edi,0x2f61(%rip)        # 40410c <buf>
  4011ab:	c3                   	retq   
  4011ac:	48 83 ec 08          	sub    $0x8,%rsp
  4011b0:	b9 29 20 40 00       	mov    $0x402029,%ecx
  4011b5:	ba 0b 00 00 00       	mov    $0xb,%edx
  4011ba:	be 04 20 40 00       	mov    $0x402004,%esi
  4011bf:	bf 0b 20 40 00       	mov    $0x40200b,%edi
  4011c4:	e8 97 fe ff ff       	callq  401060 <__assert_fail@plt>

00000000004011c9 <producer>:
  4011c9:	53                   	push   %rbx
  4011ca:	bb 00 00 00 00       	mov    $0x0,%ebx
  4011cf:	eb 1e                	jmp    4011ef <producer+0x26>
  4011d1:	89 df                	mov    %ebx,%edi
  4011d3:	e8 ba ff ff ff       	callq  401192 <put>
  4011d8:	bf a0 40 40 00       	mov    $0x4040a0,%edi
  4011dd:	e8 8e fe ff ff       	callq  401070 <pthread_cond_signal@plt>
  4011e2:	bf e0 40 40 00       	mov    $0x4040e0,%edi
  4011e7:	e8 94 fe ff ff       	callq  401080 <pthread_mutex_unlock@plt>
  4011ec:	83 c3 01             	add    $0x1,%ebx
  4011ef:	83 fb 63             	cmp    $0x63,%ebx
  4011f2:	7f 24                	jg     401218 <producer+0x4f>
  4011f4:	bf e0 40 40 00       	mov    $0x4040e0,%edi
  4011f9:	e8 a2 fe ff ff       	callq  4010a0 <pthread_mutex_lock@plt>
  4011fe:	83 3d 03 2f 00 00 01 	cmpl   $0x1,0x2f03(%rip)        # 404108 <count>
  401205:	75 ca                	jne    4011d1 <producer+0x8>
  401207:	be e0 40 40 00       	mov    $0x4040e0,%esi
  40120c:	bf a0 40 40 00       	mov    $0x4040a0,%edi
  401211:	e8 2a fe ff ff       	callq  401040 <pthread_cond_wait@plt>
  401216:	eb e6                	jmp    4011fe <producer+0x35>
  401218:	5b                   	pop    %rbx
  401219:	c3                   	retq   

000000000040121a <get>:
  40121a:	83 3d e7 2e 00 00 01 	cmpl   $0x1,0x2ee7(%rip)        # 404108 <count>
  401221:	75 11                	jne    401234 <get+0x1a>
  401223:	c7 05 db 2e 00 00 00 	movl   $0x0,0x2edb(%rip)        # 404108 <count>
  40122a:	00 00 00 
  40122d:	8b 05 d9 2e 00 00    	mov    0x2ed9(%rip),%eax        # 40410c <buf>
  401233:	c3                   	retq   
  401234:	48 83 ec 08          	sub    $0x8,%rsp
  401238:	b9 25 20 40 00       	mov    $0x402025,%ecx
  40123d:	ba 11 00 00 00       	mov    $0x11,%edx
  401242:	be 04 20 40 00       	mov    $0x402004,%esi
  401247:	bf 16 20 40 00       	mov    $0x402016,%edi
  40124c:	e8 0f fe ff ff       	callq  401060 <__assert_fail@plt>

0000000000401251 <consumer>:
  401251:	55                   	push   %rbp
  401252:	53                   	push   %rbx
  401253:	48 83 ec 08          	sub    $0x8,%rsp
  401257:	bb 00 00 00 00       	mov    $0x0,%ebx
  40125c:	eb 34                	jmp    401292 <consumer+0x41>
  40125e:	b8 00 00 00 00       	mov    $0x0,%eax
  401263:	e8 b2 ff ff ff       	callq  40121a <get>
  401268:	89 c5                	mov    %eax,%ebp
  40126a:	bf a0 40 40 00       	mov    $0x4040a0,%edi
  40126f:	e8 fc fd ff ff       	callq  401070 <pthread_cond_signal@plt>
  401274:	bf e0 40 40 00       	mov    $0x4040e0,%edi
  401279:	e8 02 fe ff ff       	callq  401080 <pthread_mutex_unlock@plt>
  40127e:	89 ee                	mov    %ebp,%esi
  401280:	bf 21 20 40 00       	mov    $0x402021,%edi
  401285:	b8 00 00 00 00       	mov    $0x0,%eax
  40128a:	e8 c1 fd ff ff       	callq  401050 <printf@plt>
  40128f:	83 c3 01             	add    $0x1,%ebx
  401292:	83 fb 31             	cmp    $0x31,%ebx
  401295:	7f 24                	jg     4012bb <consumer+0x6a>
  401297:	bf e0 40 40 00       	mov    $0x4040e0,%edi
  40129c:	e8 ff fd ff ff       	callq  4010a0 <pthread_mutex_lock@plt>
  4012a1:	83 3d 60 2e 00 00 00 	cmpl   $0x0,0x2e60(%rip)        # 404108 <count>
  4012a8:	75 b4                	jne    40125e <consumer+0xd>
  4012aa:	be e0 40 40 00       	mov    $0x4040e0,%esi
  4012af:	bf a0 40 40 00       	mov    $0x4040a0,%edi
  4012b4:	e8 87 fd ff ff       	callq  401040 <pthread_cond_wait@plt>
  4012b9:	eb e6                	jmp    4012a1 <consumer+0x50>
  4012bb:	48 83 c4 08          	add    $0x8,%rsp
  4012bf:	5b                   	pop    %rbx
  4012c0:	5d                   	pop    %rbp
  4012c1:	c3                   	retq   

00000000004012c2 <main>:
  4012c2:	53                   	push   %rbx
  4012c3:	48 83 ec 20          	sub    $0x20,%rsp
  4012c7:	b9 00 00 00 00       	mov    $0x0,%ecx
  4012cc:	ba c9 11 40 00       	mov    $0x4011c9,%edx
  4012d1:	be 00 00 00 00       	mov    $0x0,%esi
  4012d6:	48 8d 7c 24 08       	lea    0x8(%rsp),%rdi
  4012db:	e8 50 fd ff ff       	callq  401030 <pthread_create@plt>
  4012e0:	bb 00 00 00 00       	mov    $0x0,%ebx
  4012e5:	eb 1f                	jmp    401306 <main+0x44>
  4012e7:	48 63 c3             	movslq %ebx,%rax
  4012ea:	48 8d 7c c4 10       	lea    0x10(%rsp,%rax,8),%rdi
  4012ef:	b9 00 00 00 00       	mov    $0x0,%ecx
  4012f4:	ba 51 12 40 00       	mov    $0x401251,%edx
  4012f9:	be 00 00 00 00       	mov    $0x0,%esi
  4012fe:	e8 2d fd ff ff       	callq  401030 <pthread_create@plt>
  401303:	83 c3 01             	add    $0x1,%ebx
  401306:	83 fb 01             	cmp    $0x1,%ebx
  401309:	7e dc                	jle    4012e7 <main+0x25>
  40130b:	be 00 00 00 00       	mov    $0x0,%esi
  401310:	48 8b 7c 24 08       	mov    0x8(%rsp),%rdi
  401315:	e8 76 fd ff ff       	callq  401090 <pthread_join@plt>
  40131a:	bb 00 00 00 00       	mov    $0x0,%ebx
  40131f:	eb 15                	jmp    401336 <main+0x74>
  401321:	48 63 c3             	movslq %ebx,%rax
  401324:	48 8b 7c c4 10       	mov    0x10(%rsp,%rax,8),%rdi
  401329:	be 00 00 00 00       	mov    $0x0,%esi
  40132e:	e8 5d fd ff ff       	callq  401090 <pthread_join@plt>
  401333:	83 c3 01             	add    $0x1,%ebx
  401336:	83 fb 01             	cmp    $0x1,%ebx
  401339:	7e e6                	jle    401321 <main+0x5f>
  40133b:	b8 00 00 00 00       	mov    $0x0,%eax
  401340:	48 83 c4 20          	add    $0x20,%rsp
  401344:	5b                   	pop    %rbx
  401345:	c3                   	retq   
  401346:	66 2e 0f 1f 84 00 00 	nopw   %cs:0x0(%rax,%rax,1)
  40134d:	00 00 00 

0000000000401350 <__libc_csu_init>:
  401350:	41 57                	push   %r15
  401352:	49 89 d7             	mov    %rdx,%r15
  401355:	41 56                	push   %r14
  401357:	49 89 f6             	mov    %rsi,%r14
  40135a:	41 55                	push   %r13
  40135c:	41 89 fd             	mov    %edi,%r13d
  40135f:	41 54                	push   %r12
  401361:	4c 8d 25 98 2a 00 00 	lea    0x2a98(%rip),%r12        # 403e00 <__frame_dummy_init_array_entry>
  401368:	55                   	push   %rbp
  401369:	48 8d 2d 98 2a 00 00 	lea    0x2a98(%rip),%rbp        # 403e08 <__init_array_end>
  401370:	53                   	push   %rbx
  401371:	4c 29 e5             	sub    %r12,%rbp
  401374:	48 83 ec 08          	sub    $0x8,%rsp
  401378:	e8 83 fc ff ff       	callq  401000 <_init>
  40137d:	48 c1 fd 03          	sar    $0x3,%rbp
  401381:	74 1b                	je     40139e <__libc_csu_init+0x4e>
  401383:	31 db                	xor    %ebx,%ebx
  401385:	0f 1f 00             	nopl   (%rax)
  401388:	4c 89 fa             	mov    %r15,%rdx
  40138b:	4c 89 f6             	mov    %r14,%rsi
  40138e:	44 89 ef             	mov    %r13d,%edi
  401391:	41 ff 14 dc          	callq  *(%r12,%rbx,8)
  401395:	48 83 c3 01          	add    $0x1,%rbx
  401399:	48 39 dd             	cmp    %rbx,%rbp
  40139c:	75 ea                	jne    401388 <__libc_csu_init+0x38>
  40139e:	48 83 c4 08          	add    $0x8,%rsp
  4013a2:	5b                   	pop    %rbx
  4013a3:	5d                   	pop    %rbp
  4013a4:	41 5c                	pop    %r12
  4013a6:	41 5d                	pop    %r13
  4013a8:	41 5e                	pop    %r14
  4013aa:	41 5f                	pop    %r15
  4013ac:	c3                   	retq   
  4013ad:	0f 1f 00             	nopl   (%rax)

00000000004013b0 <__libc_csu_fini>:
  4013b0:	c3                   	retq   

Disassembly of section .fini:

00000000004013b4 <_fini>:
  4013b4:	48 83 ec 08          	sub    $0x8,%rsp
  4013b8:	48 83 c4 08          	add    $0x8,%rsp
  4013bc:	c3                   	retq   
