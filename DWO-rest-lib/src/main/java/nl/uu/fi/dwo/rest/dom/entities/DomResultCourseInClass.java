package nl.uu.fi.dwo.rest.dom.entities;

import java.util.Collection;

import nl.uu.fi.dwo.rest.dom.entities.util.DomResultScoreVisitor;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;

/**
 *
 * @author G.A.J. van der Plas email: G.A.J.vanderPlas@uu.nl
 * @param <T>
 */
public class DomResultCourseInClass extends DomResultScore<DomResultScoContext> {

    private DomCourse course;
    private ViewState viewState;

    public DomResultCourseInClass(DomCourse aCourse, ViewState aState) {
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

    /** Sets the ViewState for all descendents and ancestors of the node. 
     * 
     * @param state 
     */
    public void updateViewState(ViewState state) {
        if (this instanceof DomResultCourseInClass) {
            this.viewState = state;
            this.updateViewStateOfAncestors(state);
//            this.updateViewStateOfDecendants(state);
        }
    }

//    private void updateViewStateOfDecendants(ViewState state) {
//        for (DomResultCourseInClass cic : (Collection<DomResultCourseInClass>) this.getChildren().values()) {
//            cic.updateViewState(state);
//        }
//    }

    protected void updateViewStateOfAncestors(ViewState state) {
        while(this.getParent() instanceof DomResultCourseInClass){
            ((DomResultCourseInClass) this.getParent()).updateViewStateOfAncestors(state);            
        }
    }

    @Override
    public String getId() {
      return getCourse().getId().getIdString();
    }

	@Override
	public void visit(DomResultScoreVisitor v) {
		v.visitCourseInClass(this);		
	}

}
