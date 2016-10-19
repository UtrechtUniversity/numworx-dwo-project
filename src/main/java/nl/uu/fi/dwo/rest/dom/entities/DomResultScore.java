package nl.uu.fi.dwo.rest.dom.entities;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author G.A.J. van der Plas <G.A.J.vanderPlas@uu.nl>
 */
public abstract class DomResultScore {

    private Double score = null;
    private String label;
    private DomResultScore parent = null;
    private List<? extends DomResultScore> children = Collections.emptyList();

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
    public List<? extends DomResultScore> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(List<? extends DomResultScore> children) {
        this.children = children;
    }

}
