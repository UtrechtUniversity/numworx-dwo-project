/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.rest;

/**
 * Class types that may be returned from a REST-call. Note the 
 * ClassType name has to match the Class name exactly. 
 * 
 * @author G.A.J. van der Plas
 */

public enum RestListClassTypes {
    //Dom classes
        DomApplet, 
        DomAppletConfig, 
        DomClassCourse,
        DomCourse,
        DomCourseSequence,
        DomDwoProfile,
        DomDwoSystemParameters,
        DomHasRole,
        DomImage,
        DomJars,
        DomRole,
        DomSamlUser,
        DomSchool,
        DomSchoolClass,
        DomSchoolGroup,
        DomScoContext, 
        DomScoData,
        DomStudentOfClass,
        DomStudentScoContext,
        DomStudentScoData,
        DomTeacherOfClass,
        DomUser,
        DomSchoolRoleAndClass,
        DomSchoolsRolesAndClasses,
        //Non-persistent classes
        PersistenceId
    }
