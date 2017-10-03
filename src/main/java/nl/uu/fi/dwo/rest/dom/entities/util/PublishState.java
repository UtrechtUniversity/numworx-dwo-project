package nl.uu.fi.dwo.rest.dom.entities.util;

/**
 * Publish state for Sco launchData. Edit means being changed by a teacher, review
 * is currently not being edited. Published is available for students and therefor
 * no longer editable for a teacher. While this feature is unused the default value is
 * enum ordinal 2, 'published'.
 * 
 * @author Gert van der Plas
 */
public enum PublishState {
    edit, //Someone is editing the launchdata, userid should be added
    review, // Teachers may review and test the launchdata
    published //Students may use the launchdata and it is fixed.
}
