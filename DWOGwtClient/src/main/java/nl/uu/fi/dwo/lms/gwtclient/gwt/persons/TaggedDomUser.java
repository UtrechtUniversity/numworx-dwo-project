package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;

/**
 *
 * @author Gert van der Plas
 */
public class TaggedDomUser<T extends DomUser> {
    
    private boolean tag;
    private T user;
    private List<String> memberOf;
    private String extra;

    public TaggedDomUser() {
    }

    public TaggedDomUser(T aUser) {
        user = aUser;
        memberOf = Collections.emptyList();
        extra = "";
    }

    public TaggedDomUser(T aUser, List<String> memberOf) {
      user = aUser;
      this.memberOf = memberOf;
      extra = "";
  }

    public TaggedDomUser(T student, List<String> arrayList, String extra2) {
		this(student, arrayList);
		setExtra(extra2);
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
    public T getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(T user) {
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

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}
    
}
