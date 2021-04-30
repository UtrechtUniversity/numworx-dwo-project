package nl.uu.fi.dwo.rest.dom.entities;

import java.beans.Transient;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A StudentModelStructure category score node. 
 * 
 * @author plas0006
 */
@XmlRootElement
public class DomStudentModelCategoryScore extends DomStudentModelScore<DomStudentModelObjectiveScore>{

    /**
     * @return the objectives of the category.
     */
	@Transient
    public List<DomStudentModelObjectiveScore> getObjectives() {
        return getChildren();
    }

    /**
     * @param objectives the objectives to set of the category.
     */
    public void setObjectives(List<DomStudentModelObjectiveScore> objectives) {
        setChildren(objectives);
    }
    
}
