package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Collected sparse results of Students that are in one or more SchoolClasses of
 * a Teacher.
 *
 * The information in this class is inserted client-side into a simplified
 * kd-range tree. The kd-tree has a search range of 1 and has a node type from
 * root to leave a sequence of: DomTeacher, DomSchoolClass, DomClassCourse
 * referred DomCourse,DomCourse, ..., DomCourse. A leave of the kd-tree is by
 * definition a course-leave.
 *
 * @author G.A.J. van der Plas email: G.A.J.vanderPlas@uu.nl
 */
@XmlRootElement
public class DomStudentModelScorePerTeacher {

	private DomTeacher teacher;
	private Long fetchTimeStamp;
	private List<DomMapEntry<PersistenceId, DomStudent>> students;
	private List<DomMapEntry<PersistenceId, DomStudentOfClass>> studentsOfClasses;
	private List<DomMapEntry<PersistenceId, DomSchoolClass>> schoolClasses;
	private List<DomStudentModelDataStudentScore> studentScores;
	private List<DomMapEntry<PersistenceId, DomStudentModelContext>> studentModelContexts;

	/**
	 * @return the teacher
	 */
	public DomTeacher getTeacher() {
		return teacher;
	}

	/**
	 * Teacher whose class results are collected.
	 *
	 * @param teacher the teacher to set
	 */
	public void setTeacher(DomTeacher teacher) {
		this.teacher = teacher;
	}

	/**
	 * The UT timestamp defining when the result collection was started on the
	 * server.
	 *
	 * @return the fetchTimeStamp
	 */
	public Long getFetchTimeStamp() {
		return fetchTimeStamp;
	}

	/**
	 * Setting UT timestamp which defines when the result collection was started on
	 * the server.
	 *
	 * @param fetchTimeStamp the fetchTimeStamp to set
	 */
	public void setFetchTimeStamp(Long fetchTimeStamp) {
		this.fetchTimeStamp = fetchTimeStamp;
	}

	/**
	 * Returns a map of all the students of the teacher. The hash is the
	 * PersistenceId of a student. A student of the teacher is a student that is
	 * registered to in one or more of his classes.
	 *
	 * @return the students
	 */
	public List<DomMapEntry<PersistenceId, DomStudent>> getStudents() {
		return students;
	}

	/**
	 * @param aStudents the students to set
	 */
	public void setStudents(List<DomMapEntry<PersistenceId, DomStudent>> aStudents) {
		this.students = aStudents;
	}

	/**
	 * Returns all the classes to which the teacher is registered as a member in his
	 * school. The hash is the PersistenceId of a school class.
	 *
	 * @return the schoolClasses
	 */
	public List<DomMapEntry<PersistenceId, DomSchoolClass>> getSchoolClasses() {
		return schoolClasses;
	}

	/**
	 * @param aSchoolClasses the schoolClasses to set
	 */
	public void setSchoolClasses(List<DomMapEntry<PersistenceId, DomSchoolClass>> aSchoolClasses) {
		this.schoolClasses = aSchoolClasses;
	}

	/**
	 * Returns the StudentOfClass data that tells which student links to which
	 * school class.
	 *
	 * @return the studentsOfClasses
	 */
	public List<DomMapEntry<PersistenceId, DomStudentOfClass>> getStudentsOfClasses() {
		return studentsOfClasses;
	}

	/**
	 * @param aStudentsOfClasses the studentsOfClasses to set
	 */
	public void setStudentsOfClasses(List<DomMapEntry<PersistenceId, DomStudentOfClass>> aStudentsOfClasses) {
		this.studentsOfClasses = aStudentsOfClasses;
	}

	public List<DomStudentModelDataStudentScore> getStudentScores() {
		return studentScores;
	}

	public void setStudentScores(List<DomStudentModelDataStudentScore> list) {
		this.studentScores = list;
	}

	public List<DomMapEntry<PersistenceId, DomStudentModelContext>> getStudentModelContexts() {
		return studentModelContexts;
	}

	public void setStudentModelContexts(List<DomMapEntry<PersistenceId, DomStudentModelContext>> list) {
		this.studentModelContexts = list;

	}

}
