package nl.uu.fi.dwo.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextPatch;

/**
 * Profile language version of the StudentModel.
 * 
 * @author plas0006
 */
@XmlRootElement

public class RestStudentModelContextPatch {
    
    private DomContext restContext;
    private DomStudentModelContextPatch domPatch;
    private DomDwoProfileId domDwoProfile;

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
	 * @return the domPatch
	 */
	public DomStudentModelContextPatch getDomPatch() {
		return domPatch;
	}

	/**
	 * @param domPatch the domPatch to set
	 */
	public void setDomPatch(DomStudentModelContextPatch domPatch) {
		this.domPatch = domPatch;
	}

	public DomDwoProfileId getDomDwoProfile() {
		return domDwoProfile;
	}

	public void setDomDwoProfile(DomDwoProfileId domDwoProfile) {
		this.domDwoProfile = domDwoProfile;
	}

}
