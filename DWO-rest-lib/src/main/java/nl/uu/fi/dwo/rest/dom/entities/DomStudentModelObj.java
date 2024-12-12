package nl.uu.fi.dwo.rest.dom.entities;

import java.beans.Transient;
import java.util.List;
import java.util.Objects;

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
          if (info == null) {
        	  result.setScore(0.5);
          } else {
	          Double init = info.getInit(); 
	          if (init == null) init = 0.5;
	          result.setScore(init.doubleValue());
          }
        }
        if(info != null) result.setId(info.getId());
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

	@Override
	public int hashCode() {
		return Objects.hash(info, objectives);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DomStudentModelObj)) {
			return false;
		}
		DomStudentModelObj other = (DomStudentModelObj) obj;
		return Objects.equals(info, other.info) && Objects.equals(objectives, other.objectives);
	}
}
