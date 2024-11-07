package nl.uu.fi.dwo.rest.dom.entities;

import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.rest.dom.entities.util.DomResultScoreVisitor;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Node of the ResultScoreTree. The tree is crawled to calculate a result matrix
 * for the result viewing.
 *
 * @author G.A.J. van der Plas email: G.A.J.vanderPlas@uu.nl
 * @param <T>
 */
@SuppressWarnings("rawtypes")
public abstract class DomResultScore<T extends DomResultScore> {

    private int nodeId = -1;
    private Double score = 0.0; //unmade work is always score 0.
    private double scoCount = 0;   //count is 0 because scoCount may summarize empty subtree.
    private double studentScoCount = 0;   //count is 0 because studentScoCount may summarize empty subtree.
    private String label, totalTime;
    private DomResultScore parent = null;
    private Map<PersistenceId, T> children = new HashMap<PersistenceId, T>();
    
    private Double fraction;
    private String title, description;

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
        if (parent instanceof DomResultTeacher) {
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
        this.children = children;
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

    /**
     * is visible for teachers: always!
     * @param state
     * @return
     */
    public static boolean isVisibleForTeachers(ViewState state) {
    	return true;
        //return (state == ViewState.studentsAndTeachers || state == ViewState.teachers || state == ViewState.students);
    }

    @SuppressWarnings("unchecked")
	public void collectActivities(Map<PersistenceId, DomResultScoContext> activities) {
        if (this.children.isEmpty()) {
            return;
        }
        //deepest level, most objects, no recursion
        if (this instanceof DomResultScoContext) {
            DomResultScoContext sco = (DomResultScoContext) this;
            if (!activities.containsKey(sco.getScoContext().getId())) {
                activities.put(sco.getScoContext().getId(), sco);
            }
            return;
        }
        for (DomResultScore s : this.getChildren().values()) {
            if (!(s instanceof DomResultCourseInClass) || ((s instanceof DomResultCourseInClass) && isVisibleForTeachers(((DomResultCourseInClass) s).getViewState()))) {
                s.collectActivities(activities);
            }
        }
    }

    /**
     * Fills the map courseLeaves with all Courses that are leaves in the course
     * tree.
     *
     * @param courseLeaves A map of leaves in the course tree.
     */
    @SuppressWarnings("unchecked")
	public void collectCourseLeaves(Map<PersistenceId, DomResultCourseInClass> courseLeaves) {
        if (this.children.isEmpty()) {
            return;
        }
        //deepest level, most objects, no recursion
        if (this instanceof DomResultCourseInClass && isVisibleForTeachers(((DomResultCourseInClass) this).getViewState())) {
            DomResultCourseInClass course = (DomResultCourseInClass) this;
            if (!course.getCourse().getWithChildren()) {
                courseLeaves.put(((DomResultCourseInClass) this).getCourse().getId(), (DomResultCourseInClass) this);
            }
            return;
        }
        for (DomResultScore s : this.getChildren().values()) {
            if (!(s instanceof DomResultCourseInClass) || ((s instanceof DomResultCourseInClass) && isVisibleForTeachers(((DomResultCourseInClass) s).getViewState()))) {
                s.collectCourseLeaves(courseLeaves);
            }
        }
    }

    /**
     * Fills the map courseLeaves with all Courses that are leaves in the course
     * tree.
     *
     * @param courseLeaves A map of leaves in the course tree.
     */
    @SuppressWarnings("unchecked")
	public void collectCourseLeaves(DomResultTeacher teacher, Map<PersistenceId, DomResultCourseInClass> courseLeaves) {
        if (this.children.isEmpty()) {
            return;
        }
        for (DomResultScore s : this.getChildren().values()) {
            if (!(s instanceof DomResultCourseInClass) || ((s instanceof DomResultCourseInClass) && isVisibleForTeachers(((DomResultCourseInClass) s).getViewState()))) {
                s.collectCourseLeaves(courseLeaves);
            }
        }
    }

    /**
     * Calculates the sum DomResultStudentSco scores and the number of subscores
     * in a subtree in given node. Each node in the subtree has a score equal to
     * the sum of DomResultStudentSco scores with a count for each of the
     * DomResultScoContext.
     *
     */
    public void calculateSumOfSubtreeScore() {
        //verified code.
        if (this instanceof DomResultCourseInClass && !isVisibleForTeachers(((DomResultCourseInClass) this).getViewState())) {
            this.setScore(0.0);
            this.setScoCount(0);
            this.setStudentScoCount(0.0);
            return;
        }
        //deepest level, most objects, no recursion
        if (this instanceof DomResultStudentScoContext) {
            DomResultStudentScoContext ss = (DomResultStudentScoContext) this;
            this.setScore(ss.getStudentSco().getScore());
            this.setTotalTime(ss.getStudentSco().getTotalTime());
            this.setScoCount(0);
            this.setStudentScoCount(1);
            return;
        }

        if (this instanceof DomResultScoContext) {
            this.setScore(0.0);
            this.setScoCount(1);
            this.setStudentScoCount(0.0);
            for (DomResultScore s : this.getChildren().values()) {
                s.calculateSumOfSubtreeScore();
                this.setScore(this.getScore() + s.getScore());
                this.setStudentScoCount(this.getStudentScoCount() + s.getStudentScoCount());
            }
            return;
        }

        //case course leave set scoCount
        if (this instanceof DomResultCourseInClass && isVisibleForTeachers(((DomResultCourseInClass) this).getViewState())) {
            DomResultCourseInClass course = (DomResultCourseInClass) this;
            this.setScore(0.0);
            this.setScoCount(0.0);
            this.setStudentScoCount(0.0);
            if (!course.getCourse().getWithChildren()) {
                //is course leave
                for (DomResultScore s : this.getChildren().values()) {
                    //recurse
                    s.calculateSumOfSubtreeScore();
                    //add score from children and set cnt
                    this.setScore(this.getScore() + s.getScore());
                    this.setStudentScoCount(this.getStudentScoCount() + s.getStudentScoCount());
                }
                this.setScoCount(this.getChildren().size());
            } else {
                //course node, not leave
                for (DomResultScore s : this.getChildren().values()) {
                    //recurse
                    s.calculateSumOfSubtreeScore();
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
            if (!(s instanceof DomResultCourseInClass) || ((s instanceof DomResultCourseInClass) && isVisibleForTeachers(((DomResultCourseInClass) s).getViewState()))) {
                s.calculateSumOfSubtreeScore();
                //add score from children and set cnt
                this.setScore(this.getScore() + s.getScore());
                this.setScoCount(this.getScoCount() + s.getScoCount());
                this.setStudentScoCount(this.getStudentScoCount() + s.getStudentScoCount());
            }
        }
    }

    /**
     * Calculates the sum DomResultStudentSco scores and the number of subscores
     * in a subtree in given node for a tree or subtree of a schoolClass in the
     * result tree. Each node in the subtree has a score equal to the sum of
     * DomResultStudentSco scores with a count for each of the
     * DomResultScoContext.
     *
     */
    public void calculateSumOfSubtreeScore(int studentsInClass) {
        //verified code.
        if (studentsInClass == 0) {
            return;
        }
        //deepest level, most objects, no recursion
        if (this instanceof DomResultStudentScoContext) {
            DomResultStudentScoContext ss = (DomResultStudentScoContext) this;
            this.setScore(ss.getStudentSco().getScore());
            this.setTotalTime(ss.getStudentSco().getTotalTime());
            this.setScoCount(0);
            this.setStudentScoCount(1);
            return;
        }

        if (this instanceof DomResultScoContext) {
            this.setScore(0.0);
            this.setScoCount(1);
            this.setStudentScoCount(0.0);
            for (DomResultScore s : this.getChildren().values()) {
                s.calculateSumOfSubtreeScore(studentsInClass);
                this.setScore(this.getScore() + s.getScore());
                this.setStudentScoCount(this.getStudentScoCount() + s.getStudentScoCount());
            }
            this.setScore(this.getScore() / studentsInClass);
            return;
        }

        //case course leave set scoCount
        if (this instanceof DomResultCourseInClass && isVisibleForTeachers(((DomResultCourseInClass) this).getViewState())) {
            DomResultCourseInClass course = (DomResultCourseInClass) this;
            this.setScore(0.0);
            this.setScoCount(0.0);
            this.setStudentScoCount(0.0);
            if (!course.getCourse().getWithChildren()) {
                //is course leave
                for (DomResultScore s : this.getChildren().values()) {
                    //recurse
                    s.calculateSumOfSubtreeScore(studentsInClass);
                    //add score from children and set cnt
                    this.setScore(this.getScore() + s.getScore());
                    this.setStudentScoCount(this.getStudentScoCount() + s.getStudentScoCount());
                }
                this.setScoCount(this.getChildren().size());
            } else {
                //course node, not leave
                for (DomResultScore s : this.getChildren().values()) {
                    //recurse
                    s.calculateSumOfSubtreeScore(studentsInClass);
                    //add score from children and set cnt
                    this.setScore(this.getScore() + s.getScore());
                    this.setScoCount(this.getScoCount() + s.getScoCount());
                    this.setStudentScoCount(this.getStudentScoCount() + s.getStudentScoCount());
                }
            }
            this.setScore(this.getScore() / this.getChildren().size());
            return;
        }

        //for DomResultSchoolClasses and Teachers and higher stuff
        this.setScore(0.0);
        this.setScoCount(0.0);
        this.setStudentScoCount(0.0);
        for (DomResultScore s : this.getChildren().values()) {
            if (!(s instanceof DomResultCourseInClass) || ((s instanceof DomResultCourseInClass) && isVisibleForTeachers(((DomResultCourseInClass) s).getViewState()))) {
                s.calculateSumOfSubtreeScore(studentsInClass);
                //add score from children and set cnt
                this.setScore(this.getScore() + s.getScore());
                this.setScoCount(this.getScoCount() + s.getScoCount());
                this.setStudentScoCount(this.getStudentScoCount() + s.getStudentScoCount());
            }
        }
        this.setScore(this.getScore() / this.getChildren().size());
    }

    /**
     * Flat mapping of scores per course per schoolclass.
     *
     *
     * @param schoolClass
     * @param nStudents
     * @param courseLeaves
     * @param sparseMatrix
     * @deprecated not used
     */
    @SuppressWarnings("unchecked")
	private void collectScoresPerCourseOverSchoolClass(DomResultSchoolClass schoolClass, int nStudents, Map<PersistenceId, DomResultCourseInClass> courseLeaves, Map<PersistenceId, Map<PersistenceId, DomResultCourseInClass>> sparseMatrix) {
        if (this.children.isEmpty()) {
            return;
        }
        if (this instanceof DomResultCourseInClass && !isVisibleForTeachers(((DomResultCourseInClass) this).getViewState())) {
            this.setScore(0.0);
            this.setScoCount(0);
            this.setStudentScoCount(0.0);
            return;
        }
        if (this instanceof DomResultSchoolClass) {
            schoolClass = (DomResultSchoolClass) this;
            this.setScore(0.0);
            this.setScoCount(0.0);
            this.setStudentScoCount(0.0);
            for (DomResultScore s : this.getChildren().values()) {
                s.collectScoresPerCourseOverSchoolClass(schoolClass, nStudents, courseLeaves, sparseMatrix);
                this.setScore(this.getScore() + s.getScore());
                this.setScoCount(this.getScoCount() + s.getScoCount());
                this.setStudentScoCount(this.getStudentScoCount() + s.getStudentScoCount());
            }
            return;
        }
        Object[] kids = this.getChildren().values().toArray();
        if (kids[0] instanceof DomResultScoContext && isVisibleForTeachers(((DomResultCourseInClass) this).getViewState())) {
            //add course to horizontal header
            courseLeaves.put(((DomResultCourseInClass) this).getCourse().getId(), (DomResultCourseInClass) this);
            //add course score to sparse matrix
            this.calculateSumOfSubtreeScore();
            this.setScore(this.getScore() / nStudents);
            //this.setScore(this.getScore()/nStudents);
            sparseMatrix.get(schoolClass.getSchoolClass().getId()).put(((DomResultCourseInClass) this).getCourse().getId(), (DomResultCourseInClass) this);
            return;
        } else {
            this.setScore(0.0);
            this.setScoCount(0.0);
            this.setStudentScoCount(0.0);
            for (DomResultScore s : this.getChildren().values()) {
                if (!(s instanceof DomResultCourseInClass) || ((s instanceof DomResultCourseInClass) && isVisibleForTeachers(((DomResultCourseInClass) s).getViewState()))) {
                    s.collectScoresPerCourseOverSchoolClass(schoolClass, nStudents, courseLeaves, sparseMatrix);
                    this.setScore(this.getScore() + s.getScore());
                    this.setScoCount(this.getScoCount() + s.getScoCount());
                    this.setStudentScoCount(this.getStudentScoCount() + s.getStudentScoCount());
                }
            }

        }
    }

    /**
     *
     * @param studentScores
     */
    @SuppressWarnings("unchecked")
	public void getStudentCollectedAverageSubtreeScore(Map<PersistenceId, DomResultStudent> studentScores) {
        if (this instanceof DomResultStudentScoContext) {
            DomResultStudentScoContext ss = (DomResultStudentScoContext) this;
            ss.setScore(ss.getStudentSco().getScore());
            ss.setTotalTime(ss.getStudentSco().getTotalTime());
//            if (ss != null && ss.getStudentSco() != null) { //impossible else error
            DomResultStudent studentScore = studentScores.get(ss.getStudentSco().getUserID());
            studentScore.setScore(studentScore.getScore() + ss.getScore());
            studentScore.setTotalTime(ss.getTotalTime()); // FIXME add somehow
            studentScore.setStudentScoCount(this.getStudentScoCount() + studentScore.getStudentScoCount());
            //               }

            return;
        }
        if (this.children.isEmpty()) {
            return;
        } else {
            for (DomResultScore s : this.getChildren().values()) {
                if (!(s instanceof DomResultCourseInClass) || ((s instanceof DomResultCourseInClass) && isVisibleForTeachers(((DomResultCourseInClass) s).getViewState()))) {
                    s.getStudentCollectedAverageSubtreeScore(studentScores);
                }
            }
        }

    }

    public DomResultSchoolClass getAncestralSchoolClass() {
        if (this instanceof DomResultSchoolClass) {
            return (DomResultSchoolClass) this;
        } else if (this.parent != null) {
            return this.parent.getAncestralSchoolClass();
        } else {
            return null;
        }

    }

    /**
     * A unique id in the tree.
     * 
     * @param aNodeId 
     */
    public void setNodeId(int aNodeId) {
        nodeId = aNodeId;
    }

    /**
     * @return the nodeId
     */
    public int getNodeId() {
        return nodeId;
    }

    public String getId() {
      return "";
    }

    /**
     * @return the totalTime
     */
    public String getTotalTime() {
      return totalTime;
    }

    /**
     * @param totalTime the totalTime to set
     */
    public void setTotalTime(String totalTime) {
      this.totalTime = totalTime;
    }

	public Double getFraction() {
		return fraction;
	}

	public void setFraction(Double fraction) {
		this.fraction = fraction;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public abstract void visit(DomResultScoreVisitor v);
}
