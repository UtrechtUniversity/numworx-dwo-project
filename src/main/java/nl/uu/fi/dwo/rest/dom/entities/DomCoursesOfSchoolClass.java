package nl.uu.fi.dwo.rest.dom.entities;

import java.util.Map;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * ClassCourse and Courses in the subtree of the ClassCourse Courses for a 
 * SchoolClass. This is client a different user case than the {@Link DomResultsPerTeacher}.
 * 
 * 
 * @author G.A.J. van der Plas <G.A.J.vanderPlas@uu.nl>
 */
@XmlRootElement
public class DomCoursesOfSchoolClass {
    private static Logger LOG = Logger.getLogger(DomCoursesOfSchoolClass.class.getName());

    private Long fetchTimeStamp;   
    private DomSchoolClass schoolClass;
    private Map<PersistenceId,DomClassCourse> classCourses;
    private Map<PersistenceId,DomCourse> courses;

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
     * @return the schoolClass
     */
    public DomSchoolClass getSchoolClass() {
        return schoolClass;
    }

    /**
     * @param schoolClass the schoolClass to set
     */
    public void setSchoolClass(DomSchoolClass schoolClass) {
        this.schoolClass = schoolClass;
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

    
}
