/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.rest;

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
        DomCourseStudent,
        DomDwoProfile,
        DomDwoSystemParameters,
        DomHasRole,
        DomImage,
        DomJars,
        DomRole,
        DomResultsPerTeacher,        
        DomSamlUser,
        DomSchool,
        DomSchool4DwoAdmin,
        DomSchoolAdmin,
        DomSchoolClass,
        DomSchoolGroup,
        DomScoContext, 
        DomScoData,
        DomStudent,
        DomStudentOfClass,
        DomStudentScoContext,
        DomStudentScoData,
        DomTeacher,
        DomTeacherAndHasRole,
        DomTeacherOfClass,
        DomUser,
        DomSchoolRoleAndClass,
        DomSchoolRoleAndClassV2,
        DomSchoolsRolesAndClasses,
        DomSchoolsRolesAndClassesV2,
        //Non-persistent classes
        PersistenceId
    }
