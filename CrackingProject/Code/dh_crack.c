#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include "dh_crack.h"
#include "dh_crack_c1.h"
#include "dh_crack_c2.h"
#include "dh_crack_c3.h"

/// \brief main pour le crack
/// \file dh_crack.c
/// \author Louis Gresta
/// \date 10 janvier 2021

int main(int argc, char const *argv[]){

	/* initialisation */
	char file_crypt[256]; // -i
	char methode[4]; // -m
	int len_key; // -k
	char file_dict[256]; // -d

	/* récupération des paramètres fournis à l'exécution du programme et traitement */

	if(argc==2 && strcmp(argv[1],"-h")==0){
		printf("./dh_crack -i <fichier_crypté> -m <méthode> -k <longueur_clé> [ -d <fichier_dictionnaire> ]\n");
		printf("fichier_crypté : fichier à décrypter\n");
		printf("méthode : 'c1' ou 'all'\n");
		printf("longueur_clé : longueur de la clé (maximum 7)\n");
		printf("fichier_dictionnaire : fichier du dictionnaire (option utilisée seulement pour la méthode 'all')\n");
		exit(0);
	}

	else if(argc==7){
		for(int i=0;i<argc;i++){
			if(strcmp(argv[i],"-i")==0) strcpy(file_crypt, argv[i+1]);
			else if(strcmp(argv[i],"-m")==0) strcpy(methode, argv[i+1]);
			else if(strcmp(argv[i],"-k")==0) len_key=atoi(argv[i+1]);
		}
	}
	else if(argc==9){
		for(int i=0;i<argc;i++){
			if(strcmp(argv[i],"-i")==0) strcpy(file_crypt, argv[i+1]);
			else if(strcmp(argv[i],"-m")==0) strcpy(methode, argv[i+1]);
			else if(strcmp(argv[i],"-k")==0) len_key=atoi(argv[i+1]);
			else if(strcmp(argv[i],"-d")==0) strcpy(file_dict, argv[i+1]);
		}
	}
	else {
		printf("Erreur de paramètres, faites './dh_crack -h' pour l'aide\n");
		exit(1);
	}

	/* initialisation */
	char Keys[MAX_KEYS][MAX_SIZE_KEY];
	int nbKeys, Scores[MAX_KEYS];

	if(strcmp(methode,"c1")==0 && argc==7){
		/* appel des différentes méthodes de résolution */
		nbKeys=dh_crack_c1(Keys, file_crypt, len_key);
		/* affichage des clés possibles dans l'ordre lexicographique */
		printf("[ ");
		for(int i=0;i<nbKeys;i++){
			printf("\"%s\", ", Keys[i]);
		}
		printf("]\n");
	}
	else if(strcmp(methode,"all")==0 && argc==9){
		/* appel des différentes méthodes de résolution */
		nbKeys=dh_crack_c1(Keys, file_crypt, len_key);
		dh_crack_c2(Keys, Scores, file_crypt, len_key, nbKeys);
		printf("[ ");
		for(int i=0;i<nbKeys;i++){
			printf("\"%s\"= ", Keys[i]);
			printf("%d, ", Scores[i]);
		}
		printf("]\n");
		
		dh_crack_c3(Keys, Scores, file_crypt, file_dict, len_key, nbKeys);

		/* affichage des clés triées dans l'ordre décroissant de leur score */
		printf("[ ");
		for(int i=0;i<nbKeys;i++){
			printf("\"%s\"= ", Keys[i]);
			printf("%d, ", Scores[i]);
		}
		printf("]\n");
	}
	else {
		printf("Erreur de paramètres, faites './dh_crack -h' pour l'aide\n");
		exit(1);
	}
	
	return 0;
}