/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.persistence;

/**
 * All the class types that are persistent and are put in the Store. Note the 
 * ClassType name is match the Class name exact. The ordering of the class names
 * is to be alphabetically!
 * 
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */

public enum PersistenceClassType {
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
        PersistentUser
    }
