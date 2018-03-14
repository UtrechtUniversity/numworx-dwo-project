package nl.uu.fi.dwo.rest.dom.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A StudentModelStructure score root node. 
 * 
 * @author plas0006
 * @param <T>
 */
@XmlRootElement
public class DomStudentModelScore<T extends DomStudentModelScore> {

    private static final Logger LOG = Logger.getLogger(DomStudentModelScore.class.getName());

    private List<T> children = new ArrayList<T>();
    
    private double score=0.0;
    private long count=0;

    /**
     * @return the score
     */
    public double getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(double score) {
        this.score = score;
    }


    /**
     * @return the count
     */
    public long getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(long count) {
        this.count = count;
    }
        
    /**
     * @return the children
     */
    protected List<T> getChildren() {
        return children;
    }

    /**
     * @param children
     */
    protected void setChildren(List<T> children) {
        this.children = children;
    }

}
