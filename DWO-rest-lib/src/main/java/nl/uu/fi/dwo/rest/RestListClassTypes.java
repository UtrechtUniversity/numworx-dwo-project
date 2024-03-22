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
        DomCourseStudent,
        DomDwoProfile,
        DomDwoSystemParameters,
        DomHasRole,
        DomImage,
        DomJars,
        DomMethod,
        DomRole,
        DomResultsPerTeacher,        
        DomSamlUser,
        DomSchool,
        DomSchoolFrom,
        DomSchool4DwoAdmin,
        DomSchoolAdmin,
        DomSchoolAdminAndHasRole,
        DomSchoolClass,
        DomSchoolGroup,
        DomScoContext, 
        DomScoData,
        DomStudent,
        DomStudentModelContext,
        DomStudentModelData,
        DomStudentOfClass,
        DomStudentScoContext,
        DomStudentScoData,
        DomTeacher,
        DomTeacherAndHasRole,
        DomTeacherOfClass,
        DomUser,
        DomUserFull,
        DomSchoolRoleAndClass,
        DomSchoolRoleAndClassV2,
        DomSchoolsRolesAndClasses,
        DomSchoolsRolesAndClassesV2,
        DomUserFullwLoginContext,
        DomLoginContext,
        //Non-persistent classes
        PersistenceId, 
    }
