#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "user.h"
#include "../Sha256/sha256_utils.h"
#include "../Utils/utils.h"

struct s_User {
    long int money; // en satoBnb
	char name[BUFSIZ];
};

User *user_create(char name[BUFSIZ], long int value){
	User *user = malloc(sizeof(struct s_User));
	user->money = value;
	strcpy(user->name, name);

	return user;
}
long int user_money(User *u){
	return u->money;
}

char *user_name(User *u){
	return u->name;
}

void user_delete(User *u){
	free(u);
}

char *user_transaction(User *from, User *to, long int value){
	char buffer[BUFSIZ], *transaction = malloc(BUFSIZ*sizeof(char));

	from->money -= value;
	to->money += value;
	
	strcpy(transaction, from->name);
	strcat(transaction, " envoie ");
	value /= 100000000;
	ltoa(value, buffer);
	strcat(transaction, buffer);
	strcat(transaction, " Bnb à ");
	strcat(transaction, to->name);

	return transaction;
}