package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * ClassCourses of a SchoolClass. This is a different use-case than the 
 * {@Link DomResultsPerTeacher}.
 * 
 * 
 * @author G.A.J. van der Plas  email: G.A.J.vanderPlas@uu.nl
 */
@XmlRootElement
public class DomClassCoursesOfSchoolClass {
    private static Logger LOG = Logger.getLogger(DomClassCoursesOfSchoolClass.class.getName());

    private Long fetchTimeStamp;   
    private DomSchoolClass schoolClass;
    private List<DomMapEntry<PersistenceId,DomClassCourse>> classCourses;

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
    public List<DomMapEntry<PersistenceId, DomClassCourse>> getClassCourses() {
        return classCourses;
    }

    /**
     * @param classCourses the classCourses to set
     */
    public void setClassCourses(List<DomMapEntry<PersistenceId, DomClassCourse>> classCourses) {
        this.classCourses = classCourses;
    }
    
}
