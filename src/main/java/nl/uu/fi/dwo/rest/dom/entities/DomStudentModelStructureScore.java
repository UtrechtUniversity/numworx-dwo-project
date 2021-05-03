package nl.uu.fi.dwo.rest.dom.entities;

import java.beans.Transient;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Tree root for DomStudentModel score tree.
 * 
 * @author plas0006
 */
@XmlRootElement
public class DomStudentModelStructureScore extends DomStudentModelScore<DomStudentModelCategoryScore>{

    /**
     * @return the categories
     */
	@Transient
	@XmlTransient
    public List<DomStudentModelCategoryScore> getCategories() {
        return getChildren();
    }

    /**
     * @param categories the categories to set
     */
    public void setCategories(List<DomStudentModelCategoryScore> categories) {
        this.setChildren(categories);
    }
        
}
