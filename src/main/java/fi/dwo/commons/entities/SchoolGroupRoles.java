/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.entities;

/**
 * SchoolGroup roles. Defines the different roles for member in a school group.
 * In the database it is stored in tblGroup.
 * 
 * @author plas0006
 */
public class SchoolGroupRoles {
	public static final int STUDENT = 1;
	public static final int TEACHER = 2;
	public static final int ADMIN = 3;
	public static final int DIGICODE = 4;
	public static final int SCHOOLADMIN = 5;
	public static final int LENGTH = 6;     
}
