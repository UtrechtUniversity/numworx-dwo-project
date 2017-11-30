package nl.uu.fi.dwo.rest.dom.entities;

public class DomScoData extends DomId {

	private String launchdata;
	private byte[] launchdatabytes;

	public String getLaunchdata() {
		return launchdata;
	}
	public void setLaunchdata(String launchdata) {
		this.launchdata = launchdata;
	}

	public byte[] getLaunchdatabytes() {
		return launchdatabytes;
	}
	public void setLaunchdatabytes(byte[] launchdatabytes) {
		this.launchdatabytes = launchdatabytes;
	}
	
	
}
