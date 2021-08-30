package nl.uu.fi.dwo.rest.dom.entities.util;

/**
 * Generic publish states for data. Edit means being changed by an editor, review
 * is currently not being edited. Published is available for consumers and therefor
 * no longer editable. While this feature is unused the default value is
 * enum ordinal 2, 'published'.
 * 
 * @author Gert van der Plas
 */
public enum PublishState {
    edit, //Someone is editing the data, userid should be added
    review, // Data may be reviewed and tested by editors. State may change to edit.
    published,//Users may use the data. 
    archived, //Data is available for viewing by editors for example but not visible for consumers.
    overt // public to all premium schools, implies readonly
}
