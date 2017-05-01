package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Logger;

/**
 * GWT Panel that handles the login-authentication.
 *
 * @author G.A.J. van der Plas
 */
public class SwitchSchoolPanel extends Composite implements ClickHandler {

    private static final Logger LOG = Logger.getLogger(SwitchSchoolPanel.class.getName());

    interface MyUiBinder extends UiBinder<Widget, SwitchSchoolPanel> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    private SwitchSchoolPanelHandler handler;

    @UiField
    Button cancelBtn;
    @UiField
    Button switchBtn;
//    @UiField
//    Button loginBtn;

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

    public SwitchSchoolPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        handler = new SwitchSchoolPanelHandler(this);
        //controller must be before clicks occur
        switchBtn.addClickHandler(this);

    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == switchBtn) {
            handler.switchSchool();
            parent.showResultWidget();
        }
    }
//
//    /**
//     * Called from handler after successful login.
//     */
//    public void onLoginSuccess() {
//        LOG.log(Level.INFO, "Login succeeded.");
//        parent.showResultWidget();
//    }
//
//    /**
//     * Called from handler after failed login.
//     *
//     * @param failMessage
//     */
//    public void onLoginFailure(String failMessage) {
//        LOG.log(Level.INFO, failMessage);
//        Window.alert(failMessage);
//        //reset user interface?
//    }

    public void updateView() {
        //fetch and display all the schoollogins that have teachers.
//        teacherRole.setText(
//                DwoGlobalVars.instance().getSchoolLogins().getActiveSchoolRoleAndClass().getRole().getRoleName()
//        );
        handler.init();
    }

}
