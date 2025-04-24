package nl.uu.fi.dwo.rest.dom.entities;

public class DomNewStudent extends DomNewUser {
	private String schoolClassName;
	private DomSamlUser samlUser;
	
	public DomNewStudent() {
		
	}
	public DomNewStudent(DomNewUser user, String schoolclass) {
		super(user);
		setSchoolClassName(schoolclass);
	}

	public String getSchoolClassName() {
		return schoolClassName;
	}
	/**
	 * @param schoolClassName the schoolClassName to set
	 */
	public void setSchoolClassName(String schoolClassName) {
		this.schoolClassName = schoolClassName;
	}

	public DomSamlUser getSamlUser() {
		return samlUser;
	}
	public void setSamlUser(DomSamlUser samlUser) {
		this.samlUser = samlUser;
	}
}
