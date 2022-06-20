#include <string.h>
#include <stdio.h>
#include <ctype.h>

#include "dh_crack.h"
#include "dh_crack_c2.h"

/// \brief Fonction dh_crack_c2
/// \file dh_crack_c2.c
/// \author Louis Gresta
/// \date 10 janvier 2021

void initFreq(double Freq[MAX_LETTERS]){
	/// \brief initialise les valeurs de Freq[MAX_LETTERS] à 0
	/// \param[in] Freq[MAX_LETTERS] un tableau de flottants représentant des fréquences de caractères dans un message
	for(int i=0;i<MAX_LETTERS;i++){
		Freq[i]=0;
	}
}

void Decrypt(char* file_crypt, char* file_decrypt, char* key, int len_key){
	/// \brief Décrypte le fichier codé avec une clé donnée dans le fichier donné
	/// \param[in] file_crypt une chaîne de caractère donnant l'emplacement du fichier crypté
	/// \param[in] file_decrypt une chaîne de caractère donnant l'emplacement du fichier décrypté
	/// \param[in] key une chaîne de caractère correspondant à une clé candidate
	/// \param[in] len_key un entier correspondant à la longueur de la clé

	/* initialisation et ouverture des fichier à utiliser */
	int letter, i=0;
	FILE *crypt=fopen(file_crypt, "r");
	if(crypt == NULL){ printf("erreur à la lecture du fichier %s", file_crypt); exit(1);}

	FILE *decrypt=fopen(file_decrypt, "w+");
	letter=fgetc(crypt);
	while(letter != EOF){
		fputc(letter^key[i], decrypt);
		i+=1;
		i=i%len_key;
		letter=fgetc(crypt);
		
	}
	/* fermeture des fichiers utilisés */
	fclose(crypt);
	fclose(decrypt);
}

void FreqLetter(double Freq[MAX_LETTERS], char* file_decrypt){
	/// \brief Calcul la fréquence d'apparition des lettres d'un fichier décrypté
	/// \param[in] Freq[MAX_LETTERS] un tableau de flottants représentant des fréquences de caractères dans un message
	/// \param[in] file_decrypt une chaîne de caractère donnant l'emplacement du fichier décrypté

	/* initialisation et ouverture du fichier à utiliser */

	FILE *decrypt=fopen(file_decrypt, "r");
	int letter, alpha=0;
	/* Pour chaque caractères du fichier décrypté, 
	si c'est une lettre de la langue anglaise, 
	on ajoute 1 au total de nombre de cette même lettre dans le tableau de fréquences */
	letter=fgetc(decrypt);
	while(letter != EOF){
		if(isalpha(letter)){
			alpha+=1;
			for(int i=0;i<MAX_LETTERS;i++){
				if(letter==i+97  || letter==i+65){
					Freq[i]+=1;
					break;
				}
			}
		}
		letter=fgetc(decrypt);
	}
	/* passage d'occurences de lettres à une frequence d'apparition */
	for(int i=0;i<MAX_LETTERS;i++){
		Freq[i]=Freq[i]/alpha*100;
	}
	/* fermeture du fichier utilisé */
	fclose(decrypt);
}

int d(double Freq_th[MAX_LETTERS], double Freq[MAX_LETTERS]){
	/// \brief Calcule la distance entre deux tableaux de fréquences
	/// \param[in] Freq_th[MAX_LETTERS] un tableau de flottants représentant des fréquences théoriques de caractères dans un message
	/// \param[in] Freq[MAX_LETTERS] un tableau de flottants représentant des fréquences de caractères dans un message

	/* initialisation */
	int score=0;
	/* calcul du score en parcourant chaque fréquences des lettres */
	for(int i=0;i<MAX_LETTERS;i++){
		score+=(Freq_th[i]-Freq[i])*(Freq_th[i]-Freq[i]);
	}
	return score;
}

void SortKeys(char Keys[MAX_KEYS][MAX_SIZE_KEY], int Scores[MAX_KEYS], int nbKeys, int len_key){
	/// \brief Trie la liste de clées en fonction du score qu'elles ont obtenu (tri par sélection)
	/// \param[in] Keys[MAX_KEYS][MAX_SIZE_KEY] un tableau de caractères correspondant à la liste des clés candidates
	/// \param[in] Scores[MAX_KEYS] un tableau d'entier répertoriant les scores des clés du même indice
	/// \param[in] nbKeys un entier représentant le nombre de clés candidates
	/// \param[in] len_key un entier correspondant à la longueur de la clé


	/* initialisation des variables temporaires pour échanger la place de deux élements des tableaux */
	int tmp;
	char c[MAX_SIZE_KEY];
	/* tri du tableau de scores pour trier le tableau des clés correspondantes aux scores obtenus */
	for(int i=0;i<nbKeys-1;i++)
	    for(int j=i+1;j<nbKeys;j++)
	        if(Scores[i]<Scores[j]){
	        	/* échange de la place des éléments des tableaux aux indices i et j */
	            tmp=Scores[i];
	            Scores[i]=Scores[j];
	            Scores[j]=tmp;
            	strcpy(c, Keys[i]);
            	strcpy(Keys[i], Keys[j]);
            	strcpy(Keys[j], c);
	        }
}

void dh_crack_c2(char Keys[MAX_KEYS][MAX_SIZE_KEY], int Scores[MAX_KEYS], char* file_crypt, int len_key, int nbKeys){
	/// \brief Donne les clés candidates triées en utilisant la méthode C2
	/// \param[in] Keys[MAX_KEYS][MAX_SIZE_KEY] un tableau de caractères correspondant à la liste des clés candidates
	/// \param[in] file_crypt une chaîne de caractère donnant l'emplacement du fichier crypté
	/// \param[in] len_key un entier correspondant à la longueur de la clé
	/// \param[in] nbKeys un entier représentant le nombre de clés candidates

	/* initialisation */

	double Freq[MAX_LETTERS], Freq_th[MAX_LETTERS]={8.167, 1.492, 2.782, 4.253, 12.702, 2.228, 2.015, 6.094, 6.966, 0.153, 
		0.772, 4.025, 2.406, 6.749, 7.507, 1.929, 0.095, 5.987, 6.327, 9.056, 2.758, 0.978, 2.360, 0.150, 1.974, 0.074};

	/* parcours de toutes les clés possibles et calcul de fréquences pour chaque fichier décrypté avec la clé parcourue */
	for(int i=0;i<nbKeys;i++){
		Decrypt(file_crypt, "tmp.txt", Keys[i], len_key);
		initFreq(Freq);
		FreqLetter(Freq, "tmp.txt");
		Scores[i]=d(Freq_th, Freq);
	}
	/* tri des clés en fonction du score quelles ont obtenu */
	SortKeys(Keys, Scores, nbKeys, len_key);
	remove("tmp.txt"); // suppression du fichier temporaire utilisé pour stocker les cractères décryptés par les différentes clés candidates 
}

