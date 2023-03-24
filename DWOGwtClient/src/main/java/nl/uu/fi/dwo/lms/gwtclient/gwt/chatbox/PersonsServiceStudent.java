package nl.uu.fi.dwo.lms.gwtclient.gwt.chatbox;

import java.util.List;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentSchoolClassManager;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsService;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomMoveStudentToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitTeacherToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;

@RoleScope
public class PersonsServiceStudent extends PersonsService {

	final DomContext context;
	final SecuredStudentSchoolClassManager manager;
	
	
	@Inject PersonsServiceStudent(DomContext context) {
		this.context = context;
		this.manager = new SecuredStudentSchoolClassManager();
	}

	@Override
	public Promise<List<DomStudent>> getTeachersStudents() {
		return null;
	}

	@Override
	public Promise<List<DomTeacher>> getTeachersInSchool() {
		return null;
	}

	@Override
	public Promise<List<DomSchoolClass>> getTeachersSchoolClasses() {
		return manager.getStudentsSchoolClasses(context);
	}

	@Override
	public Promise<List<DomTeacher>> getTeachersInSchoolClass(DomSchoolClass schoolClass) {
		return manager.getTeachersInSchoolClass(context, schoolClass);
	}

	@Override
	public Promise<List<DomStudent>> getStudentsInSchoolClass(DomSchoolClass schoolClass) {
		return manager.getStudentsInSchoolClass(context, schoolClass);
	}

	@Override
	public Promise<Boolean> submitTeacherToSchoolClass(DomSubmitTeacherToSchoolClass submit) {
		return null;
	}

	@Override
	public Promise<Boolean> moveStudentToSchoolClass(DomMoveStudentToSchoolClass submit) {
		return null;
	}

}
