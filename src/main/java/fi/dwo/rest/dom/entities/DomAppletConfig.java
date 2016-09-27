package fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

import fi.dwo.rest.persistence.PersistenceId;

@XmlRootElement
public class DomAppletConfig {

	private Integer appletID;
	private String language;
	private String launchdata;
	private String name;
	private PersistenceId id;

	public Integer getAppletID() {
		return appletID;
	}
	public void setAppletID(Integer appletID) {
		this.appletID = appletID;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getLaunchdata() {
		return launchdata;
	}
	public void setLaunchdata(String launchdata) {
		this.launchdata = launchdata;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public PersistenceId getId() {
		return id;
	}
	public void setId(PersistenceId id) {
		this.id = id;
	}

}
