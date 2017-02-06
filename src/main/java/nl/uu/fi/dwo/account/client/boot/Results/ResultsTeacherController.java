package nl.uu.fi.dwo.account.client.boot.Results;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherResultsManager;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import org.osgi.util.promise.Promise;

/**
 * Controller for Teacher results.
 * 
 * @author Gert van der Plas
 */
class ResultsTeacherController {

    private static final Logger LOG = Logger.getLogger(ResultsTeacherController.class.getName());

    private ResultPanel view;
    private SecuredTeacherResultsManager manager = new SecuredTeacherResultsManager();
    

    ResultsTeacherController(ResultPanel view) {
        this.view = view;
        init();
    }

    public void init() {
        //updateResultsInView();

    }

    public void updateResultsInView() {
        DomContext context = new DomContext();
        context.setDomHasRole(DwoGlobalVars.instance().getSchoolLogins().getActiveSchoolRoleAndClass().getHasRole());
        DomDwoProfile profile = new DomDwoProfile();
        Promise<DomResultsPerTeacher> promResults = manager.getTeachersResults(context, profile);
        DomResultsPerTeacher results = promResults.getValue();
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
