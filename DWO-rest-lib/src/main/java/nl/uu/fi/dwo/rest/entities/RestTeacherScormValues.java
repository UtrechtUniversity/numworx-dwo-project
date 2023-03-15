package nl.uu.fi.dwo.rest.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacherScormValues;

public class RestTeacherScormValues {
	private DomContext restContext;
	private DomTeacherScormValues domTeacherScormValues;
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
	 * @return the domTeacherScormValues
	 */
	public DomTeacherScormValues getDomTeacherScormValues() {
		return domTeacherScormValues;
	}
	/**
	 * @param domTeacherScormValues the domTeacherScormValues to set
	 */
	public void setDomTeacherScormValues(DomTeacherScormValues domTeacherScormValues) {
		this.domTeacherScormValues = domTeacherScormValues;
	}
}
