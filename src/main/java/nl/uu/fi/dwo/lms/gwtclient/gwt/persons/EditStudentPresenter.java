package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.CallManagers.MD5;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import java.util.logging.Logger;
import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelDeferred;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.MessageDialogWithOKEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomRemoveStudentFromSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.SimpleValidUserFieldsChecker;
import nl.uu.fi.dwo.rest.entities.RestGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestStudent;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

/**
 * Login Presenter.
 *
 * @author G.A.J. van der Plas
 */
public class EditStudentPresenter {

    private static final Logger LOG = Logger.getLogger(EditStudentPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private Display view;
    private SecuredTeacherSchoolClassManager manager = new SecuredTeacherSchoolClassManager();
    private Map<String, TaggedDomSchoolClass> taggedSchoolClassMap;
    private DomUserFull fullUser;
    private DomUser user;

    public interface Display extends BasicDisplay {

        void setUser(DomUser student);

        void setSingleSchoolStudent(DomUserFull student);

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

    public EditStudentPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
    }

    public void init(DomUser aUser) {
        view.clear();
        view.setEmptyTableMessage();
        view.setHelp(dwoGlobalVars.buildHelpUrl("#editStudent"));
        initView(aUser);
        user = aUser;
//        setSchoolClassesInView(aUser);
    }

//    public void setStudentInView(DomUser aUser) {
//        Promise<DomSingleSchoolStudent> userPromise;
//        DomGetSingleSchoolStudent getStudent = new DomGetSingleSchoolStudent();
//        getStudent.setDomStudent(new DomStudent(aUser));
//        //getStudent.setDomSchoolClass();
//        userPromise = manager.getSingleSchoolStudent(getStudent);
//
//        // onSuccess calculate results and show.
//        userPromise.then(new Success<DomUserFull, Void>() {
//            @Override
//            public Promise<Void> call(Promise<DomUserFull> resolved) throws Exception {
//                // calculate tree and call plotting
//                LOG.log(Level.INFO, "DomFullUser data returned.");
//                user = resolved.getValue();
//                view.setSingleSchoolStudent(user);
//                return null;
//            }
//        }, new Failure() {
//            @Override
//            public void fail(Promise<?> resolved) throws Exception {
//                Throwable fail = resolved.getFailure();
//                if (fail instanceof Dwo2Exception) {
//                    LOG.log(Level.SEVERE, fail.getMessage());
//                    eventBus.fireEvent(new AlertDialogWithOKEvent((Dwo2Exception) fail));
//                } else {
//                    LOG.log(Level.SEVERE, fail.getMessage());
//                    eventBus.fireEvent(new AlertDialogWithOKEvent(fail.getMessage()));
//                    // throw directly
//                }
//            }
//        });
//    }
    public void initView(DomUser aUser) {
        Promise p = Promises.resolved(null);

        //fetch schoolclasses
        p.then((resolved) -> {
            return manager.getTeachersSchoolClasses();

        }).then((resolved) -> {
            List<DomSchoolClass> classList = (List<DomSchoolClass>) resolved.getValue();
            taggedSchoolClassMap = new HashMap<String, TaggedDomSchoolClass>(classList.size());
            classList.forEach((v) -> taggedSchoolClassMap.put(v.getId().getIdString(), new TaggedDomSchoolClass(v)));
            DomStudent student = new DomStudent(aUser);
            RestStudent rest = new RestStudent();
            rest.setDomStudent(student);
            DomContext ctx = new DomContext();
            ctx.setDomHasRole(dwoGlobalVars.getSchoolLogins().getActiveSchoolRoleAndClass().getHasRole());
            rest.setRestContext(ctx);
            return manager.getTeachersClassesOfStudent(rest);
        }).then((resolved) -> {
            List<DomSchoolClassId> studentClassList = (List<DomSchoolClassId>) resolved.getValue();
            studentClassList.forEach((v) -> {
                taggedSchoolClassMap.get(v.getId().getIdString()).setTag(true);
            });
            view.setSchoolClasses(taggedSchoolClassMap);
            return Promises.resolved(null);
        });
        //if singleschool fetch user
        if (aUser.getSingleSchool()) {
            p.then((resolved) -> {
                DomContext ctx = new DomContext();
                DomGetSingleSchoolStudent student = new DomGetSingleSchoolStudent(new DomStudent(aUser));
                ctx.setDomHasRole(dwoGlobalVars.getSchoolLogins().getActiveSchoolRoleAndClass().getHasRole());
                for (TaggedDomSchoolClass sc : taggedSchoolClassMap.values()) {
                    if (sc.isTag()) {
                        student.setDomSchoolClass(sc.getSchoolClass());
                        break;
                    }
                }
                if (student.getDomSchoolClass() != null) {
                    RestGetSingleSchoolStudent restData = new RestGetSingleSchoolStudent();
                    restData.setRestContext(ctx);
                    restData.setDomGetSingleSchoolStudent(student);
                    return manager.getSingleSchoolStudent(restData);
                } else {
                    return Promises.resolved(null);
                }

            }).then((resolved) -> {
                DomSingleSchoolStudent student = (DomSingleSchoolStudent) resolved.getValue();
                fullUser = student;
                view.setSingleSchoolStudent(student);
                return Promises.resolved(null);
            });
        } else {
            view.setUser(aUser);
        }

        p.then(null, (failure) -> {
            eventBus.fireEvent(new AlertDialogWithOKEvent(failure.getFailure().getMessage()));
        });
    }

    @JsMethod
    public void removeStudentFromSchoolClass(String schoolClassId) {
        DomSchoolClass sc = taggedSchoolClassMap.get(schoolClassId).getSchoolClass();
        DomRemoveStudentFromSchoolClass data = new DomRemoveStudentFromSchoolClass();
        data.setSchoolClass(sc);
        data.setStudent(new DomStudent(user));
        Promise<Boolean> p = manager.removeStudentFromSchoolClass(data);
        p.then((resolved) -> {
            this.initView(user);
            return Promises.resolved(true);
        });
        p.then(null, (failure) -> {
            eventBus.fireEvent(new AlertDialogWithOKEvent(failure.getFailure().getMessage()));
        });
    }

    @JsMethod
    public void submitStudentToSchoolClass(String schoolClassId) {
        TaggedDomSchoolClass from = null;
        for (TaggedDomSchoolClass s : taggedSchoolClassMap.values()) {
            if (s.isTag() && !s.getSchoolClass().getId().getIdString().equals(schoolClassId)) {
                from = s;
                break;
            }
        }
        DomSchoolClass to = taggedSchoolClassMap.get(schoolClassId).getSchoolClass();
        DomSubmitStudentToSchoolClass data = new DomSubmitStudentToSchoolClass();
        data.setSchoolClassFrom(from.getSchoolClass());
        data.setSchoolClassTo(to);
        data.setStudent(new DomStudent(user));
        Promise<Boolean> p = manager.submitStudentToSchoolClass(data);
        p.then((resolved) -> {
            this.initView(user);
            return Promises.resolved(true);
        });
        p.then(null, (failure) -> {
            eventBus.fireEvent(new AlertDialogWithOKEvent(failure.getFailure().getMessage()));
        });

    }

    @JsMethod
    public void saveUser(String givenName, String insertion, String familyName, String email, String password) {
        DomSingleSchoolStudent changedUser = new DomSingleSchoolStudent();
        changedUser.setUserName(fullUser.getUserName());
        //set freely allowed values
        if ((password == null && SimpleValidUserFieldsChecker.isNonEmptyNorNull(familyName, givenName)) || SimpleValidUserFieldsChecker.isNonEmptyNorNull(password, familyName, givenName)) {
            LOG.log(Level.INFO, "valid required fields.");
            changedUser.setFamilyName(familyName.trim());
            changedUser.setGivenName(givenName.trim());
            if (SimpleValidUserFieldsChecker.isNonEmptyNorNull(insertion)) {
                changedUser.setInsertion(insertion.trim());
            } else {
                changedUser.setInsertion(null);
            }
        } else {
            eventBus.fireEvent(new AlertDialogWithOKEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(dwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.Rest_Registration_Required_Fields)));
            return;
        }

        //check values
        if (!SimpleValidUserFieldsChecker.isValidEmail(email)) {
            eventBus.fireEvent(new AlertDialogWithOKEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(dwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.Rest_Registration_Email_Address_Invalid)));
            return;
        } else {
            changedUser.setEmail(email.trim());
        }
        if (password == null) {
            changedUser.setPassword(fullUser.getPassword());
        } else if (!SimpleValidUserFieldsChecker.isValidPassword(password)) {
            changedUser.setPassword(MD5.md5(password));
        } else {
            //invalid password format
            eventBus.fireEvent(new AlertDialogWithOKEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(dwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.GUI_AnIncorrectPasswordWasGiven)));
            return;
        }

        Promise<Boolean> p = Promises.resolved(true); //empty promise

        p.then(
                new Success<Boolean, Boolean>() {
            @Override
            //Are you sure?
            public Promise<Boolean> call(Promise<Boolean> resolved) throws Exception {//do dialog check
                AlertDialogWithConfirmCancelDeferred dialogPromise = new AlertDialogWithConfirmCancelDeferred(DwoLocalesForGWT.instance.NUM_Dialog_User_ConfirmPasswordSwitch());
                AlertDialogWithConfirmCancelEvent event = new AlertDialogWithConfirmCancelEvent(AlertDialogWithConfirmCancelEvent.EventType.ConfirmDialog, dialogPromise);
                eventBus.fireEvent(event);
                return dialogPromise.getPromise();
            }
        }
        ).then(
                new Success<Boolean, Boolean>() {
            //sure so remove
            @Override
            public Promise<Boolean> call(Promise<Boolean> resolved) throws Exception {
                if (resolved.getValue()) {
                    Promise<Boolean> promisedUser;
                    DomContext ctx = new DomContext();
                    RestSingleSchoolStudent rest = new RestSingleSchoolStudent();
                    rest.setRestContext(ctx);
                    rest.setDomSingleSchoolStudent(changedUser);
                    ctx.setDomHasRole(dwoGlobalVars.getSchoolLogins().getActiveSchoolRoleAndClass().getHasRole());

                    promisedUser = manager.updateSingleSchoolStudent(rest);
                    return promisedUser;
                } else {
                    LOG.log(Level.INFO, "update user cancelled.");
                    return Promises.failed(null);
                }
            }
        }
        ).then(
                new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                //calculate tree and call plotting
                LOG.log(Level.INFO, "DomUser returned.");
                view.clear();
                view.setUser(changedUser);
                eventBus.fireEvent(new MessageDialogWithOKEvent(DwoLocalesForGWT.instance.NUM_Dialog_User_ConfirmChangeCommited()));
                return null;
            }
        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                if (fail instanceof Dwo2Exception) {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new AlertDialogWithOKEvent((Dwo2Exception) fail));
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new AlertDialogWithOKEvent(fail.getMessage()));
                    //throw directly
                }
            }
        }
        );
    }

}
