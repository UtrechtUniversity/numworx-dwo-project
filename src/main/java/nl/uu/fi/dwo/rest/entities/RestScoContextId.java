package nl.uu.fi.dwo.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;

@XmlRootElement
public class RestScoContextId {
	private DomContext restContext;
	private DomDwoProfileId domDwoProfile;
	private DomScoContextId domScoContext;
	private DomSchoolClassId schoolClassID;

	public DomContext getRestContext() {
		return restContext;
	}
	public void setRestContext(DomContext restContext) {
		this.restContext = restContext;
	}
	public DomDwoProfileId getDomDwoProfile() {
		return domDwoProfile;
	}
	public void setDomDwoProfile(DomDwoProfileId domDwoProfile) {
		this.domDwoProfile = domDwoProfile;
	}
	public DomScoContextId getDomScoContext() {
		return domScoContext;
	}
	public void setDomScoContext(DomScoContextId domScoContext) {
		this.domScoContext = domScoContext;
	}
	/**
	 * @return the schoolClassID
	 */
	public DomSchoolClassId getSchoolClassID() {
		return schoolClassID;
	}
	/**
	 * @param schoolClassID the schoolClassID to set
	 */
	public void setSchoolClassID(DomSchoolClassId schoolClassID) {
		this.schoolClassID = schoolClassID;
	}
	
}
