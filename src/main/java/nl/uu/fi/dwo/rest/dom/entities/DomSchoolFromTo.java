package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DomSchoolFromTo {
	private List<DomSchoolFrom> schools;
	
	public List<DomSchoolFrom> getSchools() {
		return schools;
	}

	public void setSchools(List<DomSchoolFrom> schools) {
		this.schools = schools;
	}

	public Boolean getAll() {
		return all;
	}

	public void setAll(Boolean all) {
		this.all = all;
	}

	private Boolean all;
}
