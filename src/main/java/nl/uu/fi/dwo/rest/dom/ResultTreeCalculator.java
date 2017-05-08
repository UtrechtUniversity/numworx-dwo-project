package nl.uu.fi.dwo.rest.dom;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import nl.uu.fi.dwo.rest.dom.entities.DomResultCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
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
     * Score per schoolclass per leaf course. Every sco that has no work has
     * score 0.0.
     *
     * @param tree
     * @return
     */
    public static DomResultPlotMatrix GetScoreOfTeacherClassesByLeafCourses(DomResultTree tree) {
        Map<PersistenceId, Map<PersistenceId, DomResultCourse>> sparseMatrix = new HashMap<PersistenceId, Map<PersistenceId, DomResultCourse>>();
        for (Map.Entry<PersistenceId, DomResultSchoolClass> entry : tree.getResultTree().getChildren().entrySet()) {
            sparseMatrix.put(entry.getKey(), new HashMap<PersistenceId, DomResultCourse>());
        }

        //crawl and fill
        Map<PersistenceId, DomResultCourse> courseLeaves = new HashMap<PersistenceId, DomResultCourse>();
        tree.getResultTree().collectCourseLeaves(courseLeaves);
        int nClasses = tree.getResultTree().getChildren().size();
        DomResultScore[] classes = new DomResultScore[nClasses];
        classes = tree.getResultTree().getChildren().values().toArray(new DomResultSchoolClass[0]);
        DomResultScore[] courses;
        courses = courseLeaves.values().toArray(new DomResultScore[0]);

        DomResultPlotMatrix result = new DomResultPlotMatrix(classes, courses);
        for (int i = 0; i < classes.length; i++) {
            DomResultSchoolClass resultClass = (DomResultSchoolClass) classes[i];
            tree.getResultTree().collectScoresPerCourseOverSchoolClass(resultClass, tree.getStudentTree().getChildren().get(resultClass.getSchoolClass().getId()).getChildren().size(), courseLeaves, sparseMatrix);
            for (int j = 0; j < courses.length; j++) {
                DomResultScore fieldScore = sparseMatrix.get(((DomResultSchoolClass) classes[i]).getSchoolClass().getId())
                        .get(((DomResultCourse) courses[j]).getCourse().getId());
                if (fieldScore != null) {
                    result.setMarks(i, j, fieldScore);
                } else {
                    result.setMarks(i, j, null);
                }
            }
        }
        //put sparseMatrix in result
        return result;
    }

    /**
     * Score per schoolclass per leaf course. Every sco that has no work has
     * score 0.0.
     *
     * @param tree
     * @param resultClass
     * @return
     */
    public static DomResultPlotMatrix GetScoreOfLeafCoursesByStudentsInClass(DomResultTree tree, DomResultSchoolClass resultClass) {
        DomResultSchoolClass studentClass;
        studentClass = tree.getStudentTree().getChildren().get(resultClass.getSchoolClass().getId());
        resultClass = tree.getResultTree().getChildren().get(resultClass.getSchoolClass().getId()); //ensure up-to-date just in case.
        DomResultPlotMatrix result = null;
        if(result == null) {
            return result;
        }
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
            for(DomResultStudent rStudent: studentScores.values()){
                rStudent.setScore(rStudent.getScore()/courses[j].getChildren().size());
            }
            for (int i = 0; i < students.length; i++) {
                //put studentscore in matrix
                result.setMarks(i, j, studentScores.get(students[i].getStudent().getId()));
            }
        }
        return result;
    }
}
