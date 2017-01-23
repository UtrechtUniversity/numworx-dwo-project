package nl.uu.fi.dwo.rest.dom;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import nl.uu.fi.dwo.rest.dom.entities.DomResultCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentSco;
import nl.uu.fi.dwo.rest.dom.entities.DomResultTeacher;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Calculates the score value for each node in the ResultTree. This simplifies
 * the calculation of the view matrices.
 *
 *
 * @author Gert van der Plas
 */
public class ResultTreeCalculator {

    public static void UpdateResultTree(ResultTree tree) {
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

    public static void getViewSchoolClassCourse(ResultTree tree) {
//        DomResultTeacher teacherScore = tree.getResultTree();
//        //get schoolClasses
//        Map<PersistenceId, DomResultSchoolClass> schoolClasses = teacherScore.getChildren();
//        Map<PersistenceId, DomResultCourse> courses = new HashMap<PersistenceId, DomResultCourse>();
//        //Get courses
//        for (DomResultSchoolClass schoolClass : schoolClasses.values()) {
//            for (DomResultCourse course : schoolClass.getChildren().values()) {
//                courses.put(course.getCourse().getId(), course);
//            }
//        }
//
//        //build array and fill with values
    }
    
    /**
     * 
     * 
     * @param course
     * @param scoMap 
     */
    private void crawlSchoolClassSco(DomResultScore course, Map<PersistenceId, DomResultScoContext> scoMap) {
        if (course instanceof DomResultScoContext) {
            DomResultScoContext context = (DomResultScoContext) course;
            double score = 0;
            int cnt=0;
            for(DomResultStudentSco studentScore : context.getChildren().values()){
                score+=studentScore.getScore();
                cnt+=studentScore.getCnt();
            }
            context.setScore(score);
            context.setCnt(cnt);
            scoMap.put(context.getScoContext().getId(), context);
        } else {
            Map<PersistenceId, DomResultScore> children = course.getChildren();
            if (!children.isEmpty()) {
                for (DomResultScore score : children.values()) {
                    crawlSchoolClassSco(score, scoMap);
                }
            }
        }
        
        //TODO Create view matrices
        // SchoolClass/Course
        // SchoolClass - Student/Course
        // SchoolClass/Activity
        // SchoolClass - Student/Activity

    }
}
