package nl.uu.fi.dwo.lms.gwtclient.gwt.results.old;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherResultsManager;

import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.entities.DomClearStudentDataForScoAndClass;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.entities.RestClearStudentDataForScoAndClass;
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
class ScoResultsService {

    private static final Logger LOG = Logger.getLogger(ScoResultsService.class.getName());

    private SecuredTeacherResultsManager manager = new SecuredTeacherResultsManager();    
    private final DwoGlobalVars dwoGlobalVars;
    
    public ScoResultsService(DwoGlobalVars aDwoGlobalVars){
        dwoGlobalVars=aDwoGlobalVars;
    }
    
    public Promise<Boolean> clearStudentResults(DomSchoolClass sc, DomScoContext sco) {
        DomContext context = new DomContext();
        context.setDomHasRole(dwoGlobalVars.getActiveSchoolRoleAndClass().getHasRole());
        return dwoGlobalVars.getProfile().then(new Success<DomDwoProfile, Boolean>() {

			@Override
			public Promise<Boolean> call(
					Promise<DomDwoProfile> resolved) throws Exception {
                            RestClearStudentDataForScoAndClass rest = new RestClearStudentDataForScoAndClass();
                            rest.setRestContext(context);
                            DomClearStudentDataForScoAndClass dom = new DomClearStudentDataForScoAndClass();
                            dom.setDomProfile(resolved.getValue());
                            dom.setDomSchoolClass(sc);
                            dom.setDomScoContext(sco);
                            dom.setDomStudentList(null);
                            rest.setClearStudentDataForScoAndClass(dom);
				return manager.clearStudentResults(rest);
			}});
    }
   
}
