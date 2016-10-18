package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;

/**
 *
 * @author G.A.J. van der Plas <G.A.J.vanderPlas@uu.nl>
 */
public abstract class DomResultTreeNode implements DomResultTreeNodeIF {

    private Double score = null;

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

    public void reCalculateScore() {
        List<DomResultTreeNodeIF> children = getChildren();
        if (children.size() > 0) {
            //score is sum of children's score.
            for (DomResultTreeNodeIF node : getChildren()) {
                node.reCalculateScore();
                score += node.getScore();
            }
        }
        //else score is score of StudentSco's and should be already set.
    }

}
