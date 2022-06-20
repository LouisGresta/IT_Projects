
#ifndef __MERKLETREE__H__
#define __MERKLETREE__H__
#include <stdbool.h>

#include "../Utils/list.h"

/*------------------------  MerkleTreeType  -----------------------------*/

/** \defgroup ADTMerkletree MerkleTree
 * Documentation of the implementation of the abstract data type MerkleTree.
 * @{
 */

/** \defgroup MerkleTreeType Type definition.
 * @{
 */
/** Opaque definition of the type MerkleTree */
typedef struct s_MerkleTree MerkleTree;
typedef MerkleTree *ptrMerkleTree;

/** @} */

/*------------------------  BaseMerkleTree  -----------------------------*/

/** \defgroup BasemerkleTree General function on binary trees.
 * @{
 *
*/
/** Constructor : builds an empty MerkleTree
 */
MerkleTree *merkletree_create();

/** Destructor : Delete the tree.
 */
void merkletree_delete(ptrMerkleTree *t);

/** Operator : is the tree empty ?
 * merkletree_empty : MerkleTree -> boolean
 */
bool merkletree_empty(const MerkleTree *t);

/** Operator : returns the value of the root of the tree.
 * @pre !merkletree_empty(t)
 */
const char* merkletree_root(const MerkleTree *t);

/** Operator : returns the left subtree.
 * @pre !merkletree_empty(t)
 */
MerkleTree *merkletree_left(const MerkleTree *t);

/** Operator : returns the right subtree.
 * @pre !merkletree_empty(t)
 */
MerkleTree *merkletree_right(const MerkleTree *t);

/** Operator : returns the parent subtree
 * @pre !merkletree_empty(t)
 */
MerkleTree *merkletree_parent(const MerkleTree *t);


/** Constructor : builds a MerkleTree from a list of transactions
 * @param l_trans the list to convert into a MerkleTree.
 * @pre !list_is_empty(l_trans)
 */
MerkleTree *merkletree_constructfromlist(List *l_trans);

/** @} */
/** Functor with user data to be used with the several visitors that can run on trees.
 *  This functor receive as argument subtree from which the root node must be processes and an opaque
 *  pointer to user provided data.
 */
typedef void(*OperateFunctor)(const MerkleTree *, void *);

/** Visitor : prefix, breadth first visitor.
 * This is the iterative implementation of the visitor.
 * @param t the tree to visit.
 * @param f the functor to apply on each node of the tree.
 * @param userData user defined parameters to forward to the functor.
 */
void merkletree_iterative_breadth_prefix(const MerkleTree *t, OperateFunctor f, void *userData);

/** @} */
#endif