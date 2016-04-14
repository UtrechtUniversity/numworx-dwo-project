/* Copyrighted 2015. */
package fi.dwo.gwt.lib.rest.shared.entities;

/**
 * All the class types that are persistent and are put in the Store. Note the
 * ClassType name is match the Class name exact. The ordering of the class names
 * is to be alphabetically!
 *
 * @author G.A.J. van der Plas
 */
public enum PersistenceClassType {

    none,
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
}
