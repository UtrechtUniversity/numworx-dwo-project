package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private String[] tableHeaders = { "classname", "edit", "modules","students", "teachers", "remove"};

    private Display view;
    
    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

    void addASchoolClass() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public interface Display {
        Widget asWidget();
        void clear();
        void init();
        void updateView(Map<String,SchoolclassesPresenter.ClassItem>  data);
    }

    public class ClassItem{
        public String key; //unique
        public String schoolclassName;
        public ClassItem(String aKey, String value){
            key = aKey;
            schoolclassName = value;
        }
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

    public String[] getTableHeaders(){
        return tableHeaders;
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

//    public void switchSchool() {
//        dwoGlobalVars.setActiveSchoolRoleAndClass(sracData.get(selectedIndex));
//        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.RESULTS));
//    }

}
