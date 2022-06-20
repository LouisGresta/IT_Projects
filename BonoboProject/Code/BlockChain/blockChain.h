#ifndef __BLOCKCHAIN__H__
#define __BLOCKCHAIN__H__

#include "block.h"
#include "../Sha256/sha256_utils.h"
#include "../Transaction/user.h"

/** \defgroup ADTBlockchain Blockchain
 * Documentation of the implementation of the abstract data type Blockchain.
 * @{
 */

/** \defgroup BlockchainType Type definition.
 * @{
 */
/** Opaque definition of the type Blockchain */
typedef struct s_BlockChain BlockChain;
/** @} */

/** Constructor : builds an empty Blockchain
 * @return The created blockchain
 */
BlockChain *blockchain_create();

/** Operator : is the blockchain empty ?
 * blockchain_empty : Blockchain -> boolean
 */
bool blockchain_empty(BlockChain *bc);

/** Operator : returns the last block of the blockchain.
 * @param bc The blockchain where the block is
 * @pre !blockchain_empty(t)
 */
Block *blockchain_lastblock(BlockChain *bc);

/** Destructor/Visitor : Delete the blockchain and print blocks in the blockchain.
 * @param bc The blockchain to delete
 */
void blockchain_dump(BlockChain *bc);

/** Add a block at the head of the blockchain.
 	@param bc The blockchain to modify
 	@param b The block to add
 	@return The modified blockchain
 */
BlockChain *blockchain_add(BlockChain *bc, Block *b);

/** @} */
#endif