package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;

/**
 *
 * @author Gert van der Plas
 */
public class TaggedDomUserFull {
    
    private boolean tag;
    private DomUserFull user;

    public TaggedDomUserFull() {
    }

    public TaggedDomUserFull(DomUserFull aUser) {
        user = aUser;
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
     * @return the user
     */
    public DomUserFull getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(DomUserFull user) {
        this.user = user;
    }
    
}
