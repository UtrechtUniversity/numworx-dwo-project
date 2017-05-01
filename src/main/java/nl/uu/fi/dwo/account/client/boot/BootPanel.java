package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.account.client.boot.Results.ResultsPanel;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

/**
 * BootPanel, boots the app, initializes with the server, requests a login.
 *
 * @author G.A.J. van der Plas
 */
public class BootPanel extends Composite implements EntryPoint, ClickHandler {

    private static final Logger LOG = Logger.getLogger(BootPanel.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private BootPanelHandler handler;

    static {
        //Initialize an Exception translator.
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionGWTTranslator());
    }

    /**
     * @return the schoolLabel
     */
    public Label getSchoolLabel() {
        return schoolLabel;
    }

    /**
     * @param schoolLabel the schoolLabel to set
     */
    public void setSchoolLabel(Label schoolLabel) {
        this.schoolLabel = schoolLabel;
    }

    /**
     * @return the roleLabel
     */
    public Label getRoleLabel() {
        return roleLabel;
    }

    /**
     * @param roleLabel the roleLabel to set
     */
    public void setRoleLabel(Label roleLabel) {
        this.roleLabel = roleLabel;
    }

    /**
     * @return the userLabel
     */
    public Label getUserLabel() {
        return userLabel;
    }

    /**
     * @param userLabel the userLabel to set
     */
    public void setUserLabel(Label userLabel) {
        this.userLabel = userLabel;
    }

    interface MyUiBinder extends UiBinder<Widget, BootPanel> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    Label schoolLabel;
    @UiField
    Label schoolName;
    @UiField
    private Label userLabel;
    @UiField
    Label presentationName;
    @UiField
    Label roleLabel;
    @UiField
    Label userRole;
    @UiField
    Button logoutBtn;
    @UiField
    Image dwoLogo;
    @UiField
    DeckPanel mainDeckPanel = new DeckPanel();
    @UiField(provided = true)
    Widget loginWidget = new LoginPanel();
    @UiField(provided = true)
    Widget resultWidget = new ResultsPanel();
    @UiField(provided = true)
    Widget switchSchoolWidget = new SwitchSchoolPanel();

    public BootPanel() {

    }

    @Override
    public void onModuleLoad() {
        try {
            dwoGlobalVars = new DwoGlobalVars();
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            PopupPanel popup = new PopupPanel();
            popup.add(new Label("Programmers-error"));
        }
        initWidget(uiBinder.createAndBindUi(this));
        handler = new BootPanelHandler(this);
        logoutBtn.addClickHandler(this);
        ((LoginPanel) loginWidget).setParent(this);
//        mainDeckPanel.add(loginWidget);
        mainDeckPanel.showWidget(0);
        ((SwitchSchoolPanel) switchSchoolWidget).setParent(this);
//        mainDeckPanel.add(resultWidget);
        ((ResultsPanel) resultWidget).setParent(this);
        LOG.log(Level.INFO, "Showing loginPanel.");

        RootLayoutPanel.get().add(this);
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == logoutBtn) {
            LOG.log(Level.INFO, "Logout button clicked.");
            handler.logoutClicked();
        }
    }

    public void logoutFailed() {
        Window.alert("logout failed, close browser to destroy session.");
    }

    public void logoutSuccess() {
        showLoginWidget();
    }

    public int getMainDeckCount() {
        return mainDeckPanel.getWidgetCount();
    }

    public void showLoginWidget() {
        mainDeckPanel.showWidget(0);
    }

    public void showSwitchSchoolWidget() {
        SwitchSchoolPanel panel = (SwitchSchoolPanel) switchSchoolWidget;
        panel.updateView();
        mainDeckPanel.showWidget(1);
    }

    public void showResultWidget() {
        ResultsPanel panel = (ResultsPanel) resultWidget;
        panel.updateView();
        mainDeckPanel.showWidget(2);
    }

    /**
     * @return the loginWidget
     */
    public Widget getLoginWidget() {
        return loginWidget;
    }

    /**
     * @param loginWidget the loginWidget to set
     */
    public void setLoginWidget(Widget loginWidget) {
        this.loginWidget = loginWidget;
    }

    /**
     * @return the schoolName
     */
    public Label getSchoolName() {
        return schoolName;
    }

    /**
     * @param schoolName the schoolName to set
     */
    public void setSchoolName(Label schoolName) {
        this.schoolName = schoolName;
    }

    /**
     * @return the presentationName
     */
    public Label getPresentationName() {
        return presentationName;
    }

    /**
     * @param presentationName the presentationName to set
     */
    public void setPresentationName(Label presentationName) {
        this.presentationName = presentationName;
    }

    /**
     * @return the userRole
     */
    public Label getUserRole() {
        return userRole;
    }

    /**
     * @param userRole the userRole to set
     */
    public void setUserRole(Label userRole) {
        this.userRole = userRole;
    }
}
