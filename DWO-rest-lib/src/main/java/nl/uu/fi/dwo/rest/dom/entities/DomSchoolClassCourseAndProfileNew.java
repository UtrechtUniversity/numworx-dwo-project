package nl.uu.fi.dwo.rest.dom.entities;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;


/**
 *
 * @author Gert van der Plas
 */
@XmlRootElement
public class DomSchoolClassCourseAndProfileNew extends DomSchoolClassAndProfile{
    private DomCourse course;
    private CourseType courseType;
    private Date from;
    private Date to;
    private String accessKey;
    private ViewState viewState;

    public DomSchoolClassCourseAndProfileNew() {
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
     * @return the from
     */
    public Date getFrom() {
        return from;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(Date from) {
        this.from = from;
    }

    /**
     * @return the to
     */
    public Date getTo() {
        return to;
    }

    /**
     * @param to the to to set
     */
    public void setTo(Date to) {
        this.to = to;
    }

    /**
     * @return the accessKey
     */
    public String getAccessKey() {
        return accessKey;
    }

    /**
     * @param accessKey the accessKey to set
     */
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    /**
     * @return the courseType
     */
    public CourseType getCourseType() {
        return courseType;
    }

    /**
     * @param courseType the courseType to set
     */
    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
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
