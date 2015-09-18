/* Copyrighted 2015. */
package fi.dwo.commons.persistence;

/**
 * SchoolGroup roles. Defines the different roles for member in a school group.
 * In the database it is stored in tblGroup. Order is fixed, cardinality is fixed as
 * it is used in the database.
 *  
 * @author G.A.J. van der Plas
 */
public enum RoleType
{
	NONE,
        STUDENT,
        TEACHER,
        ADMIN,
        NOSCHOOL,
        SCHOOLADMIN//order is fixed, cardinality is fixed.
}
