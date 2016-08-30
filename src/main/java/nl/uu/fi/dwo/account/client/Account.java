package nl.uu.fi.dwo.account.client;

import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentSchoolClassManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.rest.util.Dwo2ExceptionTranslator;

import java.util.logging.Level;

public class Account implements EntryPoint, ClickHandler {

    private static final Logger LOG = Logger.getLogger(Account.class.getName());
    private DomUserFull user = null;
    private SecuredUserAccountManager handler = new SecuredUserAccountManager();
    private LoginStatusPanel loginStatusPanel = new LoginStatusPanel();
    private HeaderPanel header = new HeaderPanel();
    private UserBar userBar = new UserBar();
    private Button loginButton;
    private LoginPanel loginPanel;

    static {
        //Initialize an Exception translator.
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionGWTTranslator());
    }

    @Override
    public void onModuleLoad() {
        LOG.log(Level.INFO, "onModuleLoad...");

        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
            @Override
            public void onUncaughtException(Throwable e) {
                LOG.log(Level.SEVERE, "UncaughtException:", e);
            }
        });

        //create Constants 
//        Dwo2Exceptions exceptions = (Dwo2Exceptions) GWT.create(Dwo2Exceptions.class);
//        LOG.log(Level.INFO,exceptions.Dwo2ExceptionCode_GUI_NoUserIsSignedIn());
        RootPanel.get()
                .add(header);

        header.setCenter(
                "Account");

        RootPanel.get().add(loginStatusPanel);
        loginPanel = new LoginPanel();
        loginPanel.setUserCode("gert_project");
        loginPanel.setPassWord("passw");
        loginButton = new Button();
        loginButton.setText("login");
        loginButton.addClickHandler(this);
        loginPanel.setVisible(true);
        VerticalPanel contentPanel = new VerticalPanel();
        contentPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        contentPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        contentPanel.add(loginStatusPanel);
        contentPanel.add(loginPanel);
        contentPanel.add(loginButton);
        RootPanel.get().add(contentPanel);

    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == loginButton) {
//            DomUserFull curUser;// = new DomUserFull();
//            curUser.setGivenName("Wim");
//            curUser.setInsertion("van");
//            curUser.setFamilyName("Velthoven");
//            curUser.setId(null);
//            curUser.setSingleSchool(false);
//            curUser.setPassword("passw"); //md5Hash = d79096188b670c2f81b7001f73801117
//            curUser.setUserName("project_wim");
//            user = curUser;
            //Try to loginUser and fetch the user
            LOG.log(Level.INFO, "filled in test user.");
            handler.loginUser(loginPanel.getUserCode(), loginPanel.getPassWord(), new AsyncCallback<DomUserFullwLoginContext>() {
                @Override
                public void onFailure(Throwable t) {
                    loginStatusPanel.setStatus("", false);
                    LOG.log(Level.INFO, "failure", t);
                    header.setRightWidget(null);
                }

                //Process login results
                @Override
                public void onSuccess(DomUserFullwLoginContext result) {
                    //TODO Wim wat te doen indien niet ingelogd als student?
                    loginPanel.setVisible(false);
                    loginButton.setVisible(false);
                    LOG.log(Level.INFO, "Fetched a test user with username:" + result.getDomUserFull().getUserName()+ ".");
                    user = ((DomUserFullwLoginContext) result).getDomUserFull();
                    loginStatusPanel.setStatus(user.getUserName(), true);
                    DwoGlobalVars.instance().setCurrentUser(user);
                    SecuredStudentSchoolClassManager manager = new SecuredStudentSchoolClassManager();
                    manager.getActiveSchoolClass(new AsyncCallback<DomSchoolClass>() {
                        @Override
                        public void onFailure(Throwable t) {
                        	if(t instanceof Dwo2RestException)
                        	{ 
                        		Dwo2ExceptionCode code = ((Dwo2RestException) t).getDwo2Code();
                        	    if (code == Dwo2ExceptionCode.Rest_Active_SchoolClass_Not_Set)
                        	    {
                        		   onSuccess(null);
                        		   return;
                        	    }
                        	}
                            LOG.log(Level.INFO, t.toString(), t);
                            header.setRightWidget(null);
                        }

                        //Process getActiveSchoolClass results (Only works for student?)
                        @Override
                        public void onSuccess(DomSchoolClass result) {
                            DwoGlobalVars.instance()
                                    .setCurrentSchoolClass(result);
                            if(result!=null){
                            loginStatusPanel.setSchoolClass(result.getSchoolClassName());
                            }else{
                                loginStatusPanel.setSchoolClass("");
                            }
                            header.setRightWidget(userBar);
                            LOG.log(Level.INFO,
                                    "DwoGlobalVars has user with username:" + DwoGlobalVars.instance().getCurrentUser().getDisplayName() + ".");
                        }
                    });
                }
            }, null);
        } else {
            LOG.log(Level.INFO, "Configured username for the UserBar is: " + user.getUserName() + ".");
        }
    }
}
