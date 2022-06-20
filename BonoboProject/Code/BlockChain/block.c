#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <stdbool.h>
#include <string.h>

#include "block.h"
#include "../MerkleTree/merkleTree.h"
#include "../Sha256/sha256_utils.h"
#include "../Utils/utils.h"
#include "../Utils/list.h"
#include "../Transaction/user.h"

struct s_Block {
    int numBlock;
	char previous_hash[SHA256_BLOCK_SIZE*2 + 1];
	char current_hash[SHA256_BLOCK_SIZE*2 + 1];
    char merkle_root[SHA256_BLOCK_SIZE*2 + 1];
	char timeStamp[BUFSIZ];
	int nb_transactions;
	List *l_trans;
	long int nonce;
};

/*-----------------------------------------------------------------*/

Block *block_create(int num, int nb_transactions, char previous_hash[SHA256_BLOCK_SIZE*2 + 1], List *l_trans){
    Block *b = malloc(sizeof(struct s_Block));
    b->numBlock = num;
	b->nonce = 0;
	char *date = getTimeStamp();
    strcpy(b->previous_hash, previous_hash);
	strcpy(b->timeStamp, date);
	MerkleTree *merkle_tree = merkletree_constructfromlist(l_trans);
    strcpy(b->merkle_root, merkletree_root(merkle_tree));
	b->nb_transactions = nb_transactions;
	b->l_trans = list_create();
	for(int i = 0; i < b->nb_transactions; i++){
		list_push_back(b->l_trans, list_front(l_trans));
		list_pop_front(l_trans);
	}
    return b;
}

void block_delete(Block *b){
	list_delete(&(b->l_trans));
    free(b);
}

bool block_mined(Block *b){
	return b->current_hash != NULL;
}

char *block_hash(Block *b){
	assert(block_mined(b));
	return b->current_hash;
}

bool block_goodHash(char hash[SHA256_BLOCK_SIZE*2 + 1]){
	for (int i = 0; i < NB_ZERO; i++){
		if(hash[i] != '0')
			return false;
	}
	return true;
}

void block_mining(Block *b, User *bank, User *user, long int reward){
	char buffer[11], tmpString[BUFSIZ], blockData[BUFSIZ];
	char hashBlock[SHA256_BLOCK_SIZE*2 + 1];
	itoa(b->numBlock, buffer);
	strcpy(blockData, buffer);
	strcat(blockData, b->previous_hash);
	strcat(blockData, b->merkle_root);

	// hash calculation
	strcpy(tmpString, blockData);
	ltoa(b->nonce, buffer);
	strcat(tmpString, buffer);
	sha256ofString((BYTE *)tmpString, hashBlock);
	while(!block_goodHash(hashBlock)){
		b->nonce++;
		strcpy(tmpString, blockData);
		ltoa(b->nonce, buffer);
		strcat(tmpString, buffer);
		sha256ofString((BYTE *)tmpString, hashBlock);
	}
	char *transaction = user_transaction(bank, user, reward);
	free(transaction);
	strcpy(b->current_hash, hashBlock);
	printf("Block %d mined.    Hash : %s    Nonce : %ld\n", b->numBlock, b->current_hash, b->nonce);
}

void block_print(Block *b){
	printf("Numéro de block :\t%d\n", b->numBlock);
	printf("TimeStamp : %s\n", b->timeStamp);
	printf("Nombre de transactions : %d\n", b->nb_transactions);
	printf("Liste des transactions :\n");
	for(int i=0; i < b->nb_transactions; i++){
		char *transaction = list_at(b->l_trans, i);
		printf("\ttransaction %d : %s\n", (i+1), transaction);
	}
	printf("Merkle root hash : %s\n", b->merkle_root);
	printf("Hash du block : %s\n", b->current_hash);
	printf("Hash du précédent block : %s\n", b->previous_hash);
	printf("Nonce du block :\t%ld\n", b->nonce);
	printf("\n----------------------------------------------------------------------------------------------------\n\n");
}
