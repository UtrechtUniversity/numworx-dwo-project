package nl.uu.fi.dwo.account.client;

import java.util.logging.Logger;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentSchoolClassManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserSchoolLoginManagerV2;
import fi.dwo.gwt.lib.rest.css.DwoStyle;
import fi.dwo.gwt.lib.rest.ui.MsgDialogPresenter;
import fi.dwo.gwt.lib.rest.ui.MsgDialogView;
import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

import java.util.logging.Level;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;

/**
 *
 * @author plas0006
 */
public class Account implements EntryPoint, ClickHandler {

    private static final Logger LOG = Logger.getLogger(Account.class.getName());

    EventBus bus = new SimpleEventBus();
    
    private final Failure SHOW_FAILURE = new DialogFailure(bus);
    private DomUserFull user = null;
    private SecuredUserAccountManager handler = new SecuredUserAccountManager();
    private LoginStatusPanel loginStatusPanel = new LoginStatusPanel();
    private FlowPanel header = new FlowPanel();
    private UserBar userBar = new UserBar(bus);
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
                try {
					SHOW_FAILURE.fail(Promises.failed(e));
				} catch (Exception e1) {
					// should not happen!
				}
            }
        });

        MsgDialogPresenter mdp = new MsgDialogPresenter(bus);
        DwoStyle style = GWT.<AccountBundle>create(AccountBundle.class).style();
        style.ensureInjected();
        MsgDialogView mdv = new MsgDialogView(mdp, style);
        //create Constants 
//        Dwo2Exceptions exceptions = (Dwo2Exceptions) GWT.create(Dwo2Exceptions.class);
//        LOG.log(Level.INFO,exceptions.Dwo2ExceptionCode_GUI_NoUserIsSignedIn());
        RootPanel.get()
                .add(header);

        header.add(
                new InlineLabel("Account"));

        RootPanel.get().add(loginStatusPanel);
        loginPanel = new LoginPanel();
        loginPanel.setUserCode("project_wim");
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
            handler.login(loginPanel.getUserCode(), loginPanel.getPassWord())
            .then(
            		new Success<DomUserFullwLoginContext, DomSchoolsRolesAndClassesV2>() {

						@Override
						public Promise<DomSchoolsRolesAndClassesV2> call(Promise<DomUserFullwLoginContext> resolved)
								throws Exception {
							DomUserFullwLoginContext result = resolved.getValue();
		                    //TODO Wim wat te doen indien niet ingelogd als student?
		                    loginPanel.setVisible(false);
		                    loginButton.setVisible(false);
		                    LOG.log(Level.INFO, "Fetched a test user with username:" + result.getDomUserFull().getUserName() + ".");
		                    user =  result.getDomUserFull();
		                    loginStatusPanel.setStatus(user.getUserName(), true);
		                    userBar.setSingleSchool(user.getSingleSchool());
		                    DwoGlobalVars.instance().setCurrentLoginContext(result.getDomLoginContext());
		                    DwoGlobalVars.instance().setCurrentUser(user);
		                    SecuredUserSchoolLoginManagerV2 loginManager = new SecuredUserSchoolLoginManagerV2();
		                    return loginManager.getSchoolLogins();
						}}
            , SHOW_FAILURE)
            .then(
            		new Success<DomSchoolsRolesAndClassesV2, DomSchoolClass>() {

						@Override
						public Promise<DomSchoolClass> call(Promise<DomSchoolsRolesAndClassesV2> resolved)
								throws Exception {
							DomSchoolsRolesAndClassesV2 result = resolved.getValue();
							DwoGlobalVars.instance().setSchoolLogins(result);
							DwoGlobalVars.instance().setActiveSchoolRoleAndClass(result.getActiveSchoolRoleAndClass());
							header.add(userBar);
							LOG.log(Level.INFO,
                                  "DwoGlobalVars has user with username:" + DwoGlobalVars.instance().getCurrentUser().getDisplayName() + ".");
							String s = result.getActiveSchoolRoleAndClass().getRole().getRoleName();
							if(RoleType.STUDENT.name().equals(s))
							{	SecuredStudentSchoolClassManager manager = new SecuredStudentSchoolClassManager();
								return manager.getActiveSchoolClass(DwoGlobalVars.instance().getContext()); // or 
								//return Promises.resolved(result.getActiveSchoolRoleAndClass().getSchoolClass()); 
							} else
								return null; // no class
						}
					}
            		
            		)
            .recoverWith(new Function<Promise<?>, Promise<? extends DomSchoolClass>>() {
				
				@Override
				public Promise<? extends DomSchoolClass> apply(Promise<?> p) {
					Throwable t = p.getFailure();
                  if (t instanceof Dwo2Exception) {
                  	Dwo2ExceptionCode code = ((Dwo2Exception) t).getDwo2Code();
                  	if (code == Dwo2ExceptionCode.Rest_Active_SchoolClass_Not_Set) {
                      
                      	return Promises.resolved(null);
                  	}
                  }
				  return null;
				}
			}
            		)
            .then(new Success<DomSchoolClass, Void>() {

				@Override
				public Promise<Void> call(Promise<DomSchoolClass> resolved) throws Exception {
					DomSchoolClass result = resolved.getValue();
                  DwoGlobalVars.instance()
                  .setCurrentSchoolClass(result);
                  if (result != null) {
                	  loginStatusPanel.setSchoolClass(result.getSchoolClassName());
                  } else {
                	  loginStatusPanel.setSchoolClass("");
                  }
				  return null;
				}
			}, new Failure() {
				
				@Override
				public void fail(Promise<?> resolved) throws Exception {
				  Throwable t = resolved.getFailure();
                  loginStatusPanel.setStatus("", false);
                  LOG.log(Level.INFO, "failure", t);
				}
			})
            ;
            		
            		
            		
            		
            		
            		
//            		{
//                @Override
//                public void onFailure(Throwable t) {
//                    loginStatusPanel.setStatus("", false);
//                    LOG.log(Level.INFO, "failure", t);
//                    header.setRightWidget(null);
//                }
//
//                //Process login results
//                @Override
//                public void onSuccess(DomUserFullwLoginContext result) {
//                    //TODO Wim wat te doen indien niet ingelogd als student?
//                    loginPanel.setVisible(false);
//                    loginButton.setVisible(false);
//                    LOG.log(Level.INFO, "Fetched a test user with username:" + result.getDomUserFull().getUserName() + ".");
//                    user = ((DomUserFullwLoginContext) result).getDomUserFull();
//                    loginStatusPanel.setStatus(user.getUserName(), true);
//                    userBar.setSingleSchool(user.getSingleSchool());
//                    DwoGlobalVars.instance().setCurrentUser(user);
//                    SecuredStudentSchoolClassManager manager = new SecuredStudentSchoolClassManager();
//                    final SecuredUserSchoolLoginManagerV2 loginManager = new SecuredUserSchoolLoginManagerV2();
//
//                    manager.getActiveSchoolClass(new AsyncCallback<DomSchoolClass>() {
//                        @Override
//                        public void onFailure(Throwable t) {
//                            if (t instanceof Dwo2Exception) {
//                                Dwo2ExceptionCode code = ((Dwo2Exception) t).getDwo2Code();
//                                if (code == Dwo2ExceptionCode.Rest_Active_SchoolClass_Not_Set) {
//                                    onSuccess(null);
//                                    return;
//                                }
//                            }
//                            LOG.log(Level.INFO, t.toString(), t);
//                            header.setRightWidget(null);
//                        }
//
//                        //Process getActiveSchoolClass results (Only works for student?)
//                        @Override
//                        public void onSuccess(DomSchoolClass result) {
//                            DwoGlobalVars.instance()
//                                    .setCurrentSchoolClass(result);
//                            if (result != null) {
//                                loginStatusPanel.setSchoolClass(result.getSchoolClassName());
//                            } else {
//                                loginStatusPanel.setSchoolClass("");
//                            }
//                            //Fill DWOGlobalVars with DomSchoolsRolesAndClasses

//                            loginManager.getSchoolLoginsV2(new AsyncCallback<DomSchoolsRolesAndClassesV2>() {
//                                @Override
//                                public void onFailure(Throwable t) {
//                                    if (t instanceof Dwo2Exception) {
//                                        Dwo2ExceptionCode code = ((Dwo2Exception) t).getDwo2Code();
//                                    }
//                                    LOG.log(Level.INFO, t.toString(), t);
//                                    header.setRightWidget(null);
//                                }
//                                //Process getActiveSchoolClass results (Only works for student?)
//
//                                @Override
//                                public void onSuccess(DomSchoolsRolesAndClassesV2 result) {
//                                    DwoGlobalVars.instance().setSchoolLogins(result);
//                                    header.setRightWidget(userBar);
//                                    LOG.log(Level.INFO,
//                                            "DwoGlobalVars has user with username:" + DwoGlobalVars.instance().getCurrentUser().getDisplayName() + ".");
//                                }
//                            });
//
//                        }
//                    });
//                }
//            }, null);
        } else {
            LOG.log(Level.INFO, "Configured username for the UserBar is: " + user.getUserName() + ".");
        }
    }
}
