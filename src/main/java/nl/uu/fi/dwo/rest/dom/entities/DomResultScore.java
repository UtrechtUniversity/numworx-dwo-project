package nl.uu.fi.dwo.rest.dom.entities;

import java.util.HashMap;
import java.util.Map;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Node of the ResultScoreTree. The tree is crawled to calculate a result matrix
 * for the result viewing.
 *
 * @author G.A.J. van der Plas email: G.A.J.vanderPlas@uu.nl
 */
public abstract class DomResultScore<T extends DomResultScore> {

    private double score = 0;
    private double cnt = 0;
    private String label;
    private DomResultScore parent = null;
    private Map<PersistenceId, T> children = new HashMap<PersistenceId, T>();

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
        if (parent instanceof DomResultTeacher && aParent != null) {
            throw new RuntimeException("Root node should have null as parent.");
        } else {
            parent = aParent;
        }
    }

    /**
     * @return the children
     */
    public Map<PersistenceId, T> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(Map<PersistenceId, T> children) {
//        if (children.equals(Collections.EMPTY_MAP)) {
//            this.children = new HashMap<PersistenceId, T>();
//        } else {
        this.children = children;
//        }
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

    public int countScoLeaves() {
        if (this instanceof DomResultScoContext) {
            return 1;
        } else if (this.children.isEmpty()) {
            return 0;
        } else {
            int cnt = 0;
            for (DomResultScore s : this.getChildren().values()) {
                cnt = cnt + s.countScoLeaves();
            }
            return cnt;
        }
    }

    public void fetchCourseLeaves(Map<PersistenceId, DomResultCourse> courseLeaves) {
        Object[] kids = this.getChildren().values().toArray();

        if (this.children.isEmpty()) {
            return;
        } else if (kids[0] instanceof DomResultScoContext) {
            courseLeaves.put(((DomResultCourse) this).getCourse().getId(), (DomResultCourse) this);
            return;
        } else {
            for (DomResultScore s : this.getChildren().values()) {
                s.fetchCourseLeaves(courseLeaves);
            }
        }
    }

    public void crawlSchoolClassCourse(DomResultSchoolClass schoolClass, Map<PersistenceId, DomResultCourse> courseLeaves, Map<PersistenceId, Map<PersistenceId, DomResultCourse>> sparseMatrix) {
        Object[] kids = this.getChildren().values().toArray();
        if (this instanceof DomResultSchoolClass) {
            schoolClass = (DomResultSchoolClass) this;
            for (DomResultScore s : this.getChildren().values()) {
                s.crawlSchoolClassCourse(schoolClass, courseLeaves, sparseMatrix);
            }
            return;
        }
        if (this.children.isEmpty()) {
            return;
        }
        if (kids[0] instanceof DomResultScoContext) {
            //add course to horizontal header
            courseLeaves.put(((DomResultCourse) this).getCourse().getId(), (DomResultCourse) this);
            //add course score to sparse matrix
            this.score = this.getAvgSubTreeScore(this);
            sparseMatrix.get(schoolClass.getSchoolClass().getId()).put(((DomResultCourse) this).getCourse().getId(), (DomResultCourse) this);
            return;
        } else {
            for (DomResultScore s : this.getChildren().values()) {
                s.crawlSchoolClassCourse(schoolClass, courseLeaves, sparseMatrix);
            }
        }
    }

    /**
     * Calculates the average score of a subtree. Each node in the subtree has a
     * score equal to the average score of its children with weight 1.
     *
     * @param sub
     * @return
     */
    private double getAvgSubTreeScore(DomResultScore<T> sub) {
        double result = 0.0;
        if (this instanceof DomResultStudentSco) {
            DomResultStudentSco ss = (DomResultStudentSco) this;
            result = ss.getStudentSco().getScore(); // normalized 0-100. 
        } else {
            for (DomResultScore s : sub.getChildren().values()) {
                result += this.getAvgSubTreeScore(s);
            }
        }
        return result / this.getChildren().size();
    }

}
