package nl.uu.fi.dwo.account.client.boot.Results;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherResultsManager;
import java.util.logging.Logger;

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
        updateResultsInView();

    }

    public void updateResultsInView() {
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
