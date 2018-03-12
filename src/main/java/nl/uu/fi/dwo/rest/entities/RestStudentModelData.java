package nl.uu.fi.dwo.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelData;

/**
 * Score information of the StudentModel. (Overlay)
 * 
 * @author plas0006
 */
@XmlRootElement

public class RestStudentModelData {
    
    private DomContext restContext;
    private DomStudentModelData domStudentModelData;

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
    public DomStudentModelData getDomStudentModelContext() {
        return domStudentModelData;
    }

    /**
     * @param domStudentModelData
     */
    public void setDomStudentModelContext(DomStudentModelData domStudentModelData) {
        this.domStudentModelData = domStudentModelData;
    }


}
