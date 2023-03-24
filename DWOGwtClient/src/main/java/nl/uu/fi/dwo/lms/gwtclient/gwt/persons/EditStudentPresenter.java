package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.CallManagers.MD5;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Level;

import java.util.logging.Logger;

import javax.inject.Inject;

import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
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
    DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private Display view;
    PersonsService manager;
    Map<String, TaggedDomSchoolClass> taggedSchoolClassMap;
    private DomUserFull fullUser;
    DomUser user;
    Failure FAILURE;

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

    @Inject EditStudentPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars, PersonsService m) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        manager = m;
        FAILURE = new LoggingFailure(LOG, anEventBus);
    }

    public void init(DomUser aUser) {
        view.clear();
        view.init();
        view.setEmptyTableMessage();
        view.setHelp(dwoGlobalVars.buildHelpUrl("#editStudent"));
        user = aUser;
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
        Promise<Boolean> p = Promises.resolved(true);

        //fetch schoolclasses
        p = p.then((resolved) -> {
            return manager.getTeachersSchoolClasses();

        }).then((resolved) -> {
            List<DomSchoolClass> classList = (List<DomSchoolClass>) resolved.getValue();
            taggedSchoolClassMap = new HashMap<String, TaggedDomSchoolClass>(classList.size());
            classList.forEach((v) -> taggedSchoolClassMap.put(v.getId().getIdString(), new TaggedDomSchoolClass(v)));
            DomStudent student = new DomStudent(aUser);
            RestStudent rest = new RestStudent();
            rest.setDomStudent(student);
            DomContext ctx = new DomContext();
            ctx.setDomHasRole(dwoGlobalVars.getActiveSchoolRoleAndClass().getHasRole());
            rest.setRestContext(ctx);
            return manager.getTeachersClassesOfStudent(rest);
        }).then((resolved) -> {
            if (resolved.getValue() != null) {
                List<DomSchoolClassId> studentClassList = (List<DomSchoolClassId>) resolved.getValue();
                studentClassList.forEach((v) -> {
                    taggedSchoolClassMap.get(v.getId().getIdString()).setTag(true);
                });
                view.setSchoolClasses(taggedSchoolClassMap);
                return verifyStudentInTeachersClass();
            }
            return Promises.resolved(true);
        });
        //if singleschool fetch user
        if (!dwoGlobalVars.isSaml() && aUser.getSingleSchool()) { // full saml, niet kennisnet.
            p = p.then((resolved) -> {
                if (resolved.getValue() != null && resolved.getValue().equals(true)) {
                    DomGetSingleSchoolStudent student = new DomGetSingleSchoolStudent(new DomStudent(aUser));
                    for (TaggedDomSchoolClass sc : taggedSchoolClassMap.values()) {
                        if (sc.isTag()) {
                            student.setDomSchoolClass(sc.getSchoolClass());
                            break;
                        }
                    }
                    return getSingleSchoolStudent(student);
                }
                return Promises.resolved(null);
            }).then((resolved) -> {
                if (resolved.getValue() != null) {
                    DomSingleSchoolStudent student = (DomSingleSchoolStudent) resolved.getValue();
                    fullUser = student;
                    view.setSingleSchoolStudent(student);
                }
                return Promises.resolved(null);
            });
        } else {
            view.setUser(aUser);
        }

        p.then(null, FAILURE);
    }

Promise<DomSingleSchoolStudent> getSingleSchoolStudent(DomGetSingleSchoolStudent student) {
  if (student.getDomSchoolClass() != null) {
      RestGetSingleSchoolStudent restData = new RestGetSingleSchoolStudent();
      DomContext ctx = new DomContext();
      ctx.setDomHasRole(dwoGlobalVars.getActiveSchoolRoleAndClass().getHasRole());
      ctx.setRealm(dwoGlobalVars.getCurrentLoginContext().getRealm());
      restData.setRestContext(ctx);
      restData.setDomGetSingleSchoolStudent(student);
      return manager.getSingleSchoolStudent(restData);
  } else {
      return Promises.resolved(null);
  }
}

Promise<Boolean> verifyStudentInTeachersClass() {
  Predicate<TaggedDomSchoolClass> p1 = TaggedDomSchoolClass::isTag;
  if (taggedSchoolClassMap.values().stream().filter(p1).count() < 1) {
      eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.PERSONS));
      return Promises.resolved(false);
  } else 
      return Promises.resolved(true);
}

    @JsMethod
    public void removeStudentFromSchoolClass(String schoolClassId) {
        Promise<Boolean> p = Promises.resolved(true); //empty promise
        p = isInTeachersClass(p);

        p.then((resolved) -> {
            if (resolved.getValue() == false) {
                throw (new Dwo2Exception(Dwo2ExceptionCode.User_Cancelled_RemoveStudentFromSchoolClass, "Unsubscribe schoolclass cancelled."));
            } else {
                DomSchoolClass sc = taggedSchoolClassMap.get(schoolClassId).getSchoolClass();
                DomRemoveStudentFromSchoolClass data = new DomRemoveStudentFromSchoolClass();
                data.setSchoolClass(sc);
                data.setStudent(new DomStudent(user));
                return manager.removeStudentFromSchoolClass(data);
            }
        }).then((resolved) -> {
            //update new state
            this.initView(user);
            return Promises.resolved(true);
        }, (failure) -> {
                Throwable fail = failure.getFailure();
            if (fail instanceof Dwo2Exception) {                
                Dwo2Exception f = (Dwo2Exception) fail;
                        LOG.log(Level.SEVERE, f.getDwo2Message());
                        eventBus.fireEvent(new AlertDialogWithOKEvent((Dwo2Exception) f));
                    } else {
                //Throwable fail = failure.getFailure();
                        LOG.log(Level.SEVERE, fail.getMessage());
                        eventBus.fireEvent(new AlertDialogWithOKEvent(fail.getMessage()));
                        //throw directly
                    }
            initView(user);
            //eventBus.fireEvent(new AlertDialogWithOKEvent((Dwo2Exception) failure.getFailure()));
        });
    }

    Promise<Boolean> isInTeachersClass(Promise<Boolean> p) {
      Predicate<TaggedDomSchoolClass> p1 = TaggedDomSchoolClass::isTag;
      //taggedSchoolClassMap.values().stream().anyMatch(p1);
      if (taggedSchoolClassMap.values().stream().filter(p1).count() <= 1) {
          p = p.then(new Success<Boolean, Boolean>() {
              @Override
              //Are you sure?
              public Promise<Boolean> call(Promise<Boolean> resolved) throws Exception {//do dialog check
                  //String msg = StringFormatter.format(DwoLocalesForGWT.instance.NUM_DLG_EDITSTUDENT_Q_RemoveClassFromStudent(), sracData.get(hasRoleId).getSchool().getSchoolName());
                  String msg = DwoLocalesForGWT.instance.NUM_DLG_EDITSTUDENT_Q_RemoveClassFromStudent();
                  AlertDialogWithConfirmCancelDeferred dialogPromise = new AlertDialogWithConfirmCancelDeferred(msg);
                  AlertDialogWithConfirmCancelEvent event = new AlertDialogWithConfirmCancelEvent(AlertDialogWithConfirmCancelEvent.EventType.ConfirmDialog, dialogPromise);
                  eventBus.fireEvent(event);
                  return dialogPromise.getPromise();
              }
          });
      }
      return p;
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
            return null;
        }).then(null, FAILURE);

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
        } else if (SimpleValidUserFieldsChecker.isValidPassword(password)) {
            changedUser.setPassword(MD5.md5(password));
        } else {
            //invalid password format
            eventBus.fireEvent(new AlertDialogWithOKEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(dwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.GUI_AnIncorrectPasswordWasGiven)));
            return;
        }

        changedUser.setId(fullUser.getId());

        Promise<Boolean> p = Promises.resolved(true); //empty promise

        p
//                .then(
//                new Success<Boolean, Boolean>() {
//            @Override
//            //Are you sure?
//            public Promise<Boolean> call(Promise<Boolean> resolved) throws Exception {//do dialog check
//                AlertDialogWithConfirmCancelDeferred dialogPromise = new AlertDialogWithConfirmCancelDeferred(DwoLocalesForGWT.instance.NUM_DLG_User_ConfirmPasswordSwitch());
//                AlertDialogWithConfirmCancelEvent event = new AlertDialogWithConfirmCancelEvent(AlertDialogWithConfirmCancelEvent.EventType.ConfirmDialog, dialogPromise);
//                eventBus.fireEvent(event);
//                return dialogPromise.getPromise();
//            }
//        }
//        )
                .then(
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
                    ctx.setDomHasRole(dwoGlobalVars.getActiveSchoolRoleAndClass().getHasRole());

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
                if (changedUser.getGivenName() != null) {
                    fullUser.setGivenName(changedUser.getGivenName());
                }
                if (changedUser.getInsertion() != null) {
                    fullUser.setInsertion(changedUser.getInsertion());
                }
                if (changedUser.getFamilyName() != null) {
                    fullUser.setFamilyName(changedUser.getFamilyName());
                }
                if (changedUser.getPassword() != null) {
                    fullUser.setPassword(changedUser.getPassword());
                }
                if (changedUser.getEmail() != null) {
                    fullUser.setEmail(changedUser.getEmail());
                }
                initView(fullUser);
                eventBus.fireEvent(new MessageDialogWithOKEvent(DwoLocalesForGWT.instance.NUM_DLG_User_ConfirmChangeCommited()));
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
                initView(fullUser);
            }
        }
        );
    }

}
