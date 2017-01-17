package nl.uu.fi.dwo.rest.dom.entities;

import java.util.Collections;
import java.util.Map;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Node of the ResultScoreTree. The tree is crawled to calculate a result matrix
 * for the result viewing.
 * 
 * @author G.A.J. van der Plas  email: G.A.J.vanderPlas@uu.nl
 */
public abstract class DomResultScore<T extends DomResultScore> {

    private double score = 0;
    private double cnt = 0;
    private String label;
    private DomResultScore parent = null;
    private Map<PersistenceId,T> children = Collections.emptyMap();

    /**
     * @return the score
     */
    public Double getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(Double score) {
        this.score = score;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the parent
     */
    public DomResultScore getParent() {
        return parent;
    }

    /**
     * @param aParent
     */
    public void setParent(DomResultScore aParent) {
        if (parent instanceof DomResultTeacher && aParent!=null) {
            throw new RuntimeException("Root node should have null as parent.");
        } else {
            parent = aParent;
        }
    }

    /**
     * @return the children
     */
    public Map<PersistenceId,T>  getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(Map<PersistenceId,T> children) {
        this.children = children;
    }

    /**
     * @return the cnt
     */
    public double getCnt() {
        return cnt;
    }

    /**
     * @param cnt the cnt to set
     */
    public void setCnt(double cnt) {
        this.cnt = cnt;
    }

}
