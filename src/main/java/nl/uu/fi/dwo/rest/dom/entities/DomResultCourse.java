package nl.uu.fi.dwo.rest.dom.entities;

/**
 *
 * @author G.A.J. van der Plas  email: G.A.J.vanderPlas@uu.nl
 * @param <T>
 */
public class DomResultCourse <T extends DomResultScore> extends DomResultScore<T> {
    private DomCourse course;

    public DomResultCourse(DomCourse aCourse){
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
