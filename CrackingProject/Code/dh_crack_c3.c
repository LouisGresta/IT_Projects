#include <string.h>
#include <stdio.h>
#include <ctype.h>

#include "dh_crack.h"
#include "dh_crack_c2.h"
#include "dh_crack_c3.h"

/// \brief Fonction dh_crack_c3
/// \file dh_crack_c3.c
/// \author Louis Gresta
/// \date 10 janvier 2021

void initScore(int Scores[MAX_KEYS]){
	/// \brief initialise les valeurs de Scores[MAX_KEYS] à 0
	/// \param[in] Scores[MAX_KEYS] un tableau d'entier répertoriant les scores des clés du même indice
	for(int i=0;i<MAX_KEYS;i++){
		Scores[i]=0;
	}
}
void CaseWords(int Scores[MAX_KEYS], int index_key, char* file_decrypt, char* file_dict){
	/// \brief Donne le score d'une clé en fonction du nombre de mots décryptés dans le dictionnaire 
	/// \param[in] Scores[MAX_KEYS] un tableau d'entier répertoriant les scores des clés du même indice
	/// \param[in] index_key un entier correspondant à l'indice actuel donnant la clé 
	/// \param[in] file_decrypt une chaîne de caractère donnant l'emplacement du fichier décrypté
	/// \param[in] file_dict une chaîne de caractère donnant l'emplacement du fichier du dictionnaire

	/* initialisation et ouverture des fichier à utiliser */
	FILE *decrypt=fopen(file_decrypt, "r");
	FILE *dict=fopen(file_dict, "r");
	if(decrypt == NULL){ printf("erreur à la lecture du fichier %s", file_decrypt); exit(1);}
	if(dict == NULL){ printf("erreur à la lecture du fichier %s", file_dict); exit(1);}
	int i, letter;
	char mot_decrypt[MAX_SIZE_WORD], mot_dict[MAX_SIZE_WORD];

	/* Pour chaque mots du fichier décrypté on le compare aux mots du dictionnaire fourni, 
	si il appartient au dictionnaire, on ajoute 1 au score de la clé */
	letter=fgetc(decrypt);
	while(letter!=EOF){
		/* création du mot composé d'une suite de lettres */ 
		for(i=0;letter!=EOF && isalpha(letter);i++){
			if(isupper(letter)) letter=tolower(letter);
			mot_decrypt[i]=letter;
			letter=fgetc(decrypt);
		}
		mot_decrypt[i]='\0';
		/* comparaison du mot décrypté à tout les mots du dictionnaire fourni en partant du début */
		rewind(dict);
		if(strlen(mot_decrypt)>=3 && strlen(mot_decrypt)<MAX_SIZE_WORD){
			while(fgets(mot_dict, MAX_SIZE_WORD, dict) != NULL){
				mot_dict[strlen(mot_dict)-1]='\0';
				if(strcmp(mot_decrypt, mot_dict)==0){
					Scores[index_key]+=1;
					break;
				}
			}
		}
		letter=fgetc(decrypt);
	}

	/* fermeture des fichiers utilisés */
	fclose(decrypt);
	fclose(dict);
}

void dh_crack_c3(char Keys[MAX_KEYS][MAX_SIZE_KEY], int Scores[MAX_KEYS], char* file_crypt, char* file_dict, int len_key, int nbKeys){
	/// \brief Donne les clés candidates triées en utilisant la méthode C3
	/// \param[in] Keys[MAX_KEYS][MAX_SIZE_KEY] un tableau de caractères correspondant à la liste des clés candidates
	/// \param[in] file_decrypt une chaîne de caractère donnant l'emplacement du fichier décrypté
	/// \param[in] file_dict une chaîne de caractère donnant l'emplacement du fichier du dictionnaire
	/// \param[in] len_key un entier correspondant à la longueur de la clé
	/// \param[in] nbKeys un entier représentant le nombre de clés candidates

	/* initialisation */
	initScore(Scores);
	/* parcours de toutes les clés possibles */
	for(int i=0;i<nbKeys;i++){
		Decrypt(file_crypt, "tmp.txt", Keys[i], len_key);
		CaseWords(Scores, i, "tmp.txt", file_dict);
	}
	/* tri des clés en fonction du score quelles ont obtenu */
	SortKeys(Keys, Scores, nbKeys, len_key);
	remove("tmp.txt"); // suppression du fichier temporaire utilisé pour stocker les cractères décryptés par les différentes clés candidates 
}