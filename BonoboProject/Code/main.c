#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#include "BlockChain/blockChain.h"
#include "BlockChain/block.h"
#include "MerkleTree/merkleTree.h"
#include "Transaction/user.h"
#include "Utils/list.h"
#include "Utils/utils.h"

#define NB_USERS 10 // number of users 
#define NB_BLOCKS 100 // number of blocks created
#define INFLATION 10 // number of blocks mined in the market phase before dividing the reward by 2
#define NB_MAX_TRANS 4 // maximum number of transactions randomly created for every block created

char *random_transaction(List *l_users){
    char *new_transaction;
    long int value;
    User *rand_user1;
    User *rand_user2;
    value = rand() % 10000000000; // <=> 100 Bnb
    rand_user1 = list_at(l_users, (rand()%NB_USERS));
    rand_user2 = list_at(l_users, (rand()%NB_USERS));
    new_transaction = user_transaction(rand_user1, rand_user2, value);
    return new_transaction;
}

int main(void){
    
    BlockChain *blockchain = blockchain_create();
    List *l_users = list_create();
    List *l_trans = list_create();
    User *coinbase = user_create("coinbase", 0);
    User *creator = user_create("Creator", 0);
    list_push_back(l_users, creator);
    printf("\n---------------------------------------- Genesis ----------------------------------------\n\n");
    list_push_back(l_trans, user_transaction(creator, creator, 0));
    Block *genesis = block_create(0, list_size(l_trans), "0", l_trans);
    block_mining(genesis, coinbase, creator, 5000000000); // <=> 50 Bnb
    blockchain_add(blockchain, genesis);

    int numBlock;
    char username[BUFSIZ], user[] = "User", buffer[11] = "";
    User *new_user;
    Block *new_block;
    printf("\n---------------------------------------- Helicopter Money ----------------------------------------\n\n");
    for(numBlock = 1; numBlock <= NB_USERS; numBlock++){
        strcpy(username, user);
        itoa(numBlock, buffer);
        strcat(username, buffer);
        new_user = user_create(username, 0);
        list_push_front(l_users, new_user);
        list_push_back(l_trans, user_transaction(new_user, new_user, 0));
        new_block = block_create(numBlock, list_size(l_trans), block_hash(blockchain_lastblock(blockchain)), l_trans);
        block_mining(new_block, coinbase, new_user, 5000000000); // <=> 50 Bnb
        blockchain_add(blockchain, new_block);
    }

    bool stop_inflation = false;
    int inflation_rounds = 0;
    int nb_transactions;
    long int reward = 5000000000; // <=> 50 Bnb
    User *rand_miner;
    srand(time(NULL));
    printf("\n---------------------------------------- Market ----------------------------------------\n\n");
    for(numBlock = numBlock; numBlock <= NB_BLOCKS; numBlock++){
        nb_transactions = rand() % NB_MAX_TRANS + 1;
        for(int i=0; i < nb_transactions; i++){
            list_push_back(l_trans, random_transaction(l_users));
        }
        rand_miner = list_at(l_users, (rand()%NB_USERS));
        nb_transactions = rand() % list_size(l_trans) + 1;
        new_block = block_create(numBlock, nb_transactions, block_hash(blockchain_lastblock(blockchain)), l_trans);
        block_mining(new_block, coinbase, rand_miner, reward); 
        blockchain_add(blockchain, new_block);
        if(numBlock%INFLATION == 0){
            reward /= 2;
            inflation_rounds++;
        }
        if(!stop_inflation && reward < 5000000){ // <=> 0.5 Bnb
            reward = 0;
            stop_inflation = true;
            printf("\n---------------------------------------- End of inflation ----------------------------------------\n");
            printf("Money created : %ld (%ld Bnb)\n", -(user_money(coinbase)), -(user_money(coinbase))/100000000);
            printf("Inflation rounds : %d\n\n", inflation_rounds);
        }
    }
    // free the memory
    user_delete(coinbase);
    list_delete(&l_users);
    list_delete(&l_trans);
    blockchain_dump(blockchain);
    return 0;
}
