package fi.dwo.dwojapplet.domain.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import fi.dwo.dwojapplet.REST.StoredRestManager;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextFull;
import nl.uu.fi.dwo.rest.dom.entities.DomScoData;
import nl.uu.fi.dwo.rest.entities.RestCourseFull;
import nl.uu.fi.dwo.rest.entities.RestScoContextFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 * CRUD for teachers on courses.
 * @author wim
 *
 */
public class SecuredTeacherScoContextManager {
    private static final Logger LOG = Logger.getLogger(SecuredTeacherScoContextManager.class.getName());

    /** Update a course. Not all fields are updatable!
     * @param edit the course
     * @return the edited course
    */
    public static DomScoContextFull update(DomScoContextFull edit, DomScoData data) throws Dwo2Exception {
    	RestScoContextFull rest = new RestScoContextFull();
    	DomContext context = new DomContext();
// XXX is dit wel de goede plaats?
    	context.setDomHasRole(DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getHasRole());
    	rest.setDomScoContext(edit);
    	rest.setDomScoData(data);
    	rest.setRestContext(context);
    	rest.setDomDwoProfile(DWO.getDwoProfile());
        DomScoContextFull result = StoredRestManager.getInstance().put("rest/secure/teacher/scoContext/update",DomScoContextFull.class, rest);
        LOG.log(Level.FINE, "Updated course for the teacher with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId()});
        return result;
    }

	public static DomScoContextFull add(DomScoContextFull edit, DomScoData data) throws Dwo2Exception {
    	RestScoContextFull rest = new RestScoContextFull();
    	DomContext context = new DomContext();
// XXX is dit wel de goede plaats?
    	context.setDomHasRole(DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getHasRole());
    	rest.setDomScoContext(edit);
    	rest.setRestContext(context);
    	rest.setDomScoData(data);
    	rest.setDomDwoProfile(DWO.getDwoProfile());
        DomScoContextFull result = StoredRestManager.getInstance().put("rest/secure/teacher/scoContext/add",DomScoContextFull.class, rest);
        LOG.log(Level.FINE, "Updated course for the teacher with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId()});
        return result;
	}
}
