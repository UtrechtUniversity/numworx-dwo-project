package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import java.util.List;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;

import dagger.Reusable;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
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

@Reusable
public class PersonsServiceTeacher extends PersonsService {

  private final SecuredTeacherSchoolClassManager manager;
  @Inject
  public PersonsServiceTeacher() {
    this(new SecuredTeacherSchoolClassManager());
  }

  public PersonsServiceTeacher(SecuredTeacherSchoolClassManager securedTeacherSchoolClassManager) {
    manager = securedTeacherSchoolClassManager;
  }

  @Override
  public Promise<List<DomStudent>> getTeachersStudents() {
    return manager.getTeachersStudents();
  }

  @Override
  public Promise<List<DomTeacher>> getTeachersInSchool() {
    return manager.getTeachersInSchool();
  }

  @Override
  public Promise<List<DomSchoolClass>> getTeachersSchoolClasses() {
    return manager.getTeachersSchoolClasses();
  }

  @Override
  public Promise<Boolean> submitSingleSchoolStudent(DomNewSingleSchoolStudent newStudent) {
    return manager.submitSingleSchoolStudent(newStudent);
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
    return manager.removeStudentFromSchoolClass(data);
  }

  @Override
  public Promise<Boolean> submitStudentToSchoolClass(DomSubmitStudentToSchoolClass data) {
    return manager.submitStudentToSchoolClass(data);
  }

  @Override
  public Promise<Boolean> updateSingleSchoolStudent(RestSingleSchoolStudent rest) {
    return manager.updateSingleSchoolStudent(rest);
  }

  @Override
  public Promise<Boolean> submitSchoolClass(DomSchoolClassFull schoolClass) {
    return manager.submitSchoolClass(schoolClass);
  }

  @Override
  public Promise<DomSchoolClassFull> getFullSchoolClass(DomSchoolClass aSchoolClass) {
    return manager.getFullSchoolClass(aSchoolClass);
  }

  @Override
  public Promise<Boolean> updateSchoolClass(DomSchoolClassFull fullSchoolClass) {
    return manager.updateSchoolClass(fullSchoolClass);
  }

  @Override
  public Promise<Boolean> removeSchoolClass(DomSchoolClass schoolClass) {
    return manager.removeSchoolClass(schoolClass);
  }

  @Override
  public Promise<List<DomTeacher>> getTeachersInSchoolClass(DomSchoolClass schoolClass) {
    return manager.getTeachersInSchoolClass(schoolClass);
  }

  @Override
  public Promise<List<DomStudent>> getStudentsInSchoolClass(DomSchoolClass schoolClass) {
    return manager.getStudentsInSchoolClass(schoolClass);
  }

  @Override
  public Promise<DomCoursesOfSchoolClass4Teacher> getModules(DomContext context,
      DomSchoolClassAndProfile sap) {
    return manager.getModules(context, sap);
  }

  @Override
  public Promise<Boolean> submitTeacherToSchoolClass(DomSubmitTeacherToSchoolClass submit) {
    return manager.submitTeacherToSchoolClass(submit);
  }

  @Override
  public Promise<Boolean> moveStudentToSchoolClass(DomMoveStudentToSchoolClass submit) {
    return super.moveStudentToSchoolClass(submit);
  }

  @Override
  public Promise<List<DomSchoolClassId>> getSharedTeacherClasses(RestTeacher rest) {
    return manager.getSharedTeacherClasses(rest);
  }

  @Override
  public Promise<Boolean> removeTeacherFromSchoolClass(DomRemoveTeacherFromSchoolClass data) {
    return manager.removeTeacherFromSchoolClass(data);
  }

}
