package nl.uu.fi.dwo.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.dom.entities.DomAppletId;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;

@XmlRootElement
public class RestAppletId {
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
	 * @return the domAppletId
	 */
	public DomAppletId getDomAppletId() {
		return domAppletId;
	}
	/**
	 * @param domAppletId the domAppletId to set
	 */
	public void setDomAppletId(DomAppletId domAppletId) {
		this.domAppletId = domAppletId;
	}

	private DomContext restContext;
	private DomAppletId domAppletId;

}
