package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import java.util.List;

import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;

/**
 *
 * @author Gert van der Plas
 */
public class TaggedDomUser {
    
    private boolean tag;
    private DomUser user;
    private List<String> memberOf;

    public TaggedDomUser() {
    }

    public TaggedDomUser(DomUser aUser) {
        user = aUser;
    }

    public TaggedDomUser(DomUser aUser, List<String> memberOf) {
      user = aUser;
      this.memberOf = memberOf;
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
    public DomUser getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(DomUserFull user) {
        this.user = user;
    }

    /**
     * @return the memberOf
     */
    public List<String> getMemberOf() {
      return memberOf;
    }

    /**
     * @param memberOf the memberOf to set
     */
    public void setMemberOf(List<String> memberOf) {
      this.memberOf = memberOf;
    }
    
}
