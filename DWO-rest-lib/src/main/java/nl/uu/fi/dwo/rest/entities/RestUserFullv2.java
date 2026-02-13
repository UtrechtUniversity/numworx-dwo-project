package nl.uu.fi.dwo.rest.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;

public class RestUserFullv2 extends RestUserFull {
	
	private DomDwoProfileId dwoProfile;

	public RestUserFullv2() {
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
