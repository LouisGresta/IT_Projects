#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <stdbool.h>
#include <string.h>

#include "blockChain.h"
#include "block.h"
#include "../Sha256/sha256_utils.h"
#include "../Transaction/user.h"

typedef struct s_Node{
    Block *block;
	struct s_Node *previous;
} Node;

struct s_BlockChain {
	int difficulty;
	Node *head;
	Node *tail;
	int size;
};


/*-----------------------------------------------------------------*/

BlockChain *blockchain_create() {
	BlockChain *bc = NULL;
	bc = malloc(sizeof(struct s_BlockChain));
	bc->head = bc->tail = NULL;
	bc->size = 0;
	bc->difficulty = NB_ZERO;
	return bc;
}

bool blockchain_empty(BlockChain *bc){
	return bc->size == 0;
}

Block *blockchain_lastblock(BlockChain *bc){
	assert(!blockchain_empty(bc));
	return bc->head->block;
}

void blockchain_dump(BlockChain *bc){
	printf("\n---------------------------------------- Blockchain Dump ----------------------------------------\n\n");
	Node *del, *tmp = bc->head;
	while(tmp){
		del = tmp;
		tmp = tmp->previous;
		block_print(del->block);
		block_delete(del->block);
		free(del);
	}
	free(bc);
}

/*-----------------------------------------------------------------*/

BlockChain *blockchain_add(BlockChain *bc, Block *b) {
	Node *new = malloc(sizeof(struct s_Node));
	new->block = b;
	new->previous = bc->head;
	if(blockchain_empty(bc))
		bc->tail = new;
	bc->head = new;
	bc->size++;
	return bc;
}
