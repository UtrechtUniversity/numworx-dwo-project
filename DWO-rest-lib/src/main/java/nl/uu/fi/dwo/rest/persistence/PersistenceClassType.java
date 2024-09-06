/* Copyrighted 2015. */
package nl.uu.fi.dwo.rest.persistence;

import java.io.Serializable;

/**
 * All the class types that are persistent and are put in the Store. Note the
 * ClassType name is match the Class name exact. The ordering of the class names
 * is to be alphabetically!
 *
 * @author G.A.J. van der Plas
 */
public enum PersistenceClassType implements Serializable {

    none,
    PersistentApplet,
    PersistentAppletConfig,
    PersistentAppletConfigData,
    PersistentClassCourse,
    @Deprecated
    PersistentCourseInClass,
    PersistentCourse,
    @Deprecated
    PersistentCourseSequence,
    PersistentDwoProfile,
    PersistentDwoSystemParameters,
    PersistentHasRole,
    PersistentImage,
    @Deprecated
    PersistentJars,
    PersistentLoginContext,
    PersistentRole,
    PersistentSamlUser,
    PersistentSchool,
    PersistentSchoolClass,
    PersistentSchoolData,
    PersistentSchoolGroup,
    PersistentScoContext,
    PersistentScoData,
    PersistentStudentModelContext,
    PersistentStudentModelData,
    PersistentStudentOfClass,
    PersistentStudentScoContext,
    PersistentStudentScoData,
    PersistentTeacherOfClass,
    PersistentUrnResource,
    PersistentUser, 

    PersistentACL,
    PersistentMethod,
    PersistentScoPage,

}
