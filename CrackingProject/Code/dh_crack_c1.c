#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <ctype.h>

#include "dh_crack.h"
#include "dh_crack_c1.h"


/// \brief Fonction dh_crack_c1
/// \file dh_crack_c1.c
/// \author Louis Gresta
/// \date 10 janvier 2021

int GoodChar(int c){
	/// \brief Vérifie qu'un caractère est acceptable
	/// \param[in] c un entier représentant un caractère
	/// \return vrai ou faux (1 ou 0)
	if( isalnum(c)!=0 || isspace(c)!=0 || ispunct(c)!=0 ) return 1;
	return 0;

}

void Possibility(char* file_crypt, char** tabC, int len_key){
	/// \brief Vérifie les possibilités des caractères de la clé 
	/// \param[in] file_crypt une chaîne de caractère donnant l'emplacement du fichier crypté
	/// \param[in] tabC un tableau de chaînes de caractères comportant les différentes possibilitées de caractères pour la clé
	/// \param[in] len_key un entier correspondant à la longueur de la clé

	/* initialisation et ouverture des fichier à utiliser */
	int i=0, j, nbrChar, letter;
	FILE *crypt = fopen(file_crypt, "r");
	if(crypt == NULL){ printf("erreur à la lecture du fichier"); exit(1);}

	/* Pour chaque caractères du fichier crypté,
	si le XOR entre le caractère de la clé correspondant ne donne pas un caractère acceptable,
	on supprime le caractère de la clé */
	letter=fgetc(crypt);
	while(letter != EOF){
		nbrChar=MAX_VALID_KEY_CHARS;
		j=0;
		/* parcours des différents caractères de la clé[i] */
		while(tabC[i][j]!='\0'){
			if(! GoodChar(letter^tabC[i][j])){
				for(int k=j;k<nbrChar;k++){
					tabC[i][k]=tabC[i][k+1];
				}
				nbrChar-=1;
				j-=1;
			}
			j+=1;
		}
		i+=1;
		i=i%len_key;
		letter=fgetc(crypt);
	}
	/* fermeture du fichier utilisé */
	fclose(crypt);
}


void LKeyPotential(char Keys[MAX_KEYS][MAX_SIZE_KEY], char** tabC, char c[MAX_SIZE_KEY], int *possibilities, int i, int len_key){
	/// \brief Vérifie les possibilités des caractères de la clé 
	/// \param[in] Keys[MAX_KEYS][MAX_SIZE_KEY] un tableau de caractères correspondant à la liste des clés candidates
	/// \param[in] tabC un tableau de chaînes de caractères comportant les différentes possibilitées de caractères pour la clé
	/// \param[in] c une chaîne de caractères correspondant aux caractères de la clé candidate
	/// \param[in] *possibilities un pointeur sur la valeur de l'entier possibilities qui correspond à l'indice auquel stocker la clé candidate dans Keys[MAX_KEYS][MAX_SIZE_KEY]
	/// \param[in] i un entier correspondant à l'indice du caractère i+1 de la clé
	/// \param[in] len_key un entier correspondant à la longueur de la clé

	/* initialisation */
	int lenT;
	lenT=strlen(tabC[i]);
	/* si clé[i] n'est pas le dernier caractère de la clé,
	on parcours les caractères possibles pour clé[i]
	et on ajoute ce caractère à 'c' puis on rappelle LKeyPotential */
	if (i<len_key-1){
		for(int j=0;j<lenT;j++){
			c[i]=tabC[i][j];
			LKeyPotential(Keys, tabC, c, possibilities, i+1, len_key);
		}
	}
	/* si clé[i] est le dernier caractère de la clé, 
	on parcours les caractères possibles pour clé[i] 
	et pour chaque clé[i] on ajoute à 'Keys' les clés possibles commençant par c et finissant par clé[i] */
	else{
		for(int j=0;j<lenT;j++){
			c[i]=tabC[i][j];
			c[i+1]='\0';
			for(int x=0; x<=len_key;x++){
				Keys[*possibilities][x]=c[x];
			}
			*possibilities+=1;
		}
	}
}

int dh_crack_c1(char Keys[MAX_KEYS][MAX_SIZE_KEY], char* file_crypt, int len_key){
	/// \brief Donne les clés candidates en utilisant la méthode C1
	/// \param[in] Keys[MAX_KEYS][MAX_SIZE_KEY] un tableau de caractères correspondant à la liste des clés candidates
	/// \param[in] file_crypt une chaîne de caractère donnant l'emplacement du fichier crypté
	/// \param[in] len_key un entier correspondant à la longueur de la clé
	/// \return possibilities un entier qui correspond au nombre de possibilités de clés

	/* initialisation */
	t_tab_keys C;
	char c[MAX_SIZE_KEY];
	int j, x, possibilities=0;
	/* allocation dynamique de la mémoire de tabC */
	C.key_size=len_key;
	C.tab_keys=(char**)malloc(C.key_size*sizeof(char*));
	if (C.tab_keys==NULL){printf("Pb d'allocation"); exit(1);}

	/* allocation dynamique de la mémoire pour chaque tabC[i] et initialisation de ses valeurs par défaut */
	for(int i=0;i<C.key_size;i++){

		C.tab_keys[i]=(char*)malloc(MAX_VALID_KEY_CHARS*sizeof(char));
		if (C.tab_keys[i]==NULL){printf("Pb d'allocation"); exit(1);}

		if(i==0) x=49;
		else x=48;
		j=0;
		while(x<=57){
			C.tab_keys[i][j]=x;
			x+=1;
			j+=1;
		}
		C.tab_keys[i][j]='\0';
	}
	/* suppression des caractères de clés impossibles */
	Possibility(file_crypt, C.tab_keys, C.key_size);
	/* liste les différentes clées possibles pour les caractères de clé possible */
	LKeyPotential(Keys, C.tab_keys, c, &possibilities, 0, C.key_size);
	/* libération de la mémoire */
	for(int i=0;i<C.key_size;i++){ free(C.tab_keys[i]); } 
	free(C.tab_keys);

	return possibilities;
}

