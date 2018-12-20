package nl.uu.fi.dwo.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.dom.entities.DomClassCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;

/**
 * RestCourse contains a minimal DwoCourse info.
 * 
 * @author W.P.G. van Velthoven
 */
@XmlRootElement
public class RestClassCourseFull {

	private DomContext restContext;
	private DomClassCourseFull domClassCourse;
	/**
	 * @return the restContext
	 */
	public DomContext getRestContext() {
		return restContext;
	}
	/**
	 * @param restContext the restContext to set
	 */
	public void setRestContext(DomContext restContext) {
		this.restContext = restContext;
	}
	/**
	 * @return the domClassCourse
	 */
	public DomClassCourseFull getDomCourse() {
		return domClassCourse;
	}
	/**
	 * @param domClassCourse the domCourse to set
	 */
	public void setDomCourse(DomClassCourseFull domClassCourse) {
		this.domClassCourse = domClassCourse;
	}
}
