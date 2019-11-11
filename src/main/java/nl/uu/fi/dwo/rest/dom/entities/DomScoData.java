package nl.uu.fi.dwo.rest.dom.entities;

public class DomScoData extends DomId {

	private String launchdata;
	private byte[] launchdatabytes;
	private String description;

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
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
	
	
}
