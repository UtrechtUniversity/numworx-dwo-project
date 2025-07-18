package nl.uu.fi.dwo.rest.dom.entities.util;

/**
 * Visibility of course for students, teachers and results. Used in 
 * {@link fi.dwo.commons.persistence.entities.PersistentClassCourse PersistentClassCourse}.
 * 
 * @author Gert van der Plas
 */
public enum ViewState {
    invisible,
    students,
    teachers,
    studentsAndTeachers,
    
    none,
    onlyStudents,
    studentsNorTeachers,
    studentsOrTeachers;
	
	/**
	 * visible for students in modules.
	 * @return
	 */
	public boolean moduleVisible() {
		return this == studentsAndTeachers || this == onlyStudents || this == studentsOrTeachers;
	}
    
}
