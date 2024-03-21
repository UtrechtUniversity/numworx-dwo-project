package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestSchoolAdmin;
import nl.uu.fi.dwo.rest.entities.RestSchoolFull;
import nl.uu.fi.dwo.rest.entities.RestSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestStudent;
import nl.uu.fi.dwo.rest.entities.RestTeacher;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.entities.RestUserFull;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;

/**
 * Manages the school roles and classes registered in HasRole.
 *
 * @author G.A.J. van der Plas
 */
public class SecureSchoolAdminSchoolManager implements SchoolManager {

  private static final Logger LOG =
      Logger.getLogger(SecureSchoolAdminSchoolManager.class.getName());

  public static List<DomTeacher> getTeachersInSchool() throws Dwo2Exception {
    List<DomTeacher> src;
    RestContext rest = new RestContext();
    rest.setRestContext(getContext());
    src = StoredRestManager.getInstance().getPutList(
        "rest/sec:" + PathId.getId(getContext()) + "/schooladmin/school/getTeachersInSchoolList", RestListClassTypes.DomTeacher, rest);
    LOG.log(Level.FINE,
        "Retrieved list of teachers in the school for the schooladmin with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername()});
    return src;
  }

  public static List<DomStudent> getStudentsInSchool() throws Dwo2Exception {
    List<DomStudent> src;
    RestContext rest = new RestContext();
    rest.setRestContext(getContext());
    src = StoredRestManager.getInstance().getPutList(
        "rest/sec:" + PathId.getId(getContext()) + "/schooladmin/school/getStudentsInSchoolList", RestListClassTypes.DomStudent, rest);
    LOG.log(Level.FINE,
        "Retrieved list of students in the school for the schooladmin with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername()});
    return src;
  }

  public static List<DomSchoolAdmin> getSchoolAdminsInSchool() throws Dwo2Exception {
    List<DomSchoolAdmin> src;
    RestContext rest = new RestContext();
    rest.setRestContext(getContext());
    src = StoredRestManager.getInstance().getPutList(
        "rest/sec:" + PathId.getId(getContext()) + "/schooladmin/school/getSchoolAdminList", RestListClassTypes.DomSchoolAdmin, rest);
    LOG.log(Level.FINE,
        "Retrieved list of schooladmins in the school for the schooladmin with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername()});
    return src;
  }

  public static DomSingleSchoolStudent getSingleSchoolStudent(DomGetSingleSchoolStudent submit)
      throws Dwo2Exception {
    RestGetSingleSchoolStudent rest = new RestGetSingleSchoolStudent();
    rest.setRestContext(getContext());
    rest.setDomGetSingleSchoolStudent(submit);
    DomSingleSchoolStudent result =
        StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/schooladmin/school/getSingleSchoolStudent",
            DomSingleSchoolStudent.class, rest);
    LOG.log(Level.FINE, "Retrieved full single school student {1} for  teacher with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername(),
            rest.getDomGetSingleSchoolStudent().getDomStudent().getId()});
    return result;
  }

  public static Boolean updateSingleSchoolStudent(DomSingleSchoolStudent submit)
      throws Dwo2Exception {
    RestSingleSchoolStudent rest = new RestSingleSchoolStudent();
    rest.setRestContext(getContext());
    rest.setDomSingleSchoolStudent(submit);
    Boolean result = StoredRestManager.getInstance()
        .put("rest/sec:" + PathId.getId(getContext()) + "/schooladmin/school/updateSingleSchoolStudent", Boolean.class, rest);
    LOG.log(Level.FINE, "Updated acount data for singlschoolstudent {1} by user {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername(),
            rest.getDomSingleSchoolStudent().getId()});
    return result;
  }


  public static Boolean removeStudentFromSchool(DomStudent submit) throws Dwo2Exception {
    RestStudent rest = new RestStudent();
    rest.setRestContext(getContext());
    rest.setDomStudent(submit);

    Boolean result = StoredRestManager.getInstance()
        .put("rest/sec:" + PathId.getId(getContext()) + "/schooladmin/school/removeStudent", Boolean.class, rest);
    LOG.log(Level.FINE, "Submitted student {1} for removal from school by user with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername(), rest.getDomStudent().getId()});
    return result;
  }

  public static Boolean removeSingleSchoolStudentFromSchool(DomStudent submit)
      throws Dwo2Exception {
    RestStudent rest = new RestStudent();
    rest.setRestContext(getContext());
    rest.setDomStudent(submit);

    Boolean result = StoredRestManager.getInstance().put(
        "rest/sec:" + PathId.getId(getContext()) + "/schooladmin/school/removeSingleSchoolStudentFromSchool", Boolean.class, rest);
    LOG.log(Level.FINE, "Submitted student {1} for removal from school by user with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername(), rest.getDomStudent().getId()});
    return result;
  }

  public static Boolean removeTeacherFromSchool(DomTeacher submit) throws Dwo2Exception {
    RestTeacher rest = new RestTeacher();
    rest.setRestContext(getContext());
    rest.setDomTeacher(submit);

    Boolean result = StoredRestManager.getInstance()
        .put("rest/sec:" + PathId.getId(getContext()) + "/schooladmin/school/removeTeacher", Boolean.class, rest);
    LOG.log(Level.FINE, "Submitted student {1} for removal from school by user with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername(), rest.getDomTeacher().getId()});
    return result;
  }

  static DomContext getContext() {
    return StoredRestManager.getInstance().getContext();
  }

  public static Boolean removeSchoolAdminFromSchool(DomSchoolAdmin submit) throws Dwo2Exception {
    RestSchoolAdmin rest = new RestSchoolAdmin();
    rest.setRestContext(getContext());
    rest.setDomSchoolAdmin(submit);

    Boolean result = StoredRestManager.getInstance()
        .put("rest/sec:" + PathId.getId(getContext()) + "/schooladmin/school/removeSchoolAdmin", Boolean.class, rest);
    LOG.log(Level.FINE, "Submitted student {1} for removal from school by user with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername(),
            rest.getDomSchoolAdmin().getId()});
    return result;
  }

  public static List<DomSchoolClass> GetTeachersSchoolClasses(DomTeacher domTeacher)
      throws Dwo2Exception {
    RestTeacher rest = new RestTeacher();
    rest.setRestContext(getContext());
    rest.setDomTeacher(domTeacher);
    List<DomSchoolClass> result = StoredRestManager.getInstance().getPutList(
        "rest/sec:" + PathId.getId(getContext()) + "/schooladmin/school/getTeachersSchoolClassList",
        RestListClassTypes.DomSchoolClass, rest);
    LOG.log(Level.FINE,
        "Retrieved {1} schoolclasses of teacher {2} for schooladmin with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername(), result.size(),
            rest.getDomTeacher().getId()});
    return result;
  }

  public static List<DomSchoolClass> GetStudentsSchoolClasses(DomStudent domStudent)
      throws Dwo2Exception {
    RestStudent rest = new RestStudent();
    rest.setRestContext(getContext());
    rest.setDomStudent(domStudent);
    List<DomSchoolClass> result = StoredRestManager.getInstance().getPutList(
        "rest/sec:" + PathId.getId(getContext()) + "/schooladmin/school/getStudentsSchoolClassList",
        RestListClassTypes.DomSchoolClass, rest);
    LOG.log(Level.FINE,
        "Retrieved {1} schoolclasses of student {2} for schooladmin with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername(), result.size(),
            rest.getDomStudent().getId()});
    return result;
  }

  public static Boolean submitTeacher(DomUserFull submit) throws Dwo2Exception {
    RestUserFull rest = new RestUserFull();
    rest.setRestContext(getContext());
    rest.setDomUserFull(submit);
    Boolean result = StoredRestManager.getInstance()
        .put("rest/sec:" + PathId.getId(getContext()) + "/schooladmin/school/submitTeacher", Boolean.class, rest);
    LOG.log(Level.FINE, "Submitted new user {1} enlisted as teacher in the school by user {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername(),
            rest.getDomUserFull().getId()});
    return result;
  }

  public Boolean updateSchool(DomSchoolFull submit) throws Dwo2Exception {
    RestSchoolFull rest = new RestSchoolFull();
    rest.setRestContext(getContext());
    rest.setDomSchoolFull(submit);
    Boolean result = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/schooladmin/school/update",
        Boolean.class, rest);
    LOG.log(Level.FINE, "Updated data for school {1} by username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername(), submit.getId()});
    return result;
  }

  
  public static Boolean inviteTeacher(DomStudent student) throws Dwo2Exception {
	  RestStudent rest = new RestStudent();
	  rest.setRestContext(getContext());
	  rest.setDomStudent(student);
	  Boolean result = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/schooladmin/school/inviteTeacher", Boolean.class, rest);
	  return result;
  }

  public static Boolean inviteStudent(DomTeacher teacher) throws Dwo2Exception {
	  RestTeacher rest = new RestTeacher();
	  rest.setRestContext(getContext());
	  rest.setDomTeacher(teacher);
	  Boolean result = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/schooladmin/school/inviteStudent", Boolean.class, rest);
	  return result;
  }

}
