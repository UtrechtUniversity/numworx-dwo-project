/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.rest;

/**
 * All the class types that are use in the rest interface and are put in the Store. Note the 
 * ClassType name has to match the Class name exactly. 
 * 
 * @author G.A.J. van der Plas
 */

public enum RestClassType {
    //Persistent classes
        PersistentApplet, 
        PersistentAppletConfig, 
        PersistentClassCourse,
        PersistentCourse,
        PersistentCourseSequence,
        PersistentDwoProfile,
        PersistentDwoSystemParameters,
        PersistentHasRole,
        PersistentImage,
        PersistentJars,
        PersistentRole,
        PersistentSamlUser,
        PersistentSchool,
        PersistentSchoolClass,
        PersistentSchoolGroup,
        PersistentScoContext, 
        PersistentScoData,
        PersistentStudentOfClass,
        PersistentStudentScoContext,
        PersistentStudentScoData,
        PersistentTeacherOfClass,
        PersistentUser,
        //Non-peristent classes
        MySQLPersistenceId,
        SchoolRoleAndClass,
        SchoolsRolesAndClasses
    }
