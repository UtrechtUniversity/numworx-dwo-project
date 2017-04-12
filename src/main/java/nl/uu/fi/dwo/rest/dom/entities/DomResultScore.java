package nl.uu.fi.dwo.rest.dom.entities;

import java.util.HashMap;
import java.util.Map;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Node of the ResultScoreTree. The tree is crawled to calculate a result matrix
 * for the result viewing.
 *
 * @author G.A.J. van der Plas email: G.A.J.vanderPlas@uu.nl
 * @param <T>
 */
public abstract class DomResultScore<T extends DomResultScore> {

    private double score = 0; //unmade work is always score 0.
    private double cnt = 0;   //count is 0 because cnt may summarize empty subtree.
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

    /**
     * Fills the map courseLeaves with all Courses that are leaves in the course
     * tree.
     *
     * @param course the node that is the root of the subtree searched.
     * @param courseLeaves A map of leaves in the course tree.
     */
    public void collectCourseLeaves(DomResultCourse course, Map<PersistenceId, DomResultCourse> courseLeaves) {
        if (this.children.isEmpty()) {
            return;
        }
        if (!course.getCourse().getWithChildren()) {
            courseLeaves.put(((DomResultCourse) this).getCourse().getId(), (DomResultCourse) this);
        } else {
            for (DomResultScore s : this.getChildren().values()) {
                s.collectCourseLeaves((DomResultCourse) s, courseLeaves);
            }
        }
    }

    /**
     * Fills the map courseLeaves with all Courses that are leaves in the course
     * tree.
     *
     * @param schoolClass
     * @param courseLeaves A map of leaves in the course tree.
     */
    public void collectCourseLeaves(DomResultSchoolClass schoolClass, Map<PersistenceId, DomResultCourse> courseLeaves) {
        if (this.children.isEmpty()) {
            return;
        }
        for (DomResultScore s : this.getChildren().values()) {
            s.collectCourseLeaves((DomResultCourse) s, courseLeaves);
        }
    }

    /**
     * Fills the map courseLeaves with all Courses that are leaves in the course
     * tree.
     *
     * @param schoolClass
     * @param courseLeaves A map of leaves in the course tree.
     */
    public void collectCourseLeaves(DomResultTeacher teacher, Map<PersistenceId, DomResultCourse> courseLeaves) {
        if (this.children.isEmpty()) {
            return;
        }
        for (DomResultScore s : this.getChildren().values()) {
            s.collectCourseLeaves((DomResultCourse) s, courseLeaves);
        }
    }
//    
//    public static DomResultCourse getScore(DomResultSchoolClass schoolClass, DomResultCourse course) {
//        course.getAverageSubtreeScore(course,course);
//        return new DomResultCourse(course.getCourse()
//        );
//    }

    /**
     * Calculates the sum DomResultStudentSco scores and the number of subscores
     * in a subtree in given node. Each node in the subtree has a score equal to
     * the sum of DomResultStudentSco scores which has a weight of 1 for the
     * count.
     *
     * @param parent
     * @param node
     */
    private void getAverageSubtreeScore(DomResultScore<T> parent, DomResultScore<T> node) {
        if (parent == node) {
            parent.setScore(0.0);
        }
        if (node instanceof DomResultStudentSco) {
            node.setCnt(1);
            if (node.getScore() != null) {
                DomResultStudentSco ss = (DomResultStudentSco) node;
                node.setScore(ss.getStudentSco().getScore());
                parent.setScore(parent.getScore() + node.getScore());
            }// do nothing.
            parent.setCnt(parent.getCnt() + node.getCnt());
        } else {
            for (DomResultScore s : node.getChildren().values()) {
                s.getAverageSubtreeScore(node, s);
                parent.setScore(parent.getScore() + s.getScore());
                parent.setCnt(parent.getCnt() + s.getCnt());
            }
        }
    }

    /**
     *
     *
     *
     * @param schoolClass
     * @param courseLeaves
     * @param sparseMatrix
     */
    public void collectScoresPerCourseOverSchoolClass(DomResultSchoolClass schoolClass, Map<PersistenceId, DomResultCourse> courseLeaves, Map<PersistenceId, Map<PersistenceId, DomResultCourse>> sparseMatrix) {
        Object[] kids = this.getChildren().values().toArray();
        if (this instanceof DomResultSchoolClass) {
            schoolClass = (DomResultSchoolClass) this;
            for (DomResultScore s : this.getChildren().values()) {
                s.collectScoresPerCourseOverSchoolClass(schoolClass, courseLeaves, sparseMatrix);
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
            this.getAverageSubtreeScore(this, this);
            sparseMatrix.get(schoolClass.getSchoolClass().getId()).put(((DomResultCourse) this).getCourse().getId(), (DomResultCourse) this);
            return;
        } else {
            for (DomResultScore s : this.getChildren().values()) {
                s.collectScoresPerCourseOverSchoolClass(schoolClass, courseLeaves, sparseMatrix);
            }
        }
    }
//
//    /**
//     *
//     *
//     * @param course
//     * @param courseLeaves
//     * @param sparseMatrix, double HashMap with Persistent course, studentId
//     * containing DomResultStudentScovalues.
//     */
//    public void collectScoresPerStudentOverCourseInSchoolclass(DomResultCourse course, Map<PersistenceId, DomResultCourse> courseLeaves, Map<Persisten ceId, Map<PersistenceId, DomResultStudent>> 
//        sparseMatrix) {
//        //case course has children, (not being sco) recurse.
//        if (this instanceof DomResultCourse && course.getCourse().getWithChildren()) {
//            //children are of classType
//            for (DomResultScore s : this.getChildren().values()) {
//                s.collectScoresPerStudentOverCourseInSchoolclass((DomResultCourse) s, courseLeaves, sparseMatrix);
//            }
//            return;
//        }
//
//        // case course has no children this is a course tree leave. then calculate subtree score and fill sparse Matrix 
//        if (course.getChildren().isEmpty()) {
//            // no DomResultScoContext objects, therefor no scores.
//            return;
//        }
//
//        //there are kids but not of class DomResultCourse therefor of DomResultScoContext class.
//        //add this to the course leaves and collect score.
//        courseLeaves.put(((DomResultCourse) this).getCourse().getId(), (DomResultCourse) this);
//        Map<PersistenceId, DomResultStudent> courseResults = this.getStudentCollectedAverageSubtreeScore(this);
//        sparseMatrix.put(course.getCourse().getId(), courseResults);
//        return;
//    }

    public void getStudentCollectedAverageSubtreeScore(DomResultScore<T> aThis, Map<PersistenceId, DomResultStudent> studentScores) {
//        Object[] kids = this.getChildren().values().toArray();
        if (this instanceof DomResultStudentSco) {
            DomResultStudentSco ss = (DomResultStudentSco) this;
            if(ss!=null && ss.getStudentSco()!=null){
            DomResultStudent studentScore = studentScores.get(ss.getStudentSco().getUserID());
            if(studentScore!=null){
            studentScore.setScore(studentScore.getScore() + ss.getScore());
            studentScore.setCnt(studentScore.getCnt() + 1);
            }
            }
            return;
        }
        if (this.children.isEmpty()) {
            return;
        }else{
            for (DomResultScore s : this.getChildren().values()) {
                s.getStudentCollectedAverageSubtreeScore(s, studentScores);
            }
            
        }

    }

}
