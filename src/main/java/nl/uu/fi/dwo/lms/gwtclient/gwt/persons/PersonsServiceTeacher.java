package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import java.util.List;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;

import dagger.Reusable;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomRemoveStudentFromSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.entities.RestGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestStudent;

@Reusable
public class PersonsServiceTeacher extends PersonsService {

  private final SecuredTeacherSchoolClassManager manager;
  @Inject PersonsServiceTeacher() {
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
  
  
}
