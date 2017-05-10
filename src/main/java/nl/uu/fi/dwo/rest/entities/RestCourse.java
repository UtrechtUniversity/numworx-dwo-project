package nl.uu.fi.dwo.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;

/**
 * RestCourse contains a minimal DwoCourse info.
 * 
 * @author W.P.G. van Velthoven
 */
@XmlRootElement
public class RestCourse {

	private DomContext restContext;
	private DomCourse domCourse;
	private DomDwoProfile domDwoProfile; // context parameter
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
	public DomCourse getDomCourse() {
		return domCourse;
	}
	/**
	 * @param domCourse the domCourse to set
	 */
	public void setDomCourse(DomCourse domCourse) {
		this.domCourse = domCourse;
	}
	/**
	 * @return the domDwoProfile
	 */
	public DomDwoProfile getDomDwoProfile() {
		return domDwoProfile;
	}
	/**
	 * @param domDwoProfile the domDwoProfile to set
	 */
	public void setDomDwoProfile(DomDwoProfile domDwoProfile) {
		this.domDwoProfile = domDwoProfile;
	}
}
