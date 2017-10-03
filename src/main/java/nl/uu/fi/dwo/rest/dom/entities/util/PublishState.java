package nl.uu.fi.dwo.rest.dom.entities.util;

/**
 *
 * @author Gert van der Plas
 */
public enum PublishState {
    edit, //Someone is editing the launchdata, userid should be added
    review, // Teachers may review and test the launchdata
    published //Students may use the launchdata and it is fixed.
}
