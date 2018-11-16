package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import java.util.List;

import org.osgi.util.promise.Promise;

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

public abstract class PersonsService {

  public Promise<List<DomStudent>> getTeachersStudents() {
    // TODO Auto-generated method stub
    return null;
  }

  public Promise<List<DomTeacher>> getTeachersInSchool() {
    // TODO Auto-generated method stub
    return null;
  }

  public Promise<List<DomSchoolClass>> getTeachersSchoolClasses() {
    // TODO Auto-generated method stub
    return null;
  }

  public Promise<Boolean> submitSingleSchoolStudent(DomNewSingleSchoolStudent newStudent) {
    // TODO Auto-generated method stub
    return null;
  }

  public Promise<List<DomSchoolClassId>> getTeachersClassesOfStudent(RestStudent rest) {
    // TODO Auto-generated method stub
    return null;
  }

  public Promise<DomSingleSchoolStudent> getSingleSchoolStudent(RestGetSingleSchoolStudent restData) {
    // TODO Auto-generated method stub
    return null;
  }

  public Promise<Boolean> removeStudentFromSchoolClass(DomRemoveStudentFromSchoolClass data) {
    // TODO Auto-generated method stub
    return null;
  }

  public Promise<Boolean> submitStudentToSchoolClass(DomSubmitStudentToSchoolClass data) {
    // TODO Auto-generated method stub
    return null;
  }

  public Promise<Boolean> updateSingleSchoolStudent(RestSingleSchoolStudent rest) {
    // TODO Auto-generated method stub
    return null;
  }

}
