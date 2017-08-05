package nl.uu.fi.dwo.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;

/**
 * RestCourse contains a minimal DwoCourse info.
 * 
 * @author W.P.G. van Velthoven
 */
@XmlRootElement
public class RestCourseFull {

	private DomContext restContext;
	private DomCourseFull domCourse;
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
	 * @return the domCourse
	 */
	public DomCourseFull getDomCourse() {
		return domCourse;
	}
	/**
	 * @param domCourse the domCourse to set
	 */
	public void setDomCourse(DomCourseFull domCourse) {
		this.domCourse = domCourse;
	}
}
