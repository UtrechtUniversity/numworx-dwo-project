package nl.uu.fi.dwo.lms.gwtclient.gwt.roleswitch;

import com.google.gwt.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserSchoolLoginManagerV2;
import fi.dwo.gwt.lib.rest.ui.DialogEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

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
    private Map<String, DomSchoolRoleAndClassV2> sracData;
    private String[] tableHeaders = {"docentrollen"};

    private SecuredUserSchoolLoginManagerV2 manager = new SecuredUserSchoolLoginManagerV2();

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

    public class SchoolItem {

        private String key; //unique
        private String schoolName;

        public SchoolItem(String aKey, String value) {
            key = aKey;
            schoolName = value;
        }

        /**
         * @return the key
         */
        public String getKey() {
            return key;
        }

        /**
         * @param key the key to set
         */
        public void setKey(String key) {
            this.key = key;
        }

        /**
         * @return the schoolName
         */
        public String getSchoolName() {
            return schoolName;
        }

        /**
         * @param schoolName the schoolName to set
         */
        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }
    }

    public interface Display {

        void clear();

        void init();

        void updateView(Map<String, SwitchSchoolPresenter.SchoolItem> data, SwitchSchoolPresenter.SchoolItem selected);
    }

    public SwitchSchoolPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
    }

    private Map<String, DomSchoolRoleAndClassV2> getTeacherRoles() {
        Map<String, DomSchoolRoleAndClassV2> result = new HashMap<String, DomSchoolRoleAndClassV2>();
        DomSchoolsRolesAndClassesV2 sl = dwoGlobalVars.getSchoolLogins();
        List<DomSchoolRoleAndClassV2> fullList = sl.getSchoolsRolesAndClassesList();
        for (DomSchoolRoleAndClassV2 hasRole : fullList) {
            if (hasRole.getRole().getRoleName().equals("TEACHER")) {
                result.put(hasRole.getHasRole().getId().getIdString(), hasRole);
            }
        }
        return result;
    }

    public void init() {
        sracData = getTeacherRoles();
        Map<String, SchoolItem> data = new HashMap<String, SchoolItem>(sracData.size());
        for (DomSchoolRoleAndClassV2 srac : sracData.values()) {
            if (srac.getHasRole().getId().equals(srac.getHasRole().getId())) {
                selectedItem = new SchoolItem(srac.getHasRole().getId().getIdString(), srac.getSchool().getSchoolName());
            }
            SchoolItem item = selectedItem;
            data.put(item.getKey(), item);
        }
        view.init();
        view.updateView(data, selectedItem);
    }

    /**
     * @param row the course to set
     */
    @JsMethod
    public void select(SchoolItem item) {
        if (item != null) {
            selectedItem = item;
            return;
        }
    }

    @JsMethod
    public void switchSchool() {
        dwoGlobalVars.setActiveSchoolRoleAndClass(sracData.get(selectedItem.getKey()));
        DomSchoolRoleAndClassV2 srac = dwoGlobalVars.getActiveSchoolRoleAndClass();
        dwoGlobalVars.getSchoolLogins().setActiveSchoolRoleAndClass(srac);
        Promise<DomSchoolRoleAndClassV2> promise = manager.switchToSchoolLogin(srac);

        promise.then(new Success<DomSchoolRoleAndClassV2, Void>() {
            @Override
            public Promise<Void> call(Promise<DomSchoolRoleAndClassV2> resolved) throws Exception {
                if (!dwoGlobalVars.getSchoolLogins().getActiveSchoolRoleAndClass().getSchool().licenseIsValid()) {
                    eventBus.fireEvent(new DialogEvent(new Dwo2Exception(Dwo2ExceptionCode.Rest_Registration_School_license_expired, "License expired.")));
                };

                //flip back to schoolclasses screen 
                eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.WELCOME));
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
}
