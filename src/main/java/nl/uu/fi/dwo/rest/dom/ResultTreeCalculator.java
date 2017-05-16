package nl.uu.fi.dwo.rest.dom;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import nl.uu.fi.dwo.rest.dom.entities.DomResultCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Calculates the score value for each node in the DomResultTree. This
 * simplifies the calculation of the view matrices. The general assumption is
 * that missing work is scored 0.
 *
 * @author Gert van der Plas
 */
public class ResultTreeCalculator {

    public static void UpdateResultTree(DomResultTree tree) {
        DomResultScore node = tree.getResultTree();
        updateNode(node);
    }

    public static void updateNode(DomResultScore resultScore) {
        double score = 0;
        int cnt = 0;
        Iterator<DomResultScore> iterator = resultScore.getChildren().values().iterator();
        while (iterator.hasNext()) {
            DomResultScore node = iterator.next();
            updateNode(node);
            score += node.getScore();
            cnt++;
        }
        resultScore.setScore(score / cnt);
    }

    /**
     * Score per school class per leaf course. Every sco that has no work has
     * score 0.0.
     *
     * @param tree
     * @return
     */
    public static DomResultPlotMatrix GetScoreOfTeacherClassesByLeafCourses(DomResultTree tree) {
        //verified

        // show all classes  with courses.
        DomResultSchoolClass[] classes;// = new DomResultSchoolClass[tree.getResultTree().getChildren().size()];
        classes = tree.getResultTree().getChildren().values().toArray(new DomResultSchoolClass[0]);

        //crawl and collect course leaves.
        Map<PersistenceId, DomResultCourse> courseLeaves = new HashMap<PersistenceId, DomResultCourse>();
        tree.getResultTree().collectCourseLeaves(courseLeaves);
        DomResultCourse[] courses;
        courses = courseLeaves.values().toArray(new DomResultCourse[0]);

        DomResultPlotMatrix result = new DomResultPlotMatrix(classes, courses);
        for (int i = 0; i < classes.length; i++) {
            DomResultSchoolClass resultClass = (DomResultSchoolClass) classes[i];
            //recalculate for the number of students in a score. 
            resultClass.calculateSumOfSubtreeScore(tree.getStudentTree().getChildren().get(classes[i].getSchoolClass().getId()).getChildren().size());
            //assuming single threaded access to all variables.
            for (int j = 0; j < courses.length; j++) {
                DomResultScore fieldScore = (DomResultCourse) resultClass.getChildren().get(courses[j].getCourse().getId());
                result.setMarks(i, j, fieldScore);
            }
        }
        //put sparseMatrix in result
        return result;
    }

    /**
     * Score per student in a given school class per leaf course. Every Sco that
     * has no work has score 0.0.
     *
     * @param tree
     * @param resultClass
     * @return
     */
    public static DomResultPlotMatrix GetScoreOfLeafCoursesByStudentsInClass(DomResultTree tree, DomResultSchoolClass resultClass) {
        //verified
        DomResultSchoolClass studentClass;
        studentClass = tree.getStudentTree().getChildren().get(resultClass.getSchoolClass().getId());
        resultClass = tree.getResultTree().getChildren().get(resultClass.getSchoolClass().getId()); //ensure up-to-date just in case.
        DomResultPlotMatrix result = null;
        //collect leave courses in schoolClass
        Map<PersistenceId, DomResultCourse> courseLeaves = new HashMap<PersistenceId, DomResultCourse>();
        //collect courseLeaves in class
        resultClass.collectCourseLeaves(courseLeaves);
        DomResultScore[] courses;
        courses = courseLeaves.values().toArray(new DomResultScore[0]);

        //collect students
        DomStudent[] domStudents = (DomStudent[]) studentClass.getChildren().values().toArray(new DomStudent[0]);
        DomResultStudent[] students = new DomResultStudent[domStudents.length];
        for (int i = 0; i < domStudents.length; i++) {
            students[i] = new DomResultStudent(domStudents[i]);
        }
        result = new DomResultPlotMatrix(students, courses);

        //put sparseMatrix in result
        for (int j = 0; j < courses.length; j++) {
            //fetch student hashmap for course, with key the student's userid
            Map<PersistenceId, DomResultStudent> studentScores = new HashMap<PersistenceId, DomResultStudent>(domStudents.length);
            //fill map with new DomResultStudents
            for (DomStudent student : domStudents) {
                studentScores.put(student.getId(), new DomResultStudent(student));
            }
            courses[j].getStudentCollectedAverageSubtreeScore(studentScores);
            for (DomResultStudent rStudent : studentScores.values()) {
                rStudent.setScore(rStudent.getScore() / courses[j].getChildren().size());
            }
            for (int i = 0; i < students.length; i++) {
                //put studentscore in matrix
                result.setMarks(i, j, studentScores.get(students[i].getStudent().getId()));
            }
        }
        return result;
    }

    /**
     * Score per school class in a given course per activity. Every Sco that has
     * no work has score 0.0.
     *
     * @param tree
     * @param resultClass
     * @return
     */
    public static DomResultPlotMatrix GetScoreOfTeacherClassesByActivitiesOfCourse(DomResultTree tree, DomResultCourse resultCourse) {
        //collect classes
        DomResultSchoolClass[] classes;// = new DomResultSchoolClass[tree.getResultTree().getChildren().size()];
        classes = tree.getResultTree().getChildren().values().toArray(new DomResultSchoolClass[0]);

        Map<PersistenceId, DomResultScoContext> activityMap = new HashMap<PersistenceId, DomResultScoContext>();
        tree.getResultTree().collectActivities(activityMap);
        DomResultScoContext[] activities = activityMap.values().toArray(new DomResultScoContext[0]);
        DomResultPlotMatrix result = new DomResultPlotMatrix(classes, activities);
        for (int i = 0; i < classes.length; i++) {
            //collect course leaves in class
            Map<PersistenceId, DomResultCourse> courseLeaves = new HashMap<PersistenceId, DomResultCourse>();
            classes[i].collectCourseLeaves(courseLeaves);
            resultCourse = courseLeaves.get(resultCourse.getCourse().getId());
            //calculate
            if (tree.getStudentTree().getChildren() != null
                    && tree.getStudentTree().getChildren().get(classes[i].getSchoolClass().getId()) != null
                    && tree.getStudentTree().getChildren().get(classes[i].getSchoolClass().getId()).getChildren() != null) {
                resultCourse.calculateSumOfSubtreeScore(
                        tree.getStudentTree()
                                .getChildren()
                                .get(classes[i]
                                        .getSchoolClass()
                                        .getId())
                                .getChildren()
                                .size());
            } else {
                return result;
            }

            //crawl and collect course leaves.
            //collect activities
            Map<PersistenceId, DomResultScoContext> courseActivities;
            courseActivities = resultCourse.getChildren();

            //recalculate for the number of students in a score. 
            //assuming single threaded access to all variables.
            for (int j = 0; j < activities.length; j++) {
                if (courseActivities.containsKey(activities[j].getScoContext().getId())) {
                    DomResultScore fieldScore = (DomResultScoContext) courseActivities.get(activities[j].getScoContext().getId());
                    result.setMarks(i, j, fieldScore);
                }
            }
        }
        //put sparseMatrix in result
        return result;
    }

    /**
     * Scores in a schoolclass per student per activity of a course. Every sco
     * that has no work has score 0.0.
     *
     * @param tree
     * @param resultClass
     * @return
     */
    public static DomResultPlotMatrix GetScoreOfActivitiesOfCourseByStudentsInClass(DomResultTree tree, DomResultCourse resultCourse, DomResultSchoolClass resultClass) {
        DomResultPlotMatrix result = null;

        DomResultSchoolClass studentClass;
        resultClass = tree.getResultTree().getChildren().get(resultClass.getSchoolClass().getId()); //ensure up-to-date just in case.
        studentClass = tree.getStudentTree().getChildren().get(resultClass.getSchoolClass().getId());

        //collect students
        DomStudent[] domStudents = (DomStudent[]) studentClass.getChildren().values().toArray(new DomStudent[0]);
        DomResultStudent[] students = new DomResultStudent[domStudents.length];
        for (int i = 0; i < domStudents.length; i++) {
            students[i] = new DomResultStudent(domStudents[i]);
        }

        //getCourseLeaves in class
        Map<PersistenceId, DomResultCourse> courseLeaves = new HashMap<PersistenceId, DomResultCourse>();
        resultClass.collectCourseLeaves(courseLeaves);
        resultCourse = courseLeaves.get(resultCourse.getCourse().getId());
        //collect activities
        DomResultScoContext[] activities;
        activities = (DomResultScoContext[]) resultCourse.getChildren().values().toArray(new DomResultScoContext[0]);

        //create plot matrix
        result = new DomResultPlotMatrix(students, activities);

        for (int j = 0; j < activities.length; j++) { //(DomResultScoContext activity : activities) 
            //get map of studentSco scores
            Map<PersistenceId, DomResultStudent> studentScoMap = new HashMap<PersistenceId, DomResultStudent>();
            for (int i = 0; i < domStudents.length; i++) {
                studentScoMap.put(students[i].getStudent().getId(), new DomResultStudent(domStudents[i]));
            }
            activities[j].getStudentCollectedAverageSubtreeScore(studentScoMap);
            for (int i = 0; i < students.length; i++) {
                DomResultStudent scoResult = null;
                if (studentScoMap.containsKey(students[i].getStudent().getId())) {
                    DomResultStudent calc = studentScoMap.get(students[i].getStudent().getId());
                    scoResult = new DomResultStudent(calc.getStudent());
                    scoResult.setScore(calc.getScore());
                    scoResult.setScoCount(calc.getScoCount());
                    scoResult.setStudentScoCount(calc.getStudentScoCount());
                }
                result.setMarks(i, j, scoResult);
            }

        }
        return result;
    }
}
