package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;
import java.util.logging.Logger;

/**
 * Tree root for DomStudentModel score tree.
 * 
 * @author plas0006
 */
public class DomStudentModelStructureScore extends DomStudentModelScore<DomStudentModelCategoryScore>{

    private static final Logger LOG = Logger.getLogger(DomStudentModelStructureScore.class.getName());
    

    
    /**
     * @return the categories
     */
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
