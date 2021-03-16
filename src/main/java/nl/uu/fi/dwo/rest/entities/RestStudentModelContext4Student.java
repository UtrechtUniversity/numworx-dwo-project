package nl.uu.fi.dwo.rest.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;

public class RestStudentModelContext4Student {
	private DomContext restContext;
	private DomStudentModelContext4Student domStudentModelContext;
	public DomContext getRestContext() {
		return restContext;
	}
	public void setRestContext(DomContext restContext) {
		this.restContext = restContext;
	}
	public DomStudentModelContext4Student getDomStudentModelContext() {
		return domStudentModelContext;
	}
	public void setDomStudentModelContext(DomStudentModelContext4Student domStudentModelContext) {
		this.domStudentModelContext = domStudentModelContext;
	}
	
	
}
