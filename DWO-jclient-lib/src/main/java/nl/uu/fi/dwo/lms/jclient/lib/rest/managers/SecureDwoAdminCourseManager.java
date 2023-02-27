package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestCourseFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecureDwoAdminCourseManager extends AbstractCourseManager {
  private static final Logger LOG = Logger.getLogger(SecuredTeacherCourseManager.class.getName());

  public SecureDwoAdminCourseManager(StoredRestManager manager) {
    super(manager);
    // TODO Auto-generated constructor stub
  }

  public SecureDwoAdminCourseManager() {
    this(StoredRestManager.getInstance());
  }

  public DomCourseFull update(DomCourseFull edit) throws Dwo2Exception {
    RestCourseFull rest = new RestCourseFull();
    rest.setRestContext(getContext());
    rest.setDomCourse(edit);
    DomCourseFull result =
        manager.put("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/course/update", DomCourseFull.class, rest);
    LOG.log(Level.FINE, "Updated course for the dwoadmin with username {0}.",
        new Object[] {manager.getAuthenticator().getUsername()});
    return result;
  }

  public DomCourseFull add(DomCourseFull edit) throws Dwo2Exception {
    RestCourseFull rest = new RestCourseFull();
    rest.setRestContext(getContext());
    rest.setDomCourse(edit);
    DomCourseFull result =
        manager.put("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/course/add", DomCourseFull.class, rest);
    LOG.log(Level.FINE, "Updated course for the dwoadmin with username {0}.",
        new Object[] {manager.getAuthenticator().getUsername()});
    return result;
  }

  @Override
  public Boolean remove(DomCourse course, DomDwoProfile profile) throws Dwo2Exception {
    RestCourse rest = new RestCourse();
    rest.setDomCourse(course);
    rest.setDomDwoProfile(profile);
    rest.setRestContext(getContext());
    Boolean result = manager.put("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/course/remove", Boolean.class, rest);
    LOG.log(Level.FINE, "Removed course for the dwoadmin with username {0}.",
        new Object[] {manager.getAuthenticator().getUsername()});
    return result;
  }

	@Override
	public Boolean trash(DomCourse course, DomDwoProfile profile) throws Dwo2Exception {
	    RestCourse rest = new RestCourse();
	    rest.setDomCourse(course);
	    rest.setDomDwoProfile(profile);
	    rest.setRestContext(getContext());
	    Boolean result = manager.put("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/course/trash", Boolean.class, rest);
	    LOG.log(Level.FINE, "Removed course for the teacher with username {0}.",
	        new Object[] {manager.getAuthenticator().getUsername()});
	    return result;
	}


}
