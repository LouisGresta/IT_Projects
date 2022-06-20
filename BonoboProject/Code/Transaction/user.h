#ifndef __USER__H__
#define __USER__H__

/** \defgroup ADTUser User
 * Documentation of the implementation of the abstract data type User.
 * @{
 */
/** \defgroup UserType Type definition.
 * @{
 */
/** Opaque definition of the type user */
typedef struct s_User User;
/** @} */

/** Constructor : builds a user with his money and his name
 * @param name the name of the user
 * @param value the money to eventually give to the user at the creation
 */
User *user_create(char name[BUFSIZ], long int value);

/** Operator : returns the money of the user.
 * @param u the user
 */
long int user_money(User *u);

/** Operator : returns the name of the user.
 * @param u the user
 */
char *user_name(User *u);

/** Destructor : Delete the User.
 */
void user_delete(User *u);
/** @} */

/** \defgroup other function.
 * @{
 */
/** Function : returns a string that represent the transaction between two users.
 * @param from the user who give money
 * @param to the user who take money
 * @param value the value who exchange these two users
 */
char *user_transaction(User *from, User *to, long int value);
/** @} */
/** @} */
#endif