#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <stdbool.h>
#include <string.h>

#include "merkleTree.h"
#include "../Utils/queue.h"
#include "../Utils/list.h"
#include "../Sha256/sha256_utils.h"

struct s_MerkleTree {
    struct s_MerkleTree *parent;
    struct s_MerkleTree *left;
    struct s_MerkleTree *right;
    char root[SHA256_BLOCK_SIZE*2 + 1];
};

MerkleTree *merkletree_create(){
    return NULL;
}
MerkleTree *merkletree_cons(MerkleTree *left, MerkleTree *right, char *transaction) {
    MerkleTree *t = malloc(sizeof(struct s_MerkleTree));
    char tmpStringToHash[BUFSIZ];
    t->parent = NULL;
    t->left = left;
    t->right = right;
    if(merkletree_empty(t->left) && merkletree_empty(t->right))
        strcpy(tmpStringToHash, transaction);
    else{
        strcpy(tmpStringToHash, t->left->root);
        strcat(tmpStringToHash, t->right->root);
        t->left->parent = t;
        t->right->parent = t;
    }
    sha256ofString((BYTE *)tmpStringToHash, t->root);
    return t;
}

bool merkletree_empty(const MerkleTree *t) {
    return t == NULL;
}

void merkletree_delete(ptrMerkleTree *t) {
    if(merkletree_empty(*t)) return;
    merkletree_delete(&((*t)->left));
    merkletree_delete(&((*t)->right));
	free(*t);
}

const char *merkletree_root(const MerkleTree *t) {
    assert(!merkletree_empty(t));
    return t->root;
}

MerkleTree *merkletree_left(const MerkleTree *t) {
    assert(!merkletree_empty(t));
    return t->left;
}

MerkleTree *merkletree_right(const MerkleTree *t) {
    assert(!merkletree_empty(t));
    return t->right;
}

MerkleTree *merkletree_parent(const MerkleTree *t) {
    assert(!merkletree_empty(t));
    return t->parent;
}

MerkleTree *merkletree_constructfromlist(List *l_trans){
    assert(!list_is_empty(l_trans));
    
    MerkleTree *t=NULL;
    MerkleTree *node1 = NULL, *node2 = NULL;
    char* data;
    char hash[SHA256_BLOCK_SIZE*2 + 1];
    int limit = 1, nb_transaction = list_size(l_trans);
    Queue *q_tree = createQueue();
    while(limit<=nb_transaction){
        limit*=2;
    }
    for(int i=0; i < list_is_empty(l_trans); i++){    
        data = list_at(l_trans, i);
        sha256ofString((BYTE *)data, hash);
        node1 = merkletree_cons(merkletree_create(), merkletree_create(), hash);
        queuePush(q_tree, node1);
        limit--;
    }
    while(limit>0){
        node1 = merkletree_cons(merkletree_create(), merkletree_create(), hash);
        queuePush(q_tree, node1);
        limit--;
    }
    while(queueSize(q_tree)>1){
        node1 = queueTop(q_tree);
        queuePop(q_tree);
        node2 = queueTop(q_tree);
        queuePop(q_tree);
        
        t = merkletree_cons(node1, node2, NULL);
        queuePush(q_tree, t);
    }
    t = queueTop(q_tree);
    deleteQueue(&q_tree);
    return t;
}

/*-------------------- parcours en largeur --------------------------*/

void merkletree_iterative_breadth_prefix(const MerkleTree *t, OperateFunctor f, void *userData) {
    if(t==NULL) return;
    Queue *q = createQueue();
    const MerkleTree *tmp = t;

    while(tmp){
    	f(tmp, userData);

    	if(!merkletree_empty(tmp->left)) queuePush(q, tmp->left);
    	if(!merkletree_empty(tmp->right)) queuePush(q, tmp->right);

    	if(queueEmpty(q)) tmp = NULL;
    	else{
    		tmp = queueTop(q);
    		queuePop(q);
    	}
    }
    deleteQueue(&q);
}