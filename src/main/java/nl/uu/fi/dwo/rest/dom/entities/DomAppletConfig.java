package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@XmlRootElement
public class DomAppletConfig {

	private Integer appletID;
	private String language;
	private String launchdata;
	private String name;
	private PersistenceId id;
	
	private DomDwoProfileId dwoProfileId;
	private DomAppletConfigDataId appletConfigDataId;

	public DomAppletConfig(DomAppletConfig domAppletConfig) {
		appletID = domAppletConfig.appletID;
		language = domAppletConfig.language;
		launchdata = domAppletConfig.launchdata;
		name = domAppletConfig.name;
		id   = domAppletConfig.id.duplicate();
		if(domAppletConfig.dwoProfileId != null)
			dwoProfileId = domAppletConfig.dwoProfileId.duplicate();
		if(domAppletConfig.appletConfigDataId != null)
			appletConfigDataId = domAppletConfig.appletConfigDataId.duplicate();
	}

	public DomAppletConfig() {
	}
	
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

	public DomDwoProfileId getDwoProfileId() {
		return dwoProfileId;
	}

	public void setDwoProfileId(DomDwoProfileId dwoProfileId) {
		this.dwoProfileId = dwoProfileId;
	}

	public DomAppletConfigDataId getAppletConfigDataId() {
		return appletConfigDataId;
	}

	public void setAppletConfigDataId(DomAppletConfigDataId appletConfigDataId) {
		this.appletConfigDataId = appletConfigDataId;
	}

	public DomAppletConfig duplicate() {
		return new DomAppletConfig(this);
	}

}
