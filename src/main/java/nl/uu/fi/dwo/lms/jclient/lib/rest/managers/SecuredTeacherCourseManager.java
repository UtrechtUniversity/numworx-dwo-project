package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestCourseFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;

/**
 * CRUD for teachers on courses.
 * 
 * @author wim
 *
 */
public class SecuredTeacherCourseManager extends AbstractCourseManager {
  public SecuredTeacherCourseManager(StoredRestManager manager) {
    super(manager);
  }

  public SecuredTeacherCourseManager() {
    this(StoredRestManager.getInstance());
  }

  private static final Logger LOG = Logger.getLogger(SecuredTeacherCourseManager.class.getName());

  /**
   * Update a course. Not all fields are updatable!
   * 
   * @param edit the course
   * @return the edited course
   */
  public DomCourseFull update(DomCourseFull edit) throws Dwo2Exception {
    RestCourseFull rest = new RestCourseFull();
    rest.setRestContext(getContext());
    rest.setDomCourse(edit);
    DomCourseFull result =
        manager.put("rest/sec:" + PathId.getId(getContext()) + "/teacher/course/update", DomCourseFull.class, rest);
    LOG.log(Level.FINE, "Updated course for the teacher with username {0}.",
        new Object[] {manager.getAuthenticator().getUsername()});
    return result;
  }

  public DomCourseFull add(DomCourseFull edit) throws Dwo2Exception {
    RestCourseFull rest = new RestCourseFull();
    rest.setRestContext(getContext());
    rest.setDomCourse(edit);
    DomCourseFull result = manager.put("rest/sec:" + PathId.getId(getContext()) + "/teacher/course/add", DomCourseFull.class, rest);
    LOG.log(Level.FINE, "Updated course for the teacher with username {0}.",
        new Object[] {manager.getAuthenticator().getUsername()});
    return result;
  }

  @Override
  public Boolean remove(DomCourse course, DomDwoProfile profile) throws Dwo2Exception {
    RestCourse rest = new RestCourse();
    rest.setDomCourse(course);
    rest.setDomDwoProfile(profile);
    rest.setRestContext(getContext());
    Boolean result = manager.put("rest/sec:" + PathId.getId(getContext()) + "/teacher/course/remove", Boolean.class, rest);
    LOG.log(Level.FINE, "Removed course for the teacher with username {0}.",
        new Object[] {manager.getAuthenticator().getUsername()});
    return result;
  }

	@Override
	public Boolean trash(DomCourse course, DomDwoProfile profile) throws Dwo2Exception {
	    RestCourse rest = new RestCourse();
	    rest.setDomCourse(course);
	    rest.setDomDwoProfile(profile);
	    rest.setRestContext(getContext());
	    Boolean result = manager.put("rest/sec:" + PathId.getId(getContext()) + "/teacher/course/trash", Boolean.class, rest);
	    LOG.log(Level.FINE, "Removed course for the teacher with username {0}.",
	        new Object[] {manager.getAuthenticator().getUsername()});
	    return result;
	}
}
