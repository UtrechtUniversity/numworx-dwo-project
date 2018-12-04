package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import java.util.logging.Logger;

import javax.inject.Inject;

import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelDeferred;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomRemoveTeacherFromSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitTeacherToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.entities.RestTeacher;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

/**
 * Login Presenter.
 *
 * @author G.A.J. van der Plas
 */
public class EditTeacherPresenter {

    private static final Logger LOG = Logger.getLogger(EditTeacherPresenter.class.getName());
    private final DwoGlobalVars dwoGlobalVars;
    private final EventBus eventBus;
    private Display view;
    private final PersonsService manager;
    private Map<String, TaggedDomSchoolClass> taggedSchoolClassMap;
    private DomUser user;
    private final LoggingFailure FAILURE;

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

    @Inject EditTeacherPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars, PersonsService m) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        manager = m;
        FAILURE = new LoggingFailure(LOG, anEventBus);
    }

    public void init(DomUser aUser) {
        view.clear();
        view.init();
        view.setHelp(dwoGlobalVars.buildHelpUrl("#editTeacher"));
        view.setEmptyTableMessage();
        view.setUser(aUser);
        user = aUser;
        initView(aUser);
//        setSchoolClassesInView(aUser);
    }

    public void initView(DomUser aUser) {

        //fetch schoolclasses
        manager.getTeachersSchoolClasses()
        .then((resolved) -> {
            List<DomSchoolClass> classList = resolved.getValue();
            taggedSchoolClassMap = new HashMap<String, TaggedDomSchoolClass>(classList.size());
            classList.forEach((v) -> taggedSchoolClassMap.put(v.getId().getIdString(), new TaggedDomSchoolClass(v)));
            DomTeacher teacher = new DomTeacher(aUser);
            RestTeacher rest = new RestTeacher();
            rest.setDomTeacher(teacher);
            DomContext ctx = new DomContext();
            ctx.setDomHasRole(dwoGlobalVars.getActiveSchoolRoleAndClass().getHasRole());
            rest.setRestContext(ctx);
            return manager.getSharedTeacherClasses(rest);
        }).then((resolved) -> {
            List<DomSchoolClassId> studentClassList = resolved.getValue();
            studentClassList.forEach((v) -> {
                taggedSchoolClassMap.get(v.getId().getIdString()).setTag(true);
            });
            view.setSchoolClasses(taggedSchoolClassMap);
            return null;
        }).then(null, (failure) -> {
            eventBus.fireEvent(new AlertDialogWithOKEvent(failure.getFailure().getMessage()));
        });
    }

    @JsMethod
    public void removeTeacherFromSchoolClass(String schoolClassId) {
        Promise<Boolean> p = Promises.resolved(true); //empty promise

        //ask to be sure if removing yourself!
        if (user.getId().getIdString().equals(dwoGlobalVars.getCurrentUser().getId().getIdString())) {
            p = p.then(new Success<Boolean, Boolean>() {
                @Override
                //Are you sure?
                public Promise<Boolean> call(Promise<Boolean> resolved) throws Exception {//do dialog check
                    String msg = DwoLocalesForGWT.instance.NUM_DLG_EDITTEACHER_Q_RemoveClassFromTeacher();
                    AlertDialogWithConfirmCancelDeferred dialogPromise = new AlertDialogWithConfirmCancelDeferred(msg);
                    AlertDialogWithConfirmCancelEvent event = new AlertDialogWithConfirmCancelEvent(AlertDialogWithConfirmCancelEvent.EventType.ConfirmDialog, dialogPromise);
                    eventBus.fireEvent(event);
                    return dialogPromise.getPromise();
                }
            });
        };

        //remove
        p.then(new Success<Boolean, Boolean>() {
            //sure so remove
            @Override
            public Promise<Boolean> call(Promise<Boolean> resolved) throws Exception {
                if (resolved.getValue()) {
                    DomSchoolClass sc = taggedSchoolClassMap.get(schoolClassId).getSchoolClass();
                    DomRemoveTeacherFromSchoolClass data = new DomRemoveTeacherFromSchoolClass();
                    data.setSchoolClass(sc);
                    data.setTeacher(new DomTeacher(user));
                    Promise<Boolean> p = manager.removeTeacherFromSchoolClass(data);
                    return p;
                } else {
                    LOG.log(Level.INFO, "Unsubscribe schoolclass cancelled.");
                    throw new Dwo2Exception(Dwo2ExceptionCode.User_Cancelled_RemoveTeacherFromSchoolClass, "Unsubscribe schoolclass cancelled.");
                }
            }
        }).then((resolved) -> {
            this.initView(user);
            return Promises.resolved(true);
        }).then(null, (failure) -> {
            Throwable fail = failure.getFailure();
           if (fail instanceof Dwo2Exception) {                
                        LOG.log(Level.SEVERE, fail.getMessage());
                        eventBus.fireEvent(new AlertDialogWithOKEvent((Dwo2Exception) fail));
                    } else {                
                        LOG.log(Level.SEVERE, fail.getMessage());
                        eventBus.fireEvent(new AlertDialogWithOKEvent(fail.getMessage()));
                        //throw directly
                    }
                this.initView(user);// eventBus.fireEvent(new AlertDialogWithOKEvent(failure.getFailure().getMessage()));
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
