/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.ui.DialogEvent;
import jsinterop.annotations.JsMethod;

import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.DomResultTree;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomResultCourseInClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 *
 * @author plas0006
 */
public class SelectedResultsPresenter {

    private static final Logger LOG = Logger.getLogger(SelectedResultsPresenter.class.getName());

    private final EventBus eventBus;
    private final DwoGlobalVars dwoGlobalVars;

    private Display view;
    private ResultsService resultService;
    private JavaScriptObject resultState;
    //model
    private DomResultTree resultTree;

    public interface Display {

        void clear();

        void updateResultTree(DomResultTree data);

        void init(JavaScriptObject aResultState);

        void setEmptyTableMessage();

        void setLoadingTableMessage();

    }

    public SelectedResultsPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        resultService = new ResultsService(dwoGlobalVars);

    }

    public void init(DomResultTree aResultTree, JavaScriptObject aResultState) {
        resultTree = aResultTree;
        resultState = aResultState;
        view.init(aResultState);
    }

    public void updateTree() {
        //view.clear();
        LOG.log(Level.INFO, "DwoGlobalVarsState = " + dwoGlobalVars.getState().name());
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
                view.updateResultTree(resultTree);
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
    public void sealModuleActivities(String courseID, String classid) {
    	PersistenceId id = new PersistenceId(courseID);
    	DomCourse course = new DomCourse();course.setId(id);
    	
    	
    // TODO verzegel course, dus alle activitetien	
    }
    
    @JsMethod
    public void sealSingleActivity(String scoId, String classid) {
    	resultTree.getPlottedResultTree();
    }
    
}
