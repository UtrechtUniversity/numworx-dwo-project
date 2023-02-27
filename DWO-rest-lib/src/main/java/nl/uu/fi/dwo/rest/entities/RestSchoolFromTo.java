package nl.uu.fi.dwo.rest.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFromTo;

public class RestSchoolFromTo {
	private DomContext restContext;
	private DomSchoolFromTo schoolFromTo;

	public DomContext getRestContext() {
		return restContext;
	}
	public void setRestContext(DomContext restContext) {
		this.restContext = restContext;
	}
	public DomSchoolFromTo getSchoolFromTo() {
		return schoolFromTo;
	}
	public void setSchoolFromTo(DomSchoolFromTo schoolFromTo) {
		this.schoolFromTo = schoolFromTo;
	}
	
}
