package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;

/**
 *
 * @author Gert van der Plas
 */
public class TaggedDomSchoolClass {
    
    private boolean tag;
    private DomSchoolClass schoolClass;

    public TaggedDomSchoolClass() {
    }

    public TaggedDomSchoolClass(DomSchoolClass aSchoolClass) {
        schoolClass = aSchoolClass;
    }

    /**
     * @return the tag
     */
    public boolean isTag() {
        return tag;
    }

    /**
     * @param tag the tag to set
     */
    public void setTag(boolean tag) {
        this.tag = tag;
    }

    /**
     * @return the schoolClass
     */
    public DomSchoolClass getSchoolClass() {
        return schoolClass;
    }

    /**
     * @param schoolClass the schoolClass to set
     */
    public void setSchoolClass(DomSchoolClass schoolClass) {
        this.schoolClass = schoolClass;
    }
    
}
