#ifndef __BLOCK__H__
#define __BLOCK__H__

#include "../Sha256/sha256_utils.h"
#include "../Transaction/user.h"
#include "../Utils/list.h"

#define NB_ZERO 4 // number of zero for the mining

/** \defgroup ADTBlock Block
 * Documentation of the implementation of the abstract data type Block.
 * @{
 */

/** \defgroup BlockType Type definition.
 * @{
 */
/** Opaque definition of the type Block */
typedef struct s_Block Block;
/** @} */

/** Constructor : builds a Block
 * @param num The number of the block
 * @param nb_transactions The number of transactions to add at the block
 * @param previous_hash The hash of the previous block in the blockchain
 * @param l_trans The list of transactions
 * @return The created block
 */
Block *block_create(int num, int nb_transactions, char previous_hash[SHA256_BLOCK_SIZE*2 + 1], List *l_trans);

/** Destructor : Delete the block.
 */
void block_delete(Block *b);

/** Operator : Is the block mined ?
 * block_mined : Block -> boolean
 */
bool block_mined(Block *b);

/** Operator : returns the hash of the block.
 * @pre block_mined(b)
 */
char *block_hash(Block *b);

/** \defgroup other functions.
 * @{
 */
/** Function : Is the hash is at the good number of zero ?
 * @pre block_mined(b)
 */
bool block_goodHash(char hash[SHA256_BLOCK_SIZE*2 + 1]);

/** Function : A User mined the block.
 * @param b The block to mine
 * @param bank The user who gives the reward to every user who mined a block 
 * @param user The user who mined the block
 * @param reward The reward send to the user who mine the block
 */
void block_mining(Block *b, User *bank, User *user, long int reward);

/** Printer : print infos in the block mined.
 * @param b the block to print
 * @pre block_mined(b)
 */
void block_print(Block *b);
/** @} */
/** @} */

#endif