package nl.uu.fi.dwo.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;

/**
 * Profile language version of the StudentModel.
 * 
 * @author plas0006
 */
@XmlRootElement

public class RestStudentModelContext {
    
    private DomContext restContext;
    private DomDwoProfile domDwoProfile;
    private DomStudentModelStructure domStudentModelContext;

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
    public DomStudentModelStructure getDomStudentModelContext() {
        return domStudentModelContext;
    }

    /**
     * @param domStudentModelContext the domStudentModelContext to set
     */
    public void setDomStudentModelContext(DomStudentModelStructure domStudentModelContext) {
        this.domStudentModelContext = domStudentModelContext;
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
