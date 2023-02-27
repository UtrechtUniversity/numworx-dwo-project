package nl.uu.fi.dwo.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;

/**
 * REST version of the StudentModel.
 * 
 * @author plas0006
 */
@XmlRootElement
public class RestStudentModelContextId {
    
    private DomContext restContext;
    private DomStudentModelContextId domStudentModelContextId;
    private DomSchoolClassId domSchoolClass;

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
    public DomStudentModelContextId getDomStudentModelContext() {
        return domStudentModelContextId;
    }

    /**
     * @param aDomStudentModelContextId the domStudentModelContext to set
     */
    public void setDomStudentModelContext(DomStudentModelContextId aDomStudentModelContextId) {
        this.domStudentModelContextId = aDomStudentModelContextId;
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


}
