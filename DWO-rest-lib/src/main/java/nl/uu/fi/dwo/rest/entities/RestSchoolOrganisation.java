package nl.uu.fi.dwo.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolOrganisation;

@XmlRootElement
public class RestSchoolOrganisation {
    private DomContext restContext;
    private DomSchoolOrganisation domSchoolOrganisation;

    public RestSchoolOrganisation(DomContext context, DomSchoolOrganisation org) {
		this.restContext = context;
		this.domSchoolOrganisation = org;
	}
        
	public RestSchoolOrganisation() {
	}

	public DomContext getRestContext() {
		return restContext;
	}
	public void setRestContext(DomContext restContext) {
		this.restContext = restContext;
	}
	public DomSchoolOrganisation getDomSchoolOrganisation() {
		return domSchoolOrganisation;
	}
	public void setDomSchoolOrganisation(DomSchoolOrganisation domSchoolOrganisation) {
		this.domSchoolOrganisation = domSchoolOrganisation;
	}

}
