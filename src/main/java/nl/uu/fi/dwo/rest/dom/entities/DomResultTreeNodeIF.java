package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;

/**
 *
 * @author G.A.J. van der Plas <G.A.J.vanderPlas@uu.nl>
 */
public interface DomResultTreeNodeIF {

    /**
     * @return the node label
     */
    public String getLabel();
    
    /**
     * @return the children
     */
    public List<DomResultTreeNodeIF> getChildren();

//    /**
//     * @param children the children to set
//     */
//    public void setChildren(List<DomResultTreeNode> children);

    /**
     * @return the parent
     */
    public DomResultTreeNodeIF getParent();

//    /**
//     * @param parent the parent to set
//     */
//    public void setParent(DomResultTreeNode parent);
    
    /**
     * Returns the cumulative score of the sub branches.
     * @return 
     */
    public Double getScore();
    
    /**
     * Recalculates the score for the subtree.
     * 
     * @return 
     */
    public void reCalculateScore();
}
