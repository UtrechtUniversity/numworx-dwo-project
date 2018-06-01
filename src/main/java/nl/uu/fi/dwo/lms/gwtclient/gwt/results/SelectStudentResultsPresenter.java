package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import com.google.web.bindery.event.shared.EventBus;

import java.util.logging.Logger;
import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
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

    private Display view;
    private ResultsService resultService;
    private DomResultTree resultTree;
    public interface Display {

        void clear();

        void setResultTree(DomResultTree data);

        void setEmptyTableMessage();

        void setLoadingTableMessage();

    }

    public SelectStudentResultsPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        resultService = new ResultsService(dwoGlobalVars);

    }

    public void init(DomResultTree aResultTree) {
        resultTree = aResultTree;
    }

    public void setView(Display aView) {
        view = aView;
    }

    @JsMethod
    public void showSelectedResults() {
        eventBus.fireEvent(new AlertDialogWithOKEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));        
    }
}
