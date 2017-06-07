package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import com.google.gwt.i18n.client.NumberFormat;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherResultsManager;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import org.osgi.util.promise.Promise;

/**
 * Persistent model service for Teacher results. Retrieves DomResultsPerTeacher data.
 * In the future it may cache this data and merge updates into it. it may also request
 * Updates if required. In example, fetch new data if older than xx seconds or
 * Check if changes of results exist within the schoolgroup.
 * 
 * @author Gert van der Plas
 */
class ResultsService {

    private static final Logger LOG = Logger.getLogger(ResultsService.class.getName());

    private SecuredTeacherResultsManager manager = new SecuredTeacherResultsManager();
    
    public Promise<DomResultsPerTeacher> getResultsPerTeacher() {
        DomContext context = new DomContext();
        context.setDomHasRole(DwoGlobalVars.instance().getActiveSchoolRoleAndClass().getHasRole());
        DomDwoProfile profile = new DomDwoProfile();
        int profileId = DwoGlobalVars.instance().getProfileId();
        String formattedId = NumberFormat.getFormat("00000000000000000000").format(profileId);
        profile.setId(new PersistenceId("MYSQL;PersistentDwoProfile;"+formattedId));
        profile.setDwoProfileName("test");
        profile.setDwoProfileRights("_");
        return manager.getTeachersResults(context, profile);

    }

}
