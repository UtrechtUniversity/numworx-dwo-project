package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * The DWO profile information.
 * @author velth101
 *
 */
@XmlRootElement
public class DomDwoProfile implements Cloneable {
    private PersistenceId id;

    private String dwoProfileName;
    private String dwoProfileRights;
    
    public DomDwoProfile() {}
    
    public DomDwoProfile(DomDwoProfile p) {
    	id = p.id.duplicate();
    	dwoProfileName = p.dwoProfileName;
    	dwoProfileRights = p.dwoProfileRights;
    }

    public DomDwoProfile duplicate() {
    	return new DomDwoProfile(this);
    }

	public PersistenceId getId() {
		return id;
	}

	public void setId(PersistenceId id) {
		this.id = id;
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
