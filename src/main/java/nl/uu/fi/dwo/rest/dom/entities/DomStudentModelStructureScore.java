package nl.uu.fi.dwo.rest.dom.entities;

import java.beans.Transient;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Tree root for DomStudentModel score tree.
 * 
 * @author plas0006
 */
@XmlRootElement
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
    
    /**
     * Assumes leafs in the tree have correct value and recalculates ancestral node values. Adding 
     * properties.
     */
    @Transient
    public void recalculateAncestors(){
        long count =0L;
        double score = 0.0;
        for (DomStudentModelCategoryScore cat :this.getCategories()){
            for (DomStudentModelObjectiveScore obj :cat.getObjectives()){
                count += obj.getCount();
                score += obj.getScore();
            }
            cat.setCount(count);
            cat.setScore(score);
        }
    }
    
}
