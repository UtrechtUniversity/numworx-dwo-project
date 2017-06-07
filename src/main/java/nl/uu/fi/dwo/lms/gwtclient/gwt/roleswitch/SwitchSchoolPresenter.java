package nl.uu.fi.dwo.lms.gwtclient.gwt.roleswitch;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class SwitchSchoolPresenter {

    private static final Logger LOG = Logger.getLogger(SwitchSchoolPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private SchoolItem selectedItem;
    private Map<String,DomSchoolRoleAndClassV2> sracData;
        private String[] tableHeaders = {"school"};

    private Display view;

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

    public String[] getTableHeaders() {
        return tableHeaders;
    }
    
    public class SchoolItem{
        public String key; //unique
        public String schoolName;
        public SchoolItem(String aKey, String value){
            key = aKey;
            schoolName = value;
        }
    }

    public interface Display {
        Widget asWidget();
        void clear();
        void init();
        void updateView(Map<String,SwitchSchoolPresenter.SchoolItem>  data);
    }

    public SwitchSchoolPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
    }

    private Map<String,DomSchoolRoleAndClassV2> getTeacherRoles() {
        Map<String,DomSchoolRoleAndClassV2> result = new HashMap<String,DomSchoolRoleAndClassV2>();
        DomSchoolsRolesAndClassesV2 sl = dwoGlobalVars.getSchoolLogins();
        List<DomSchoolRoleAndClassV2> fullList = sl.getSchoolsRolesAndClassesList();
        for (DomSchoolRoleAndClassV2 hasRole : fullList) {
            if (hasRole.getRole().getRoleName().equals("TEACHER")) {
                result.put(hasRole.getHasRole().getId().getIdString(),hasRole);
            }
        }
        return result;
    }

    public void init() {
        sracData = getTeacherRoles();
        Map<String,SchoolItem> data = new HashMap<String,SchoolItem>(sracData.size());
        for (DomSchoolRoleAndClassV2 srac : sracData.values()) {
            if (srac.getHasRole().getId().equals(srac.getHasRole().getId())) {
                selectedItem =  new SchoolItem(srac.getHasRole().getId().getIdString(), srac.getSchool().getSchoolName());
            }
            SchoolItem item = selectedItem;
            data.put(item.key,item);
        }
        view.init();
        view.updateView(data);
    }

    /**
     * @param row the course to set
     */
    public void select(SchoolItem item) {
        if (item != null) {
            selectedItem = item;
            return;
        }
    }

    public void switchSchool() {
        dwoGlobalVars.setActiveSchoolRoleAndClass(sracData.get(selectedItem.key));
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.RESULTS));
    }

//    private String[][] buildPlotData() {
//        String[][] data = new String[sracData.size()+1][1];
//        data[0][0] = "School";//<div style=\"text-align: left; background-color: #aaaaaa; padding: 2px; overflow auto;\">School</div>";
//        int i = 1;
//        selectedIndex=0;
//        for (DomSchoolRoleAndClassV2 srac : sracData) {
//            data[i][0] = srac.getSchool().getSchoolName();
//            if (srac.getHasRole().getId().equals(srac.getHasRole().getId())) {
//                selectedIndex = i;
//            }
//            i++;
//        }
//        return data;
//    }
}
