/**
 * 
 */
package nl.uu.fi.dwo.rest.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolMethod;

/**
 * @author peterboon
 *
 */
public class RestSchoolMethod {
    private DomContext restContext;
    private DomSchoolMethod domSchoolMethod;

    public DomContext getRestContext() {
		return restContext;
	}
	public void setRestContext(DomContext restContext) {
		this.restContext = restContext;
	}
	public DomSchoolMethod getDomSchoolMethod() {
		return domSchoolMethod;
	}
	public void setDomSchoolMethod(DomSchoolMethod domSchoolMethod) {
		this.domSchoolMethod = domSchoolMethod;
	}

}
