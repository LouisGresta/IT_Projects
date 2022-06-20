/*-----------------------------------------------------------------*/
/*
 Licence Informatique - Structures de données
 Mathias Paulin (Mathias.Paulin@irit.fr)
 
 Implantation du TAD List vu en cours.
 */
/*-----------------------------------------------------------------*/
#include <stdio.h>
#
#include <stdlib.h>
#include <assert.h>

#include "list.h"

typedef struct s_LinkedElement {
	void *value;
	struct s_LinkedElement *previous;
	struct s_LinkedElement *next;
} LinkedElement;

/* Use of a sentinel for implementing the list :
 The sentinel is a LinkedElement * whose next pointer refer always to the head of the list and previous pointer to the tail of the list
 */
struct s_List {
	LinkedElement *sentinel;
	int size;
};


/*-----------------------------------------------------------------*/

List *list_create() {
	List *l = NULL;
	l = malloc(sizeof(List));
	l->sentinel = malloc(sizeof(LinkedElement));
	l->sentinel->previous = l->sentinel->next = l->sentinel;
	l->size = 0;
	return l;
}

/*-----------------------------------------------------------------*/

List *list_push_back(List *l, void *v) {
	LinkedElement *new_sentinel = malloc(sizeof(LinkedElement));
	new_sentinel->value = v;
	new_sentinel->next = l->sentinel;
	new_sentinel->previous = l->sentinel->previous;
	new_sentinel->previous->next = new_sentinel;
	l->sentinel->previous = new_sentinel;
	l->size++;
	return l;
}

/*-----------------------------------------------------------------*/

void list_delete(ptrList *l) {
	LinkedElement *tmp = (*l)->sentinel->next;
	while(tmp!=(*l)->sentinel){
		LinkedElement *del = tmp;
		tmp = tmp->next;
		free(del->value);
		free(del);
	}
	free(tmp);
	free(*l);
	*l=NULL;
}

/*-----------------------------------------------------------------*/

List *list_push_front(List *l, void *v) {
	assert(!list_is_empty(l));
	LinkedElement *new_sentinel = malloc(sizeof(LinkedElement));
	new_sentinel->value = v;
	new_sentinel->previous = l->sentinel;
	new_sentinel->next = l->sentinel->next;
	new_sentinel->next->previous = new_sentinel;
	l->sentinel->next = new_sentinel;
	l->size++;
	return l;
}

/*-----------------------------------------------------------------*/

void *list_front(List *l) {
	assert(!list_is_empty(l));
	return l->sentinel->next->value;
}

/*-----------------------------------------------------------------*/

void *list_back(List *l) {
	assert(!list_is_empty(l));
	return l->sentinel->previous->value;
}

/*-----------------------------------------------------------------*/

List *list_pop_front(List *l) {
	assert(!list_is_empty(l));
	LinkedElement *temp;
	temp = l->sentinel->next;
	l->sentinel->next = temp->next;
	l->sentinel->next->previous = l->sentinel;
	l->size--;
	free(temp);
	return l;
}

/*-----------------------------------------------------------------*/

List *list_pop_back(List *l){
	assert(!list_is_empty(l));
	LinkedElement *temp;
	temp = l->sentinel->previous;
	l->sentinel->previous = temp->previous;
	l->sentinel->previous->next = l->sentinel;
	l->size--;
	free(temp);
	return l;
}

/*-----------------------------------------------------------------*/

List *list_insert_at(List *l, int p, void *v) {
	LinkedElement *temp;
	LinkedElement *new_sentinel = malloc(sizeof(LinkedElement));
	temp = l->sentinel->next;
	while(p > 0){
		temp=temp->next;
		p--;
	}
	new_sentinel->value = v;
	new_sentinel->next = temp;
    new_sentinel->previous = temp->previous;
    new_sentinel->previous->next = new_sentinel;
    new_sentinel->next->previous = new_sentinel;
    l->size++;

	return l;
}

/*-----------------------------------------------------------------*/

List *list_remove_at(List *l, int p) {
	(void)p;
	LinkedElement *temp;
	temp = l->sentinel->next;
	while(p > 0){
		temp=temp->next;
		p--;
	}
	temp->previous->next = temp->next;
	temp->next->previous = temp->previous;
	l->size--;
	free(temp);

	return l;
}

/*-----------------------------------------------------------------*/

void *list_at(List *l, int p) {
	(void)l;
	LinkedElement *temp;
	temp = l->sentinel->next;
	while(p > 0){
		temp=temp->next;
		p--;
	}
	return temp->value;
}

/*-----------------------------------------------------------------*/

bool list_is_empty(List *l) {
	(void)l;
	return l->size==0;
}

/*-----------------------------------------------------------------*/

int list_size(List *l) {
	(void)l;
	return l->size;
}

