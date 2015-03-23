/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.util;

/**
 * Enumerates the classes which can be persistent and have a PeristentID.
 * @author plas0006
 */

    public enum PersistentClassType {
        none,
        School,
        SchoolGroup,
        Group,
        SchoolClass,
        TeacherOf,
        StudentOf,
        User,
        SamlUser,
        Sco,
        ScoContext,
        ScoData,
        StudentSco, //aggegrate ScoContext en ScoData
        StudentScoContext, 
        StudentScoData
    }
