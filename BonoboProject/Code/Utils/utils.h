#ifndef __UTILS__H__
#define __UTILS__H__

/** \defgroup some functions to help coding.
 * @{
 */

/** Function : returns date and hour of the system.
 */
char *getTimeStamp();

/** Function : convert int n to characters in char *s.
 * @param n integer to convert
 * @param s pointer of the string result of the conversion of the integer
 */
void itoa(int n, char *s);

/** Function : convert long int n to characters in char *s.
 * @param n long integer to convert
 * @param s pointer of the string result of the conversion of the long integer
 */
void ltoa(long int n, char *s);

/** @} */
#endif