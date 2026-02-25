package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import java.util.List;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomMoveStudentToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomRemoveStudentFromSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomRemoveTeacherFromSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitTeacherToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.entities.RestGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestStudent;
import nl.uu.fi.dwo.rest.entities.RestTeacher;

@RoleScope
public class PersonsServiceTeacher extends PersonsService {

  private final SecuredTeacherSchoolClassManager manager;
  private final DomContext context;
  private final DwoGlobalVars vars;
  @Inject
  public PersonsServiceTeacher(DwoGlobalVars vars) {
    this(new SecuredTeacherSchoolClassManager(), vars);
  }

  private PersonsServiceTeacher(SecuredTeacherSchoolClassManager securedTeacherSchoolClassManager, DwoGlobalVars vars2) {
    manager = securedTeacherSchoolClassManager;
    context = new DomContext();
    vars = vars2;
    context.setDomHasRole(vars2.getActiveSchoolRoleAndClass().getHasRole());
    context.setRealm(vars2.getCurrentLoginContext().getRealm());
  }

  @Override
  public Promise<List<DomStudent>> getTeachersStudents() {
    return manager.getTeachersStudents(context);
  }

  @Override
  public Promise<List<DomTeacher>> getTeachersInSchool() {
    return manager.getTeachersInSchool(context);
  }

  @Override
  public Promise<List<DomSchoolClass>> getTeachersSchoolClasses() {
    return manager.getTeachersSchoolClasses(context);
  }

  @Override
  public Promise<Boolean> submitSingleSchoolStudent(DomNewSingleSchoolStudent newStudent) {
    return manager.submitSingleSchoolStudent(context, newStudent);
  }

  @Override
  public Promise<List<DomSchoolClassId>> getTeachersClassesOfStudent(RestStudent rest) {
    return manager.getTeachersClassesOfStudent(rest);
  }

  @Override
  public Promise<DomSingleSchoolStudent> getSingleSchoolStudent(RestGetSingleSchoolStudent restData) {
    return manager.getSingleSchoolStudent(restData);
  }

  @Override
  public Promise<Boolean> removeStudentFromSchoolClass(DomRemoveStudentFromSchoolClass data) {
    return manager.removeStudentFromSchoolClass(context, data);
  }

  @Override
  public Promise<Boolean> submitStudentToSchoolClass(DomSubmitStudentToSchoolClass data) {
    return manager.submitStudentToSchoolClass(context, data);
  }

  @Override
  public Promise<Boolean> updateSingleSchoolStudent(RestSingleSchoolStudent rest) {
    return manager.updateSingleSchoolStudent(rest);
  }

  @Override
  public Promise<Boolean> submitSchoolClass(DomSchoolClassFull schoolClass) {
    return manager.submitSchoolClass(context, schoolClass);
  }

  @Override
  public Promise<DomSchoolClassFull> getFullSchoolClass(DomSchoolClass aSchoolClass) {
    return manager.getFullSchoolClass(context, aSchoolClass);
  }

  @Override
  public Promise<Boolean> updateSchoolClass(DomSchoolClassFull fullSchoolClass) {
    return manager.updateSchoolClass(context, fullSchoolClass);
  }

  @Override
  public Promise<Boolean> removeSchoolClass(DomSchoolClass schoolClass) {
    return manager.removeSchoolClass(context, schoolClass);
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
  public Promise<DomCoursesOfSchoolClass4Teacher> getModules(DomContext context,
      DomSchoolClassAndProfile sap) {
    return manager.getModules(context, sap);
  }

  @Override
  public Promise<Boolean> submitTeacherToSchoolClass(DomSubmitTeacherToSchoolClass submit) {
    return manager.submitTeacherToSchoolClass(context, submit);
  }

  @Override
  public Promise<Boolean> moveStudentToSchoolClass(DomMoveStudentToSchoolClass submit) {
    return manager.moveStudentToSchoolClass(context, submit);
  }

  @Override
  public Promise<List<DomSchoolClassId>> getSharedTeacherClasses(RestTeacher rest) {
    return manager.getSharedTeacherClasses(rest);
  }

  @Override
  public Promise<Boolean> removeTeacherFromSchoolClass(DomRemoveTeacherFromSchoolClass data) {
    return manager.removeTeacherFromSchoolClass(context, data);
  }
  @Override
  public Promise<Boolean> submitSingleSchoolStudentv2(DomNewSingleSchoolStudent newStudent) {
	return vars.getProfile().then(p -> 
			manager.submitSingleSchoolStudentv2(context, newStudent, p.getValue()));
  } 

}
