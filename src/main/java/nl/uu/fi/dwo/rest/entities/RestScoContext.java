package nl.uu.fi.dwo.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;

@XmlRootElement
public class RestScoContext {
	private DomContext restContext;
	private DomDwoProfile domDwoProfile;
	private DomScoContext domScoContext;

	public DomContext getRestContext() {
		return restContext;
	}
	public void setRestContext(DomContext restContext) {
		this.restContext = restContext;
	}
	public DomDwoProfile getDomDwoProfile() {
		return domDwoProfile;
	}
	public void setDomDwoProfile(DomDwoProfile domDwoProfile) {
		this.domDwoProfile = domDwoProfile;
	}
	public DomScoContext getDomScoContext() {
		return domScoContext;
	}
	public void setDomScoContext(DomScoContext domScoContext) {
		this.domScoContext = domScoContext;
	}
	
}
