package nl.uu.fi.dwo.lms.gwtclient.gwt.account;

import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.CallManagers.MD5;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.gwt.lib.rest.ui.DialogEvent;
import fi.dwo.gwt.lib.rest.util.StringFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent.State;
import nl.uu.fi.dwo.lms.gwtclient.gwt.modules.ModulesPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelDeferred;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.MessageDialogWithOKEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.ProgressDialogWithAbortDeferred;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.PromisedDialogWithConfirmDeferred;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.PromisedMessageDialogWithConfirmEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.SimpleValidUserFieldsChecker;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
@RoleScope
public class AccountPresenter {

    private static final Logger LOG = Logger.getLogger(AccountPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private AccountService accountService;
    private Map<String, DomSchoolRoleAndClassV2> sracData;

    private Display view;
private LoggingFailure FAILURE;

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

//    void goBackToResults() {
//        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.RESULTS));
//    }

    public interface Display extends BasicDisplay {

        void updateSchoolLoginsView(DomSchoolsRolesAndClassesV2 schoolLogins);

        void setEmptyTableMessage();

        void setLoadingTableMessage();

        void updateUserView(DomUserFull user);

        void clearAddSchoolLogin();
    }

    @Inject
    AccountPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars, SecuredUserAccountManager accountManager) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        accountService = new AccountService(dwoGlobalVars, accountManager);
        FAILURE = new LoggingFailure(LOG, eventBus);
    }

    public void init() {
        view.clear();
        view.init();
        view.setEmptyTableMessage();
        view.setHelp(dwoGlobalVars.buildHelpUrl("#account"));
        sracData = getAccountRoles();
        view.setLoadingTableMessage();
        updateUserDataInView();
    }

    private Map<String, DomSchoolRoleAndClassV2> getAccountRoles() {
        Map<String, DomSchoolRoleAndClassV2> result = new HashMap<String, DomSchoolRoleAndClassV2>();
        DomSchoolsRolesAndClassesV2 sl = dwoGlobalVars.getSchoolLogins();
        List<DomSchoolRoleAndClassV2> fullList = sl.getSchoolsRolesAndClassesList();
        for (DomSchoolRoleAndClassV2 hasRole : fullList) {
            if (!hasRole.getSchool().getId().getIdString().equals(dwoGlobalVars.getSchoolLogins().getNullSchool().getId().getIdString())) {
                result.put(hasRole.getHasRole().getId().getIdString(), hasRole);
            }
        }
        return result;
    }

    @JsMethod
    public void switchSchoolLogin(String hasRoleId) {
        DomSchoolRoleAndClassV2 srac = sracData.get(hasRoleId);

        if (srac != null) { //&& srac.getRole().getRoleName().equals(RoleType.TEACHER.name())) {
            dwoGlobalVars.setActiveSchoolRoleAndClass(srac);
            dwoGlobalVars.getSchoolLogins().setActiveSchoolRoleAndClass(srac);
            Promise<DomSchoolRoleAndClassV2> promise = accountService.switchToSchoolLogin(srac);

            promise.then(new Success<DomSchoolRoleAndClassV2, Void>() {
                @Override
                public Promise<Void> call(Promise<DomSchoolRoleAndClassV2> resolved) throws Exception {
                    if (!dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool().licenseIsValid()) {
                        eventBus.fireEvent(new AlertDialogWithOKEvent(new Dwo2Exception(Dwo2ExceptionCode.Rest_Registration_School_license_expired, "License expired.")));
                    };
                    //flip back to schoolclasses screen 
                    eventBus.fireEvent(new LoginEvent(LoginEvent.State.SUCCESS_WELCOME));
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
        } else {
            //jump to app.dwo.nl/leerling
            eventBus.fireEvent(new MessageDialogWithOKEvent(new Dwo2Exception(Dwo2ExceptionCode.Client_InternalError, "Internal error")));
            //LOG.log(Level.SEVERE, "Switching to other roles than teacher currently not supported.");
        }
    }

    @JsMethod
    public void addASchoolLogin(String role, String schoolLogin, String accessCode) {
        LOG.log(Level.INFO, "role " + role + " schoolLogin " + schoolLogin);
        Promise<Boolean> promise = accountService.addASchoolLogin(role, schoolLogin, accessCode);

        promise.then(new Success<Boolean, DomSchoolsRolesAndClassesV2>() {
            @Override
            public Promise<DomSchoolsRolesAndClassesV2> call(Promise<Boolean> resolved) throws Exception {
                //Role added, clear ui input.
                view.clearAddSchoolLogin();
                //get role Update.
                view.setLoadingTableMessage();
                Promise<DomSchoolsRolesAndClassesV2> update = accountService.getSchoolLogins();
                return update;
            }
        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                view.setEmptyTableMessage();
                if (fail instanceof Dwo2Exception) {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new MessageDialogWithOKEvent((Dwo2Exception) fail));
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new MessageDialogWithOKEvent(fail.getMessage()));
                    //throw directly
                }
            }
        }).then(new Success<DomSchoolsRolesAndClassesV2, Void>() {
                    @Override
                    public Promise<Void> call(Promise<DomSchoolsRolesAndClassesV2> resolved) throws Exception {
                        DomSchoolsRolesAndClassesV2 result = resolved.getValue();
                        //update schoolRoles
                        dwoGlobalVars.setSchoolLogins(result);
                        //dwoGlobalVars.setActiveSchoolRoleAndClass(result.getActiveSchoolRoleAndClass());
                        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.ACCOUNT));
                        return null;
                    }
                },
                        new Failure() {
                    @Override
                    public void fail(Promise<?> resolved) throws Exception {
                        Throwable fail = resolved.getFailure();
                        view.setEmptyTableMessage();
                        if (fail instanceof Dwo2Exception) {
                            LOG.log(Level.SEVERE, fail.getMessage());
                            eventBus.fireEvent(new MessageDialogWithOKEvent((Dwo2Exception) fail));
                        } else {
                            LOG.log(Level.SEVERE, fail.getMessage());
                            eventBus.fireEvent(new MessageDialogWithOKEvent(fail.getMessage()));
                            //throw directly
                        }
                    }
                });
    }

    @JsMethod
    public void removeASchoolLogin(String hasRoleId) {
        LOG.log(Level.INFO, "Removing schoolLogin " + hasRoleId);
        Promise<Boolean> p = Promises.resolved(true); //empty promise
        p.then(new Success<Boolean, Boolean>() {
            @Override
            //Are you sure?
            public Promise<Boolean> call(Promise<Boolean> resolved) throws Exception {//do dialog check
                String msg = StringFormatter.format(DwoLocalesForGWT.instance.NUM_DLG_User_ConfirmSchoolLoginDelete(), sracData.get(hasRoleId).getSchool().getSchoolName(), sracData.get(hasRoleId).getRole().getRoleName());
                AlertDialogWithConfirmCancelDeferred dialogPromise = new AlertDialogWithConfirmCancelDeferred(msg);
                AlertDialogWithConfirmCancelEvent event = new AlertDialogWithConfirmCancelEvent(AlertDialogWithConfirmCancelEvent.EventType.ConfirmDialog, dialogPromise);
                eventBus.fireEvent(event);
                return dialogPromise.getPromise();
            }
        }).then(new Success<Boolean, Boolean>() {
            //sure so remove
            @Override
            public Promise<Boolean> call(Promise<Boolean> resolved) throws Exception {
                if (resolved.getValue()) {
                    view.setLoadingTableMessage();
                    Promise<Boolean> promise = accountService.removeASchoolLogin(sracData.get(hasRoleId));
                    return promise;
                } else {
                    LOG.log(Level.INFO, "update user cancelled.");
                    return Promises.failed(null);
                }
            }
        }).then(new Success<Boolean, DomSchoolsRolesAndClassesV2>() {
            //clear input and fetch update
            @Override
            public Promise<DomSchoolsRolesAndClassesV2> call(Promise<Boolean> resolved) throws Exception {
                //Role added, clear ui input.
                view.clearAddSchoolLogin();
                //get role Update.
                Promise<DomSchoolsRolesAndClassesV2> update = accountService.getSchoolLogins();
                return update;
            }
        }).then(new Success<DomSchoolsRolesAndClassesV2, Void>() {
            //update succeeded, update dwoGlobals and UI
            @Override
            public Promise<Void> call(Promise<DomSchoolsRolesAndClassesV2> resolved) throws Exception {
                DomSchoolsRolesAndClassesV2 result = resolved.getValue();
                //update schoolRoles
                dwoGlobalVars.setSchoolLogins(result);
                //dwoGlobalVars.setActiveSchoolRoleAndClass(result.getActiveSchoolRoleAndClass());
                //set ui
                eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.ACCOUNT));
                return null;
            }
        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                view.setEmptyTableMessage();
                if (fail instanceof Dwo2Exception) {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new MessageDialogWithOKEvent((Dwo2Exception) fail));
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new MessageDialogWithOKEvent(fail.getMessage()));
                    //throw directly
                }
            }
        });
    }

    @JsMethod
    public void saveUser(String givenName, String insertion, String familyName, String email, String curPassword, String newPassword, String newPasswordAgain) {
        if (!MD5.md5(curPassword).equals(dwoGlobalVars.getCurrentUser().getPassword())) {
            eventBus.fireEvent(new AlertDialogWithOKEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(dwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.GUI_AnIncorrectPasswordWasGiven)));
            return;
        }

        DomUserFull user = new DomUserFull();
        user.setUserName(dwoGlobalVars.getCurrentUser().getUserName());
        //set freely allowed values
        if (SimpleValidUserFieldsChecker.isNonEmptyNorNull(curPassword, familyName, givenName)) {
            LOG.log(Level.INFO, "valid required fields.");
            user.setFamilyName(familyName.trim());
            user.setGivenName(givenName.trim());
            if (SimpleValidUserFieldsChecker.isNonEmptyNorNull(insertion)) {
                user.setInsertion(insertion.trim());
            } else {
                user.setInsertion(null);
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
            user.setEmail(email.trim());
        }

        if (!SimpleValidUserFieldsChecker.isNonEmptyNorNull(newPassword)
                && !SimpleValidUserFieldsChecker.isNonEmptyNorNull(newPasswordAgain)) {
            user.setPassword(dwoGlobalVars.getCurrentUser().getPassword());
        } else if (SimpleValidUserFieldsChecker.isNonEmptyNorNull(newPassword)
                && SimpleValidUserFieldsChecker.isNonEmptyNorNull(newPasswordAgain)
                && newPassword.compareTo(newPasswordAgain) == 0) {
            if (!SimpleValidUserFieldsChecker.isValidPassword(newPassword)) {
                //invalid password format
                eventBus.fireEvent(new AlertDialogWithOKEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(dwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.User_NewPasswordsDoNotMatch)));
            } else {
                user.setPassword(MD5.md5(newPassword));
            }
        } else {
            eventBus.fireEvent(new AlertDialogWithOKEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(dwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.User_NewPasswordsDoNotMatch)));
            return;
        }
        //Start with OK/Cancel Promised AlerDialog
        Promise<Boolean> p = Promises.resolved(true); //empty promise
        p.then((resolved) -> {
            //do dialog check
            AlertDialogWithConfirmCancelDeferred dialogPromise = new AlertDialogWithConfirmCancelDeferred(DwoLocalesForGWT.instance.GUI_Dialog_User_ConfirmPasswordSwitch());
            AlertDialogWithConfirmCancelEvent event = new AlertDialogWithConfirmCancelEvent(AlertDialogWithConfirmCancelEvent.EventType.ConfirmDialog, dialogPromise);
            eventBus.fireEvent(event);
            return dialogPromise.getPromise();
        })
                // if dialog has success update user data
                .then((resolved) -> {
                    Boolean doIt = resolved.getValue();
                    if (doIt) {
                        LOG.log(Level.INFO, "update user requested.");
                        //All is well, proceed with REST-request
                        Promise<DomUserFull> promisedUser;
                        promisedUser = accountService.UpdateUserData(user);
                        return promisedUser;
                    } else {
                        LOG.log(Level.INFO, "update user cancelled.");
                        return Promises.failed(null);
                    }
                }).then(
                new Success<DomUserFull, Void>() {
            @Override
            public Promise<Void> call(Promise<DomUserFull> resolved) throws Exception {
                //calculate tree and call plotting
                LOG.log(Level.INFO, "DomUser returned.");
                DomUserFull u = resolved.getValue();
                if (u.getInsertion() == null) u.setInsertion(""); // komt voor
                dwoGlobalVars.setCurrentUser(u, dwoGlobalVars.getRealm());
                view.clear();
                view.updateUserView(u);
                eventBus.fireEvent(new MessageDialogWithOKEvent(DwoLocalesForGWT.instance.NUM_DLG_User_ConfirmChangeCommited()));
                return null;
            }
        },
                FAILURE
        );

    }

    @JsMethod
    public void updateUserDataInView() {
        Promise<DomUserFull> userPromise;
        view.setLoadingTableMessage();
        userPromise = accountService.getUserData();
        // onSuccess calculate results and show.
        userPromise.then(new Success<DomUserFull, Void>() {
            @Override
            public Promise<Void> call(Promise<DomUserFull> resolved) throws Exception {
                // calculate tree and call plotting
                LOG.log(Level.INFO, "DomFullUser data returned.");
                DomUserFull uf = resolved.getValue();
                dwoGlobalVars.setCurrentUser(uf);// updating data
                if (uf.getInsertion()==null) uf.setInsertion("");
                view.updateUserView(uf);
                DomSchoolsRolesAndClassesV2 srac = new DomSchoolsRolesAndClassesV2();
                srac.setNullSchool(dwoGlobalVars.getSchoolLogins().getNullSchool());
                srac.setActiveSchoolRoleAndClass(dwoGlobalVars.getActiveSchoolRoleAndClass());
                List<DomSchoolRoleAndClassV2> sracList = new ArrayList<>(sracData.size());
                sracData.forEach((k, v) -> {
                    sracList.add(v);
                });
                srac.setSchoolsRolesAndClassesList(sracList);
                view.updateSchoolLoginsView(srac);
//                if (!srac.getActiveSchoolRoleAndClass().getRole().getRoleName().equals(RoleType.TEACHER.name())) {
//                    eventBus.fireEvent(new AlertDialogWithOKEvent(DwoLocalesForGWT.instance.NUM_DLG_User_NoTeacher()));
//                }
                return null;
            }
        }, new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                view.setEmptyTableMessage();
                if (fail instanceof Dwo2Exception) {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new AlertDialogWithOKEvent((Dwo2Exception) fail));
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new AlertDialogWithOKEvent(fail.getMessage()));
                    // throw directly
                }
            }
        });
    }

    @Inject ModulesPresenter modules;
 
    @JsMethod
    void removeCurrentUser(String password) {
      password = MD5.md5(password);
      if ( password .equals(dwoGlobalVars.getCurrentUser().getPassword())) {
        AlertDialogWithConfirmCancelDeferred defer;
        defer = new AlertDialogWithConfirmCancelDeferred(DwoLocalesForGWT.instance.NUM_DLG_ORGANISATION_CONFIRM_REMOVE1());
        eventBus.fireEvent(new AlertDialogWithConfirmCancelEvent(AlertDialogWithConfirmCancelEvent.EventType.ConfirmDialog, defer));
        
        Promise<Boolean> promise = defer.getPromise();
        promise = promise.then(modules::logout);
        promise = promise.then( (p) -> 
          {
            if (p.getValue().booleanValue()) {
              return accountService.removeCurrentUser();
          }
          return p;
        });
        promise = promise.then( p -> {
            if (p.getValue().booleanValue()) {
              eventBus.fireEvent(new LoginEvent(State.LOGOUT));
            } 
            return p;
          }, FAILURE);
      } else {
        eventBus.fireEvent(new AlertDialogWithOKEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(dwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.GUI_AnIncorrectPasswordWasGiven)));
      }
    }
    
    
}
