package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import com.google.gwt.json.client.JSONObject;
import com.google.web.bindery.event.shared.EventBus;

import fi.dwo.gwt.lib.rest.ui.DialogEvent;

import java.util.logging.Level;
import java.util.logging.Logger;
import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.rest.dom.DomResultPlotMatrix;
import nl.uu.fi.dwo.rest.dom.DomResultTree;
import nl.uu.fi.dwo.rest.dom.entities.DomResultCourseInClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Controller for ResultsView actions.
 *
 * @author Gert van der Plas
 */
public class ResultsPresenter {

    private static final Logger LOG = Logger.getLogger(ResultsPresenter.class.getName());

    private final EventBus eventBus;
    private final DwoGlobalVars dwoGlobalVars;

    private Display view;
    private ResultsService resultService;
    //model
    private DomResultTree resultTree;
    private DomResultPlotMatrix resultMatrix;
    private DomResultCourseInClass course = null; //null means all courses.
    private DomResultSchoolClass schoolClass = null; //null means all classes.

    public interface Display {

        void clear();

        void setResultTree(DomResultTree data);

        void setEmptyTableMessage();

        void setLoadingTableMessage();

    }

    public ResultsPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        resultService = new ResultsService(dwoGlobalVars);

    }

    public void init() {
        //view.clear();
        LOG.log(Level.INFO, "DwoGlobalVarsState = " + dwoGlobalVars.getState().name());
        course = null;
        schoolClass = null;
        Promise<DomResultsPerTeacher> promResults;
        promResults = resultService.getResultsPerTeacher();
        // onSuccess calculate results and show.
        promResults.then(new Success<DomResultsPerTeacher, Void>() {
            @Override
            public Promise<Void> call(Promise<DomResultsPerTeacher> resolved) throws Exception {
                //calculate tree and call plotting
                LOG.log(Level.INFO, "DomResults returned.");
                resultTree = new DomResultTree(resolved.getValue());
                LOG.log(Level.INFO, "ResultTree obtained.");// plots the result tree.
                view.setResultTree(resultTree);
                LOG.log(Level.INFO, "plotted ResultMatrix.");
                return null;
            }
        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                if (fail instanceof Dwo2Exception) {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new DialogEvent((Dwo2Exception) fail));
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new DialogEvent(fail.getMessage()));
                    //throw directly
                }
            }
        });
    }

    public void setView(Display aView) {
        view = aView;
    }

    @JsMethod
    public void showSelectedResults(String schoolClassId, boolean showOpenModules, boolean showClosedModules, JSONObject courseIds) {
        DomResultSchoolClass resultSchoolClass = resultTree.getStudentTree().getChildren().get(schoolClassId);
        eventBus.fireEvent(
                new SwitchViewEvent(SwitchViewEvent.SelectedView.SELECTEDRESULTS, resultTree,
                        resultSchoolClass.getSchoolClass(), showOpenModules, showClosedModules, courseIds)
        );
    }

    @JsMethod
    public void selectStudentResults() {
        eventBus.fireEvent(
                new SwitchViewEvent(SwitchViewEvent.SelectedView.SELECTSTUDENTRESULTS, resultTree)
        );
    }

}
