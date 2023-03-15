package nl.uu.fi.dwo.rest.dom.entities.util;

import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;

/**
 *
 * @author G.A.J. van der Plas
 */
public class DomCourseInClass extends DomClassCourse {
    private DomCourse course;

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
