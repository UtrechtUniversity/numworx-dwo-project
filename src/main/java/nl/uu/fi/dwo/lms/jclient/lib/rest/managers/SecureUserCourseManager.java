package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.List;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecureUserCourseManager {

  /**
   * get public toplevel courses from a profile. Security: if profile is limited, only members of
   * some schools are allowed.
   *
   * @param profile
   * @return ordered list of courses
   * @throws Dwo2Exception
   */
  public static List<DomCourseStudent> getCourses(DomDwoProfile profile) throws Dwo2Exception {
    // Als een profiel "L"imited is, dan is er geen guest access mogelijk.
    // select * from tblCourse where parent = NULL, profile = %profile, school = NULL
    RestDwoProfile rest = new RestDwoProfile();
    rest.setDomDwoProfile(profile);
    rest.setRestContext(getContext());
    List<DomCourseStudent> result = StoredRestManager.getInstance()
        .getPutList("rest/sec:" + PathId.getId(getContext()) + "/user/course/getRoot", RestListClassTypes.DomCourseStudent, rest);

    return result;

  }

  /**
   * get toplevel courses from a school. Security: if profile is limited, only members of
   * some schools are allowed.
   *
   * @param profile
   * @return ordered list of courses
   * @throws Dwo2Exception
   */
  public static List<DomCourseStudent> getCoursesSchool(DomDwoProfile profile) throws Dwo2Exception {
    RestDwoProfile rest = new RestDwoProfile();
    rest.setDomDwoProfile(profile);
    rest.setRestContext(getContext());
    List<DomCourseStudent> result = StoredRestManager.getInstance()
        .getPutList("rest/sec:" + PathId.getId(getContext()) + "/user/course/getSchool", RestListClassTypes.DomCourseStudent, rest);

    return result;

  }

  private static DomContext getContext() {
    return RestAuthenticator.getInstance().getContext();
  }

  /**
   * get a course. Security: profile can be limited. The course can be an assessment. Wrong profile,
   * Wrong school
   *
   * @param course
   * @param profile
   * @return a course
   * @throws Dwo2Exception
   */
  public static DomCourseStudent getCourse(DomCourse course, DomDwoProfile profile)
      throws Dwo2Exception {
    // Als een profiel "L"imited is, dan is er geen guest access mogelijk.
    RestCourse rest = new RestCourse();
    rest.setDomDwoProfile(profile);
    rest.setRestContext(getContext());
    rest.setDomCourse(course);
    // select * from tblCourse where id = $%id, profile = %profile and school = NULL
    DomCourseStudent result =
        StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/user/course/get", DomCourseStudent.class, rest);
    return result;
  }

  /**
   * get children of a course. The course must have children. Security: profile can be limited, The
   * course can be an assessment. Wrong profile, Wrong school
   *
   * @param course
   * @param profile
   * @return ordered children courses of a folder
   * @throws Dwo2Exception
   */
  public static List<DomCourseStudent> getCourses(DomCourse course, DomDwoProfile profile)
      throws Dwo2Exception {
    RestCourse rest = new RestCourse();
    rest.setDomDwoProfile(profile);
    rest.setDomCourse(course);
    rest.setRestContext(getContext());
    List<DomCourseStudent> result = StoredRestManager.getInstance()
        .getPutList("rest/sec:" + PathId.getId(getContext()) + "/user/course/getChildren", RestListClassTypes.DomCourseStudent, rest);

    return result;
  }

  public static List<DomCourseStudent> getTrash(DomCourse course, DomDwoProfile profile) throws Dwo2Exception {
	    RestCourse rest = new RestCourse();
	    rest.setDomDwoProfile(profile);
	    rest.setDomCourse(course);
	    rest.setRestContext(getContext());
	    List<DomCourseStudent> result = StoredRestManager.getInstance()
	        .getPutList("rest/sec:" + PathId.getId(getContext()) + "/user/course/getTrashedChildren", RestListClassTypes.DomCourseStudent, rest);

	    return result;	  
  }
  
  public static List<DomCourseStudent> getTrash(DomDwoProfile profile) throws Dwo2Exception {
	    RestDwoProfile rest = new RestDwoProfile();
	    rest.setDomDwoProfile(profile);
	    rest.setRestContext(getContext());
	    List<DomCourseStudent> result = StoredRestManager.getInstance()
	        .getPutList("rest/sec:" + PathId.getId(getContext()) + "/user/course/getTrashedSchool", RestListClassTypes.DomCourseStudent, rest);

	    return result;
  
  }
}
