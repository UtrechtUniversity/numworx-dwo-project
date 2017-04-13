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
    private double scoCount = 0;   //count is 0 because scoCount may summarize empty subtree.
    private double studentScoCount = 0;   //count is 0 
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
     * @return the scoCount
     */
    public double getScoCount() {
        return scoCount;
    }

    /**
     * @param scoCount the scoCount to set
     */
    public void setScoCount(double scoCount) {
        this.scoCount = scoCount;
    }

    /**
     * @return the studentScoCount
     */
    public double getStudentScoCount() {
        return studentScoCount;
    }

    /**
     * @param studentScoCount the studentScoCount to set
     */
    public void setStudentScoCount(double studentScoCount) {
        this.studentScoCount = studentScoCount;
    }
//
//    /**
//     * Fills the map courseLeaves with all Courses that are leaves in the course
//     * tree.
//     *
//     * @param schoolClass the node that is the root of the subtree searched.
//     * @param courseLeaves A map of leaves in the course tree.
//     */
//    public void collectCourseLeaves(DomResultSchoolClass schoolClass, Map<PersistenceId, DomResultCourse> courseLeaves) {
//        if (this.children.isEmpty()) {
//            return;
//        }
//        for (DomResultScore s : this.getChildren().values()) {
//            s.collectCourseLeaves((DomResultCourse) s, courseLeaves);
//        }
//    }
//
//    /**
//     * Fills the map courseLeaves with all Courses that are leaves in the course
//     * tree.
//     *
//     * @param course the node that is the root of the subtree searched.
//     * @param courseLeaves A map of leaves in the course tree.
//     */
//    public void collectCourseLeaves(DomResultCourse course, Map<PersistenceId, DomResultCourse> courseLeaves) {
//        if (this.children.isEmpty()) {
//            return;
//        }
//        if (!course.getCourse().getWithChildren()) {
//            courseLeaves.put(((DomResultCourse) this).getCourse().getId(), (DomResultCourse) this);
//        } else {
//            for (DomResultScore s : this.getChildren().values()) {
//                s.collectCourseLeaves((DomResultCourse) s, courseLeaves);
//            }
//        }
//    }

    /**
     * Fills the map courseLeaves with all Courses that are leaves in the course
     * tree.
     *
     * @param schoolClass
     * @param courseLeaves A map of leaves in the course tree.
     */
    public void collectCourseLeaves(Map<PersistenceId, DomResultCourse> courseLeaves) {
        if (this.children.isEmpty()) {
            return;
        }
        //deepest level, most objects, no recursion
        if (this instanceof DomResultCourse) {
            DomResultCourse course = (DomResultCourse) this;
            if (!course.getCourse().getWithChildren()) {
                courseLeaves.put(((DomResultCourse) this).getCourse().getId(), (DomResultCourse) this);
            }
            return;
        }
        for (DomResultScore s : this.getChildren().values()) {
            s.collectCourseLeaves(courseLeaves);
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
            s.collectCourseLeaves(courseLeaves);
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
     * the sum of DomResultStudentSco scores with a count for each of the
     * DomResultScoContext.
     *
     * @param parent
     * @param node
     */
    private void calculateAverageSubtreeScore() {
        //deepest level, most objects, no recursion
        if (this instanceof DomResultStudentSco) {
            DomResultStudentSco ss = (DomResultStudentSco) this;
            this.setScore(ss.getStudentSco().getScore());
            this.setScoCount(0);
            this.setStudentScoCount(1);
            return;
        }

        if (this instanceof DomResultScoContext) {
            this.setScore(0.0);
            this.setScoCount(1);
            this.setStudentScoCount(0.0);
            for (DomResultScore s : this.getChildren().values()) {
                s.calculateAverageSubtreeScore();
                this.setScore(this.getScore() + s.getScore());
                this.setStudentScoCount(this.getStudentScoCount() + s.getStudentScoCount());
            }
            return;
        }

        //case course leave set scoCount
        if (this instanceof DomResultCourse) {
            DomResultCourse course = (DomResultCourse) this;
            if (!course.getCourse().getWithChildren()) {
                //is course leave
                this.setScore(0.0);
                this.setScoCount(0.0);
                this.setStudentScoCount(0.0);
                for (DomResultScore s : this.getChildren().values()) {
                    //recurse
                    s.calculateAverageSubtreeScore();
                    //add score from children and set cnt
                    this.setScore(this.getScore() + s.getScore());
                    this.setScoCount(this.getChildren().size());
                    this.setStudentScoCount(this.getStudentScoCount() + s.getStudentScoCount());
                }
            } else {
                //course node, not leave
                this.setScore(0.0);
                this.setScoCount(0.0);
                this.setStudentScoCount(0.0);
                for (DomResultScore s : this.getChildren().values()) {
                    //recurse
                    s.calculateAverageSubtreeScore();
                    //add score from children and set cnt
                    this.setScore(this.getScore() + s.getScore());
                    this.setScoCount(this.getScoCount() + s.getScoCount());
                    this.setStudentScoCount(this.getStudentScoCount() + s.getStudentScoCount());
                }
            }
            return;
        }

        //for DomResultSchoolClasses and Teachers and higher stuff
        this.setScore(0.0);
        this.setScoCount(0.0);
        this.setStudentScoCount(0.0);
        for (DomResultScore s : this.getChildren().values()) {
            s.calculateAverageSubtreeScore();
            //add score from children and set cnt
            this.setScore(this.getScore() + s.getScore());
            this.setScoCount(this.getChildren().size());
            this.setStudentScoCount(this.getStudentScoCount() + s.getStudentScoCount());
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
        if (this.children.isEmpty()) {
            return;
        }
        if (this instanceof DomResultSchoolClass) {
            schoolClass = (DomResultSchoolClass) this;
            this.setScore(0.0);
            this.setScoCount(0.0);
            this.setStudentScoCount(0.0);
            for (DomResultScore s : this.getChildren().values()) {
                s.collectScoresPerCourseOverSchoolClass(schoolClass, courseLeaves, sparseMatrix);
                this.setScore(this.getScore() + s.getScore());
                this.setScoCount(this.getScoCount() + s.getScoCount());
                this.setStudentScoCount(this.getStudentScoCount() + s.getStudentScoCount());
            }
            return;
        }
        Object[] kids = this.getChildren().values().toArray();
        if (kids[0] instanceof DomResultScoContext) {
            //add course to horizontal header
            courseLeaves.put(((DomResultCourse) this).getCourse().getId(), (DomResultCourse) this);
            //add course score to sparse matrix
            this.calculateAverageSubtreeScore();
            //this.setScore(this.getScore()/nStudents);
            sparseMatrix.get(schoolClass.getSchoolClass().getId()).put(((DomResultCourse) this).getCourse().getId(), (DomResultCourse) this);
            return;
        } else {
            this.setScore(0.0);
            this.setScoCount(0.0);
            this.setStudentScoCount(0.0);
            for (DomResultScore s : this.getChildren().values()) {
                s.collectScoresPerCourseOverSchoolClass(schoolClass, courseLeaves, sparseMatrix);
                this.setScore(this.getScore() + s.getScore());
                this.setScoCount(this.getScoCount() + s.getScoCount());
                this.setStudentScoCount(this.getStudentScoCount() + s.getStudentScoCount());
            }
        }
    }

    public void getStudentCollectedAverageSubtreeScore(Map<PersistenceId, DomResultStudent> studentScores) {
        if (this instanceof DomResultStudentSco) {
            DomResultStudentSco ss = (DomResultStudentSco) this;
            if (ss != null && ss.getStudentSco() != null) {
                DomResultStudent studentScore = studentScores.get(ss.getStudentSco().getUserID());
                if (studentScore != null) {
                    studentScore.setScore(studentScore.getScore() + ss.getScore());
                    studentScore.setScoCount(studentScore.getScoCount() + 1);
                }
            }
            return;
        }
        if (this.children.isEmpty()) {
            return;
        } else {
            for (DomResultScore s : this.getChildren().values()) {
                s.getStudentCollectedAverageSubtreeScore(studentScores);
            }

        }

    }

}
