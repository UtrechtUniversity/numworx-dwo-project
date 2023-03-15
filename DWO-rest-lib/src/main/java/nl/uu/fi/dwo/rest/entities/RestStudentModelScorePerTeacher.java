package nl.uu.fi.dwo.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScorePerTeacher;

@XmlRootElement
public class RestStudentModelScorePerTeacher {

	public RestStudentModelScorePerTeacher() {
	}

	public RestStudentModelScorePerTeacher(DomContext restContext,
			DomStudentModelScorePerTeacher domStudentModelScorePerTeacher) {
		this.restContext = restContext;
		this.setDomStudentModelScorePerTeacher(domStudentModelScorePerTeacher);
	}

	private DomContext restContext;
	private DomStudentModelScorePerTeacher domStudentModelScorePerTeacher;

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

	public DomStudentModelScorePerTeacher getDomStudentModelScorePerTeacher() {
		return domStudentModelScorePerTeacher;
	}

	public void setDomStudentModelScorePerTeacher(DomStudentModelScorePerTeacher domStudentModelScorePerTeacher) {
		this.domStudentModelScorePerTeacher = domStudentModelScorePerTeacher;
	}

}
