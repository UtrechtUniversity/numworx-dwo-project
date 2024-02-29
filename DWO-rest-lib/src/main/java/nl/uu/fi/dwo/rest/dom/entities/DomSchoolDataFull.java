package nl.uu.fi.dwo.rest.dom.entities;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class DomSchoolDataFull extends DomSchoolDataId {

	public DomSchoolDataFull(PersistenceId id) {
		super(id);
	}

	public DomSchoolDataFull() {
	}

	public String getSchoolData() {
		return schoolData;
	}

	public void setSchoolData(String schoolData) {
		this.schoolData = schoolData;
	}

	private String schoolData;
	
}
