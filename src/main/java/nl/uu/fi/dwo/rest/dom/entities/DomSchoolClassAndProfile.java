/**
 * Copyrighted Sep 24, 2015
 */
package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class DomSchoolClassAndProfile {
    private DomSchoolClass domSchoolClass;
    private DomDwoProfile domDwoProfile;
    
    public DomDwoProfile getDomDwoProfile() {
		return domDwoProfile;
	}

	public void setDomDwoProfile(DomDwoProfile domDwoProfile) {
		this.domDwoProfile = domDwoProfile;
	}

	public DomSchoolClassAndProfile(){
        
    }

    /**
     * @return the domSchoolClass
     */
    public DomSchoolClass getDomSchoolClass() {
        return domSchoolClass;
    }

    /**
     * @param domSchoolClass the domSchoolClass to set
     */
    public void setDomSchoolClass(DomSchoolClass domSchoolClass) {
        this.domSchoolClass = domSchoolClass;
    }

    
}
