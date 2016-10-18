package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;

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
    private List<DomStudent> students;
    private List<DomSchoolClass> schoolClasses;
    private List<DomClassCourse> classCourses;
    private List<DomCourse> courses;
    private List<DomScoContext> scoContexts;
    private List<DomStudentScoContext> studentScoContext;    

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
    public List<DomStudent> getStudents() {
        return students;
    }

    /**
     * @param students the students to set
     */
    public void setStudents(List<DomStudent> students) {
        this.students = students;
    }

    /**
     * @return the schoolClasses
     */
    public List<DomSchoolClass> getSchoolClasses() {
        return schoolClasses;
    }

    /**
     * @param schoolClasses the schoolClasses to set
     */
    public void setSchoolClasses(List<DomSchoolClass> schoolClasses) {
        this.schoolClasses = schoolClasses;
    }

    /**
     * @return the scoContexts
     */
    public List<DomScoContext> getScoContexts() {
        return scoContexts;
    }

    /**
     * @param scoContexts the scoContexts to set
     */
    public void setScoContexts(List<DomScoContext> scoContexts) {
        this.scoContexts = scoContexts;
    }

    /**
     * @return the studentScoContext
     */
    public List<DomStudentScoContext> getStudentScoContext() {
        return studentScoContext;
    }

    /**
     * @param studentScoContext the studentScoContext to set
     */
    public void setStudentScoContext(List<DomStudentScoContext> studentScoContext) {
        this.studentScoContext = studentScoContext;
    }

    /**
     * @return the courses
     */
    public List<DomCourse> getCourses() {
        return courses;
    }

    /**
     * @param courses the courses to set
     */
    public void setCourses(List<DomCourse> courses) {
        this.courses = courses;
    }

    /**
     * @return the classCourses
     */
    public List<DomClassCourse> getClassCourses() {
        return classCourses;
    }

    /**
     * @param classCourses the classCourses to set
     */
    public void setClassCourses(List<DomClassCourse> classCourses) {
        this.classCourses = classCourses;
    }
    
}
