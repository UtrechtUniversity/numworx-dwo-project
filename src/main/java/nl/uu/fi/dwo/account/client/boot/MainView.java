package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;
import java.util.Iterator;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.boot.Results.ResultsView;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

/**
 *
 * @author Gert van der Plas
 */
public class MainView extends Composite implements HasWidgets, MainPresenter.Display {

    private static final Logger LOG = Logger.getLogger(MainView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, MainView> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    static {
        //Initialize an Exception translator.
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionGWTTranslator());
    }

    MainPresenter mainPresenter;
    
    @UiField
    Label schoolLabel;
    @UiField
    Label schoolName;
//    @UiField
//    Label userLabel;
    @UiField
    Label presentationName;
//    @UiField
//    Label roleLabel;
    @UiField
    Label userRole;
    @UiField
    PushButton menuButton;
    @UiField
    Image dwoLogo;
    @UiField
    Label statusMsg;
    @UiField
    CheckBox autoUpdateResults;
    @UiField
    FlowPanel headerView;
    @UiField
    FlowPanel statusView;
//    @UiField
//    FlowPanel menuView;
    @UiField
    DeckPanel mainDeckPanel = new DeckPanel();
    @UiField(provided = true)
    LoginView loginView;
    @UiField(provided = true)
    ResultsView resultsView;
    @UiField(provided = true)
    SwitchSchoolView switchSchoolView;

    boolean showMenu = false;

    ViewFactory clientFactory;

    public MainView(MainPresenter lp) {
        mainPresenter = lp;
    }

    public void init(ViewFactory clientFactory) {
        this.clientFactory = clientFactory;
        loginView = clientFactory.getLoginView();
        resultsView = clientFactory.getResultsView();
        switchSchoolView = clientFactory.getSwitchSchoolView();
        initWidget(uiBinder.createAndBindUi(this));
        int loginIndex = mainDeckPanel.getWidgetIndex(loginView);
        mainDeckPanel.showWidget(loginIndex);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void add(Widget w) {
        this.add(w);
    }

    @Override
    public void clear() {
        this.clear();
    }

    @Override
    public Iterator<Widget> iterator() {
        return this.iterator();
    }

    @Override
    public boolean remove(Widget w) {
        return this.remove(w);
    }

    @Override
    public MainView getViewInstance() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HasClickHandlers getMenuButton() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void showPostLoginWidgets() {
        schoolName.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
        userRole.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
        presentationName.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
        menuButton.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
    }

    @Override
    public void hidePostLoginWidgets() {
        schoolName.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        userRole.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        presentationName.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        menuButton.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
    }

    @Override
    public void setSchoolName(String schoolName) {
        this.schoolName.setText(schoolName);
    }

    @Override
    public void setUserRole(String userRole) {
        this.userRole.setText(userRole);
    }

    @Override
    public void setPresentationName(String presentationName) {
        this.presentationName.setText(presentationName);
    }

    @Override
    public void setStatusMsg(String statusMsg) {
        this.setStatusMsg(statusMsg);
    }

    @Override
    public void showLogin() {
        mainDeckPanel.showWidget(0);
    }
//    
//    /**
//     * @return the autoUpdateResults
//     */
//    public CheckBox getAutoUpdateResults() {
//        return autoUpdateResults;
//    }
//
//    /**
//     * @param autoUpdateResults the autoUpdateResults to set
//     */
//    public void setAutoUpdateResults(CheckBox autoUpdateResults) {
//        this.autoUpdateResults = autoUpdateResults;
//    }
//
//    public void hideStatus() {
//        statusView.setVisible(false);
//    }
//
//    public void showStatus() {
//        if (statusView != null) {
//            statusView.setVisible(true);
//        }
//    }
//
//    public void setStatus(String msg) {
//        statusMsg.setText(msg);
//    }
//
//    /**
//     * @return the schoolLabel
//     */
//    public Label getSchoolLabel() {
//        return schoolLabel;
//    }
//
//    /**
//     * @param schoolLabel the schoolLabel to set
//     */
//    public void setSchoolLabel(Label schoolLabel) {
//        this.schoolLabel = schoolLabel;
//    }
//
//    public void onClick(ClickEvent event) {
//        if (event.getSource() == getMenuBtn()) {
//            LOG.log(Level.INFO, "Menu button clicked.");
//            LOG.log(Level.INFO, menuView.getElement().getStyle().getOpacity());
//            if (!showMenu) {
//                menuView.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
//                menuView.addStyleName("menuGrow");
//                LOG.log(Level.INFO, "Menu grow.");
//                showMenu = true;
//            } else {
//                menuView.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
//                menuView.removeStyleName("menuGrow");
//                LOG.log(Level.INFO, "Menu shrink.");
//                showMenu = false;
//            }
//            //handler.logoutClicked();
//        }
//    }
//
//    public void logoutFailed() {
//        Window.alert("logout failed, close browser to destroy session.");
//    }
//
//    public void logoutSuccess() {
//        showLoginWidget();
//    }
//
//    public int getMainDeckCount() {
//        return mainDeckPanel.getWidgetCount();
//    }
//

    public void showLoginWidget() {
        mainDeckPanel.showWidget(0);
    }
//
//    public void showSwitchSchoolWidget() {
//        SwitchSchoolView panel = (SwitchSchoolView) switchSchoolView;
//        panel.init();
//        panel.updateView();
//        mainDeckPanel.showWidget(1);
//    }
//
//    public void showResultWidget() {
//        ResultsView panel = (ResultsView) resultsView;
//        statusView.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
//        panel.init();
//        mainDeckPanel.showWidget(2);
//    }
//
//    /**
//     * @return the loginView
//     */
//    public Widget getLoginWidget() {
//        return loginView;
//    }
//
//    /**
//     * @param loginView the loginView to set
//     */
//    public void setLoginWidget(Widget loginView) {
//        this.loginView = loginView;
//    }
//
//    /**
//     * @return the schoolName
//     */
//    public Label getSchoolName() {
//        return schoolName;
//    }
//
//    /**
//     * @param schoolName the schoolName to set
//     */
//    public void setSchoolName(Label schoolName) {
//        this.schoolName = schoolName;
//    }
//
//    /**
//     * @return the presentationName
//     */
//    public Label getPresentationName() {
//        return presentationName;
//    }
//
//    /**
//     * @param presentationName the presentationName to set
//     */
//    public void setPresentationName(Label presentationName) {
//        this.presentationName = presentationName;
//    }
//
//    /**
//     * @return the userRole
//     */
//    public Label getUserRole() {
//        return userRole;
//    }
//
//    /**
//     * @param userRole the userRole to set
//     */
//    public void setUserRole(Label userRole) {
//        this.userRole = userRole;
//    }
//
//    /**
//     * @return the logoutBtn
//     */
//    public PushButton getMenuBtn() {
//        return menuButton;
//    }
//
//    /**
//     * @param menuButton
//     */
//    public void menuButton(PushButton menuButton) {
//        this.menuButton = menuButton;
//    }

    /**
     * @return the loginView
     */
    public LoginView getLoginView() {
        return loginView;
    }

    /**
     * @return the resultsView
     */
    public ResultsView getResultsView() {
        return resultsView;
    }

    /**
     * @return the switchSchoolView
     */
    public SwitchSchoolView getSwitchSchoolView() {
        return switchSchoolView;
    }

}
