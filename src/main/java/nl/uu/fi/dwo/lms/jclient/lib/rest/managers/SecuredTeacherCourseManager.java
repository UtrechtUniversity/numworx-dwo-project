package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.entities.RestCourseFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 * CRUD for teachers on courses.
 * @author wim
 *
 */
public class SecuredTeacherCourseManager {
    private static final Logger LOG = Logger.getLogger(SecuredTeacherCourseManager.class.getName());

    /** Update a course. Not all fields are updatable!
     * @param edit the course
     * @return the edited course
    */
    public static DomCourseFull update(DomCourseFull edit) throws Dwo2Exception {
    	RestCourseFull rest = new RestCourseFull();
    	rest.setRestContext(RestAuthenticator.getInstance().getContext());
    	rest.setDomCourse(edit);
        DomCourseFull result = StoredRestManager.getInstance().put("rest/secure/teacher/course/update",DomCourseFull.class, rest);
        LOG.log(Level.FINE, "Updated course for the teacher with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername()});
        return result;
    }

    public static DomCourseFull add(DomCourseFull edit) throws Dwo2Exception {
    	RestCourseFull rest = new RestCourseFull();
    	rest.setRestContext(RestAuthenticator.getInstance().getContext());
    	rest.setDomCourse(edit);
        DomCourseFull result = StoredRestManager.getInstance().put("rest/secure/teacher/course/add",DomCourseFull.class, rest);
        LOG.log(Level.FINE, "Updated course for the teacher with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername()});
        return result;
    }
}
