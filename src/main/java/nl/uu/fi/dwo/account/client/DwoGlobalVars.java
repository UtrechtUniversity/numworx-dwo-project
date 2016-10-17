package nl.uu.fi.dwo.account.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import fi.dwo.gwt.lib.rest.DwoConstants;
import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.util.RestAuthenticator;
import nl.uu.fi.dwo.rest.DwoLocale;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;

/**
 * Stores global variables The class is state is initialized by calls in
 * different boot phases. Whenever a global state is changed it should be called
 * and have the state updated.
 *
 * @author Gert van der Plas
 */
public class DwoGlobalVars {

    private static final Logger LOG = Logger.getLogger(DwoGlobalVars.class.getName());

    private static volatile DwoGlobalVars instance;
    private DomSchoolClass currentSchoolClass=null;
    private DomUserFull currentUser;
    private DomLoginContext currentLoginContext;
    private static DomSchoolsRolesAndClassesV2 schoolLogins;
   
    

    public static DwoGlobalVars getInstance() {
        return instance;
    }

    public static void setInstance(DwoGlobalVars instance) {
        DwoGlobalVars.instance = instance;
    }
    
    public DomSchoolClass getCurrentSchoolClass() {
        return currentSchoolClass;
    }

    public void setCurrentSchoolClass(DomSchoolClass currentSchoolClass) {
        this.currentSchoolClass = currentSchoolClass;
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

    public DwoGlobalVars() throws Dwo2Exception {
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
     * @param curUser the currentUser to set
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

    
}
