/**
 * Copyrighted Sep 24, 2015
 */
package nl.uu.fi.dwo.rest.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class RestSchoolClassAndProfile {
    private DomContext restContext;
    private DomSchoolClass domSchoolClass;
    private DomDwoProfile domDwoProfile;
    
    public DomDwoProfile getDomDwoProfile() {
		return domDwoProfile;
	}

	public void setDomDwoProfile(DomDwoProfile domDwoProfile) {
		this.domDwoProfile = domDwoProfile;
	}

	public RestSchoolClassAndProfile(){
        
    }

    /**
     * @return the restContext
     */
    public DomContext getRestContext() {
        return restContext;
    }

    /**
     * @param restContext the restContext to set
     */
    public void setRestContext(DomContext restContext) {
        this.restContext = restContext;
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
