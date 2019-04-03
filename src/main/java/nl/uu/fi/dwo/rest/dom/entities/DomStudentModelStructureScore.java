package nl.uu.fi.dwo.rest.dom.entities;

import java.beans.Transient;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

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
        setCount(0L);
        setScore(0L);
        for (DomStudentModelCategoryScore cat :this.getCategories()){
            long count =0L;
            double score = 0.0;
            for (DomStudentModelObjectiveScore obj :cat.getObjectives()){
                count += obj.getCount();
                score += obj.getScore();
            }
            cat.setCount(count); setCount(count + getCount());
            cat.setScore(score); setScore(count + getScore());
        }
    }
    
}
