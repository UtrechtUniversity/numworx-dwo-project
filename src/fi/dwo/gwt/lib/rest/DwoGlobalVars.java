package fi.dwo.gwt.lib.rest;

import com.google.gwt.user.client.Window;
import static com.google.gwt.user.client.ui.RootPanel.get;
import fi.dwo.gwt.lib.rest.util.RestAuthenticator;
import fi.dwo.rest.DwoLocale;
import fi.dwo.rest.dom.entities.DomSchool;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.exceptions.Dwo2Exception;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private DomSchool nullSchool=null;
    private DomSchoolClass currentSchoolClass=null;

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
            LOG.log(Level.SEVERE, null, ex);
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

    /**
     * @return the nullSchool
     */
    public DomSchool getNullSchool() {
        return nullSchool;
    }

    /**
     * @param aNullSchool the nullSchool to set
     */
    public void setNullSchool(DomSchool aNullSchool) {
        nullSchool = aNullSchool;
    }
    private RestAuthenticator authenticator = new RestAuthenticator();

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
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        return instance;
    }

    //Runtime Variabes
    DomUserFull currentUser;

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
        setAuthenticator(new RestAuthenticator());
        DefaultFilterawareDispatcher.singleton().addFilter(this.getAuthenticator());
        //init basic stuff
        get nullschool here? After login?
        get currentschoolclass here?
//            restService = GWT.create(DWO2RestCaller.class);
        LOG.log(Level.INFO, "Done initObjects():");
    }

    private void initVars() throws Dwo2Exception {
        //TODO fill DwoSystemParameters and more into the instance.
    }

    public void setUser(DomUserFull user) {
        this.currentUser = user;
        getAuthenticator().setCredentials(user.getUserName(), user.getPassword());
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
    }

    /**
     * @return the authenticator
     */
    public RestAuthenticator getAuthenticator() {
        return authenticator;
    }

    /**
     * @param authenticator the authenticator to set
     */
    public void setAuthenticator(RestAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

}
