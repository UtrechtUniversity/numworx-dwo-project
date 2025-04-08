package nl.uu.fi.dwo.rest.dom.entities;

public class DomNewStudent extends DomNewUser {
	private String schoolClassName;
	
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
}
