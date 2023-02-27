package nl.uu.fi.dwo.rest.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;

public class RestClassCourse {
	private DomContext restContext;
	private DomClassCourse domClassCourse;
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
	 * @return the domClassCourse
	 */
	public DomClassCourse getDomClassCourse() {
		return domClassCourse;
	}
	/**
	 * @param domClassCourse the domClassCourse to set
	 */
	public void setDomClassCourse(DomClassCourse domClassCourse) {
		this.domClassCourse = domClassCourse;
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
