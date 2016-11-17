package nl.uu.fi.dwo.rest.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;

/**
 * RestDwoProfile contains a minimal DwoProfile info.
 * 
 * @author G.A.J. van der Plas <G.A.J.vanderPlas@uu.nl>
 */
public class RestDwoProfile {
	private DomContext restContext;
	private DomDwoProfile domDwoProfile;

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
     * @return the domDwoProfile
     */
    public DomDwoProfile getDomDwoProfile() {
        return domDwoProfile;
    }

    /**
     * @param domDwoProfile the domDwoProfile to set
     */
    public void setDomDwoProfile(DomDwoProfile domDwoProfile) {
        this.domDwoProfile = domDwoProfile;
    }
    
}
