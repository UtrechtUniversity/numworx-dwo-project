package fi.dwo.dwojapplet.domain.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import fi.dwo.dwojapplet.REST.StoredRestManager;
import fi.dwo.dwojapplet.domain.DwoHelper;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
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
    	DomContext context = new DomContext();
// XXX is dit wel de goede plaats?
    	context.setDomHasRole(DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getHasRole());
    	rest.setDomCourse(edit);
    	rest.setRestContext(context);
        DomCourseFull result = StoredRestManager.getInstance().put("rest/secure/teacher/course/update",DomCourseFull.class, rest);
        LOG.log(Level.FINE, "Updated course for the teacher with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId()});
        return result;
    }
}
