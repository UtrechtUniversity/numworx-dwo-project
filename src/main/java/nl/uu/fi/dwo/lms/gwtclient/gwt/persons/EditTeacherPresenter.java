package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.logging.Logger;
import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomRemoveTeacherFromSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitTeacherToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.entities.RestTeacher;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

/**
 * Login Presenter.
 *
 * @author G.A.J. van der Plas
 */
public class EditTeacherPresenter {

    private static final Logger LOG = Logger.getLogger(EditTeacherPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private Display view;
    private SecuredTeacherSchoolClassManager manager = new SecuredTeacherSchoolClassManager();
    private Map<String, TaggedDomSchoolClass> taggedSchoolClassMap;
    private DomUser user;

    public interface Display extends BasicDisplay {

        void setUser(DomUser user);

        void setSchoolClasses(Map<String, TaggedDomSchoolClass> schoolClasses);

        void setEmptyTableMessage();

        void setLoadingTableMessage();
    }

    /**
     * @return the view
     */
    public Display getView() {
        return view;
    }

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

    public EditTeacherPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
    }

    public void init(DomUser aUser) {
        view.clear();
        view.setHelp(dwoGlobalVars.buildHelpUrl("#editTeacher"));                
        view.setEmptyTableMessage();
        view.setUser(aUser);
        user = aUser;
        initView(aUser);
//        setSchoolClassesInView(aUser);
    }

    public void initView(DomUser aUser) {
        Promise p = Promises.resolved(null);

        //fetch schoolclasses
        p=p.then((resolved) -> {
            return manager.getTeachersSchoolClasses();

        }).then((resolved) -> {
            List<DomSchoolClass> classList = (List<DomSchoolClass>) resolved.getValue();
            taggedSchoolClassMap = new HashMap<String, TaggedDomSchoolClass>(classList.size());
            classList.forEach((v) -> taggedSchoolClassMap.put(v.getId().getIdString(), new TaggedDomSchoolClass(v)));
            DomTeacher teacher = new DomTeacher(aUser);
            RestTeacher rest = new RestTeacher();
            rest.setDomTeacher(teacher);
            DomContext ctx = new DomContext();
            ctx.setDomHasRole(dwoGlobalVars.getSchoolLogins().getActiveSchoolRoleAndClass().getHasRole());
            rest.setRestContext(ctx);
            return manager.getSharedTeacherClasses(rest);
        }).then((resolved) -> {
            List<DomSchoolClassId> studentClassList = (List<DomSchoolClassId>) resolved.getValue();
            studentClassList.forEach((v) -> {
                taggedSchoolClassMap.get(v.getId().getIdString()).setTag(true);
            });
            view.setSchoolClasses(taggedSchoolClassMap);
            return Promises.resolved(null);
        }).then(null, (failure) -> {
            eventBus.fireEvent(new AlertDialogWithOKEvent(failure.getFailure().getMessage()));
        });
    }

    @JsMethod
    public void removeTeacherFromSchoolClass(String schoolClassId) {
        DomSchoolClass sc = taggedSchoolClassMap.get(schoolClassId).getSchoolClass();
        DomRemoveTeacherFromSchoolClass data = new DomRemoveTeacherFromSchoolClass();
        data.setSchoolClass(sc);
        data.setTeacher(new DomTeacher(user));
        Promise<Boolean> p = manager.removeTeacherFromSchoolClass(data);
        p.then((resolved) -> {
            this.initView(user);
            return Promises.resolved(true);
        }).then(null, (failure) -> {
            eventBus.fireEvent(new AlertDialogWithOKEvent(failure.getFailure().getMessage()));
        });
    }

    @JsMethod
    public void submitTeacherToSchoolClass(String schoolClassId) {
        DomSchoolClass sc = taggedSchoolClassMap.get(schoolClassId).getSchoolClass();
        DomSubmitTeacherToSchoolClass data = new DomSubmitTeacherToSchoolClass();
        data.setSchoolClass(sc);
        data.setTeacher(new DomTeacher(user));
        Promise<Boolean> p = manager.submitTeacherToSchoolClass(data);
        p.then((resolved) -> {
            this.initView(user);
            return Promises.resolved(true);
        }).then(null, (failure) -> {
            eventBus.fireEvent(new AlertDialogWithOKEvent(failure.getFailure().getMessage()));
        });
    }

}
