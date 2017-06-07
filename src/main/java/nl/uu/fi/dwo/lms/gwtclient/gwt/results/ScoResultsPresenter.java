package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import java.util.List;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class ScoResultsPresenter {

    private static final Logger LOG = Logger.getLogger(ScoResultsPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private int selectedIndex = 0;
    private List<DomStudentScoContext> resultScoData;

    private Display view;

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

    void goBackToResults() {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.RESULTS));
    }

    public interface Display {
        Widget asWidget();
        void clear();
        void init();
        void updateView(int height, int width, String[][] data);
    }

    public ScoResultsPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
    }

    public void init() {
//        sracData = getTeacherRoles();
//        int i = 0;
//        selectedIndex = i;
//        for (DomSchoolRoleAndClassV2 srac : sracData) {
//            if (srac.getHasRole().getId().equals(srac.getHasRole().getId())) {
//                selectedIndex = i;
//            }
//            i++;
//        }
//        String[][] data = buildPlotData();
//        view.init();
//        view.updateView(data.length,1,data);
    }

    /**
     * @param row the course to set
     */
    public void selectRow(int row) {
        if (row != -1) {
            selectedIndex = row;
            return;
        }
    }

    private String[][] buildPlotData() {
        String[][] data = new String[resultScoData.size()+1][1];
//        data[0][0] = "School";//<div style=\"text-align: left; background-color: #aaaaaa; padding: 2px; overflow auto;\">School</div>";
//        int i = 1;
//        selectedIndex=0;
//        for (DomSchoolRoleAndClassV2 srac : resultScoData) {
//            data[i][0] = srac.getSchool().getSchoolName();
//            if (srac.getHasRole().getId().equals(srac.getHasRole().getId())) {
//                selectedIndex = i;
//            }
//            i++;
//        }
        return data;
    }
}
