package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;

/**
 * Handler for BootPanel actions.
 *
 * @author Gert van der Plas
 */
public class MainPresenter implements SwitchViewEventHandler {

    private static final Logger LOG = Logger.getLogger(MainPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;

    public interface Display {
        public void init(ViewFactory clientFactory);
        public Widget asWidget();
        public MainView getViewInstance();
        public HasClickHandlers getMenuButton(); // handle clicking on button
        void showPostLoginWidgets();
        void hidePostLoginWidgets();
        public void setSchoolName(String schoolName);
        public void setUserRole(String userRole);
        public void setPresentationName(String presentationName);
        public void setStatusMsg(String statusMsg);
        void clear();
        public void showLoginView();
        public void showSwitchSchoolView();
        public void showResultsView();  
        public void showSchoolclassesView();
        public void showMenuButton();
        public void hideMenuButton();
        public void showMenuView();
        public void hideMenuView();
        public boolean menuVisible();
    }

    private MainPresenter.Display display;

    MainPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        eventBus.addHandler(SwitchViewEvent.TYPE, this);
    }

    public void init() {
        display.showLoginView();

    }

    /**
     * @param display the display to set
     */
    public void setDisplay(MainPresenter.Display display) {
        this.display = display;
    }
//
//    void goLogin() {
//        display.showLoginView();
//    }

    public void menuButtonClicked(){
       if(display.menuVisible()){
           display.hideMenuView();
       }else{
           display.showMenuView();
       }
    }
    
    public void selectView(SwitchViewEvent.SelectedView selectedView) {
        eventBus.fireEvent(new SwitchViewEvent(selectedView));
    }
    
    @Override
    public void onSwitchViewEvent(SwitchViewEvent switchViewEvent) {
        onSwitchViewEvent(switchViewEvent.getEventValue());
    }

    private void onSwitchViewEvent(SwitchViewEvent.SelectedView selectedView) {
        switch(selectedView){
            case LOGIN:
                display.showLoginView();
                break;
            case SWITCHSCHOOL:
                display.showSwitchSchoolView();
                break;
            case RESULTS:
                display.showResultsView();
                break;
            case SCHOOLCLASSES:
                display.showSchoolclassesView();
                break;
        }
    }
    
}
