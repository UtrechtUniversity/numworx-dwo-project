package nl.uu.fi.dwo.mobile.client.sco;

public class SCORM_DWOmAccess extends SCORM_guest implements Scorm2004IF {

	private int userID;
	private int scoID;

	public SCORM_DWOmAccess(int userID) {
		this.userID = userID;
	}

	public int getScoID() {
		return scoID;
	}

	public void setScoID(int scoID) {
		this.scoID = scoID;
	}


}
