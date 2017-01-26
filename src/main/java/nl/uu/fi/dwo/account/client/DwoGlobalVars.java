package nl.uu.fi.dwo.account.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;

import com.google.gwt.user.client.Window;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserSchoolLoginManagerV2;

import fi.dwo.gwt.lib.rest.DwoConstants;
import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.util.RestAuthenticator;
import nl.uu.fi.dwo.rest.DwoLocale;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import org.osgi.util.promise.Promise;

/**
 * Stores global variables The class is state is initialized by calls in
 * different boot phases. Whenever a global state is changed it should be called
 * and have the state updated. The following states exist and occur in the 
 * following order Unintialized, Initializing, NotLoggedIn, LoggedIn, Closing. 
 * Additionally each state can transition to the Uninitialized.
 * 
 * 
 * 
 *
 * @author Gert van der Plas
 */
public class DwoGlobalVars {

    private static final Logger LOG = Logger.getLogger(DwoGlobalVars.class.getName());

    /**
     * @return the state
     */
    public DwoGlobalVarsState getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(DwoGlobalVarsState state) {
        this.state = state;
    }

    /**
     * DwoGlobalStates that define which functions can be called without problems.
     * 
     */
    
    public enum DwoGlobalVarsState {

        /**
         * Nothing is set.
         */
        Unintialized, 

        /**
         * DwoSystemParameters
         */
        Initializing, 

        /**
         *
         */
        NotLoggedIn, 

        /**
         *
         */
        LoggedIn, 

        /**
         *
         */
        Closing
    }
    
    private DwoGlobalVarsState state=DwoGlobalVarsState.Unintialized;
    private static volatile DwoGlobalVars instance;
    private DomUserFull currentUser;
    private DomLoginContext currentLoginContext;
    private DomSchoolsRolesAndClassesV2 schoolLogins;
   
    /**
     *
     * @return
     */
    public static DwoGlobalVars getInstance() {
        return instance;
    }

    /**
     *
     * @param instance
     */
    public static void setInstance(DwoGlobalVars instance) {
        DwoGlobalVars.instance = instance;
    }
    
    /**
     *
     * @return
     */
    public DomSchoolClass getCurrentSchoolClass() {
        return schoolLogins.getActiveSchoolRoleAndClass().getSchoolClass();
    }

    /**
     *
     * @param currentSchoolClass
     */
    public void setCurrentSchoolClass(DomSchoolClass currentSchoolClass) {
        schoolLogins.getActiveSchoolRoleAndClass().setSchoolClass(currentSchoolClass);
    }

    static {
        try {
            instance = new DwoGlobalVars();

        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            Window.alert("System error: app improperly configured.");
        }
    }

    /**
     * @return the dwoLocale
     */
    public static DwoLocale getDwoLocale() {
        return dwoLocale;
    }

    /**
     * @param aDwoLocale the dwoLocale to set
     */
    public static void setDwoLocale(DwoLocale aDwoLocale) {
        dwoLocale = aDwoLocale;
    }

    //properties
    private static String server;
    private static DwoLocale dwoLocale = new DwoLocale("nl-NL");

    /**
     * @return the instance
     */
    public static DwoGlobalVars instance() {
        if (instance == null) {
            try {
                instance = new DwoGlobalVars();
            } catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, "", ex);
            }
        }
        return instance;
    }

    /**
     *
     * @throws Dwo2Exception
     */
    public DwoGlobalVars() throws Dwo2Exception {
        //TODO define initialization stages: Unintialized, Initializing, NotLoggedIn, LoggedIn. Closing.
        initProperties();
        initObjects();
        initVars();
    }

    /**
     * boot phase one
     */
    private void initProperties() throws Dwo2Exception {
        LOG.log(Level.INFO, "Starting initProperties():");
        setServer(DwoConstants.constants.server());
        LOG.log(Level.INFO, "restserver=" + server + ".");
        LOG.log(Level.INFO, "Done initProperties():");
    }

    /**
     * boot phase two
     */
    private void initObjects() {
        LOG.log(Level.INFO, "Starting initObjects():");
        Defaults.setServiceRoot(this.getServer());
        Defaults.setDispatcher(DefaultFilterawareDispatcher.singleton());
        GwtRestVars.instance().setAuthenticator(RestAuthenticator.instance);
//            restService = GWT.create(DWO2RestCaller.class);
        LOG.log(Level.INFO, "Done initObjects():");
    }

    private void initVars() throws Dwo2Exception {
        //TODO fill DwoSystemParameters and more into the instance.
//            schoolLogins = SecureUserAccountLoginsManager.getSchoolLogins();
//            control.setActiveSchoolClass(sc, new AsyncCallback<Boolean>() {
//                                @Override
//                                public void onFailure(Throwable t) {
//                                    //fail and reset all the data.
//                                    Window.alert(t.getMessage());
//                                    //TODO Wim
//                                    //Window.alert("wim handles error here.");
//                                }
//
//                                @Override
//                                public void onSuccess(Boolean result) {
//                                    popup.hide();
//                                    resetLogin.execute();
//                                }
//                            });
    }
    
    public void initUser(DomUserFull user) throws Dwo2Exception {
        SecuredUserSchoolLoginManagerV2 accountManager = new SecuredUserSchoolLoginManagerV2();
        Promise<DomSchoolsRolesAndClassesV2>  logins = accountManager.getSchoolLogins();
        setCurrentUser(user);
        setSchoolLogins(logins.getValue());
    }
    
    public void initUser() throws Dwo2Exception {
        SecuredUserAccountManager userManager = new SecuredUserAccountManager();
        Promise<DomUserFull>  user = userManager.getAccountData();
        initUser(user.getValue());
    }    

    /**
     * @return the server
     */
    public String getServer() {
        return server;
    }

    /**
     * @param server the server to set
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * @return the currentUser
     */
    public DomUserFull getCurrentUser() {
        return currentUser;
    }

    /**
     * @param aCurUser the currentUser to set
     */
    public void setCurrentUser(DomUserFull aCurUser) {
        currentUser = aCurUser;
        this.currentUser = aCurUser;
        //notify the gwt-rest interface configuration
        GwtRestVars.getInstance().setCurrentUser(aCurUser);
        
    }

    /**
     * @return the currentLoginContext
     */
    public DomLoginContext getCurrentLoginContext() {
        return currentLoginContext;
    }

    /**
     * @param currentLoginContext the currentLoginContext to set
     */
    public void setCurrentLoginContext(DomLoginContext currentLoginContext) {
        this.currentLoginContext = currentLoginContext;
    }

    /**
     * @return the schoolLogins
     */
    public DomSchoolsRolesAndClassesV2 getSchoolLogins() {
        return schoolLogins;
    }

    /**
     * @param schoolLogins the schoolLogins to set
     */
    public void setSchoolLogins(DomSchoolsRolesAndClassesV2 schoolLogins) {
        this.schoolLogins = schoolLogins;
    }

    
}
