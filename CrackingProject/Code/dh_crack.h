#ifndef CRACK_H
#define CRACK_H

#define  MAX_SIZE_KEY 8  /* 7 + '\0' caractères max dans la clef */
#define  MAX_VALID_KEY_CHARS 10 // dix caractères numériques possibles pour chaque caractère de la clef
#define  MAX_KEYS 30000 // nombre max de clefs potentielles gérées


typedef struct {
	int key_size; // longueur de la clef
	char **tab_keys; // 2 dim : ligne i = liste des caractères admissibles pour clef[i]
} t_tab_keys;

#endif
