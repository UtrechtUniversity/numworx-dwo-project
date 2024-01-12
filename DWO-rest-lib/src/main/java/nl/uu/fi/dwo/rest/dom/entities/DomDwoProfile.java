package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The DWO profile information.
 * @author velth101
 *
 */
@XmlRootElement
public class DomDwoProfile extends DomDwoProfileId implements Cloneable {

    private String dwoProfileName;
    private String dwoProfileRights;
    
    public DomDwoProfile() {}
    
    public DomDwoProfile(DomDwoProfile p) {
    	setId( p.getId().duplicate());
    	setOptLock(p.getOptLock());
    	dwoProfileName = p.dwoProfileName;
    	dwoProfileRights = p.dwoProfileRights;
    }

    public DomDwoProfile duplicate() {
    	return new DomDwoProfile(this);
    }

	public String getDwoProfileName() {
		return dwoProfileName;
	}

	public void setDwoProfileName(String dwoProfileName) {
		this.dwoProfileName = dwoProfileName;
	}

	public String getDwoProfileRights() {
		return dwoProfileRights;
	}

	public void setDwoProfileRights(String dwoProfileRights) {
		this.dwoProfileRights = dwoProfileRights;
	}
    
}
