#ifndef DH_CRACK_C1_H
#define DH_CRACK_C1_H

#include <string.h>
#include <stdlib.h>
#include <stdio.h>

#include "dh_crack.h"

int GoodChar(int c);

void Possibility(char* file_crypt, char** tabC, int len_key);

void LKeyPotential(char Keys[MAX_KEYS][MAX_SIZE_KEY], char** tabC, char* c, int *possibilities, int i, int len_key);

int dh_crack_c1(char Keys[MAX_KEYS][MAX_SIZE_KEY], char* file_crypt, int len_key);

#endif /* DH_CRACK_C1_H */