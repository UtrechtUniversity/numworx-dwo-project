package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.dwojapplet.REST.StoredRestManager;
import fi.dwo.dwojapplet.domain.DwoHelper;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 *
 * @author G.A.J. van der Plas
 */
public class SecuredTeacherResultsManager {
    private static final Logger LOG = Logger.getLogger(SecuredTeacherResultsManager.class.getName());

    public static DomResultsPerTeacher getTeachersResults(DomDwoProfile profile) throws Dwo2Exception {
        RestDwoProfile restProfile = new RestDwoProfile();
        restProfile.setDomDwoProfile(profile);
        
        DomContext context = new DomContext();
        context.setDomHasRole(DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getHasRole());
        restProfile.setRestContext(context);
        DomResultsPerTeacher src;
        src = StoredRestManager.getInstance().put("rest/secure/teacher/results/getTeachersResults",DomResultsPerTeacher.class, restProfile);
        LOG.log(Level.FINE, "Retrieved teacher results for the teacher with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId()});
        return src;
    }
}
