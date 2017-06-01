package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class SchoolclassesPresenter {

    private static final Logger LOG = Logger.getLogger(SchoolclassesPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private int selectedIndex = 0;
    private List<DomSchoolRoleAndClassV2> sracData;

    private Display view;

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

    public interface Display {
        Widget asWidget();
        void clear();
        void init();
        void updateView(int height, int width, String[][] data);
    }

    SchoolclassesPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
    }

    private List<DomSchoolRoleAndClassV2> getTeacherRoles() {
        List<DomSchoolRoleAndClassV2> result = new ArrayList<DomSchoolRoleAndClassV2>();
        DomSchoolsRolesAndClassesV2 sl = dwoGlobalVars.getSchoolLogins();
        List<DomSchoolRoleAndClassV2> fullList = sl.getSchoolsRolesAndClassesList();
        for (DomSchoolRoleAndClassV2 hasRole : fullList) {
            if (hasRole.getRole().getRoleName().equals("TEACHER")) {
                result.add(hasRole);
            }
        }
        return result;
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
        view.init();
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

    public void switchSchool() {
        dwoGlobalVars.setActiveSchoolRoleAndClass(sracData.get(selectedIndex));
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.RESULTS));
    }

    private String[][] buildPlotData() {
        String[][] data = new String[sracData.size()+1][1];
        data[0][0] = "School";//<div style=\"text-align: left; background-color: #aaaaaa; padding: 2px; overflow auto;\">School</div>";
        int i = 1;
        selectedIndex=0;
        for (DomSchoolRoleAndClassV2 srac : sracData) {
            data[i][0] = srac.getSchool().getSchoolName();
            if (srac.getHasRole().getId().equals(srac.getHasRole().getId())) {
                selectedIndex = i;
            }
            i++;
        }
        return data;
    }
}
