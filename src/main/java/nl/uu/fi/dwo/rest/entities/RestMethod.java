package nl.uu.fi.dwo.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;

@XmlRootElement
public class RestMethod {
    private DomContext restContext;
    private DomMethod domMethod;

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
	 * @return the domMethod
	 */
	public DomMethod getDomMethod() {
		return domMethod;
	}
	/**
	 * @param domMethod the domMethod to set
	 */
	public void setDomMethod(DomMethod domMethod) {
		this.domMethod = domMethod;
	}

}
