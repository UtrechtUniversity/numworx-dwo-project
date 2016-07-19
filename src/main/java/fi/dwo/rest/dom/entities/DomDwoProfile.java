package fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

import fi.dwo.rest.persistence.PersistenceId;

/**
 * The DWO profile information.
 * @author velth101
 *
 */
@XmlRootElement
public class DomDwoProfile implements Cloneable {
    private PersistenceId id;

    private String dwoProfileName;
    private String dwoProfileText;
    private String dwoProfileRights;
    private String dwoProfileDescription;
    
    public DomDwoProfile() {}
    
    public DomDwoProfile(DomDwoProfile p) {
    	id = p.id.duplicate();
    	dwoProfileName = p.dwoProfileName;
    	dwoProfileText = p.dwoProfileText;
    	dwoProfileRights = p.dwoProfileRights;
    	dwoProfileDescription = p.dwoProfileDescription;
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

	public String getDwoProfileText() {
		return dwoProfileText;
	}

	public void setDwoProfileText(String dwoProfileText) {
		this.dwoProfileText = dwoProfileText;
	}

	public String getDwoProfileRights() {
		return dwoProfileRights;
	}

	public void setDwoProfileRights(String dwoProfileRights) {
		this.dwoProfileRights = dwoProfileRights;
	}

	public String getDwoProfileDescription() {
		return dwoProfileDescription;
	}

	public void setDwoProfileDescription(String dwoProfileDescription) {
		this.dwoProfileDescription = dwoProfileDescription;
	}
    
}
