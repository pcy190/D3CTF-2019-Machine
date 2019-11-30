
1.
init的时候，主要考察Ackermann函数。这里用的是其模拟栈版本。
大致的代码如下
```
long ackman ( long m, long n ) {
 int pos = 1;
	stack[0] = m;
	stack[1] = n;

	while ( pos ) {
		n = stack[pos--];
		m = stack[pos];

		if ( m == 0 )
			stack[pos] = n + 1;

		if ( m != 0 && n == 0 ) {
			stack[pos++] = m - 1;
			stack[pos] = 1;
		}

		if ( m != 0 && n != 0 ) {
			stack[pos++] = m - 1;
			stack[pos++] = m;
			stack[pos] = n - 1;
		}
	}

	return stack[0];
}
```
可以把核心计算函数抽出来单独跑，也可以通过其函数的性质，在n比较大的时候，需要不少时间去运行这个函数代码。如果知道性质的话，可以很快得出
当m为3，n>=5时，其结果为2^(n+3)-3 
(可参见[Ackermann](https://baike.baidu.com/item/%E9%98%BF%E5%85%8B%E6%9B%BC%E5%87%BD%E6%95%B0/10988285?fr=aladdin#%E6%84%8F%E4%B9%89))

这里我们用的是ackman(3,16)作为密钥的一部分。

2. JNI的函数采用的是动态注册， 直接可以在onload的结束部分看到相应的结构体数组，可以发现verify对应的native函数地址，跟入即可。

3. 程序在Onload的时候采用了多种反调试机制，除了常见的ptrace和进程搜索，还加入了对frida的检测，inotify对map内存文件的监控，由于相关的代码没有加混淆保护，所以很容易看到，可以直接patch掉，但是反调试函数的返回值是有用的，不难发现其理想的返回值之间亦或后应该是1107，即没有调试的状态。（如果有调试，则会加入随机数，影响initarry的初始值，导致加密解密失败）

4. 主代码逻辑由kotlin实现，逆向后的多数函数逻辑和纯java写出的class类似

5.
加解密的主体是改造后的tea加密算法，xtea
前半flag进行32轮加密，后半flag进行64轮加密。
密钥分别为
2045 4093 8189 16381
1107 4093 524285 262141
密文分别为
246553640 2138606322
-1227780298 -783437692
解密脚本见decode.cpp