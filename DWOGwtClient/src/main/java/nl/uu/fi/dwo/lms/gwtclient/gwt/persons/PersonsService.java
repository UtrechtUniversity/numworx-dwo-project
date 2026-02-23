package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import java.util.List;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

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
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.entities.RestGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestStudent;
import nl.uu.fi.dwo.rest.entities.RestTeacher;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

public abstract class PersonsService {

  public abstract Promise<List<DomStudent>> getTeachersStudents();
  public abstract Promise<List<DomTeacher>> getTeachersInSchool();
  public abstract Promise<List<DomSchoolClass>> getTeachersSchoolClasses();

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
  public Promise<Boolean> submitSchoolClass(DomSchoolClassFull schoolClass) {
    // TODO Auto-generated method stub
    return null;
  }
  public Promise<DomSchoolClassFull> getFullSchoolClass(DomSchoolClass aSchoolClass) {
    // TODO Auto-generated method stub
    return null;
  }
  public Promise<Boolean> updateSchoolClass(DomSchoolClassFull fullSchoolClass) {
    // TODO Auto-generated method stub
    return null;
  }
  public Promise<Boolean> removeSchoolClass(DomSchoolClass schoolClass) {
    // TODO Auto-generated method stub
    return null;
  }
  public abstract Promise<List<DomTeacher>> getTeachersInSchoolClass(DomSchoolClass schoolClass);
  public abstract Promise<List<DomStudent>> getStudentsInSchoolClass(DomSchoolClass schoolClass);
  
  public Promise<DomCoursesOfSchoolClass4Teacher> getModules(DomContext context,
      DomSchoolClassAndProfile sap) {
    return Promises.failed(new Dwo2Exception(Dwo2ExceptionCode.Client_InternalError, "no implementation of getModules"));
  }
  
  public abstract Promise<Boolean> submitTeacherToSchoolClass(DomSubmitTeacherToSchoolClass submit);
 
  public abstract Promise<Boolean> moveStudentToSchoolClass(DomMoveStudentToSchoolClass submit);

  public Promise<List<DomSchoolClassId>> getSharedTeacherClasses(RestTeacher rest) {
    // TODO Auto-generated method stub
    return null;
  }
  public Promise<Boolean> removeTeacherFromSchoolClass(DomRemoveTeacherFromSchoolClass data) {
    // TODO Auto-generated method stub
    return null;
  }
  public Promise<Boolean> submitTeacher(DomUserFull newUser) {
    return Promises.failed(new Dwo2Exception(Dwo2ExceptionCode.Client_InternalError, "no implementation of submitTeacher"));
  }
  public Promise<Boolean> submitTeacherv2(DomUserFull newUser) {
	    return Promises.failed(new Dwo2Exception(Dwo2ExceptionCode.Client_InternalError, "no implementation of submitTeacherv2"));
	  }
  public Promise<Boolean> submitSingleSchoolStudentv2(DomNewSingleSchoolStudent newStudent) {
	    return Promises.failed(new Dwo2Exception(Dwo2ExceptionCode.Client_InternalError, "no implementation of submitSingleSchoolStudentv2"));
	  }

  
  
}
