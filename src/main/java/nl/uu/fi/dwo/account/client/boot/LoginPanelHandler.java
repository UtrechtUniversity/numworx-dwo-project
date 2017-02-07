package nl.uu.fi.dwo.account.client.boot;

import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Controller for Login.
 *
 * @author Gert van der Plas
 */
class LoginPanelHandler {

    private static final Logger LOG = Logger.getLogger(LoginPanelHandler.class.getName());

    private LoginPanel view;
    private LoginController controller = new LoginController();

    LoginPanelHandler(LoginPanel view) {
        this.view = view;
        init();
    }

    public void init() {

    }

//    public Promise<DomSchoolRoleAndClass> switchToSchoolLogin(DomSchoolRoleAndClass sc) {
//    public void login(....){
    //}
    //   }
    public void loginClicked(String user, String password) {
        Promise<DomUserFullwLoginContext> loginUser = controller.login(user, password);
        loginUser.then(new Success<DomUserFullwLoginContext, Void>() {
            @Override
            public Promise<Void> call(Promise<DomUserFullwLoginContext> resolved) throws Exception {
//                    DomUserFull user = resolved.getValue().getDomUserFull();
//                    DwoGlobalVars.instance().initUser(user);
//                    parent.mainDeckPanel.showWidget(1);
                view.onLoginSuccess();
                return null;
            }
        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                // complain...
                LOG.log(Level.INFO, resolved.getFailure().getMessage());
                view.onLoginFailure(resolved);
            }
        }
        );
    }

//                @Override
//                public void onFailure(Throwable t) {
//                    //TODO show fail
//                };
//
//                //Process login results
//                @Override
//                public void onSuccess(DomUserFullwLoginContext result) {
//                    //TODO switch to other panel
//                    LOG.log(Level.INFO, "Login for user with username:" + result.getDomUserFull().getUserName() + ".");
//                    user = ((DomUserFullwLoginContext) result).getDomUserFull();
//                    loginStatusPanel.setStatus(user.getUserName(), true);
//                    userBar.setSingleSchool(user.getSingleSchool());
//                    DwoGlobalVars.instance().setCurrentUser(user);
//                    SecuredStudentSchoolClassManager manager = new SecuredStudentSchoolClassManager();
//                    final SecuredUserSchoolLoginManagerV2 loginManager = new SecuredUserSchoolLoginManagerV2();
//
//                    manager.getActiveSchoolClass(new AsyncCallback<DomSchoolClass>() {
//                        @Override
//                        public void onFailure(Throwable t) {
//                            if (t instanceof Dwo2Exception) {
//                                Dwo2ExceptionCode code = ((Dwo2Exception) t).getDwo2Code();
//                                if (code == Dwo2ExceptionCode.Rest_Active_SchoolClass_Not_Set) {
//                                    onSuccess(null);
//                                    return;
//                                }
//                            }
//                            LOG.log(Level.INFO, t.toString(), t);
//                            header.setRightWidget(null);
//                        }
//
//                        //Process getActiveSchoolClass results (Only works for student?)
//                        @Override
//                        public void onSuccess(DomSchoolClass result) {
//                            DwoGlobalVars.instance()
//                                    .setCurrentSchoolClass(result);
//                            if (result != null) {
//                                loginStatusPanel.setSchoolClass(result.getSchoolClassName());
//                            } else {
//                                loginStatusPanel.setSchoolClass("");
//                            }
//                            //Fill DWOGlobalVars with DomSchoolsRolesAndClasses
//
//                            loginManager.getSchoolLoginsV2(new AsyncCallback<DomSchoolsRolesAndClassesV2>() {
//                                @Override
//                                public void onFailure(Throwable t) {
//                                    if (t instanceof Dwo2Exception) {
//                                        Dwo2ExceptionCode code = ((Dwo2Exception) t).getDwo2Code();
//                                    }
//                                    LOG.log(Level.INFO, t.toString(), t);
//                                    header.setRightWidget(null);
//                                }
//                                //Process getActiveSchoolClass results (Only works for student?)
//
//                                @Override
//                                public void onSuccess(DomSchoolsRolesAndClassesV2 result) {
//                                    DwoGlobalVars.getInstance().setSchoolLogins(result);
//                                    header.setRightWidget(userBar);
//                                    LOG.log(Level.INFO,
//                                            "DwoGlobalVars has user with username:" + DwoGlobalVars.instance().getCurrentUser().getDisplayName() + ".");
//                                }
//                            });
//
//                        }
//                    });
//                }}    
}
