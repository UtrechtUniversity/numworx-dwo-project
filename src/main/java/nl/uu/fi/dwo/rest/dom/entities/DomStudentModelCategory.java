package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A StudentModelContext node.
 * 
 * @author plas0006
 */
@XmlRootElement
public class DomStudentModelCategory {
    private DomStudentModelContextInfo info;
    private List<DomStudentModelObj> objectives;

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
    
    /**
     * @return the objectives
     */
    public List<DomStudentModelObj> getObjectives() {
        return objectives;
    }

    /**
     * @param objectives the objectives to set
     */
    public void setObjectives(List<DomStudentModelObj> objectives) {
        this.objectives = objectives;
    }
}
