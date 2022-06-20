#include <stdio.h>
#include <stdlib.h>

#include "../Code/MerkleTree/merkleTree.h"
#include "../Code/Utils/list.h"
/**
 * Print the value of a node.
 * @param t the tree node to output
 * @param userData unused pointer.
 */
void print_tree(const MerkleTree *t, void *userData) {
    (void) userData;
    printf("%s\n\t", merkletree_root(t));
}

/** Main function for testing the MerkleTree implementation.
 * The main function expects one parameter that is the file where values added to the tree, searched into the
 * tree and removed from the tree are to be read.
 *
 * This file must contain the following informations :
 * - on the first line, the number of values to be added to the tree,
 * - on the second line, the values to be added, separated by a space (or tab).
 *
 * The values will be added in the order they are read from the file.
 */
int main(int argc, char **argv) {

    if (argc < 2) {
        fprintf(stderr, "usage : %s filename\n", argv[0]);
        return 1;
    }

    FILE *input = fopen(argv[1], "r");

    if (!input) {
        perror(argv[1]);
        return 1;
    }

    List *l_transaction = list_create();

    /* add transactions to the List */
    printf("Adding transactions to the List.\n\t");
    int n;
    fscanf(input, "%d", &n);

    for (int i = 0; i < n; ++i) {
        char *str = malloc(BUFSIZ*sizeof(char));
        fscanf(input, "%s", str);
        printf("%s\n\t", str);
        list_push_front(l_transaction, str);
    }
    printf("\nDone.\n");

    /* add transactions to the MerkleTree */
    printf("Construct the tree from transactions in the List.\n\t");
    MerkleTree *theTree = NULL;
    theTree = merkletree_constructfromlist(l_transaction);

    /* iterative breadfirst and depth-first traversal of the tree to visualize the tree */
    merkletree_iterative_breadth_prefix(theTree, print_tree, NULL);
    printf("\nDone.\n");
    printf("MerkleRoot.\n\t");
    printf("%s", merkletree_root(theTree));
    printf("\nDone.\n");
    
    /* free all used memory */
    printf("Deleting the List.");
    list_delete(&l_transaction);
    printf("\nDone.\n");
    printf("Deleting the tree.");
    merkletree_delete(&theTree);
    printf("\nDone.\n");

    fclose(input);
    return 0;
}