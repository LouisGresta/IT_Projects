#ifndef DH_CRACK_C2_H
#define DH_CRACK_C2_H

#define MAX_LETTERS 26 // nombre maximum de lettres dans l'alphabet anglais

#include <string.h>
#include <stdlib.h>
#include <stdio.h>

#include "dh_crack.h"
#include "dh_crack_c3.h"

void initFreq(double Freq[MAX_LETTERS]);

void Decrypt(char* file_crypt, char* file_decrypt, char* key, int len_key);

void FreqLetter(double Freq[MAX_LETTERS], char* file_decrypt);

int d(double Freq_th[MAX_LETTERS], double Freq[MAX_LETTERS]);

void SortKeys(char Keys[MAX_KEYS][MAX_SIZE_KEY], int Scores[MAX_KEYS], int nbKeys, int len_key);

void dh_crack_c2(char Keys[MAX_KEYS][MAX_SIZE_KEY], int Scores[MAX_KEYS], char* file_crypt, int len_key, int nbKeys);

#endif /* DH_CRACK_C2_H */