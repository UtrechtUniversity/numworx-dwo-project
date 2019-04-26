package nl.uu.fi.dwo.rest.dom.entities;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A StudentModelContext node.
 * 
 * @author plas0006
 */
@XmlRootElement
public class DomStudentModelObj {
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

    @Transient
    DomStudentModelObjectiveScore buildDomStudentModelObjectiveScore() {
        DomStudentModelObjectiveScore result = new DomStudentModelObjectiveScore();
        if (objectives != null) {
          List<DomStudentModelObjectiveScore> children = result.getChildren();
          for(DomStudentModelObj obj : objectives) {
            children.add(obj.buildDomStudentModelObjectiveScore());
          } 
        } else {
          result.setChildren(null);
        }
        return result;
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
