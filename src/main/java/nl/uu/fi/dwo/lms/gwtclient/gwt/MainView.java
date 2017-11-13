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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddStudentsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.CoursesOfSchoolclassView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.StudentsInSchoolclassView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.TeachersInSchoolclassView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.welcome.WelcomeView;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

/**
 *
 * @author Gert van der Plas
 */
public class MainView extends Composite implements HasWidgets, ClickHandler, MainPresenter.Display {

    private static final Logger LOG = Logger.getLogger(MainView.class.getName());

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    interface MyUiBinder extends UiBinder<Widget, MainView> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    private static final DwoLocalesForGWT rb = GWT.create(DwoLocalesForGWT.class);
    
    static {
        //Initialize an Exception translator.
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionGWTTranslator());
    }

    MainPresenter mainPresenter;

    @UiField
    Label activeDeckWidgetLabel;
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
    Label menuButton;
    @UiField
    LayoutPanel layout;
    @UiField
    Image dwoLogo;
//    @UiField
//    Label statusMsg;
//    @UiField
//    CheckBox autoUpdateResults;
    @UiField
    FlowPanel headerView;
//    @UiField
//    FlowPanel statusView;
    @UiField
    FlowPanel menuView;
    @UiField
    DeckPanel mainDeckPanel = new DeckPanel();
    @UiField(provided = true)
    WelcomeView welcomeView;
    @UiField(provided = true)
    AccountView accountView;
    @UiField(provided = true)
    LoginView loginView;
    @UiField(provided = true)
    SwitchSchoolView switchSchoolView;
    @UiField(provided = true)
    SchoolclassesView schoolclassesView;
    @UiField(provided = true)
    CoursesOfSchoolclassView coursesOfSchoolclassView;
    @UiField(provided = true)
    StudentsInSchoolclassView studentsInSchoolclassView;
    @UiField(provided = true)
    AddStudentsView addStudentsView;
    @UiField(provided = true)
    TeachersInSchoolclassView teachersInSchoolclassView;
    @UiField(provided = true)
    ResultsView resultsView;
    @UiField(provided = true)
    ScoResultsView scoResultsView;

//    @UiField(provided = true)
    MainMessageView mainMessageView = new MainMessageView();

    @UiField(provided = true)
    Label accountLabel = new MenuLabel();
    @UiField
    Image accountImage = new Image();
    @UiField(provided = true)
    Label switchRoleLabel = new MenuLabel();
    @UiField
    Image switchRoleImage = new Image();
    @UiField(provided = true)
    Label classesLabel = new MenuLabel();
    @UiField
    Image classesImage = new Image();
    @UiField(provided = true)
    Label resultsLabel = new MenuLabel();
    @UiField
    Image resultsImage = new Image();
    @UiField(provided = true)
    Label logoutLabel = new MenuLabel();
    @UiField
    Image logoutImage = new Image();

    boolean showMenu = false;

    ViewFactory clientFactory;

    public MainView(MainPresenter lp) {
        mainPresenter = lp;
        mainPresenter.setView(this);
    }

//    @Override
    public void init(ViewFactory clientFactory) {
        this.clientFactory = clientFactory;
        loginView = (LoginView) clientFactory.getLoginView();
        welcomeView = (WelcomeView) clientFactory.getWelcomeView();
        accountView = (AccountView) clientFactory.getAccountView();
        resultsView = (ResultsView) clientFactory.getResultsView();
        switchSchoolView = (SwitchSchoolView) clientFactory.getSwitchSchoolView();
        scoResultsView = (ScoResultsView) clientFactory.getScoResultsView();
        schoolclassesView = (SchoolclassesView) clientFactory.getSchoolclassesView();
        coursesOfSchoolclassView = (CoursesOfSchoolclassView) clientFactory.getCoursesOfSchoolclassView();
        studentsInSchoolclassView = (StudentsInSchoolclassView) clientFactory.getStudentsInSchoolclassView();
        addStudentsView = (AddStudentsView) clientFactory.getAddStudentsView();
        teachersInSchoolclassView = (TeachersInSchoolclassView) clientFactory.getTeachersInSchoolclassView();
        initWidget(uiBinder.createAndBindUi(this));
        hidePostLoginWidgets();
        int loginIndex = mainDeckPanel.getWidgetIndex(loginView);
        mainDeckPanel.showWidget(loginIndex);
        menuButton.addClickHandler(this);
        hideMenuView();
        switchRoleLabel.addClickHandler(this);
        switchRoleImage.addClickHandler(this);
        accountLabel.addClickHandler(this);
        accountImage.addClickHandler(this);
        logoutLabel.addClickHandler(this);
        logoutImage.addClickHandler(this);
        classesLabel.addClickHandler(this);
        classesImage.addClickHandler(this);
        accountLabel.addClickHandler(this);
        accountImage.addClickHandler(this);
        resultsLabel.addClickHandler(this);
        resultsImage.addClickHandler(this);
        dwoLogo.setTitle(BUILD.version+"-"+BUILD.buildNumber);
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
    public Iterator<Widget> iterator() {
        return this.iterator();
    }

    @Override
    public boolean remove(Widget w) {
        return this.remove(w);
    }

    @Override
    public void showPostLoginWidgets() {
        schoolLabel.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
        schoolName.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
        userRole.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
        presentationName.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
    }

    @Override
    public void hidePostLoginWidgets() {
        schoolLabel.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        schoolName.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        userRole.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        presentationName.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
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
    public void showAccountView() {
        setCurrentPanelName(rb.GUI_View_ACCOUNT());
        int index = mainDeckPanel.getWidgetIndex(accountView);
        mainDeckPanel.showWidget(index);
    }

    @Override
    public void showWelcomeView() {
        setCurrentPanelName(rb.GUI_View_ACCOUNT());
        int index = mainDeckPanel.getWidgetIndex(welcomeView);
        mainDeckPanel.showWidget(index);
    }

    @Override
    public void showLoginView() {
        setCurrentPanelName(rb.GUI_View_LOGIN());
        //TODO revert
 //       int index = mainDeckPanel.getWidgetIndex(loginView);
 //       mainDeckPanel.showWidget(index);
        //    showMessageDialog("hello world");
    }

    @Override
    public void showSwitchSchoolView() {
        setCurrentPanelName(rb.GUI_View_SWITCHSCHOOL());
        int index = mainDeckPanel.getWidgetIndex(switchSchoolView.asWidget());
        mainDeckPanel.showWidget(index);
    }

    @Override
    public void showResultsView() {
        setCurrentPanelName(rb.GUI_View_RESULTS());
        int index = mainDeckPanel.getWidgetIndex(resultsView.asWidget());
        mainDeckPanel.showWidget(index);
    }

    @Override
    public void showScoResultsView() {
        setCurrentPanelName(rb.GUI_View_SCORESULTS());
        int index = mainDeckPanel.getWidgetIndex(scoResultsView);
        mainDeckPanel.showWidget(index);

    }

    @Override
    public void showSchoolclassesView() {
        setCurrentPanelName(rb.GUI_View_SCHOOLCLASSES());
        int index = mainDeckPanel.getWidgetIndex(schoolclassesView.asWidget());
        mainDeckPanel.showWidget(index);
    }

    @Override
    public void showCoursesOfSchoolclassView() {
        setCurrentPanelName(rb.GUI_View_COURSESOFSCHOOLCLASS());
        int index = mainDeckPanel.getWidgetIndex(coursesOfSchoolclassView.asWidget());        
        mainDeckPanel.showWidget(index);
    }

    @Override
    public void showStudentsInSchoolclassView() {
        setCurrentPanelName(rb.GUI_View_STUDENTSINSCHOOLCLASS());
        int index = mainDeckPanel.getWidgetIndex(studentsInSchoolclassView.asWidget());
        mainDeckPanel.showWidget(index);
    }

    @Override
    public void showAddStudentsView() {
        setCurrentPanelName(rb.GUI_View_ADDSTUDENTS());
        int index = mainDeckPanel.getWidgetIndex(addStudentsView.asWidget());
        mainDeckPanel.showWidget(index);
    }

    @Override
    public void showTeachersInSchoolclassView() {
        setCurrentPanelName(rb.GUI_View_TEACHERSINSCHOOLCLASS());
        int index = mainDeckPanel.getWidgetIndex(teachersInSchoolclassView.asWidget());
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
        menuView.addStyleName("dwoMenuIn");
        LOG.log(Level.INFO, "Menu dwoMenuOut.");
        setMenuVisible(true);
    }

    @Override
    public void hideMenuView() {
        menuView.removeStyleName("dwoMenuIn");
        setMenuVisible(false);
    }
    
    @Override
    public void setCurrentPanelName(String panel){
        activeDeckWidgetLabel.setText(panel);
    }

//    @Override
//    public void showMessageDialog(String msg) {
//        final DialogBox dialogBox = new DialogBox();
//        MainMessageView msgView = new MainMessageView();
//        msgView.setMsg(msg);
//        ClickHandler okHandler = new ClickHandler() {
//            public void onClick(ClickEvent event) {
//                dialogBox.hide();
//            }
//        };
//        msgView.addOkClickHandler(okHandler);
//        dialogBox.add(msgView.asWidget());
//        dialogBox.setModal(true);
//        dialogBox.setAutoHideEnabled(true);
//        dialogBox.setGlassEnabled(true);
//        dialogBox.setAnimationEnabled(true);
//        dialogBox.center();
//        dialogBox.show();
//    }
//
//    @Override
//    public void showErrorDialog(String errMsg) {
//        Window.alert(errMsg);
//    }

    @Override
    public boolean isMenuVisible() {
        return showMenu;
    }

    private void setMenuVisible(boolean visible) {
        showMenu = visible;
        layout.setWidgetVisible(menuView, visible);
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == menuButton) {
            LOG.log(Level.INFO, "Menu button clicked.");
            LOG.log(Level.INFO, menuView.getElement().getStyle().getOpacity());
            if (!showMenu) {
                setMenuVisible(true);
                menuView.addStyleName("menuGrow");
                LOG.log(Level.INFO, "Menu grow.");
            } else {
                setMenuVisible(false);
                menuView.removeStyleName("menuGrow");
                LOG.log(Level.INFO, "Menu shrink.");
                showMenu = false;
            }
            //handler.logoutClicked();
        } else if (event.getSource() == accountLabel || event.getSource() == accountImage) {
            hideMenuView();
            mainPresenter.selectView(SwitchViewEvent.SelectedView.ACCOUNT);
        } else if (event.getSource() == switchRoleLabel || event.getSource() == switchRoleImage) {
            hideMenuView();
            mainPresenter.selectView(SwitchViewEvent.SelectedView.SWITCHSCHOOL);
        } else if (event.getSource() == resultsLabel || event.getSource() == resultsImage) {
            hideMenuView();
            mainPresenter.selectView(SwitchViewEvent.SelectedView.RESULTS);
        } else if (event.getSource() == logoutLabel || event.getSource() == logoutImage) {
            hideMenuView();
            Window.Location.replace(Window.Location.getHref());
        } else if (event.getSource() == classesLabel || event.getSource() == classesImage) {
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
