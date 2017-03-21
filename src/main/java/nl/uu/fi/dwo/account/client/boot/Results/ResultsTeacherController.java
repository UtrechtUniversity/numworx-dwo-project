package nl.uu.fi.dwo.account.client.boot.Results;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherResultsManager;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import org.osgi.util.promise.Promise;

/**
 * Persistent model Controller for Teacher results. Retrieves DomResultsPerTeacher data.
 * In the future it may cache this data and merge updates into it. it may also request
 * Updates if required. In example, fetch new data if older than xx seconds or
 * Check if changes of results exist within the schoolgroup.
 * 
 * @author Gert van der Plas
 */
class ResultsTeacherController {

    private static final Logger LOG = Logger.getLogger(ResultsTeacherController.class.getName());

    private SecuredTeacherResultsManager manager = new SecuredTeacherResultsManager();
    
    public Promise<DomResultsPerTeacher> getResultsPerTeacher() {
        DomContext context = new DomContext();
        context.setDomHasRole(DwoGlobalVars.instance().getSchoolLogins().getActiveSchoolRoleAndClass().getHasRole());
        DomDwoProfile profile = new DomDwoProfile();
        profile.setId(new PersistenceId("MYSQL;PersistentDwoProfile;00000000000000000001"));;
        profile.setDwoProfileName("test");
        profile.setDwoProfileRights("_");
        //Promise<DomResultsPerTeacher> promResults = 
        return manager.getTeachersResults(context, profile);
//        manager.getTeachersResults(new AsyncCallback<List<DomSchoolClass>>() {
//            @Override
//            public void onFailure(Throwable t) {
//                //fail and reset all the data.
//                LOG.log(Level.INFO, t.getMessage());
//                DwoViewer.showMessage(Dwo2ExceptionCode.Rest_ConnectionTimeout);
//            }
//
//            @Override
//            public void onSuccess(List<DomSchoolClass> result) {
//                //success and set all the data in the view
//                LOG.log(Level.INFO, "Fetched students schoolclasses.");
//                schoolClasses = result;
//                view.setSchoolClasses(schoolClasses);
//            }
//        });
    }

}
