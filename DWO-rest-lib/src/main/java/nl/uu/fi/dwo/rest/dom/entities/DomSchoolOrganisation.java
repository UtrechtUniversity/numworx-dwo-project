package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;

public class DomSchoolOrganisation {
	
	private Long skip, limit;
	
	private List<DomStudent> students;
    private List<DomStudentOfClass> studentsOfClasses;
	private List<DomSchoolClass> schoolClasses;
 
    public Long getSkip() {
		return skip;
	}
	public void setSkip(Long skip) {
		this.skip = skip;
	}
	public Long getLimit() {
		return limit;
	}
	public void setLimit(Long limit) {
		this.limit = limit;
	}
	public List<DomStudent> getStudents() {
		return students;
	}
	public void setStudents(List<DomStudent> students) {
		this.students = students;
	}
	public List<DomStudentOfClass> getStudentsOfClasses() {
		return studentsOfClasses;
	}
	public void setStudentsOfClasses(List<DomStudentOfClass> studentsOfClasses) {
		this.studentsOfClasses = studentsOfClasses;
	}
	public List<DomSchoolClass> getSchoolClasses() {
		return schoolClasses;
	}
	public void setSchoolClasses(List<DomSchoolClass> schoolClasses) {
		this.schoolClasses = schoolClasses;
	}
}
