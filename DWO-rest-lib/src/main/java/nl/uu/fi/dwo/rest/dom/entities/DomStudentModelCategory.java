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

    @Transient
    DomStudentModelCategoryScore buildDomStudentModelCategoryScore() {
        DomStudentModelCategoryScore result = new DomStudentModelCategoryScore();
        for(DomStudentModelObj obj : objectives) {
            result.getObjectives().add(obj.buildDomStudentModelObjectiveScore());
        }
       if(info != null) result.setId(info.getId());
       return result;
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
		if (!(obj instanceof DomStudentModelCategory)) {
			return false;
		}
		DomStudentModelCategory other = (DomStudentModelCategory) obj;
		return Objects.equals(info, other.info) && Objects.equals(objectives, other.objectives);
	}
}
