package nl.uu.fi.dwo.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextFull;
import nl.uu.fi.dwo.rest.dom.entities.DomScoData;

/**
 * RestScoContextFull contains a DomScoContextFull info.
 * 
 * @author W.P.G. van Velthoven
 */
@XmlRootElement
public class RestScoContextFull {

	private DomContext restContext;
	private DomScoContextFull domScoContext;
	private DomDwoProfile domDwoProfile;
	private DomScoData domScoData;
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
	public DomScoContextFull getDomScoContext() {
		return domScoContext;
	}

	public void setDomScoContext(DomScoContextFull domScoContext) {
		this.domScoContext = domScoContext;
	}
	public DomDwoProfile getDomDwoProfile() {
		return domDwoProfile;
	}
	public void setDomDwoProfile(DomDwoProfile domDwoProfile) {
		this.domDwoProfile = domDwoProfile;
	}
	public DomScoData getDomScoData() {
		return domScoData;
	}
	public void setDomScoData(DomScoData domScoData) {
		this.domScoData = domScoData;
	}
}
