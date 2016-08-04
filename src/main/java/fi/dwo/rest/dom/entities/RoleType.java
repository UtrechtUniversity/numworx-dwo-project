/* Copyrighted 2015. */
package fi.dwo.rest.dom.entities;

/**
 * SchoolGroup roles. Defines the different roles for member in a school group.
 * In the database it is stored in tblGroup. Order is fixed, cardinality is fixed as
 * it is used in the database.
 *  
 * @author G.A.J. van der Plas
 */
public enum RoleType
{
	NONE, //init value
        STUDENT, 
        TEACHER,
        ADMIN,
        ANONYMOUS, //for further use.
        SCHOOLADMIN//order is fixed, cardinality is fixed.
}
