package nl.uu.fi.dwo.rest.dom.entities;

import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A StudentModelContext node. 
 * 
 * @author plas0006
 */
@XmlRootElement
public class DomStudentModelContext extends DomStudentModelContextId {

    private static final Logger LOG = Logger.getLogger(DomStudentModelContext.class.getName());
    private DomStudentModelStructure modelStructure;

    /**
     * @return the context
     */
    public DomStudentModelStructure getModelStructure() {
        return modelStructure;
    }

    /**
     * @param context the context to set
     */
    public void setModelStructure(DomStudentModelStructure context) {
        this.modelStructure = context;
    }
    
}
