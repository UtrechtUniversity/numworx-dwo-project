package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A StudentModelContext node.
 * 
 * @author plas0006
 */
@XmlRootElement
public class DomStudentModelObj {
    private DomStudentModelContextInfo info;

    /**
     * @return the info
     */
    public DomStudentModelContextInfo getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(DomStudentModelContextInfo info) {
        this.info = info;
    }
    
}
