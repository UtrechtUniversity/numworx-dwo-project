package nl.uu.fi.dwo.lms.gwtclient.gwt;

import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.roleswitch.SwitchSchoolView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ScoResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ResultsView;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

/**
 *
 * @author Gert van der Plas
 */
public class MainView extends ResizeComposite implements HasWidgets, ClickHandler, MainPresenter.Display {

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
    @UiField
    FlowPanel menuView;
    @UiField
    DeckPanel mainDeckPanel = new DeckPanel();
    @UiField(provided = true)
    AccountView accountView;
    @UiField(provided = true)
    LoginView loginView;
    @UiField(provided = true)
    SwitchSchoolView switchSchoolView;
    @UiField(provided = true)
    SchoolclassesView schoolclassesView;
    @UiField(provided = true)
    ResultsView resultsView;
    @UiField(provided = true)
    ScoResultsView scoResultsView;

    @UiField(provided = true)
    Label accountLabel = new MenuLabel();
    @UiField(provided = true)
    Label switchRoleLabel = new MenuLabel();
    @UiField(provided = true)
    Label classesLabel = new MenuLabel();
    @UiField(provided = true)
    Label resultsLabel = new MenuLabel();
    @UiField(provided = true)
    Label logoutLabel = new MenuLabel();

    boolean showMenu = false;

    ViewFactory clientFactory;

    public MainView(MainPresenter lp) {
        mainPresenter = lp;
        mainPresenter.setDisplay(this);
    }

    @Override
    public void init(ViewFactory clientFactory) {
        this.clientFactory = clientFactory;
        loginView = (LoginView) clientFactory.getLoginView();
        accountView = (AccountView) clientFactory.getAccountView();
        resultsView = (ResultsView) clientFactory.getResultsView();
        switchSchoolView = (SwitchSchoolView) clientFactory.getSwitchSchoolView();
        scoResultsView = (ScoResultsView) clientFactory.getScoResultsView();
        schoolclassesView = (SchoolclassesView) clientFactory.getSchoolclassesView();
        initWidget(uiBinder.createAndBindUi(this));
        int loginIndex = mainDeckPanel.getWidgetIndex(loginView);
        mainDeckPanel.showWidget(loginIndex);
        menuButton.addClickHandler(this);
        switchRoleLabel.addClickHandler(this);
        resultsLabel.addClickHandler(this);
        logoutLabel.addClickHandler(this);
        classesLabel.addClickHandler(this);
        accountLabel.addClickHandler(this);
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
    public void showAccountView() {
        int index = mainDeckPanel.getWidgetIndex(accountView);
        mainDeckPanel.showWidget(index);
    }
    
    @Override
    public void showLoginView() {
        int index = mainDeckPanel.getWidgetIndex(loginView);
        mainDeckPanel.showWidget(index);
    }

    @Override
    public void showSwitchSchoolView() {
        int index = mainDeckPanel.getWidgetIndex(switchSchoolView.asWidget());
        mainDeckPanel.showWidget(index);
    }

    @Override
    public void showResultsView() {
        int index = mainDeckPanel.getWidgetIndex(resultsView.asWidget());
        mainDeckPanel.showWidget(index);
    }

    @Override
    public void showScoResultsView() {
        int index = mainDeckPanel.getWidgetIndex(scoResultsView);
        mainDeckPanel.showWidget(index);

    }

    @Override
    public void showSchoolclassesView() {
        int index = mainDeckPanel.getWidgetIndex(schoolclassesView.asWidget());
        mainDeckPanel.showWidget(index);
    }

    @Override
    public void showMenuButton() {
        menuButton.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
    }

    @Override
    public void hideMenuButton() {
        menuButton.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
    }

    @Override
    public void showMenuView() {
        menuView.addStyleName("menuGrow");
        menuView.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
        LOG.log(Level.INFO, "Menu grow.");
        showMenu = true;
    }

    @Override
    public void hideMenuView() {
        menuView.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        menuView.removeStyleName("menuGrow");
        LOG.log(Level.INFO, "Menu shrink.");
        showMenu = false;
    }

    @Override
    public void showMessageDialog(String msg) {
        Window.confirm(msg);
    }

    @Override
    public void showErrorDialog(String errMsg) {
        Window.alert(errMsg);
    }
        
    @Override
    public boolean menuVisible() {
        return showMenu;
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
    public void onClick(ClickEvent event) {
        if (event.getSource() == menuButton) {
            LOG.log(Level.INFO, "Menu button clicked.");
            LOG.log(Level.INFO, menuView.getElement().getStyle().getOpacity());
            if (!showMenu) {
                menuView.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
                menuView.addStyleName("menuGrow");
                LOG.log(Level.INFO, "Menu grow.");
                showMenu = true;
            } else {
                menuView.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
                menuView.removeStyleName("menuGrow");
                LOG.log(Level.INFO, "Menu shrink.");
                showMenu = false;
            }
            //handler.logoutClicked();
        } else if (event.getSource() == accountLabel) {
            hideMenuView();
            mainPresenter.selectView(SwitchViewEvent.SelectedView.ACCOUNT);
        } else if (event.getSource() == switchRoleLabel) {
            hideMenuView();
            mainPresenter.selectView(SwitchViewEvent.SelectedView.SWITCHSCHOOL);
        } else if (event.getSource() == resultsLabel) {
            hideMenuView();
            mainPresenter.selectView(SwitchViewEvent.SelectedView.RESULTS);
        } else if (event.getSource() == logoutLabel) {
            hideMenuView();
            Window.Location.assign("");
        } else if (event.getSource() == classesLabel) {
            hideMenuView();
            mainPresenter.selectView(SwitchViewEvent.SelectedView.SCHOOLCLASSES);
        }
    }

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

    public SchoolclassesView getSchoolclassesView() {
        return schoolclassesView;
    }

}
