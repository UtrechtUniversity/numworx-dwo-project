package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.account.client.RPCHandlerV1;
import nl.uu.fi.dwo.account.client.RPCHandlerV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Panel that handles the login-authentication.
 *
 * @author G.A.J. van der Plas
 */
public class LoginPanel extends Composite implements ClickHandler {

    private static final Logger LOG = Logger.getLogger(LoginPanel.class.getName());

    interface MyUiBinder extends UiBinder<Widget, LoginPanel> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    private SecuredUserAccountManager handler = new SecuredUserAccountManager();
//    //login
//	private final class DWO2RPCHandler extends RPCHandlerV2 {
//		private DWO2RPCHandler(String server, int profile) {
//			super(server, profile);
//		}
//
//		@Override
//		public void getUserResults(Object courseID, Object userID,
//				AsyncCallback<List<Map<String,Object>>> getUserResultsCallback) {
//			//Object schoolGroupID = getSchoolGroupID();
//			//getUserResultsHelper(courseID, userID, schoolGroupID, getUserResultsCallback);
//		}
//
//	}    
//    private DWO2RPCHandler handler = new DWO2RPCHandler();

    @UiField
    TextBox usernameText;
    @UiField
    PasswordTextBox passwordTextBox;
    @UiField
    CheckBox checkBox;
    @UiField
    Button loginBtn;

    private BootPanel parent;

    public void setParent(BootPanel aParent) {
        parent = aParent;
    }

    /**
     * @return the parent
     */
    public BootPanel getParent() {
        return parent;
    }

    public LoginPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        loginBtn.addClickHandler(this);
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == loginBtn) {
            LOG.log(Level.INFO, "Login clicked.");
            Promise<DomUserFullwLoginContext> result
                    = handler.login(this.usernameText.getText(), this.passwordTextBox.getText());
            result.then(new Success<DomUserFullwLoginContext, Void>() {
                @Override
                public Promise<Void> call(Promise<DomUserFullwLoginContext> resolved) throws Exception {
                    DomUserFull user = resolved.getValue().getDomUserFull();
                    DwoGlobalVars.instance().setCurrentUser(user);
                    parent.mainDeckPanel.showWidget(1);
                    return null;
                }
            },
                    new Failure() {
                @Override
                public void fail(Promise<?> resolved) throws Exception {
                    // complain...
                }
            }
            );
        }
    }

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
