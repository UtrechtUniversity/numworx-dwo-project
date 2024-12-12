package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import java.util.List;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredSchoolAdminSchoolClassManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredSchoolAdminSchoolManager;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomMoveStudentToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomRemoveStudentFromSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomRemoveTeacherFromSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolOrganisation;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitTeacherToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.entities.RestGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestStudent;
import nl.uu.fi.dwo.rest.entities.RestTeacher;

@RoleScope
public class PersonsServiceSchoolAdmin extends PersonsService {

  @Override
  public Promise<Boolean> submitSchoolClass(DomSchoolClassFull schoolClass) {
    return manager.submitSchoolClass(context, schoolClass);
  }

  @Override
  public Promise<List<DomSchoolClass>> getTeachersSchoolClasses() {
    return manager.getSchoolClasses(context);
  }

  @Override
  public Promise<Boolean> submitSingleSchoolStudent(DomNewSingleSchoolStudent newStudent) {
    return manager.submitSingleSchoolStudent(context, newStudent);
  }

  final SecuredSchoolAdminSchoolClassManager manager;
  final SecuredSchoolAdminSchoolManager manager2;
  final DomContext context;
  
  @Inject PersonsServiceSchoolAdmin(DomContext context) {
    manager = new SecuredSchoolAdminSchoolClassManager();
    manager2 = new SecuredSchoolAdminSchoolManager();
    this.context = context;
  }

  @Override
  public Promise<List<DomStudent>> getTeachersStudents() {
    return manager.getStudentsInSchool(context);
  }

  @Override
  public Promise<List<DomTeacher>> getTeachersInSchool() {
    return manager.getTeachersInSchool(context);
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
  public Promise<Boolean> submitTeacherToSchoolClass(DomSubmitTeacherToSchoolClass submit) {
    return manager.submitTeacherToSchoolClass(context, submit);
  }

  public Promise<List<DomSchoolClassId>> getSharedTeacherClasses(DomTeacher rest) {
    return manager2.getTeachersSchoolClasses(context, rest);
  }
  
  @Override
  @Deprecated
  public Promise<List<DomSchoolClassId>> getSharedTeacherClasses(RestTeacher rest) {
    return getSharedTeacherClasses(rest.getDomTeacher());
  }

  @Override
  public Promise<Boolean> removeTeacherFromSchoolClass(DomRemoveTeacherFromSchoolClass data) {
    return manager.removeTeacherFromSchoolClass(context, data);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override @Deprecated
  public Promise<List<DomSchoolClassId>> getTeachersClassesOfStudent(RestStudent rest) {
    return manager2.getStudentsSchoolClasses(context, rest.getDomStudent()).map(v -> (List)v );
  }

  @Override @Deprecated
  public Promise<DomSingleSchoolStudent> getSingleSchoolStudent(
      RestGetSingleSchoolStudent restData) {
    return getSingleSchoolStudent(restData.getDomGetSingleSchoolStudent());
  }
  
  public Promise<DomSingleSchoolStudent> getSingleSchoolStudent(DomGetSingleSchoolStudent student) {
    return manager2.getSingleSchoolStudent(context, student);
  }
  
  @Override
  public Promise<Boolean> removeStudentFromSchoolClass(DomRemoveStudentFromSchoolClass data) {
    return manager.removeStudentFromSchoolClass(context, data);
  }

  @Override
  public Promise<Boolean> submitStudentToSchoolClass(DomSubmitStudentToSchoolClass data) {
    return manager.submitStudentToSchoolClass(context, data);
  }

  @Override @Deprecated
  public Promise<Boolean> updateSingleSchoolStudent(RestSingleSchoolStudent rest) {
    return updateSingleSchoolStudent(rest.getDomSingleSchoolStudent());
  }

  public Promise<Boolean> updateSingleSchoolStudent(DomSingleSchoolStudent student) {
    return manager2.updateSingleSchoolStudent(context, student);
  }

  @Override
  public Promise<Boolean> moveStudentToSchoolClass(DomMoveStudentToSchoolClass submit) {
    return manager.moveStudentToSchoolClass(context, submit);
  }

  @Override
  public Promise<Boolean> submitTeacher(DomUserFull newUser) {
    return manager2.submitTeacher(context, newUser);
  }

  public Promise<List<DomSchoolAdmin>> getSchoolAdminsInSchool() {
     return manager2.getSchoolAdminsInSchool(context);
  }

  public Promise<Boolean> updateSchool(DomSchoolFull school) {
    return manager2.updateSchool(context, school);
  }

  public Promise<Boolean> removeStudentFromSchool(DomStudent student) {
    return manager2.removeStudentFromSchool(context, student);
  }

  public Promise<Boolean> removeSingleSchoolStudentFromSchool(DomStudent student) {
    return manager2.removeSingleSchoolStudentFromSchool(context, student);
  }

  public Promise<Boolean> removeTeacherFromSchool(DomTeacher user) {
    return manager2.removeTeacherFromSchool(context, user);
  }

  public Promise<Boolean> removeSchoolAdminFromSchool(DomSchoolAdmin user) {
    return manager2.removeSchoolAdminFromSchool(context, user);
  }

public Promise<DomSchoolOrganisation> getStudentsInSchool(DomSchoolOrganisation domSchoolOrganisation) {
	return manager2.getStudentsInSchool(context, domSchoolOrganisation);
} 
  
}
