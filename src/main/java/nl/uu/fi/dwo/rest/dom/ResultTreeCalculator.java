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
        tree.getResultTree().collectScoresPerCourseOverSchoolClass(null, courseLeaves, sparseMatrix);
        int nClasses = tree.getResultTree().getChildren().size();
        DomResultScore[] classes = new DomResultScore[nClasses];
        classes = tree.getResultTree().getChildren().values().toArray(new DomResultSchoolClass[0]);
        DomResultScore[] courses;
        courses = courseLeaves.values().toArray(new DomResultScore[0]);
        DomResultPlotMatrix result = new DomResultPlotMatrix(classes, courses);
        for (int i = 0; i < classes.length; i++) {
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
//
//    /**
//     * Score per schoolclass per leaf course. Every sco that has no work has score
//     * 0.0.
//     * @param tree
//     * @return 
//     */
//    public static DomResultPlotMatrix GetScoreOfLeafCoursesByStudentsInClass(DomResultSchoolClass schoolClass) {
//        //create sparse matrix of courses
//        Map<PersistenceId, Map<PersistenceId, DomResultStudent>> sparseMatrix = new HashMap<PersistenceId, Map<PersistenceId, DomResultStudent>>();
//        Map<PersistenceId, DomResultCourse> courseLeaves = new HashMap<PersistenceId, DomResultCourse>();
//        schoolClass.getChildren().crawlCourseForLeaves(null, courseLeaves, sparseMatrix);
//        //crawl and fill
//        Map<PersistenceId, DomResultCourse> courseLeaves = new HashMap<PersistenceId, DomResultCourse>();
//        tree.getResultTree().crawlSchoolClassCourse(null, courseLeaves, sparseMatrix);
//        int nClasses = tree.getResultTree().getChildren().size();
//        DomResultScore[] classes = new DomResultScore[nClasses];
//        classes = tree.getResultTree().getChildren().values().toArray(new DomResultSchoolClass[0]);
//        DomResultScore[] courses;
//        courses = courseLeaves.values().toArray(new DomResultScore[0]);
//        DomResultPlotMatrix result = new DomResultPlotMatrix(classes, courses);
//        for (int i = 0; i < classes.length; i++) {
//            for (int j = 0; j < courses.length; j++) {
//                DomResultScore fieldScore= sparseMatrix.get(((DomResultSchoolClass) classes[i]).getSchoolClass().getId())
//                        .get(((DomResultCourse) courses[j]).getCourse().getId());
//                if(fieldScore != null
//                        ){
//                    result.setMarks(i, j, fieldScore);
//                }else{
//                    result.setMarks(i, j, null);
//                }
//            }
//        }
//        //put sparseMatrix in result
//        return result;
//    }
//    

    /**
     * Score per schoolclass per leaf course. Every sco that has no work has
     * score 0.0.
     *
     * @param studentClass
     * @param resultClass
     * @return
     */
    public static DomResultPlotMatrix GetScoreOfLeafCoursesByStudentsInClass(DomResultSchoolClass studentClass, DomResultSchoolClass resultClass) {
        DomResultPlotMatrix result = null;
        //collect leave courses in schoolClass
        Map<PersistenceId, DomResultCourse> courseLeaves = new HashMap<PersistenceId, DomResultCourse>();
        //collect courseLeaves in class
        resultClass.collectCourseLeaves(resultClass, courseLeaves);
        DomResultScore[] courses;
        courses = courseLeaves.values().toArray(new DomResultScore[0]);
        
        //collect students
        DomStudent[] domStudents = (DomStudent[]) studentClass.getChildren().values().toArray(new DomStudent[0]);
        DomResultStudent[] students = new DomResultStudent[domStudents.length];
        for(int i =0; i< domStudents.length;i++){
            students[i] = new DomResultStudent(domStudents[i]);
        }
        result = new DomResultPlotMatrix(students, courses);
        
            //put sparseMatrix in result
        for (int j = 0; j < courses.length; j++) {
            //fetch student hashmap for course
            Map<PersistenceId, DomResultStudent> studentScores = new HashMap<PersistenceId, DomResultStudent>(students.length);
            for (int i = 0; i < students.length; i++) {
            studentScores.put(students[i].getStudent().getId(), students[i]);
                    }
            courses[j].getStudentCollectedAverageSubtreeScore(courses[j],studentScores);
            for (int i = 0; i < students.length; i++) {
                //put studentscore in matrix
                 if(studentScores.containsKey(students[i].getStudent().getId())){
                     result.setMarks(i, j, studentScores.get(students[i].getStudent().getId()));
                 }
            }
        }        
        return result;
    }
}
