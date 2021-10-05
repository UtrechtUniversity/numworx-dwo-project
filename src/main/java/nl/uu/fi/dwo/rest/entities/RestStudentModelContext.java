package nl.uu.fi.dwo.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;

/**
 * Profile language version of the StudentModel.
 * 
 * @author plas0006
 */
@XmlRootElement
public class RestStudentModelContext {
    
    private DomContext restContext;
    private DomStudentModelContext domStudentModelContext;
    private DomSchoolClassId domSchoolClass;
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
     * @return the dpmStudentModelContext
     */
    public DomStudentModelContext getDomStudentModelContext() {
        return domStudentModelContext;
    }

    /**
     * @param domStudentModelContext the domStudentModelContext to set
     */
    public void setDomStudentModelContext(DomStudentModelContext domStudentModelContext) {
        this.domStudentModelContext = domStudentModelContext;
    }

	/**
	 * @return the domSchoolClass
	 */
	public DomSchoolClassId getDomSchoolClass() {
		return domSchoolClass;
	}

	/**
	 * @param domSchoolClass the domSchoolClass to set
	 */
	public void setDomSchoolClass(DomSchoolClassId domSchoolClass) {
		this.domSchoolClass = domSchoolClass;
	}

	/**
	 * @return the domDwoProfile
	 */
	public DomDwoProfileId getDomDwoProfile() {
		return domDwoProfile;
	}

	/**
	 * @param domDwoProfile the domDwoProfile to set
	 */
	public void setDomDwoProfile(DomDwoProfileId domDwoProfile) {
		this.domDwoProfile = domDwoProfile;
	}


}
