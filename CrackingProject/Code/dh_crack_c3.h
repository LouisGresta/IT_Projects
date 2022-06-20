#ifndef DH_CRACK_C3_H
#define DH_CRACK_C3_H

#include <string.h>
#include <stdio.h>
#include <ctype.h>

#include "dh_crack.h"
#include "dh_crack_c2.h"

#define MAX_SIZE_WORD 46 // Le mot le plus long à apparaître dans un dictionnaire non technique est de 45 caractères d'où 46 pour compter le '\0'

void initScore(int Scores[MAX_KEYS]);

void CaseWords(int Scores[MAX_KEYS], int index_key, char* file_decrypt, char* file_dict);

void dh_crack_c3(char Keys[MAX_KEYS][MAX_SIZE_KEY], int Scores[MAX_KEYS], char* file_crypt, char* file_dict, int len_key, int nbKeys);

#endif /* DH_CRACK_C3_H */