package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    private LoginPanelHandler handler;

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
        handler = new LoginPanelHandler(this);
        //controller must be before clicks occur
        loginBtn.addClickHandler(this);
        
    }
    
    public void onClick(ClickEvent event) {
        if (event.getSource() == loginBtn) {
            LOG.log(Level.INFO, "Login clicked.");
            handler.loginClicked(this.usernameText.getText(), this.passwordTextBox.getText());
            //            curUser.setPassword("passw"); //md5Hash = d79096188b670c2f81b7001f73801117
        }
    }
    
    public void onLoginSuccess() {
        LOG.log(Level.INFO,"Login succeeded.");
        parent.showResultWidget();
    }
    
    public void onLoginFailure(Promise<?> failure) {
        LOG.log(Level.INFO, failure.getFailure().getMessage());
        Window.alert(failure.getFailure().getMessage());
        //reset user interface?
    }
    
    

//    public Promise<?> LoginFailPromise() {
//        return new Failure() {
//            @Override
//            public void fail(Promise<?> resolved) throws Exception {
//                // complain...
//                onLoginFailure(resolved);
//            }
//        };
//    }
        
//    
//    public Promise<DomUserFullwLoginContext> getLoginResultPromise() {
//        Promise<DomUserFullwLoginContext> result
//                = handler.loginClicked(this.usernameText.getText(), this.passwordTextBox.getText());
////            result.then(null, result.getFailure());
//        result.then(new Success<DomUserFullwLoginContext, Void>() {
//            @Override
//            public Promise<Void> call(Promise<DomUserFullwLoginContext> resolved) throws Exception {
//                onLoginSuccess();
//                return null;
//            }
//        },
//                new Failure() {
//            @Override
//            public void fail(Promise<?> resolved) throws Exception {
//                // complain...
//                onLoginFailure(resolved);
//            }
//        }
//        );
//        return result;
//    }
}
