package nl.uu.fi.dwo.rest.dom.entities;

import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;

/**
 *
 * @author G.A.J. van der Plas  email: G.A.J.vanderPlas@uu.nl
 * @param <T>
 */
public class DomResultCourse <T extends DomResultScore> extends DomResultScore<T> {
    private DomCourse course;
    private ViewState viewState;

    public DomResultCourse(DomCourse aCourse, ViewState aState){
        course = aCourse;
        viewState = aState;
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

    /**
     * @return the viewState
     */
    public ViewState getViewState() {
        return viewState;
    }

    /**
     * @param viewState the viewState to set
     */
    public void setViewState(ViewState viewState) {
        this.viewState = viewState;
    }
}
