/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.persistence;

/**
 * Defines the classes which may have a {@Link PersistentId PersistentId}.
 * 
 * @author plas0006
 */
public enum PersistentIdClassTypes {
    none,
    persistentUser,
    persistentTeacher,
    persistentStudent,
    persistentSchool,
    persistentCourse
}
