package nl.uu.fi.dwo.lms.gwtclient.gwt;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.History;
import com.google.web.bindery.event.shared.EventBus;

import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent.SelectedView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.modules.ModulesPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelDeferred;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.PromisedDialogWithConfirmDeferred;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.PromisedMessageDialogWithConfirmEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.PromisedMessageDialogWithConfirmEvent.EventType;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

/**
 * Handler for BootPanel actions.
 *
 * @author Gert van der Plas
 */
@RoleScope
public class MainPresenter implements ValueChangeHandler<String> {

    private static final Logger LOG = Logger.getLogger(MainPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    protected EventBus eventBus;
    private int stage;

    /**
	 * @param stage the stage to set
	 */
	public void setStage(int stage) {
		this.stage = stage;
	}

	public interface Display {

        public boolean isMenuVisible();

        public void setSchoolName(String schoolName);

        public void setPresentationName(String presentationName);

        public void showWelcomeView();

        public void showAccountView();

        public void showLoginView();

        public void showResultsView();

        public void showSelectedResultsView();

        public void showStudentResultsView();

        public void showSelectStudentResultsView();

        public void showStudentScoResultView();

        public void showSchoolclassesView(); // has AddSchoolClass function

        public void showEditSchoolclassView();

        public void showAddStudentToSchoolClassView();

        public void showCopyOrMoveStudentToSchoolClassView();

        public void showAddTeacherToSchoolClassView();

        public void showEditCoursesOfSchoolClassView();

//        public void showStudentsInSchoolclassView();
//
//        public void showTeachersInSchoolclassView();
//
//        public void showCoursesOfSchoolClassView();
        public void setCurrentPanelName(String panel);

        public void showPersonsView();

        public void showAddPersonView();

        public void showEditPersonView();

        public void showImportPersonsView();

        public void showModulesView(boolean box);

        public void showOrganisationView();
        
        public void showLogResultsView();

        void setUserRole(RoleType userRole, boolean single);

        public void showStudentSchoolclassView();

        String getSearchInput();

		void setTrails(JavaScriptObject object);

        void selectView(SelectedView view);

		public void showStudentResults();

		void setPremium(boolean set);

		void setSearchBox(boolean on);

		void setIdleTimeout(int millis);
		void unsetIdleTimeout();
		boolean isIdleOn();

		void showStudentResultsGraphView();

		void showTeacherStudentModelView();

		void showTeacherSMClassResultsView();

		void showTeacherClassFilterView();

		void showChatboxView();

		void showChat(boolean show);
    }

    private MainPresenter.Display display;
    

    @Inject ModulesPresenter modules;
    @Inject MainPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars, BootPanelController source) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        anEventBus.addHandlerToSource(ValueChangeEvent.getType(), source, this);
    }

    public void init() {
    	display.setSearchBox(stage > 1);
    }

    /**
     * @param display the display to set
     */
    //@Inject
    public void setView(MainPresenter.Display display) {
        this.display = display;
    }

    @JsMethod
    public void onArrowUp() {
      selectView(SelectedView.ARROWUP);
    }
    
    @JsMethod
    public void selectView(String selectedView) {
      SwitchViewEvent event;
      if (selectedView.startsWith("GOTO_URL:")) {
    	  selectedView = "GOTO:" + selectedView.substring(9);
    	  event = new SwitchViewEvent(SelectedView.GOTO_URL, Collections.singletonMap("message", selectedView));  
      } else 
      if (selectedView.startsWith("GOTO:")) {
          event = new SwitchViewEvent(SelectedView.GOTO, Collections.singletonMap("message", selectedView));  
      } else {
// probeersel
    	  if (selectedView.equals(History.getToken()))
    	  {  // newItem is een noop.
        	  event = new SwitchViewEvent(SelectedView.valueOf(selectedView));
    	  } else {
    		  History.newItem(selectedView);
    		  return;
    	  }
      }
      eventBus.fireEvent(event);
    }

    public void selectView(SwitchViewEvent.SelectedView selectedView) {
        eventBus.fireEvent(new SwitchViewEvent(selectedView));
    }

    public String getSearchInput() {
      return display.getSearchInput();
    }
    
    @JsMethod public void search(String input) {
      eventBus.fireEvent(new SwitchViewEvent(SelectedView.SEARCH, Collections.singletonMap("input", input)));
    }
    
    @JsMethod
    public void logout() {
        LOG.log(Level.INFO, "Logging out");
        Promise<Boolean> p = Promises.resolved(true); //empty promise
        p = p.then(new Success<Boolean, Boolean>() {
            @Override
            //Are you sure?
            public Promise<Boolean> call(Promise<Boolean> resolved) throws Exception {//do dialog check
                AlertDialogWithConfirmCancelDeferred dialogPromise = new AlertDialogWithConfirmCancelDeferred(DwoLocalesForGWT.instance.NUM_DLG_User_ConfirmLogout());
                AlertDialogWithConfirmCancelEvent event = new AlertDialogWithConfirmCancelEvent(AlertDialogWithConfirmCancelEvent.EventType.YesNoDialog, dialogPromise);
                eventBus.fireEvent(event);
                return dialogPromise.getPromise();
            }
        });       
        logout(p);
    }

	void forceLogout() {
		logout(Promises.resolved(Boolean.TRUE));
	}
    
    
    
    void logout(Promise<Boolean> p) {
		p.then(modules::logout)
        .then(resolved -> { 
            if (resolved.getValue()) {
              return dwoGlobalVars.logout(true)
                  .map(r -> Boolean.TRUE)
                  .fallbackTo(Promises.resolved(Boolean.TRUE));
            }
            return resolved;
        })
        
        .then(new Success<Boolean, Void>() {
            @Override
            //Are you sure?
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                if (resolved.getValue()) {
                    eventBus.fireEvent(new LoginEvent(LoginEvent.State.LOGOUT));
                } else {
                }
                return null;
            }
        },
                new LoggingFailure(LOG,eventBus) 
        );
	}
    
    public void setTrails(JavaScriptObject object) {
    	display.setTrails(object);
    	if (object == null && stage == 1) {
    		display.setSearchBox(false);
    	}
    }

	public void showModulesView() {
		display.showModulesView(stage > 1);
	}

	private PromisedDialogWithConfirmDeferred defer;
	private Promise<?> ask;
	private boolean idleOn;
	public final static int IDLE = 900000*2; // 15 minuten moet worden 30 minuten, voor de test op 2 minuten

	public void maybeLogout() {
	  LOG.info("maybe logout");
	  if (ask == null) {
	    doIdle();
	  }
	}
	
	
	@JsMethod
	public void onIdle() {
		LOG.info("on idle");
		if (ask != null && !ask.isDone()) {
		    defer.fail(new Error());
			ask = null; defer = null;
			logout(Promises.resolved(Boolean.TRUE));
			return;
		}
        doIdle();
	}

  private void doIdle() {
    defer = new PromisedDialogWithConfirmDeferred(DwoLocalesForGWT.instance.NUM_LBL_LOGGEDIN());
    PromisedMessageDialogWithConfirmEvent event = new PromisedMessageDialogWithConfirmEvent(EventType.ConfirmDialog, defer);		
    ask = defer.getPromise();
    idleOn = display.isIdleOn();
    eventBus.fireEvent(event);
    setIdleTimeout(15000);
    ask.then((q) -> { 
      ask = null;
      if (idleOn)
    	  setIdleTimeout(IDLE);
      else 
    	  unsetIdleTimeout();
      return null;
    });
    LOG.fine("doIdle");
  }

	/**
	 * @param millis
	 * @see nl.uu.fi.dwo.lms.gwtclient.gwt.MainPresenter.Display#setIdleTimeout(int)
	 */
	public void setIdleTimeout(int millis) {
		LOG.info("set idle " + millis/1000.0);
		display.setIdleTimeout(millis);
	}

	/**
	 * 
	 * @see nl.uu.fi.dwo.lms.gwtclient.gwt.MainPresenter.Display#unsetIdleTimeout()
	 */
	public void unsetIdleTimeout() {
		LOG.info("unset idle");
		display.unsetIdleTimeout();
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		try {
			String token = event.getValue();
			SelectedView selected = SelectedView.valueOf(token);
			selectView(selected);
		} catch(Exception oops) {
			selectView(SelectedView.WELCOME);
		}
		
	}
	
	
}
