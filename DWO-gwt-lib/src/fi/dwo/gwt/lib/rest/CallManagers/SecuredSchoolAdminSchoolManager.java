package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.List;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.shared.GWT;

import static fi.dwo.gwt.lib.rest.GwtRestVars.F;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredSchoolAdminSchoolRestCaller;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAdminAndHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolOrganisation;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacherAndHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestSchoolAdmin;
import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolFull;
import nl.uu.fi.dwo.rest.entities.RestSchoolOrganisation;
import nl.uu.fi.dwo.rest.entities.RestSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestStudent;
import nl.uu.fi.dwo.rest.entities.RestTeacher;
import nl.uu.fi.dwo.rest.entities.RestUserFull;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecuredSchoolAdminSchoolManager {

  private static SecuredSchoolAdminSchoolRestCaller service =
      GWT.create(SecuredSchoolAdminSchoolRestCaller.class);

  public Promise<List<DomSchoolAdmin>> getSchoolAdminsInSchool(DomContext context) {
    RestContext rest = new RestContext();
    rest.setRestContext(context);
    return F(service::getSchoolAdminsInSchool,PathId.getId(context), rest);
  }

  public Promise<List<DomSchoolAdminAndHasRole>> getSchoolAdminsAndHasRoleInSchool(DomContext context) {
	    RestContext rest = new RestContext();
	    rest.setRestContext(context);
	    return F(service::getSchoolAdminsAndHasRoleInSchool,PathId.getId(context), rest);
	  }

  public Promise<List<DomTeacherAndHasRole>> getTeachersAndHasRoleInSchool(DomContext context) {
    RestContext rest = new RestContext();
    rest.setRestContext(context);
    return F(service::getTeachersAndHasRoleInSchool,PathId.getId(context), rest);
  }

  public Promise<List<DomTeacher>> getTeachersInSchool(DomContext context) {
	    RestContext rest = new RestContext();
	    rest.setRestContext(context);
	    return F(service::getTeachersInSchool,PathId.getId(context), rest);
	  }

  
  public Promise<List<DomStudent>> getStudentsInSchool(DomContext context) {
    RestContext rest = new RestContext();
    rest.setRestContext(context);
    return F(service::getStudentsInSchool,PathId.getId(context), rest);
  }

  public Promise<List<DomTeacher>> getTeachersInSchoolClass(DomContext context,
      DomSchoolClass schoolclass) {
    RestSchoolClass restData = new RestSchoolClass();
    restData.setDomSchoolClass(schoolclass);
    restData.setRestContext(context);
    return F(service::getTeachersInSchoolClass,PathId.getId(context), restData);
  }

  public Promise<List<DomSchoolClass>> getSchoolsClasses(DomContext context) {
    RestContext rest = new RestContext();
    rest.setRestContext(context);
    return F(service::getSchoolsClasses,PathId.getId(context), rest);
  }

  public Promise<List<DomStudent>> getSingleSchoolStudentsInSchool(DomContext context) {
    RestContext rest = new RestContext();
    rest.setRestContext(context);
    return F(service::getSingleSchoolStudentsInSchool,PathId.getId(context), rest);
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
    return F(service::submitSingleSchoolStudent,PathId.getId(context),
        restNewSingleSchoolStudent(context, schoolClass));
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
    return F(service::getSingleSchoolStudent,PathId.getId(context),
        restGetSingleSchoolStudent(context, student));
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
    return F(service::updateSingleSchoolStudent,PathId.getId(context),
        restSingleSchoolStudent(context, student));
  }

  private static RestTeacher restTeacher(DomContext context, DomTeacher teacher) {
    RestTeacher result = new RestTeacher();
    result.setDomTeacher(teacher);
    result.setRestContext(context);
    return result;
  }

  public Promise<List<DomSchoolClassId>> getTeachersSchoolClasses(DomContext context,
      DomTeacher teacher) {
    return F(service::getTeachersSchoolClasses,PathId.getId(context), restTeacher(context, teacher));
  }

  private static RestUserFull restUserFull(DomContext context, DomUserFull teacher) {
    RestUserFull result = new RestUserFull();
    result.setDomUserFull(teacher);
    result.setRestContext(context);
    return result;
  }

  public Promise<Boolean> submitTeacher(DomContext context, DomUserFull teacher) {
    return F(service::submitTeacher,PathId.getId(context), restUserFull(context, teacher));
  }

  private static RestStudent restStudent(DomContext context, DomStudent student) {
    RestStudent result = new RestStudent();
    result.setDomStudent(student);
    result.setRestContext(context);
    return result;
  }

  public Promise<List<DomSchoolClass>> getStudentsSchoolClasses(DomContext context,
      DomStudent student) {
    return F(service::getStudentsSchoolClasses,PathId.getId(context), restStudent(context, student));
  }

  private static RestSchoolFull restSchoolFull(DomContext context, DomSchoolFull school) {
    RestSchoolFull result = new RestSchoolFull();
    result.setDomSchoolFull(school);
    result.setRestContext(context);
    return result;
  }

  public Promise<Boolean> updateSchool(DomContext context, DomSchoolFull school) {
    return F(service::updateSchool,PathId.getId(context), restSchoolFull(context, school));
  }

  private static RestSchoolAdmin restSchoolAdmin(DomContext context, DomSchoolAdmin admin) {
    RestSchoolAdmin result = new RestSchoolAdmin();
    result.setDomSchoolAdmin(admin);
    result.setRestContext(context);
    return result;
  }

  public Promise<Boolean> removeSchoolAdminFromSchool(DomContext context, DomSchoolAdmin admin) {
    return F(service::removeSchoolAdminFromSchool,PathId.getId(context), restSchoolAdmin(context, admin));
  }

  public Promise<Boolean> removeSingleSchoolStudentFromSchool(DomContext context,
      DomStudent student) {
    return F(service::removeSingleSchoolStudentFromSchool,PathId.getId(context),
        restStudent(context, student));
  }

  public Promise<Boolean> removeStudentFromSchool(DomContext context, DomStudent student) {
    return F(service::removeStudentFromSchool,PathId.getId(context), restStudent(context, student));
  }

  public Promise<Boolean> removeTeacherFromSchool(DomContext context, DomTeacher teacher) {
    return F(service::removeTeacherFromSchool,PathId.getId(context), restTeacher(context, teacher));
  }

  public Promise<DomSchoolOrganisation> getStudentsInSchool(DomContext context, DomSchoolOrganisation org) {
	return F(service::getStudentsInSchool, PathId.getId(context), new RestSchoolOrganisation(context, org));
  }
}
