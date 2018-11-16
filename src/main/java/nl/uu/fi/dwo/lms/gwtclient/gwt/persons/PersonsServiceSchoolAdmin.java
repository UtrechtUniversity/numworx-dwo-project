package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import java.util.List;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;

import dagger.Reusable;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredSchoolAdminSchoolClassManager;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;

@Reusable
public class PersonsServiceSchoolAdmin extends PersonsService {

  @Override
  public Promise<List<DomSchoolClass>> getTeachersSchoolClasses() {
    return manager.getSchoolClasses();
  }

  @Override
  public Promise<Boolean> submitSingleSchoolStudent(DomNewSingleSchoolStudent newStudent) {
    return manager.submitSingleSchoolStudent(newStudent);
  }

  final SecuredSchoolAdminSchoolClassManager manager;
  
  @Inject PersonsServiceSchoolAdmin() {
    manager = new SecuredSchoolAdminSchoolClassManager();  
  }

  @Override
  public Promise<List<DomStudent>> getTeachersStudents() {
    return manager.getStudentsInSchool();
  }

  @Override
  public Promise<List<DomTeacher>> getTeachersInSchool() {
    return manager.getTeachersInSchool();
  }
  
}
