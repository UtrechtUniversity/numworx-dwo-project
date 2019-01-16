package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import com.google.web.bindery.event.shared.EventBus;

import java.util.logging.Logger;

import javax.inject.Inject;

import org.osgi.util.promise.Failure;

import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.DomResultTree;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

/**
 * Selects a student within a school. All results of this student within the school
 * are retrieved of any module assigned to one or more school classes of the teacher.
 *
 * @author Gert van der Plas
 */
public class SelectStudentResultsPresenter {

    private static final Logger LOG = Logger.getLogger(SelectStudentResultsPresenter.class.getName());

    private final EventBus eventBus;
    private final DwoGlobalVars dwoGlobalVars;
    private final Failure FAILURE;

    private Display view;
    @Inject ResultsService resultService;
    private DomResultTree resultTree;
    public interface Display  extends BasicDisplay{

        void setResultTree(DomResultTree data);

        void setEmptyTableMessage();

        void setLoadingTableMessage();

    }

    @Inject SelectStudentResultsPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        FAILURE = new LoggingFailure(LOG,anEventBus);
    }

    public void init(DomResultTree aResultTree) {
        view.init();
        resultTree = aResultTree;
    }

    public void setView(Display aView) {
        view = aView;
        view.setHelp(dwoGlobalVars.buildHelpUrl("#editStudent"));        
    }

    @JsMethod
    public void showSelectedResults() {
        eventBus.fireEvent(new AlertDialogWithOKEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));        
    }
}
