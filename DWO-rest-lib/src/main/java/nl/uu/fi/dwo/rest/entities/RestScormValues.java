package nl.uu.fi.dwo.rest.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomScormValues;

public class RestScormValues {
	private DomContext restContext;
	private DomScormValues domScormValues;
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
	 * @return the domScormValues
	 */
	public DomScormValues getDomScormValues() {
		return domScormValues;
	}
	/**
	 * @param domScormValues the domScormValues to set
	 */
	public void setDomScormValues(DomScormValues domScormValues) {
		this.domScormValues = domScormValues;
	}
	
}
