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
    
    /**
     * Opties van het profiel. 
     * <ul>
     * <li>a
     * <li>c - css styling. <i>name</i>.css in apps/css/ folder
     * <li>I - Inf: Met de berichten applicatie 
     * <li>H - Hoger onderwijs
     * <li>l - limited profile, no guest, restricties aan scholen
     * <li>n - navigatie kolom altijd zichtbaar
     * <li>O - met OAuth2 (entree of conext of .... )
	 * <li>o
     * <li>p
     * <li>R - Remedial standaard modules
     * <li>r
     * <li>w - profile is responsive
     * <li>4 - geen open/close alleen <b>modules</> kolom.
     * </ul>
     */
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
