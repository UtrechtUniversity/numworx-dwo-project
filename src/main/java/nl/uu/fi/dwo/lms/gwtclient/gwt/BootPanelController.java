package nl.uu.fi.dwo.lms.gwtclient.gwt;


import nl.uu.fi.dwo.lms.gwtclient.gwt.gui.DialogEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import fi.dwo.gwt.lib.rest.CallManagers.PublicProfileManager;

import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;
import fi.dwo.gwt.lib.rest.util.Dwo2LocaleMessageGWTTranslator;
import java.util.logging.Level;

import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent;
import static nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent.State.FAIL;
import static nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent.State.LOGOUT;
import static nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent.State.SUCCESS;
import static nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent.State.SUCCESS_RESULTS;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEventHandler;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import nl.uu.fi.dwo.rest.util.Dwo2LocaleMessageTranslator;
import org.osgi.util.promise.Promise;

/**
 * Controller for Login.
 *
 * @author Gert van der Plas
 */
class BootPanelController {

    private static final Logger LOG = Logger.getLogger(BootPanelController.class.getName());

    private ViewFactory viewFactory;
    private PresenterFactory presenterFactory;
    private DwoGlobalVars dwoGlobalVars;

    static {
        //Initialize an Exception translator.
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionGWTTranslator());
        Dwo2LocaleMessageTranslator.setTranslator(new Dwo2LocaleMessageGWTTranslator());
    }

    EventBus eventBus;
    HasWidgets rootPanel;

    BootPanelController(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void go(RootLayoutPanel rootPanel) {
        
        //intialize our global and environmental variables instance.
        try{
        dwoGlobalVars = new DwoGlobalVars();
        }catch(Dwo2Exception e){
            //ugly emergency code in case server fails.
            String msg = "Fatal server error! "+e.getDwo2Message();
            LOG.log(Level.INFO,e.getDwo2Message());
            DialogBox dialogBox = new DialogBox();
            Label label=new Label();
            label.setText(msg);
            dialogBox.add(label);
            dialogBox.add(new Button ("OK"));
            dialogBox.setModal(true);
            dialogBox.setAutoHideEnabled(false);
            dialogBox.setGlassEnabled(true);
            dialogBox.setAnimationEnabled(true);
            dialogBox.center();
            dialogBox.show();
            return;
        }

        //parse profile if it exists.
        String value = com.google.gwt.user.client.Window.Location.getParameter("profile");
        if (value == null || value.isEmpty()) {
        	value = "77";
        }	
        	
        Promise<DomDwoProfileFull> profile = new PublicProfileManager().get(value);
        dwoGlobalVars.setProfile(profile);
        LOG.log(Level.INFO, "Parsed and set profile id to " + value + ".");

        //show main panel
        this.rootPanel = rootPanel;

        //create client factories
        presenterFactory = new PresenterFactoryImpl(eventBus, dwoGlobalVars);
        viewFactory = new ViewFactoryImpl(presenterFactory);

        //handle login events
        eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {
            @Override
            public void onLoginEvent(LoginEvent loginEvent) {
                switch (loginEvent.getState()) {
                    case SUCCESS_RESULTS:
                        LOG.log(Level.INFO, "Login succeeded. Showing results view.");
                        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.RESULTS));
//                        viewFactory.getMainView().showMenuButton();
                        viewFactory.getMainView().showPostLoginWidgets();
                        //presenterFactory.getResultsPresenter().init();
                        break;
                    case SUCCESS_SCHOOLCLASSES:
                        LOG.log(Level.INFO, "Login succeeded. Showing schoolclasses view.");
                        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SCHOOLCLASSES));
//                        viewFactory.getMainView().showMenuButton();
                        viewFactory.getMainView().showPostLoginWidgets();
                        //presenterFactory.getResultsPresenter().init();
                        break;
                    case SUCCESS:
                        LOG.log(Level.INFO, "Login succeeded. Showing switch role view.");
                        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SWITCHSCHOOL));
//                        viewFactory.getMainView().showMenuButton();
//                        viewFactory.getMainView().showPostLoginWidgets();
                        presenterFactory.getSwitchSchoolPresenter().init();
                        break;
                    case FAIL:
                        eventBus.fireEvent(new DialogEvent("Login failed."));
                        Window.Location.replace(Window.Location.getHref());
                        break;
                    case LOGOUT:
                        LOG.log(Level.INFO, "Login succeeded. Showing switch role view.");
                        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SWITCHSCHOOL));
                        viewFactory.getMainView().hideMenuButton();
                        dwoGlobalVars.clearCurrentUser();
                        presenterFactory.getMainPresenter().onSwitchViewEvent(new SwitchViewEvent(SwitchViewEvent.eventValue.LOGIN));
                    default:
                        LOG.log(Level.INFO, "Login fail in app controller.");
                }
            }
        });

        //handle switch deckpanel events.
        eventBus.addHandler(SwitchViewEvent.TYPE, new SwitchViewEventHandler() {
            @Override
            public void onSwitchViewEvent(SwitchViewEvent switchViewEvent) {
                switch (switchViewEvent.getEventValue()) {
                    case ACCOUNT:
                        presenterFactory.getAccountPresenter().init();
                        break;
                    case LOGIN:
                        presenterFactory.getLoginPresenter().init();
                        break;
                    case SWITCHSCHOOL:
                        presenterFactory.getSwitchSchoolPresenter().init();
                        break;
                    case ACTIVERESULTS:
                        //do nothing presenterFactory.getResultsPresenter().init();
                        break;
                    case RESULTS:
                        presenterFactory.getResultsPresenter().init();
                        break;
                    case SCHOOLCLASSES:
                        presenterFactory.getSchoolclassesPresenter().init();
                        break;
                    case COURSESOFSCHOOLCLASS:
                        presenterFactory.getCoursesOfSchoolclassPresenter().init(switchViewEvent.getSchoolClass());
                        break;
                    case STUDENTSINSCHOOLCLASS:
                        presenterFactory.getStudentsInSchoolclassPresenter().init(switchViewEvent.getSchoolClass());
                        break;
                    case ADDSTUDENTS:
                        presenterFactory.getAddStudentsPresenter().init(switchViewEvent.getSchoolClass());
                        break;
                    case TEACHERSINSCHOOLCLASS:
                        presenterFactory.getTeachersInSchoolclassPresenter().init(switchViewEvent.getSchoolClass());
                        break;
                    case SCORESULTS:
                        presenterFactory.getScoResultsPresenter().init(switchViewEvent.getResultTree(), switchViewEvent.getResultScoContext(), switchViewEvent.getResultStudent(), switchViewEvent.getSchoolClass());
                        break;
                    default:
                        LOG.log(Level.INFO, "Switch fail in app controller.");
                }
            }
        });

        MainPresenter.Display mainView = viewFactory.getMainView();
        mainView.init(viewFactory);
        this.rootPanel.add(mainView.asWidget());
        LOG.log(Level.INFO, "Intiated Main view.");
        MainPresenter mainPresenter = presenterFactory.getMainPresenter();
        LOG.log(Level.INFO, "Intiating Main presenter. Showing login screen.");
        mainPresenter.init();
        LOG.log(Level.INFO, "Initiated Main presenter.");
    }
//
//    public Promise<DomLoginContext> logout() {
//        return accountManager.logout();
//    }

    //add 
}
