package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherResultsManager;

import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

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
        return DwoGlobalVars.instance().getProfile().then(new Success<DomDwoProfile, DomResultsPerTeacher>() {

			@Override
			public Promise<DomResultsPerTeacher> call(
					Promise<DomDwoProfile> resolved) throws Exception {
				return manager.getTeachersResults(context, resolved.getValue());
			}});
    }

}
