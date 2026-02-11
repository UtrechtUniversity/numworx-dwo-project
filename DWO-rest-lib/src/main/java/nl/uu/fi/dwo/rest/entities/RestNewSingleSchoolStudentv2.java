package nl.uu.fi.dwo.rest.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;

public class RestNewSingleSchoolStudentv2 extends RestNewSingleSchoolStudent {
	
	private DomDwoProfileId dwoProfile;

	public RestNewSingleSchoolStudentv2() {
	}

	/**
	 * @return the dwoProfile
	 */
	public DomDwoProfileId getDwoProfile() {
		return dwoProfile;
	}

	/**
	 * @param dwoProfile the dwoProfile to set
	 */
	public void setDwoProfile(DomDwoProfileId dwoProfile) {
		this.dwoProfile = dwoProfile;
	}

}
