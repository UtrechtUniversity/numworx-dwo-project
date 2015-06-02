/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.persistence;

/**
 * SchoolGroup roles. Defines the different roles for member in a school group.
 * In the database it is stored in tblGroup.
 *  
 * @author plas0006
 */
public enum RoleType
{
	NONE,
        STUDENT,
        TEACHER,
        NOSCHOOL,
        SCHOOLADMIN, //order is fixed, cardinality is fixed.
}
