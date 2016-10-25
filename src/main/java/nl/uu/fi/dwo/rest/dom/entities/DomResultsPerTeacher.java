package nl.uu.fi.dwo.rest.dom.entities;

import java.util.Map;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Collected sparse results of Students that are in one or more SchoolClasses of a Teacher.
 * 
 * The information in this class is inserted client-side into a simplified kd-range 
 * tree. The kd-tree has a search range of 1 and has a node type from root to 
 * leave a sequence of: DomTeacher, DomSchoolClass, DomClassCourse referred 
 * DomCourse,DomCourse, ..., DomCourse. A leave of the kd-tree is by definition 
 * a course-leave. 
 * 
 * @author G.A.J. van der Plas <G.A.J.vanderPlas@uu.nl>
 */
@XmlRootElement
public class DomResultsPerTeacher {
    private static final Logger LOG = Logger.getLogger(DomResultsPerTeacher.class.getName());
    
    private DomTeacher teacher;
    private Long fetchTimeStamp;   
    private Map<PersistenceId,DomStudent> students;
    private Map<PersistenceId,DomSchoolClass> schoolClasses;
    private Map<PersistenceId,DomClassCourse> classCourses;
    private Map<PersistenceId,DomCourse> courses;
    private Map<PersistenceId,DomScoContext> scoContexts;
    private Map<PersistenceId,DomStudentScoContext> studentScoContexts;    

    /**
     * @return the teacher
     */
    public DomTeacher getTeacher() {
        return teacher;
    }

    /**
     * @param teacher the teacher to set
     */
    public void setTeacher(DomTeacher teacher) {
        this.teacher = teacher;
    }

    /**
     * @return the fetchTimeStamp
     */
    public Long getFetchTimeStamp() {
        return fetchTimeStamp;
    }

    /**
     * @param fetchTimeStamp the fetchTimeStamp to set
     */
    public void setFetchTimeStamp(Long fetchTimeStamp) {
        this.fetchTimeStamp = fetchTimeStamp;
    }

    /**
     * @return the students
     */
    public Map<PersistenceId,DomStudent> getStudents() {
        return students;
    }

    /**
     * @param students the students to set
     */
    public void setStudents(Map<PersistenceId,DomStudent> students) {
        this.students = students;
    }

    /**
     * @return the schoolClasses
     */
    public Map<PersistenceId,DomSchoolClass> getSchoolClasses() {
        return schoolClasses;
    }

    /**
     * @param schoolClasses the schoolClasses to set
     */
    public void setSchoolClasses(Map<PersistenceId,DomSchoolClass> schoolClasses) {
        this.schoolClasses = schoolClasses;
    }

    /**
     * @return the scoContexts
     */
    public Map<PersistenceId,DomScoContext> getScoContexts() {
        return scoContexts;
    }

    /**
     * @param scoContexts the scoContexts to set
     */
    public void setScoContexts(Map<PersistenceId,DomScoContext> scoContexts) {
        this.scoContexts = scoContexts;
    }

    /**
     * @return the studentScoContexts
     */
    public Map<PersistenceId,DomStudentScoContext> getStudentScoContexts() {
        return studentScoContexts;
    }

    /**
     * @param studentScoContexts the studentScoContexts to set
     */
    public void setStudentScoContexts(Map<PersistenceId,DomStudentScoContext> studentScoContexts) {
        this.studentScoContexts = studentScoContexts;
    }

    /**
     * @return the courses
     */
    public Map<PersistenceId,DomCourse> getCourses() {
        return courses;
    }

    /**
     * @param courses the courses to set
     */
    public void setCourses(Map<PersistenceId,DomCourse> courses) {
        this.courses = courses;
    }

    /**
     * @return the classCourses
     */
    public Map<PersistenceId,DomClassCourse> getClassCourses() {
        return classCourses;
    }

    /**
     * @param classCourses the classCourses to set
     */
    public void setClassCourses(Map<PersistenceId,DomClassCourse> classCourses) {
        this.classCourses = classCourses;
    }
    
}
