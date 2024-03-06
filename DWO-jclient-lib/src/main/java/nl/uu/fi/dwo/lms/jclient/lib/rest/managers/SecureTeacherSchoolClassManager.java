package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import nl.uu.fi.dwo.rest.dom.entities.DomClassCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomRemoveStudentFromSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomRemoveTeacherFromSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseAndProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseAndProfileNew;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseProfilewAccessKey;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseProfilewFrom;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseProfilewTo;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseProfilewType;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitTeacherToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.entities.RestClassCourseFull;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestRemoveStudentFromSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestRemoveTeacherFromSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseAndProfile;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseAndProfileNew;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseProfilewAccessKey;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseProfilewFrom;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseProfilewTo;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseProfilewType;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassFull;
import nl.uu.fi.dwo.rest.entities.RestSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestStudent;
import nl.uu.fi.dwo.rest.entities.RestSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSubmitTeacherToSchoolClass;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the school roles and classes registered in HasRole.
 *
 * @author G.A.J. van der Plas
 */
public class SecureTeacherSchoolClassManager {

  private static final Logger LOG =
      Logger.getLogger(SecureTeacherSchoolClassManager.class.getName());

  
  /**
   * Returns the current user 'logged in'. The information is extracted from the security context
   * which depends on the credentials used for accessing the rest interface. Technically it should
   * be equal to the data in the DwoHelper.
   *
   * @return
   * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
   */
  public static List<DomSchoolClass> getTeachersSchoolClasses() throws Dwo2Exception {
	return getTeachersSchoolClasses(getRestManager());
  }

  /**
   * Returns the current user 'logged in'. The information is extracted from the security context
   * which depends on the credentials used for accessing the rest interface. Technically it should
   * be equal to the data in the DwoHelper.
   * @param restManager TODO
   *
   * @return
   * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
   */
  public static List<DomSchoolClass> getTeachersSchoolClasses(StoredRestManager restManager) throws Dwo2Exception {
    List<DomSchoolClass> src;
    RestContext rest = new RestContext();
    rest.setRestContext(restManager.getContext());
    src = restManager.getPutList("rest/sec:" + PathId.getId(restManager.getContext()) + "/teacher/schoolclass/getList",
        RestListClassTypes.DomSchoolClass, rest);
    LOG.log(Level.FINE, "Retrieved list of schoolclasses of the teacher with username {0}.",
        new Object[] {getUserName()});
    return src;
  }

  public static List<DomTeacher> getTeachersInSchool() throws Dwo2Exception {
    List<DomTeacher> src;
    RestContext rest = new RestContext();
    rest.setRestContext(getContext());
    src = getRestManager().getPutList("rest/sec:" + PathId.getId(getContext()) + "/teacher/schoolclass/getTeachersInSchoolList",
        RestListClassTypes.DomTeacher, rest);
    LOG.log(Level.FINE,
        "Retrieved list of teachers in the school for the teacher with username {0}.",
        new Object[] {getUserName()});
    return src;
  }

  public static List<DomStudent> getTeachersStudents() throws Dwo2Exception {
	return getTeachersStudents(getRestManager());
  }

  public static List<DomStudent> getTeachersStudents(StoredRestManager restManager) throws Dwo2Exception {
	  List<DomStudent> src;
	  RestContext rest = new RestContext();
	  rest.setRestContext(restManager.getContext());
	    src = restManager.getPutList("rest/sec:" + PathId.getId(restManager.getContext()) + "/teacher/schoolclass/getTeachersStudents",
	            RestListClassTypes.DomStudent, rest);
	        LOG.log(Level.FINE,
	            "Retrieved list of teachers in the school for the teacher with username {0}.",
	            new Object[] {getUserName()});
	        return src;
 }
  
  private static String getUserName() {
    return getRestManager().getAuthenticator().getUsername();
  }

//  public static List<DomStudent> getStudentsInSchool() throws Dwo2Exception {
//    List<DomStudent> src;
//    RestContext rest = new RestContext();
//    rest.setRestContext(getContext());
//    src = getRestManager().getPutList("rest/sec:" + PathId.getId(getContext()) + "/teacher/schoolclass/getStudentsInSchoolList",
//        RestListClassTypes.DomStudent, rest);
//    LOG.log(Level.FINE,
//        "Retrieved list of single school students in the school for the teacher with username {0}.",
//        new Object[] {getUserName()});
//    return src;
//  }

  public static Boolean submitSchoolClass(DomSchoolClassFull schoolClass) throws Dwo2Exception {
    RestSchoolClassFull rest = new RestSchoolClassFull();
    rest.setRestContext(getContext());
    rest.setDomSchoolClassFull(schoolClass);
    Boolean result =
        getRestManager().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/schoolclass/submit", Boolean.class, rest);
    LOG.log(Level.FINE, "Submitted schoolclass {1} for teacher with username {0}.",
        new Object[] {getUserName(),
            rest.getDomSchoolClassFull().getSchoolClassName()});
    return result;
  }

  public static List<DomTeacher> getTeachersInSchoolClass(DomSchoolClass schoolClass)
      throws Dwo2Exception {
    RestSchoolClass rest = new RestSchoolClass();
    rest.setRestContext(getContext());
    rest.setDomSchoolClass(schoolClass);
    List<DomTeacher> result = getRestManager().getPutList(
        "rest/sec:" + PathId.getId(getContext()) + "/teacher/schoolclass/getTeacherList", RestListClassTypes.DomTeacher, rest);
    LOG.log(Level.FINE,
        "Retrieved {1} teachers that are in schoolclass {2} for user with username {0}.",
        new Object[] {getUserName(), result.size(),
            rest.getDomSchoolClass().getId()});
    return result;
  }

  public static List<DomStudent> getStudentsInSchoolClass(DomSchoolClass schoolClass)
      throws Dwo2Exception {
    RestSchoolClass rest = new RestSchoolClass();
    rest.setRestContext(getContext());
    rest.setDomSchoolClass(schoolClass);
    List<DomStudent> result = getRestManager().getPutList(
        "rest/sec:" + PathId.getId(getContext()) + "/teacher/schoolclass/getStudentList", RestListClassTypes.DomStudent, rest);
    LOG.log(Level.FINE,
        "Retrieved {1} students that are in schoolclass {2} for user with username {0}.",
        new Object[] {getUserName(), result.size(),
            rest.getDomSchoolClass().getId()});
    return result;
  }

  public static Boolean removeSchoolClass(DomSchoolClass schoolClass) throws Dwo2Exception {
    RestSchoolClass rest = new RestSchoolClass();
    rest.setRestContext(getContext());
    rest.setDomSchoolClass(schoolClass);
    Boolean result =
        getRestManager().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/schoolclass/remove", Boolean.class, rest);
    LOG.log(Level.FINE, "Removed schoolclass with username {0} for user with id {1}.",
        new Object[] {rest.getDomSchoolClass().getId(),
            getUserName()});
    return result;
  }

  public static Boolean submitTeacherToSchoolClass(DomSubmitTeacherToSchoolClass submit)
      throws Dwo2Exception {
    RestSubmitTeacherToSchoolClass rest = new RestSubmitTeacherToSchoolClass();
    rest.setRestContext(getContext());
    rest.setDomSubmitTeacherToSchoolClass(submit);
    Boolean result =
        getRestManager().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/schoolclass/submitTeacher", Boolean.class, rest);
    LOG.log(Level.FINE, "Submitted teacher {1} to schoolclass {2} for user with username {0}.",
        new Object[] {getUserName(),
            rest.getDomSubmitTeacherToSchoolClass().getTeacher().getId(),
            rest.getDomSubmitTeacherToSchoolClass().getSchoolClass().getId()});
    return result;
  }

  public static Boolean submitStudentToSchoolClass(DomSubmitStudentToSchoolClass submit)
      throws Dwo2Exception {
    RestSubmitStudentToSchoolClass rest = new RestSubmitStudentToSchoolClass();
    rest.setRestContext(getContext());
    rest.setDomSubmitStudentToSchoolClass(submit);
    Boolean result =
        getRestManager().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/schoolclass/submitStudent", Boolean.class, rest);
    LOG.log(Level.FINE, "Submitted student {1} to schoolclass {2} for user with username {0}.",
        new Object[] {getUserName(),
            rest.getDomSubmitStudentToSchoolClass().getStudent().getId(),
            rest.getDomSubmitStudentToSchoolClass().getSchoolClassTo().getId()});
    return result;
  }

  public static Boolean updateSchoolClass(DomSchoolClassFull schoolClass) throws Dwo2Exception {
    RestSchoolClassFull rest = new RestSchoolClassFull();
    rest.setRestContext(getContext());
    rest.setDomSchoolClassFull(schoolClass);
    Boolean result =
        getRestManager().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/schoolclass/update", Boolean.class, rest);
    LOG.log(Level.FINE, "Updated schoolclass {1} for teacher with username {0}.",
        new Object[] {getUserName(),
            rest.getDomSchoolClassFull().getSchoolClassName()});
    return result;
  }

  public static DomSchoolClassFull getFullSchoolClass(DomSchoolClass schoolClass)
      throws Dwo2Exception {
    RestSchoolClass rest = new RestSchoolClass();
    rest.setRestContext(getContext());
    rest.setDomSchoolClass(schoolClass);
    DomSchoolClassFull result = getRestManager().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/schoolclass/getFull",
        DomSchoolClassFull.class, rest);
    LOG.log(Level.FINE, "Retrieved full schoolclass {1} for teacher with username {0}.",
        new Object[] {getUserName(),
            rest.getDomSchoolClass().getSchoolClassName()});
    return result;
  }

  public static Boolean removeTeacherFromSchoolClass(DomRemoveTeacherFromSchoolClass submit)
      throws Dwo2Exception {
    RestRemoveTeacherFromSchoolClass rest = new RestRemoveTeacherFromSchoolClass();
    rest.setRestContext(getContext());
    rest.setDomRemoveTeacherFromSchoolClass(submit);
    Boolean result =
        getRestManager().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/schoolclass/removeTeacher", Boolean.class, rest);
    LOG.log(Level.FINE,
        "Submitted teacher {1} to remove from schoolclass {2} for user with username {0}.",
        new Object[] {getUserName(),
            rest.getDomRemoveTeacherFromSchoolClass().getTeacher().getId(),
            rest.getDomRemoveTeacherFromSchoolClass().getSchoolClass().getId()});
    return result;
  }

  public static Boolean removeStudentFromSchoolClass(DomRemoveStudentFromSchoolClass submit)
      throws Dwo2Exception {
    RestRemoveStudentFromSchoolClass rest = new RestRemoveStudentFromSchoolClass();
    rest.setRestContext(getContext());
    rest.setDomRemoveStudentFromSchoolClass(submit);
    Boolean result =
        getRestManager().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/schoolclass/removeStudent", Boolean.class, rest);
    LOG.log(Level.FINE,
        "Submitted student {1} to remove from schoolclass {2} for user with username {0}.",
        new Object[] {getUserName(),
            rest.getDomRemoveStudentFromSchoolClass().getStudent().getId(),
            rest.getDomRemoveStudentFromSchoolClass().getSchoolClass().getId()});
    return result;
  }

  public static List<DomStudent> getSingleSchoolStudentsInSchool() throws Dwo2Exception {
    List<DomStudent> src;
    RestContext rest = new RestContext();
    rest.setRestContext(getContext());
    src = getRestManager().getPutList(
        "rest/sec:" + PathId.getId(getContext()) + "/teacher/schoolclass/getSingleSchoolStudentsInSchoolList",
        RestListClassTypes.DomStudent, rest);
    LOG.log(Level.FINE,
        "Retrieved list of single school students in the school for the teacher with username {0}.",
        new Object[] {getUserName()});
    return src;
  }

  public static Boolean submitSingleSchoolStudent(DomNewSingleSchoolStudent submit)
      throws Dwo2Exception {
    RestNewSingleSchoolStudent rest = new RestNewSingleSchoolStudent();
    rest.setRestContext(getContext());
    rest.setDomNewSingleSchoolStudent(submit);
    Boolean result = getRestManager()
        .put("rest/sec:" + PathId.getId(getContext()) + "/teacher/schoolclass/submitSingleSchoolStudent", Boolean.class, rest);
    LOG.log(Level.FINE, "Submitted teacher {1} to schoolclass {2} for user with username {0}.",
        new Object[] {getUserName(),
            rest.getDomNewSingleSchoolStudent().getDomSingleSchoolStudent().getId(),
            rest.getDomNewSingleSchoolStudent().getDomSchoolClass().getId()});
    return result;
  }

  public static DomSingleSchoolStudent getSingleSchoolStudent(DomGetSingleSchoolStudent submit)
      throws Dwo2Exception {
    RestGetSingleSchoolStudent rest = new RestGetSingleSchoolStudent();
    rest.setRestContext(getContext());
    rest.setDomGetSingleSchoolStudent(submit);
    DomSingleSchoolStudent result =
        getRestManager().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/schoolclass/getSingleSchoolStudent",
            DomSingleSchoolStudent.class, rest);
    LOG.log(Level.FINE, "Retrieved full single school student {1} for  teacher with username {0}.",
        new Object[] {getUserName(),
            rest.getDomGetSingleSchoolStudent().getDomStudent().getId()});
    return result;
  }

  public static Boolean updateSingleSchoolStudent(DomSingleSchoolStudent submit)
      throws Dwo2Exception {
    RestSingleSchoolStudent rest = new RestSingleSchoolStudent();
    rest.setRestContext(getContext());
    rest.setDomSingleSchoolStudent(submit);
    Boolean result = getRestManager()
        .put("rest/sec:" + PathId.getId(getContext()) + "/teacher/schoolclass/updateSingleSchoolStudent", Boolean.class, rest);
    LOG.log(Level.FINE, "Updated acount data for singlschoolstudent {1} by user {0}.",
        new Object[] {getUserName(),
            rest.getDomSingleSchoolStudent().getId()});
    return result;
  }

  public static Boolean attachCourseToClass(DomSchoolClassCourseAndProfile dom)
      throws Dwo2Exception {
    RestSchoolClassCourseAndProfile rest = new RestSchoolClassCourseAndProfile();
    rest.setDomSchoolClassCourseAndProfile(dom);
    rest.setRestContext(getContext());
    Boolean result = getRestManager().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/schoolclass/attachCourseToClass",
        Boolean.class, rest);
    return result;
  }

  public static Boolean addCourseToClass(DomSchoolClassCourseAndProfileNew dom) throws Dwo2Exception {
    RestSchoolClassCourseAndProfileNew rest = new RestSchoolClassCourseAndProfileNew();
    rest.setDomSchoolClassCourseAndProfileNew(dom);
    rest.setRestContext(getContext());
    Boolean result = getRestManager().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/schoolclass/addCourseToClass",
      Boolean.class, rest);
    return result;
  }
   
  private static StoredRestManager getRestManager() {
    return StoredRestManager.getInstance();
  }

  private static DomContext getContext() {
    return getRestManager().getContext();
  }

  public static Boolean detachCourseFromClass(DomSchoolClassCourseAndProfile dom)
      throws Dwo2Exception {
    RestSchoolClassCourseAndProfile rest = new RestSchoolClassCourseAndProfile();
    rest.setDomSchoolClassCourseAndProfile(dom);
    rest.setRestContext(getContext());
    Boolean result = getRestManager().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/schoolclass/detachCourseFromClass",
        Boolean.class, rest);
    return result;
  }

  public static Boolean setFromDataClassCourse(DomSchoolClassCourseProfilewFrom dom)
      throws Dwo2Exception {
    RestSchoolClassCourseProfilewFrom rest = new RestSchoolClassCourseProfilewFrom();
    rest.setDomSchoolClassCourseProfilewFrom(dom);
    rest.setRestContext(getContext());
    Boolean result = getRestManager().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/schoolclass/setFromDateClassCourse",
        Boolean.class, rest);
    return result;
  }

  public static Boolean setToDataClassCourse(DomSchoolClassCourseProfilewTo dom)
      throws Dwo2Exception {
    RestSchoolClassCourseProfilewTo rest = new RestSchoolClassCourseProfilewTo();
    rest.setDomSchoolClassCourseProfilewTo(dom);
    rest.setRestContext(getContext());
    Boolean result = getRestManager().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/schoolclass/setToDateClassCourse",
        Boolean.class, rest);
    return result;
  }

  public static Boolean setClassCourseType(DomSchoolClassCourseProfilewType dom)
      throws Dwo2Exception {
    RestSchoolClassCourseProfilewType rest = new RestSchoolClassCourseProfilewType();
    rest.setDomSchoolClassCourseProfilewType(dom);
    rest.setRestContext(getContext());
    Boolean result = getRestManager().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/schoolclass/setClassCourseType",
        Boolean.class, rest);
    return result;
  }

  public static Boolean setAccessKeyClassCourse(DomSchoolClassCourseProfilewAccessKey dom)
      throws Dwo2Exception {
    RestSchoolClassCourseProfilewAccessKey rest = new RestSchoolClassCourseProfilewAccessKey();
    rest.setDomSchoolClassCourseProfilewAccessKey(dom);
    rest.setRestContext(getContext());
    Boolean result = getRestManager().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/schoolclass/setAccessKeyClassCourse",
        Boolean.class, rest);
    return result;
  }

  public static DomCoursesOfSchoolClass4Teacher getModules(DomSchoolClassAndProfile submit) throws Dwo2Exception {
    RestSchoolClassAndProfile restData = new RestSchoolClassAndProfile();
    restData.setRestContext(getContext());
    restData.setDomSchoolClassAndProfile(submit);
    DomCoursesOfSchoolClass4Teacher result;
    
    result = getRestManager().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/schoolclass/getModules",
      DomCoursesOfSchoolClass4Teacher.class, restData);
    
    return result;
  }

  /** utility method */
  public static DomCoursesOfSchoolClass4Teacher getModules(DomSchoolClass schoolClass, DomDwoProfile profile) throws Dwo2Exception {
    DomSchoolClassAndProfile submit = new DomSchoolClassAndProfile();
    submit.setDomDwoProfile(profile);
    submit.setDomSchoolClass(schoolClass);
    return getModules(submit);
  }
  
  public static DomClassCourseFull updateClassCourse(DomClassCourseFull submit) throws Dwo2Exception {
	  RestClassCourseFull rest = new RestClassCourseFull();
	  rest.setDomCourse(submit);
	  rest.setRestContext(getContext());
	  return getRestManager().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/classcourse/update", DomClassCourseFull.class, rest);
  }

	public static String getBearerToken(DomStudent domStudent, StoredRestManager manager) throws Dwo2Exception {
		RestStudent rest = new RestStudent();
		rest.setDomStudent(domStudent);
		rest.setRestContext(manager.getContext());
		return manager.put("rest/sec:" + PathId.getId(manager.getContext()) + "/teacher/schoolclass/getBearerToken", String.class, rest);
	}
  
}
