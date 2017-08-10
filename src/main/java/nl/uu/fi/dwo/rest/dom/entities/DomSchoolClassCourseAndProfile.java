package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author Gert van der Plas
 */
@XmlRootElement
public class DomSchoolClassCourseAndProfile extends DomSchoolClassAndProfile{
    private DomCourse course;

    public DomSchoolClassCourseAndProfile() {
    }

    /**
     * @return the course
     */
    public DomCourse getCourse() {
        return course;
    }

    /**
     * @param course the course to set
     */
    public void setCourse(DomCourse course) {
        this.course = course;
    }
}
