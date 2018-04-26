package nl.uu.fi.dwo.lms.gwtclient.gwt.account;

import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.CallManagers.MD5;
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
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.SimpleValidUserFieldsChecker;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class AccountPresenter {

    private static final Logger LOG = Logger.getLogger(AccountPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private AccountService accountService;
    private Map<String, DomSchoolRoleAndClassV2> sracData;

    private Display view;

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

    void goBackToResults() {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.RESULTS));
    }

    public interface Display {

        void clear();

        void init();

	void updateSchoolLoginsView(DomSchoolsRolesAndClassesV2 schoolLogins);

	// void updateView(String username, String firstName, String insertion,
	// String familyName, String email);

	void updateUserView(DomUserFull user);
        
        void clearAddSchoolLogin();

    }

    public AccountPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        accountService = new AccountService(dwoGlobalVars);
    }

    public void init() {
        view.init();
	sracData = getTeacherRoles();
	updateUserDataInView();
    }

    private Map<String, DomSchoolRoleAndClassV2> getTeacherRoles() {
        Map<String, DomSchoolRoleAndClassV2> result = new HashMap<String, DomSchoolRoleAndClassV2>();
        DomSchoolsRolesAndClassesV2 sl = dwoGlobalVars.getSchoolLogins();
        List<DomSchoolRoleAndClassV2> fullList = sl.getSchoolsRolesAndClassesList();
        for (DomSchoolRoleAndClassV2 hasRole : fullList) {
//            if (hasRole.getRole().getRoleName().equals("TEACHER")) {
                result.put(hasRole.getHasRole().getId().getIdString(), hasRole);
  //          }
        }
        return result;
    }

    @JsMethod
    public void switchSchoolLogin(String hasRoleId) {
        LOG.log(Level.INFO, "Switching to hasRoleId: " + hasRoleId);
        DomSchoolRoleAndClassV2 srac = sracData.get(hasRoleId);
        LOG.log(Level.INFO, "Showing sracData: " + sracData);
        LOG.log(Level.INFO, "Showing sracData: " + srac);
        
        
        if (srac != null){ //&& srac.getRole().getRoleName().equals(RoleType.TEACHER.name())) {
            dwoGlobalVars.setActiveSchoolRoleAndClass(srac);
            dwoGlobalVars.getSchoolLogins().setActiveSchoolRoleAndClass(srac);
            Promise<DomSchoolRoleAndClassV2> promise = accountService.switchToSchoolLogin(srac);

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
        } else {
            //jump to app.dwo.nl/leerling
            eventBus.fireEvent(new DialogEvent(new Dwo2Exception(Dwo2ExceptionCode.Client_InternalError, "Internal error")));
            //LOG.log(Level.SEVERE, "Switching to other roles than teacher currently not supported.");
        }
    }

    @JsMethod
    public void addASchoolLogin(String role, String schoolLogin, String accessCode) {
        LOG.log(Level.INFO, "role " + role + " schoolLogin " + schoolLogin);
        Promise<Boolean> promise = accountService.addASchoolLogin(role, schoolLogin, accessCode);

        promise.then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                //flip back to schoolclasses screen 
                eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.ACCOUNT));
                view.clearAddSchoolLogin();
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

    @JsMethod
    public void removeASchoolLogin(String hasRoleId) {
        LOG.log(Level.INFO, "Removing schoolLogin " + hasRoleId);
        Promise<Boolean> promise = accountService.removeASchoolLogin(sracData.get(hasRoleId));
        promise.then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                //flip back to schoolclasses screen 
                eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.ACCOUNT));
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
    
//    
//    @JsMethod
//    public void changePasword(String curPassword, String newPassword, String newPasswordAgain) {
//        if (!MD5.md5(curPassword).equals(dwoGlobalVars.getCurrentUser().getPassword())) {
//            eventBus.fireEvent(new DialogEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(dwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.GUI_AnIncorrectPasswordWasGiven)));
//            //DwoViewer.showMessage(Dwo2ExceptionCode.GUI_AnIncorrectPasswordWasGiven);
//            return;
//        }
//
//        DomUserFull user = new DomUserFull();
//        user.setUserName(dwoGlobalVars.getCurrentUser().getUserName());
//        user.setGivenName(dwoGlobalVars.getCurrentUser().getGivenName());
//        user.setEmail(dwoGlobalVars.getCurrentUser().getEmail());
//        user.setInsertion(dwoGlobalVars.getCurrentUser().getInsertion());
//        user.setFamilyName(dwoGlobalVars.getCurrentUser().getFamilyName());
//        user.setId(dwoGlobalVars.getCurrentUser().getId());
//        //set freely allowed values
//        if (SimpleValidUserFieldsChecker.isNonEmptyNorNull(curPassword, newPassword, newPasswordAgain)) {
//            LOG.log(Level.INFO, "valid required fields.");
//            user.setPassword(newPassword);
////            if (SimpleValidUserFieldsChecker.isNonEmptyNorNull(dwoGlobalVars.getCurrentUser().getInsertion())) {
////                user.setInsertion(dwoGlobalVars.getCurrentUser().getInsertion().trim());
////            } else {
////                user.setInsertion(null);
////            }
//        } else {
//            eventBus.fireEvent(new DialogEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(dwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.Rest_Registration_Required_Fields)));
//            return;
//        }
//
//        //All is well, proceed with REST-request
//        Promise<DomUserFull> promisedUser;
//        promisedUser = accountService.UpdateUserData(user);
//        // onSuccess calculate results and show.
//        promisedUser.then(new Success<DomUserFull, Void>() {
//            @Override
//            public Promise<Void> call(Promise<DomUserFull> resolved) throws Exception {
//                //calculate tree and call plotting
//                LOG.log(Level.INFO, "Success. DomUser returned.");
//                DomUserFull u = resolved.getValue();
//                dwoGlobalVars.setCurrentUser(u);
//                view.clear();
////                view.updateView(u.getUserName(), u.getGivenName(), u.getInsertion(), u.getFamilyName(), u.getEmail());
//                eventBus.fireEvent(new DialogEvent("Success"));
//                return null;
//            }
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
        if (!MD5.md5(curPassword).equals(dwoGlobalVars.getCurrentUser().getPassword())) {
            eventBus.fireEvent(new DialogEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(dwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.GUI_AnIncorrectPasswordWasGiven)));
            //DwoViewer.showMessage(Dwo2ExceptionCode.GUI_AnIncorrectPasswordWasGiven);
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
            eventBus.fireEvent(new DialogEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(dwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.Rest_Registration_Required_Fields)));
            return;
        }

        //check values
        if (!SimpleValidUserFieldsChecker.isValidEmail(email)) {
            eventBus.fireEvent(new DialogEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(dwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.Rest_Registration_Email_Address_Invalid)));
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
                eventBus.fireEvent(new DialogEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(dwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.User_NewPasswordsDoNotMatch)));
            } else {
                user.setPassword(MD5.md5(newPassword));
            }
        } else {
            eventBus.fireEvent(new DialogEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(dwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.User_NewPasswordsDoNotMatch)));
            return;
        }

        //All is well, proceed with REST-request
        Promise<DomUserFull> promisedUser;
	promisedUser = accountService.UpdateUserData(user);
        // onSuccess calculate results and show.
        promisedUser.then(new Success<DomUserFull, Void>() {
            @Override
            public Promise<Void> call(Promise<DomUserFull> resolved) throws Exception {
                //calculate tree and call plotting
                LOG.log(Level.INFO, "DomUser returned.");
                DomUserFull u = resolved.getValue();
                dwoGlobalVars.setCurrentUser(u);
                view.clear();
		view.updateUserView(u);
                eventBus.fireEvent(new DialogEvent("Success"));
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
        LOG.log(Level.INFO, "Data send to server.");
    }

    @JsMethod
    public void updateUserDataInView() {
	Promise<DomUserFull> userPromise;
	userPromise = accountService.getUserData();
	// onSuccess calculate results and show.
	userPromise.then(new Success<DomUserFull, Void>() {
	    @Override
	    public Promise<Void> call(Promise<DomUserFull> resolved) throws Exception {
		// calculate tree and call plotting
		LOG.log(Level.INFO, "DomFullUser data returned.");
		DomUserFull uf = resolved.getValue();
		dwoGlobalVars.setCurrentUser(uf);// updating data
		view.updateUserView(uf);
		view.updateSchoolLoginsView(dwoGlobalVars.getSchoolLogins());
		return null;
	    }
	}, new Failure() {
	    @Override
	    public void fail(Promise<?> resolved) throws Exception {
		Throwable fail = resolved.getFailure();
		if (fail instanceof Dwo2Exception) {
		    LOG.log(Level.SEVERE, fail.getMessage());
		    eventBus.fireEvent(new DialogEvent((Dwo2Exception) fail));
		} else {
		    LOG.log(Level.SEVERE, fail.getMessage());
		    eventBus.fireEvent(new DialogEvent(fail.getMessage()));
		    // throw directly
		}
	    }
	});
    }
    
}
