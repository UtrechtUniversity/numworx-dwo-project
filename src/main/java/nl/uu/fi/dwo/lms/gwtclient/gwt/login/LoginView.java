package nl.uu.fi.dwo.lms.gwtclient.gwt.login;

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
import nl.uu.fi.dwo.lms.gwtclient.gwt.BootPanel;

/**
 * GWT Panel that handles the login-authentication.
 *
 * @author G.A.J. van der Plas
 */
public class LoginView extends Composite implements ClickHandler, LoginPresenter.Display {

    private static final Logger LOG = Logger.getLogger(LoginView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, LoginView> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    private LoginPresenter loginPresenter;
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

    public LoginView(LoginPresenter lp){
        initWidget(uiBinder.createAndBindUi(this));
        loginPresenter = lp;
        //controller must be before clicks occur
        loginBtn.addClickHandler(this);

    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == loginBtn) {
            LOG.log(Level.INFO, "Login button clicked.");
            loginPresenter.loginClicked(this.usernameText.getText(), this.passwordTextBox.getText(), switchSchoolBox.getValue());
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

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setUsername(String username) {
        this.usernameText.setText(username);
    }

    @Override
    public void setPassword(String password) {
        passwordTextBox.setText(password);
    }    
}
