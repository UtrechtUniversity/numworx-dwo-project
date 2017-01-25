package nl.uu.fi.dwo.rest.dom;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentSco;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Calculates the score value for each node in the ResultTree. This simplifies
 * the calculation of the view matrices.
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
     * Adds the sco's in the subtree to the scoMap given. The algorithm uses put in 
     * the map to ensure only one reference exists.
     *
     * @param course
     * @param scoMap
     */
    private static void crawlSubTreeForScos(DomResultScore node, Map<PersistenceId, DomResultScoContext> scoMap) {
        if (node instanceof DomResultScoContext) {
            DomResultScoContext context = (DomResultScoContext) node;
            double score = 0;
            int cnt = 0;
            for (DomResultStudentSco studentScore : context.getChildren().values()) {
                score += studentScore.getScore();
                cnt += studentScore.getCnt();
            }
            context.setScore(score);
            context.setCnt(cnt);
            scoMap.put(context.getScoContext().getId(), context);
        } else {
            Map<PersistenceId, DomResultScore> children = node.getChildren();
            if (!children.isEmpty()) {
                for (DomResultScore score : children.values()) {
                    crawlSubTreeForScos(score, scoMap);
                }
            }
        }

        //TODO Create view matrices
        // SchoolClass/Course
        // SchoolClass - Student/Course
        // SchoolClass/Activity
        // SchoolClass - Student/Activity
    }

    /**
     * Returns a matrix with all the ClassCourseValues.
     *
     */
    public void CrawlClassGetCourseStudent() {

    }

    /**
     * Returns a {@Link DomResultPlotMatrix} for the DomResultScoxDomResultStudent
     * in a school class.
     * 
     * 
     * @param tree 
     * @param schoolClass
     */
    public static DomResultPlotMatrix CrawlClassGetScoStudent(ResultTree tree, DomResultSchoolClass schoolClass) {
        DomResultStudent[] studentIndex; //uses label property for display
        DomResultScoContext[] scoIndex; //uses label property for display
        
        //init studentIndex
        Map<PersistenceId, DomResultStudent> studentMap = tree.getStudentTree().getChildren().get(schoolClass.getSchoolClass().getId()).getChildren();        
        Collection studentValues = studentMap.values();
        studentIndex = new DomResultStudent[studentValues.size()];
        int i=0;
        for(DomResultStudent student: studentMap.values()){
            studentIndex[i]= student;
            i++;
        }

        //init scoIndex
        Map<PersistenceId, DomResultScoContext> scoMap = new HashMap<PersistenceId, DomResultScoContext>();
        crawlSubTreeForScos(schoolClass, scoMap);
        Collection scoValues = scoMap.values();            
        scoIndex = new DomResultScoContext[scoValues.size()];
        int j=0;
        for(DomResultScoContext sco: scoMap.values()){
            scoIndex[j]= sco;
            j++;
        }
                
        DomResultPlotMatrix result = new DomResultPlotMatrix(studentIndex, scoIndex);
        //crawl and insert

        return result;
    }

    public void CrawlTeacherGetScoClass() {
    }

    public void CrawlTeacherGetCourseClass() {
    }
}

