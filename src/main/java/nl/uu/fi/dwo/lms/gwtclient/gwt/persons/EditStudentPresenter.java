package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.CallManagers.MD5;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import fi.dwo.gwt.lib.rest.ui.DialogEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import java.util.logging.Logger;
import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
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
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
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
    private DomUserFull user;

    public interface Display {

        void clear();

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
        initView(aUser);
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
            return Promises.resolved(null);
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
                for (TaggedDomSchoolClass sc : taggedSchoolClassMap.values()) {
                    if (sc.isTag()) {
                        DomGetSingleSchoolStudent student = new DomGetSingleSchoolStudent(new DomStudent(aUser));
                        student.setDomSchoolClass(sc.getSchoolClass());
                        return manager.getSingleSchoolStudent(student);
                    }else{
                        return Promises.resolved(null);
                    }
                }
                DomGetSingleSchoolStudent student = new DomGetSingleSchoolStudent(new DomStudent(aUser));
                return manager.getSingleSchoolStudent(student);

            }).then((resolved) -> {
                DomSingleSchoolStudent student = (DomSingleSchoolStudent) resolved.getValue();
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
                break;
            }

        }
    }

//    
//    /**
//     * Retrieves a list of all school classes and displays the stuff
//     */
//    private void setSchoolClassesInView(DomUser aUser) {
//        Promise<List<DomSchoolClass>> promise;
//        promise = manager.getTeachersSchoolClasses();
//        // onSuccess update view
//        promise.then(new Success<List<DomSchoolClass>, Void>() {
//            @Override
//            public Promise<Void> call(Promise<List<DomSchoolClass>> resolved) throws Exception {
//                //flip back to schoolclasses screen 
//                taggedSchoolClassMap = new HashMap<String, TaggedDomSchoolClass>();
//                for (DomSchoolClass sc : resolved.getValue()) {
//                    TaggedDomSchoolClass tsc = new TaggedDomSchoolClass();
//                    tsc.setSchoolClass(sc);
//                    tsc.setTag(false);
//                    taggedSchoolClassMap.put(sc.getId().getIdString(), tsc);
//                }
//                view.setSchoolClasses(taggedSchoolClassMap);
//                return null;
//            }
//
//        },
//                new Failure() {
//            @Override
//            public void fail(Promise<?> resolved) throws Exception {
//                Throwable fail = resolved.getFailure();
//                if (fail instanceof Dwo2Exception) {
//                    LOG.log(Level.SEVERE, fail.getMessage());
//                    eventBus.fireEvent(new DialogEvent((Dwo2Exception) fail));
//                } else {
//                    LOG.log(Level.SEVERE, fail.getMessage());
//                    eventBus.fireEvent(new DialogEvent(fail.getMessage()));
//                    //throw directly
//                }
//            }
//        });
//    }
    @JsMethod
    public void saveUser(String givenName, String insertion, String familyName, String email, String curPassword, String newPassword, String newPasswordAgain) {
        if (!MD5.md5(curPassword).equals(user.getPassword())) {
            eventBus.fireEvent(new DialogEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(dwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.GUI_AnIncorrectPasswordWasGiven)));
            //DwoViewer.showMessage(Dwo2ExceptionCode.GUI_AnIncorrectPasswordWasGiven);
            return;
        }

        DomSingleSchoolStudent changedUser = new DomSingleSchoolStudent();
        changedUser.setUserName(user.getUserName());
        //set freely allowed values
        if (SimpleValidUserFieldsChecker.isNonEmptyNorNull(curPassword, familyName, givenName)) {
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

        if (!SimpleValidUserFieldsChecker.isNonEmptyNorNull(newPassword)
                && !SimpleValidUserFieldsChecker.isNonEmptyNorNull(newPasswordAgain)) {
            changedUser.setPassword(user.getPassword());
        } else if (SimpleValidUserFieldsChecker.isNonEmptyNorNull(newPassword)
                && SimpleValidUserFieldsChecker.isNonEmptyNorNull(newPasswordAgain)
                && newPassword.compareTo(newPasswordAgain) == 0) {
            if (!SimpleValidUserFieldsChecker.isValidPassword(newPassword)) {
                //invalid password format
                eventBus.fireEvent(new AlertDialogWithOKEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(dwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.User_NewPasswordsDoNotMatch)));
            } else {
                changedUser.setPassword(MD5.md5(newPassword));
            }
        } else {
            eventBus.fireEvent(new AlertDialogWithOKEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(dwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.User_NewPasswordsDoNotMatch)));
            return;
        }

        //All is well, proceed with REST-request
        Promise<Boolean> promisedUser;
        promisedUser = manager.updateSingleSchoolStudent(changedUser);
        // onSuccess calculate results and show.
        promisedUser.then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                //calculate tree and call plotting
                LOG.log(Level.INFO, "DomUser returned.");
                view.clear();
                view.setUser(changedUser);
                eventBus.fireEvent(new AlertDialogWithOKEvent("Success"));
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
        });
    }

}
