package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserCourseManager;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class EditCoursesInSchoolclassPresenter {

    private static final Logger LOG = Logger.getLogger(EditCoursesInSchoolclassPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private SecuredUserCourseManager manager = new SecuredUserCourseManager();

    private String[] tableHeaders = {"givenname", "insertion", "familyname", "usercode", "edit", "select"};
    private DomSchoolClass schoolClass;
    private Map<String, DomStudent> studentMap;
    private Map<String, EditCoursesInSchoolclassPresenter.CourseItem> studentItems;
    private Map<String, DomSchoolClass> schoolClassMap;
    private List<SchoolClassListBoxItem> schoolClassItems;
    private Display view;
    private int requests = 0;

    public interface Display {

        Widget asWidget();

        void clear();

        void init();

        void updateView(Map<String, EditCoursesInSchoolclassPresenter.CourseItem> data);
    }

    public class CourseItem {

        public String key; //unique
        public String givenName;
        public String insertion;
        public String familyName;
        public String usercode;
        public boolean singleSchool;
        public boolean selected;

        public CourseItem(String aKey, String aFirstName, String anInsertion, String aFamilyName, String aUsercode, boolean aSingleSchool) {
            key = aKey;
            givenName = aFirstName;
            insertion = anInsertion;
            familyName = aFamilyName;
            usercode = aUsercode;
            singleSchool = aSingleSchool;
            selected = false;
        }
    }

    public EditCoursesInSchoolclassPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
    }

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

    public void init(DomSchoolClass aSchoolClass) {
        schoolClass = aSchoolClass;
        view.init();
    }

    public String[] getTableHeaders() {
        return tableHeaders;
    }

    void goBackToSchoolClasses() {
   eventBus.fireEvent (new SwitchViewEvent(SwitchViewEvent.SelectedView.SCHOOLCLASSES));
    }

}
