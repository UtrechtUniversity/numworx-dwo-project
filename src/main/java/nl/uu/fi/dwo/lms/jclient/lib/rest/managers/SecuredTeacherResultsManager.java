package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
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
        RestDwoProfile rest = new RestDwoProfile();
        rest.setDomDwoProfile(profile);
    	rest.setRestContext(RestAuthenticator.getInstance().getContext());
        DomResultsPerTeacher src;
        src = StoredRestManager.getInstance().put("rest/secure/teacher/results/getTeachersResults",DomResultsPerTeacher.class, rest);
        LOG.log(Level.FINE, "Retrieved teacher results for the teacher with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername()});
        return src;
    }
}
