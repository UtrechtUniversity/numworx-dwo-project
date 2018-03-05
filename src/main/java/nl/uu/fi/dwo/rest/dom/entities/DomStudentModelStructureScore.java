package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;
import java.util.logging.Logger;

/**
 * Tree root for DomStudentModel score tree.
 * 
 * @author plas0006
 */
public class DomStudentModelStructureScore {

    private static final Logger LOG = Logger.getLogger(DomStudentModelStructureScore.class.getName());
    
    private List<DomStudentModelCategoryScore> categories;

    
    /**
     * @return the categories
     */
    public List<DomStudentModelCategoryScore> getCategories() {
        return categories;
    }

    /**
     * @param categories the categories to set
     */
    public void setCategories(List<DomStudentModelCategoryScore> categories) {
        this.categories = categories;
    }
}
