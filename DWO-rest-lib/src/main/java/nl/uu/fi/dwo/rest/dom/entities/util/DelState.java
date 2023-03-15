package nl.uu.fi.dwo.rest.dom.entities.util;

/**
 * Delete state for entities in the persistent data store.
 * 
 * @author Gert van der Plas
 */
public enum DelState {
    not, //not deleted
    marked, //marked for deletion (time is determined by lastChangeTimeStamp?
    deleted, //deleted
}
