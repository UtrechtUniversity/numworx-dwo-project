package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.List;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.shared.GWT;

import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredSchoolAdminSchoolRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestSchoolAdmin;
import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolFull;
import nl.uu.fi.dwo.rest.entities.RestSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestStudent;
import nl.uu.fi.dwo.rest.entities.RestTeacher;
import nl.uu.fi.dwo.rest.entities.RestUserFull;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecuredSchoolAdminSchoolManager {

  private static SecuredSchoolAdminSchoolRestCaller service =
      GWT.create(SecuredSchoolAdminSchoolRestCaller.class);

  public Promise<List<DomSchoolAdmin>> getSchoolAdminsInSchool(DomContext context) {
    PromiseCallback<List<DomSchoolAdmin>> callback = new PromiseCallback<>();
    RestContext rest = new RestContext();
    rest.setRestContext(context);
    service.getSchoolAdminsInSchool(PathId.getId(context), rest, callback);
    return callback.getPromise();
  }

  public Promise<List<DomTeacher>> getTeachersInSchool(DomContext context) {
    PromiseCallback<List<DomTeacher>> callback = new PromiseCallback<>();
    RestContext rest = new RestContext();
    rest.setRestContext(context);
    service.getTeachersInSchool(PathId.getId(context), rest, callback);
    return callback.getPromise();
  }

  public Promise<List<DomStudent>> getStudentsInSchool(DomContext context) {
    PromiseCallback<List<DomStudent>> callback = new PromiseCallback<>();
    RestContext rest = new RestContext();
    rest.setRestContext(context);
    service.getStudentsInSchool(PathId.getId(context), rest, callback);
    return callback.getPromise();
  }

  public Promise<List<DomTeacher>> getTeachersInSchoolClass(DomContext context,
      DomSchoolClass schoolclass) {
    PromiseCallback<List<DomTeacher>> callback = new PromiseCallback<>();
    RestSchoolClass restData = new RestSchoolClass();
    restData.setDomSchoolClass(schoolclass);
    restData.setRestContext(context);
    service.getTeachersInSchoolClass(PathId.getId(context), restData, callback);
    return callback.getPromise();
  }

  public Promise<List<DomSchoolClass>> getSchoolsClasses(DomContext context) {
    PromiseCallback<List<DomSchoolClass>> callback = new PromiseCallback<>();
    RestContext rest = new RestContext();
    rest.setRestContext(context);
    service.getSchoolsClasses(PathId.getId(context), rest, callback);
    return callback.getPromise();
  }

  public Promise<List<DomStudent>> getSingleSchoolStudentsInSchool(DomContext context) {
    PromiseCallback<List<DomStudent>> callback = new PromiseCallback<>();
    RestContext rest = new RestContext();
    rest.setRestContext(context);
    service.getSingleSchoolStudentsInSchool(PathId.getId(context), rest, callback);
    return callback.getPromise();
  }

  private RestNewSingleSchoolStudent restNewSingleSchoolStudent(DomContext context,
      DomNewSingleSchoolStudent schoolClass) {
    RestNewSingleSchoolStudent result = new RestNewSingleSchoolStudent();
    result.setDomNewSingleSchoolStudent(schoolClass);
    result.setRestContext(context);
    return result;
  }

  public Promise<Boolean> submitSingleSchoolStudent(DomContext context,
      DomNewSingleSchoolStudent schoolClass) {
    PromiseCallback<Boolean> callBack = new PromiseCallback<>();
    service.submitSingleSchoolStudent(PathId.getId(context),
        restNewSingleSchoolStudent(context, schoolClass), callBack);
    return callBack.getPromise();

  }

  private static RestGetSingleSchoolStudent restGetSingleSchoolStudent(DomContext context,
      DomGetSingleSchoolStudent student) {
    RestGetSingleSchoolStudent result = new RestGetSingleSchoolStudent();
    result.setDomGetSingleSchoolStudent(student);
    result.setRestContext(context);
    return result;
  }

  public Promise<DomSingleSchoolStudent> getSingleSchoolStudent(DomContext context,
      DomGetSingleSchoolStudent student) {
    PromiseCallback<DomSingleSchoolStudent> callback = new PromiseCallback<>();
    service.getSingleSchoolStudent(PathId.getId(context),
        restGetSingleSchoolStudent(context, student), callback);
    return callback.getPromise();
  }

  private static RestSingleSchoolStudent restSingleSchoolStudent(DomContext context,
      DomSingleSchoolStudent student) {
    RestSingleSchoolStudent result = new RestSingleSchoolStudent();
    result.setDomSingleSchoolStudent(student);
    result.setRestContext(context);
    return result;
  }

  public Promise<Boolean> updateSingleSchoolStudent(DomContext context,
      DomSingleSchoolStudent student) {
    PromiseCallback<Boolean> callback = new PromiseCallback<>();
    service.updateSingleSchoolStudent(PathId.getId(context),
        restSingleSchoolStudent(context, student), callback);
    return callback.getPromise();
  }

  private static RestTeacher restTeacher(DomContext context, DomTeacher teacher) {
    RestTeacher result = new RestTeacher();
    result.setDomTeacher(teacher);
    result.setRestContext(context);
    return result;
  }

  public Promise<List<DomSchoolClassId>> getTeachersSchoolClasses(DomContext context,
      DomTeacher teacher) {
    PromiseCallback<List<DomSchoolClassId>> callback = new PromiseCallback<>();
    service.getTeachersSchoolClasses(PathId.getId(context), restTeacher(context, teacher),
        callback);
    return callback.getPromise();
  }

  private static RestUserFull restUserFull(DomContext context, DomUserFull teacher) {
    RestUserFull result = new RestUserFull();
    result.setDomUserFull(teacher);
    result.setRestContext(context);
    return result;
  }

  public Promise<Boolean> submitTeacher(DomContext context, DomUserFull teacher) {
    PromiseCallback<Boolean> callback = new PromiseCallback<>();
    service.submitTeacher(PathId.getId(context), restUserFull(context, teacher), callback);
    return callback.getPromise();
  }

  private static RestStudent restStudent(DomContext context, DomStudent student) {
    RestStudent result = new RestStudent();
    result.setDomStudent(student);
    result.setRestContext(context);
    return result;
  }

  public Promise<List<DomSchoolClass>> getStudentsSchoolClasses(DomContext context,
      DomStudent student) {
    PromiseCallback<List<DomSchoolClass>> callback = new PromiseCallback<>();
    service.getStudentsSchoolClasses(PathId.getId(context), restStudent(context, student),
        callback);
    return callback.getPromise();
  }

  private static RestSchoolFull restSchoolFull(DomContext context, DomSchoolFull school) {
    RestSchoolFull result = new RestSchoolFull();
    result.setDomSchoolFull(school);
    result.setRestContext(context);
    return result;
  }

  public Promise<Boolean> updateSchool(DomContext context, DomSchoolFull school) {
    PromiseCallback<Boolean> callback = new PromiseCallback<>();
    service.updateSchool(PathId.getId(context), restSchoolFull(context, school), callback);
    return callback.getPromise();
  }

  private static RestSchoolAdmin restSchoolAdmin(DomContext context, DomSchoolAdmin admin) {
    RestSchoolAdmin result = new RestSchoolAdmin();
    result.setDomSchoolAdmin(admin);
    result.setRestContext(context);
    return result;
  }

  public Promise<Boolean> removeSchoolAdminFromSchool(DomContext context, DomSchoolAdmin admin) {
    PromiseCallback<Boolean> callback = new PromiseCallback<>();
    service.removeSchoolAdminFromSchool(PathId.getId(context), restSchoolAdmin(context, admin),
        callback);
    return callback.getPromise();
  }

  public Promise<Boolean> removeSingleSchoolStudentFromSchool(DomContext context,
      DomStudent student) {
    PromiseCallback<Boolean> callback = new PromiseCallback<>();
    service.removeSingleSchoolStudentFromSchool(PathId.getId(context),
        restStudent(context, student), callback);
    return callback.getPromise();
  }

  public Promise<Boolean> removeStudentFromSchool(DomContext context, DomStudent student) {
    PromiseCallback<Boolean> callback = new PromiseCallback<>();
    service.removeStudentFromSchool(PathId.getId(context), restStudent(context, student), callback);
    return callback.getPromise();
  }

  public Promise<Boolean> removeTeacherFromSchool(DomContext context, DomTeacher teacher) {
    PromiseCallback<Boolean> callback = new PromiseCallback<>();
    service.removeTeacherFromSchool(PathId.getId(context), restTeacher(context, teacher), callback);
    return callback.getPromise();
  }

}
