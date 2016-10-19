package nl.uu.fi.dwo.rest.dom.entities;

/**
 *
 * @author G.A.J. van der Plas <G.A.J.vanderPlas@uu.nl>
 */
public class DomResultCourse extends DomResultScore{
    private DomCourse course;

        void DomResultCourse(DomCourse aCourse){
        course = aCourse;
        super.setLabel(course.getName());
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
