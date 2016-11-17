package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * The DWO profile information.
 * @author velth101
 *
 */
@XmlRootElement
public class DomDwoProfileFull extends DomDwoProfile implements Cloneable {
    private String dwoProfileText;
    private String dwoProfileDescription;
    
    public DomDwoProfileFull() {}
    
    public DomDwoProfileFull(DomDwoProfileFull p) {
        super(p);
    	dwoProfileText = p.dwoProfileText;
    	dwoProfileDescription = p.dwoProfileDescription;
    }

    public DomDwoProfileFull duplicate() {
    	return new DomDwoProfileFull(this);
    }

	public String getDwoProfileText() {
		return dwoProfileText;
	}

	public void setDwoProfileText(String dwoProfileText) {
		this.dwoProfileText = dwoProfileText;
	}

	public String getDwoProfileDescription() {
		return dwoProfileDescription;
	}

	public void setDwoProfileDescription(String dwoProfileDescription) {
		this.dwoProfileDescription = dwoProfileDescription;
	}
    
}
