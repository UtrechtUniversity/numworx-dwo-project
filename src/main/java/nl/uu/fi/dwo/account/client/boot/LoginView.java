package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
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
import nl.uu.fi.dwo.account.client.DwoGlobalVars;

/**
 * GWT Panel that handles the login-authentication.
 *
 * @author G.A.J. van der Plas
 */
public class LoginView extends Composite implements ClickHandler {

    private static final Logger LOG = Logger.getLogger(LoginView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, LoginView> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    private LoginPanelPresenter handler;
    private boolean loginClicked = false;

    @UiField
    TextBox usernameText;
    @UiField
    PasswordTextBox passwordTextBox;
    @UiField
    CheckBox switchSchoolBox;
    @UiField
    Button loginBtn;

    private BootPanel parent;

    public LoginView(){
        initWidget(uiBinder.createAndBindUi(this));
        handler = new LoginPanelPresenter(this);
        //controller must be before clicks occur
        loginBtn.addClickHandler(this);

    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == loginBtn) {
            LOG.log(Level.INFO, "Login button clicked.");
            handler.loginClicked(this.usernameText.getText(), this.passwordTextBox.getText());
            //            curUser.setPassword("passw"); //md5Hash = d79096188b670c2f81b7001f73801117
        }
    }

    /**
     * Called from handler after successful login.
     */
    public void onLoginSuccess() {
        LOG.log(Level.INFO, "Login succeeded.");
 
    }

    /**
     * Called from handler after failed login.
     *
     * @param failMessage
     */
    public void onLoginFailure(String failMessage) {
        LOG.log(Level.INFO, failMessage);
        Window.alert(failMessage);
        //reset user interface?
    }

}
