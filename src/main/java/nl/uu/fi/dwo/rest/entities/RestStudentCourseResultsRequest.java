package nl.uu.fi.dwo.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;

@XmlRootElement
public class RestStudentCourseResultsRequest {

	private DomContext restContext;
	private DomCourse  domCourse;

	public DomContext getRestContext() {
		return restContext;
	}

	public void setRestContext(DomContext restContext) {
		this.restContext = restContext;
	}

	public DomCourse getDomCourse() {
		return domCourse;
	}

	public void setDomCourse(DomCourse domCourse) {
		this.domCourse = domCourse;
	}

	public RestStudentCourseResultsRequest() {
	}

}
