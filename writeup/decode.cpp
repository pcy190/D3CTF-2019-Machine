// decode the enc

#include <iostream>
#include <stdio.h>
#include<vector>
#include<WinSock2.h>
using namespace std;


void encrypt ( unsigned long* v, unsigned long* key ) {
	unsigned long l = v[0], r = v[1], sum = 0, delta = 0x9e3779b9;

	for ( size_t i = 0; i < 32; i++ ) {
		l += ( ( ( r << 4 ) ^ ( r >> 5 ) ) + r ) ^ ( sum + key[sum & 3] );
		sum += delta;
		r += ( ( ( l << 4 ) ^ ( l >> 5 ) ) + l ) ^ ( sum + key[ ( sum >> 11 ) & 3] );
	}

	v[0] = l;
	v[1] = r;
}

void decrypt ( unsigned long* v, unsigned long* key, unsigned int count ) {
	unsigned long l = v[0], r = v[1], sum = 0, delta = 0x9e3779b9;
	sum = delta * count;

	for ( size_t i = 0; i < count; i++ ) {
		r -= ( ( ( l << 4 ) ^ ( l >> 5 ) ) + l ) ^ ( sum + key[ ( sum >> 11 ) & 3] );
		sum -= delta;
		l -= ( ( ( r << 4 ) ^ ( r >> 5 ) ) + r ) ^ ( sum + key[sum & 3] );
	}

	v[0] = l;
	v[1] = r;
}




int main ( int argc, char const* argv[] ) {
	unsigned long v[2] = {0, 0};	
	unsigned long key[4] = {2045, 4093, 8189, 16381};
	unsigned long key2[] = { 1107 ,4093, 524285 ,262141 };
	v[0] = 246553640 ;
	v[1] = 2138606322;
	decrypt ( v, key, 32 );
	for ( int i = 0; i < 8; i++ ) {
		printf ( "%c" , (( char* ) v)[i] );
	}

	v[0] = -1227780298;
	v[1] = -783437692;
	decrypt ( v, key2, 64 );
	for (int i = 0; i < 8; i++) {
		printf("%c", ((char*)v)[i]);
	}
	return 0;
}
