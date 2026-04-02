package fi.dwo.dwojapplet.domain;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import fi.beans.appletutil.AppletUtil;
import fi.beans.dwomaccess.Compressor;
import fi.beans.jxbchecker.JXBChecker;
import fi.beans.loader.Loader;
import fi.beans.mainframe.MainFrame;
import fi.beans.mainframe.JApplet;
import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JOptionPane;
import fi.beans.private_base64code.StringCodeObject;
import fi.beans.scorm.SCORM12APIInterface;
import fi.beans.scorm.SCORM2004APIInterface;
import fi.dwo.commons.exceptions.LoginException;
import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.exceptions.RegisterException;
import fi.dwo.commons.exceptions.SchoolException;
import fi.dwo.commons.exceptions.ScoException;
import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentApplet;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.BUILD;
import fi.dwo.dwojapplet.domain.utils.CheckEmail;
import fi.dwo.dwojapplet.gui.CenterSubPanel;
import fi.dwo.dwojapplet.gui.CourseManagementPanel;
import fi.dwo.dwojapplet.gui.GuiConstants;
import fi.dwo.dwojapplet.gui.GuiCreator;
import fi.dwo.dwojapplet.gui.IdleDetect;
import fi.dwo.dwojapplet.gui.IdleDetect.IdleEvent;
import fi.dwo.dwojapplet.gui.IdleDetect.IdleListener;
import fi.dwo.dwojapplet.gui.MainPanel;
import fi.dwo.dwojapplet.gui.ModuleTreePanel;
import fi.dwo.dwojapplet.gui.ScoPanel;
import fi.dwo.dwojapplet.gui.WelcomePanel;
import fi.dwo.dwojapplet.gui.action.Clipboard;
import fi.dwo.dwojapplet.gui.domainmodel.methods.MethodsProperties;
import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdrCache;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import fi.dwo.dwojapplet.persistence.StoreCreator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.cache.PublicProfileCache;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.AbstractScoContextManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.CourseManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.PublicProfileManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.PublicUserManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureDwoAdminSchoolManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherSchoolClassManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserAccountManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecuredTeacherResultsManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.DwoLocale;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextFull;
import nl.uu.fi.dwo.rest.dom.entities.DomScoData;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;
import nl.uu.fi.dwo.rest.dom.entities.util.ScoType;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

/**
 * This is the main DWO application. It can be started as an applet or as a
 * stand alone application.
 *
 */
public class DWO extends JApplet implements SCORM12APIInterface, SCORM2004APIInterface, IdleListener {

    public static final String DWO_ENV = "dwo_env";

	private static final Logger LOG = Logger.getLogger("fi.dwo");

    public static final String PROFILE_EXTENSION = "profileExtension";

    private Course currentCourse;

    // alleen nodig indien scoViewNr>0
    private Sco currentSco;

    private Course courseList[];

    private ResultsModule resultsModule;

    private Container panel;

    private volatile int nestedWait;

    private static DomDwoProfileFull dwoProfile;

    private static int dwoProfileID;
    private static String dwoProfileKey;

    private String userName;

    private String passWord, refreshToken;

    private int courseViewNr;

    private int scoViewNr;

    private Hashtable testViewKeys;

    private Properties schoolAccessKeys;

    private String languageOveride, extensionOverride;

    private String limitedSchoolAccessString;

    private String schoolAccessPropertiesString;

    FocusTraversalPolicy delegate;

    /**
     * Reads a logging properties file. It first tries to find an external one.
     * Otherwise it reads the internal one.
     */
    private static void ReadLoggingProperties() {
        // TODO set logging properties when run as an applet.
        try {
            FileInputStream file;
            // folder relative to the current directory
            String path = "./logging.properties";
            // file handle for main.properties
            file = new FileInputStream(path);

            LogManager.getLogManager().readConfiguration(file);
            LOG.log(Level.INFO, "Using external logging.properties file.");
        } catch (final Exception e) {
            Logger.getAnonymousLogger().log(Level.INFO,
                    "No logging.properties file found in current directory. Using default.");
            try {
                final InputStream inputStream2 = DWO.class.getResourceAsStream("/logging.properties");
                if (inputStream2 != null) LogManager.getLogManager().readConfiguration(inputStream2);
                Logger.getAnonymousLogger().log(Level.INFO, "logging.properties file read from property folder.");
            } catch (final IOException e2) {
                Logger.getAnonymousLogger().severe("Could not load internal logging.properties file.");
            } catch (final SecurityException e3) {
                Logger.getAnonymousLogger().severe("Could not load internal logging.properties file.");
                throw e3;
            }
        }
    }

    /**
     * Reads a config file if it exists when started as an application.
     *
     */
    private void ReadConfigProperties() throws MalformedURLException {
        //TODO set config properties when run as an applet.

        LOG.log(Level.INFO, "Checking for DWO.properties");
        Properties properties = new Properties();
        URL base = null;
        try {

            //folder relative to the current directory
            String path = "DWO.properties";

            //file handle for main.properties
            InputStream file;
            file = new URL(getDocumentBase(), path).openStream();
            base = getDocumentBase();
            // load the properties
            properties.load(file);
            LOG.log(Level.INFO, "Loaded external DWO.property file");

            // done with file
            file.close();

        } catch (Exception ex) {
            LOG.log(Level.FINE, "No external DWO.property file found");
            try {
                // try resource folder.
                String path = "/DWO.properties";

                // file handle for main.properties
                final InputStream inStream = DWO.class.getResourceAsStream("/DWO.properties");
                // load the properties
                properties.load(inStream);
                base = getCodeBase();
                LOG.log(Level.INFO, "Loaded internal DWO.property file");

                // done with file
                inStream.close();

            } catch (FileNotFoundException ex2) {
                LOG.log(Level.FINE, "No internal DWO.properties file found");
                throw new RuntimeException(ex2);
            } catch (IOException ex2) {
                LOG.log(Level.SEVERE, "IO error reading internal DWO.properties file.");
                throw new RuntimeException(ex2);
            }
        }
        //Code below should allow properties set in a jnlp file to overrule
        //internal and external property files.
//        <resources>
//        <j2se version="1.8+" href="http://java.sun.com/products/autodl/j2se" />
//          ...
//          ...
//          <property name="defaultUsername" value="jane"/>
//          </resources>
        
        
        String defaultUsernameProperty = (System.getProperty("defaultUsername") == null) 
                ? properties.getProperty("defaultUsername", getParameter("userName")) : System.getProperty("defaultUsername");
        DwoHelper.setDefaultUsername(defaultUsernameProperty);
        LOG.log(Level.INFO, "Property {0} is value: {1}", new Object[]{"defaultUsername",
            DwoHelper.getDefaultUsername()});
        String defaultPasswordProperty = (System.getProperty("defaultPassword") == null) 
                ? properties.getProperty("defaultPassword", getParameter("passWord")) : System.getProperty("defaultPassword");
        DwoHelper.setDefaultPassword(defaultPasswordProperty);
        LOG.log(Level.INFO, "Property {0} is value: {1}", new Object[]{"defaultPassword",
            DwoHelper.getDefaultPassword()});

        //assign properties to static value.
        String serverUrlPathProperty =  (System.getProperty("serverUrlPath") == null) 
                ? properties.getProperty("serverUrlPath", "./") : System.getProperty("serverUrlPath");
        DwoHelper.setServerUrlPath(new URL(base, serverUrlPathProperty));
        LOG.log(Level.INFO, "Property {0} is value: {1}", new Object[]{"serverUrlPath",
            DwoHelper.getServerUrlPath()});

        //if not set pick default path
        String resourceURLPathProperty = (System.getProperty("resourceUrlPath") == null) 
                ? properties.getProperty("resourceUrlPath", "resources/") : System.getProperty("resourceUrlPath");
        DwoHelper.setResourceUrlPath(new URL(base, resourceURLPathProperty));
        LOG.log(Level.INFO, "Property {0} is value: {1}", new Object[]{"resourceUrlLPath",
            DwoHelper.getResourceUrlPath()});

        //if not set pick default path
        String jarURLPathProperty = (System.getProperty("jarUrlPath") == null) 
                ? properties.getProperty("jarUrlPath", "jars") : System.getProperty("jarUrlPath");
        DwoHelper.setJarUrlPath(new URL(base, jarURLPathProperty));
        LOG.log(Level.INFO, "Property {0} is value: {1}", new Object[]{"jarUrlPath",
            DwoHelper.getJarUrlPath()});
        
        String appURLPathProperty = properties.getProperty("appUrlPath", "http://www.fisme.science.uu.nl/dwo/apps/");
        DwoHelper.setAppURLPath(new URL(base, appURLPathProperty));
        LOG.log(Level.INFO, "Property {0} is value: {1}", new Object[]{"appUrlPath",
            DwoHelper.getAppURLPath()});

        HttpAuthenticationType httpAuthentication = (System.getProperty("httpAuthentication") == null) 
                ? HttpAuthenticationType.valueOf(properties.getProperty("httpAuthentication", "DIGEST"))
                : HttpAuthenticationType.valueOf(System.getProperty("httpAuthentication"));
        DwoHelper.setHttpAuthentication(httpAuthentication);
        LOG.log(Level.INFO, "Property {0} is value: {1}",
                new Object[]{"httpAuthentication", DwoHelper.getHttpAuthentication()});

        String xmlrpc_debug = (System.getProperty("xmlrpc.debug") == null) 
                ? properties.getProperty("xmlrpc.debug", "false") : System.getProperty("xmlrpc.debug") ;
        
//        LOG.log(Level.INFO, "Property {0} is value: {1}", new Object[]{"xmlrpc.debug", xmlrpc_debug});
//        MySimpleXmlRpcClient.setDebug("true".equals(xmlrpc_debug));

        dwo_env = properties.getProperty(DWO_ENV, super.getParameter(DWO_ENV));
        if (dwo_env == null) dwo_env = "";
        LOG.log(Level.INFO, "Property {0} is value: {1}", new Object[]{DWO_ENV, dwo_env});

        extensionOverride = properties.getProperty(PROFILE_EXTENSION, extensionOverride);

        if(dwo_env.contains("test"))
            DwoHelper.setTest(true);
        Compressor.setSkip(false);
        if(dwo_env.contains("saml"))
        {	DwoHelper.SamlType type = DwoHelper.SamlType.SAML;
            if (dwo_env.contains("uu")) type = DwoHelper.SamlType.UU;
            DwoHelper.setSamlLogin(type);
        }
//        if (DwoHelper.isTest()) {
//          dwo_env = "test"; // legacy bij geodefiner/wiskopdr geen combinatie nog.
//        }
    }

    /**
     * Java 7 throws exceptions, catch them. Deze "catch" policy catch ze en
     * doet een default actie.
     *
     */
    final private FocusTraversalPolicy CATCH_POLICY = new FocusTraversalPolicy() {

        @Override
        public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
            try {
                return delegate.getComponentAfter(focusCycleRoot, aComponent);
            } catch (Exception e) {
                recover(e);
            }
            return getFirstComponent(focusCycleRoot); // don't crash
        }

        @Override
        public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
            try {
                return delegate.getComponentBefore(focusCycleRoot, aComponent);
            } catch (Exception e) {
                recover(e);
            }
            return getLastComponent(focusCycleRoot);
        }

        @Override
        public Component getDefaultComponent(Container focusCycleRoot) {
            try {
                return delegate.getDefaultComponent(focusCycleRoot);
            } catch (Exception e) {
                recover(e);
            }
            return null;
        }

        @Override
        public Component getFirstComponent(Container focusCycleRoot) {
            try {
                return delegate.getFirstComponent(focusCycleRoot);
            } catch (Exception e) {
                recover(e);
            }
            return null;
        }

        @Override
        public Component getInitialComponent(Window window) {
            try {
                return delegate.getInitialComponent(window);
            } catch (Exception e) {
                recover(e);
            }
            return super.getInitialComponent(window);
        }

        @Override
        public Component getLastComponent(Container focusCycleRoot) {
            try {
                return delegate.getLastComponent(focusCycleRoot);
            } catch (Exception e) {
                recover(e);
            }
            return null;
        }

        private void recover(Exception e) {
            System.err.println("recovered: " + e);
        }
    };

    private String logoutURL;

    /**
     * Creates a new DWO object.
     *
     */
    public DWO() {
        nestedWait = 0;
        dwoProfileID = -1;
        dwoProfileKey = "VO";
    }

    /**
     * Creates a new DWO object with an argument.
     *
     * <pre>
     * -s SERVLET
     * profileID
     * username
     * password
     * </pre>
     *
     * SERVLET requires the full URL path.
     *
     * @param args
     */
    public DWO(String[] args) {
        this();
        int o = 0;

        while (args != null && args.length > 1 + o
                && args[0].length() > 1
                && '-' == args[o].charAt(0)
                && "rlsxtbR".indexOf(args[0].charAt(1)) >= 0) {
            // allow definitie van Locale.
            if (args.length > 1 + o && "-l".equals(args[o])) {
                languageOveride = args[o + 1];
                o += 2;
            }
            if (args.length > o && "-t".equals(args[o])) {
                DwoHelper.setTest(true);
                o += 1; // no parameter
            }
            if (args.length > o && "-R".equals(args[o])) {
              RUNNER = true;
              o += 1;
            }
            if (args.length > 1 + o && "-x".equals(args[o])) {
                extensionOverride = args[o + 1];
                o += 2;
            }
        }
        if (args != null && args.length > o && args[o] != null) {
            try {
            	dwoProfileKey = args[o];
                dwoProfileID = Integer.parseInt(args[o]);
            } catch (NumberFormatException e) {
            }
        }
    }

    /**
     * Logs a user in into the system. The user will be remembered while the
     * user is logged in. setExtraRights
     * The "real" login was done by the LoginManager
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return If the user was successfully logged in it returns true. Otherwise
     * it returns false.
     * @throws fi.dwo.commons.exceptions.LoginException
     *
     */
    public boolean login(String username, String password) throws LoginException {
        idleOn();
    	setUserName(username);
    	setPassWord(password);
    	//MySimpleXmlRpcClient.AUTHORIZATION = StoredRestManager.getInstance().getBasicAuthString();
        return setExtraRights(DwoHelper.getCurrentFacadeUser());
    }

    private void setPassWord(String password2) {
		firePropertyChange("passWord", passWord, password2);
		passWord = password2;
		DwoHelper.setDefaultPassword(password2);
	}
    
    public void setRefreshToken(String token) {
    	firePropertyChange("refreshToken", refreshToken, token);
    	refreshToken = token;
    }
    
    public void clrPassword() {
    	firePropertyChange("passWord", passWord, "");
    	passWord = "";
    	setRefreshToken("");
    }

	private void setUserName(String username2) {
		firePropertyChange("userName", userName,username2);
		userName = username2;
		DwoHelper.setDefaultUsername(username2);
	}

	/**
     * Logs a user in into the system. The user will be remembered while the
     * user is logged in.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return If the user was successfully logged in it returns true. Otherwise
     * it returns false.
     * @throws fi.dwo.commons.exceptions.LoginException
     *
     */
    public boolean relogin() {
        idleOn();
        PersistenceFacade.instance().clearCurrentScoDataCache();
        PersistenceFacade.instance().clearCurrentCourseDataCache();
        MethodsProperties.unset();
        return setExtraRights(DwoHelper.getCurrentFacadeUser());
    }

    /**
     * @param currentUser
     * @return
     */
    // @SuppressWarnings("null")
    public boolean setExtraRights(final User currentUser) {
        DwoHelper.setAdminLoggedIn(currentUser instanceof Admin);
        DwoHelper.setScormExportLoggedIn(currentUser.hasRight(User.SCORM_EXPORT_RIGHT));
        DwoHelper.setAppletExportLoggedIn(currentUser.hasRight(User.APPLET_EXPORT_RIGHT));

        if (testViewKeys != null) {
            SchoolClass sc = currentUser.getInClass();
            if (sc == null) {
                JOptionPane.showMessageDialog(this, "leerling heeft geen klas");
                return false;
            }
            int classNumber = sc.getID();
            @SuppressWarnings("UnusedAssignment")
            String testNumberString = "0";
            if (testViewKeys.containsKey("" + classNumber)) {
                testNumberString = (String) testViewKeys.get("" + classNumber);
            } else {
                return false;
            }
            scoViewNr = Integer.parseInt(testNumberString);
            GuiConstants.fixIconizer(scoViewNr, courseViewNr);

        }

        if (schoolAccessKeys != null) {
            School s = currentUser.getSchool();
            if (s == null) {
                JOptionPane.showMessageDialog(this, "deze account is niet met een school verbonden");
                return false;
            }
            int schoolNumber = s.getSchoolID();
            String accessNumberString = schoolAccessKeys.getProperty(String.valueOf(schoolNumber));
            if (!"true".equals(accessNumberString)) {
                JOptionPane.showMessageDialog(this, "gebruikers van deze school hebben hier geen toegang");
                return false;
            }
        }
        return (currentUser != null);
    }

    /**
     * Login as guest. CurrentUser becomes an instance of class Guest.
     *
     * @throws fi.dwo.commons.exceptions.LoginException
     * @see Guest
     *
     * @return If the guest was successfully logged in it returns true.
     * Otherwise it returns false.
     *
     */
    public boolean login() throws LoginException {
        DwoHelper.setCurrentFacadeUser(Guest.instance());
        //MySimpleXmlRpcClient.AUTHORIZATION = null;
        /*
	 * Object[] args = new Object[5]; args[0] =
	 * "http://www.fi.uu.nl/wisweb/scorm/scos/nabouwenaanzichten/NabouwenAanzichten1.htm";
	 * args[1] = "name"; args[2] = "800"; args[3] = "600"; args[4] = "yes";
	 * String result = (String) window.call("NewWindow", args);
	 * 
	 * System.out.println("Aanroep NewWindow:"+ result);
         */
        return setExtraRights(getUser());
    }

    /**
     * Checks a string if he is empty or null.
     *
     * @param s The string to be check.
     * @return If the string is null or empty true is returned. Otherwise false
     * is returned.
     */
    private boolean isEmpty(String s) {
        return (s == null) || (s.equals(""));
    }

    /**
     * Register a user in the system.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @param rePassword The re-password for the user. It is used to check for a
     * typing error.
     * @param firstname The firstname of the user.
     * @param middlename The middlename of the user. <br>
     * e.g: <code>Van</code>
     * @param lastname The lastname (familyname) of the user.
     * @param email The e-mail address of the user.
     * @return If the user was successfully registered true is returned.
     * Otherwise false is returned.
     * @throws fi.dwo.commons.exceptions.RegisterException
     *
     */
//    public boolean register(String username, String password, String rePassword, String firstname, String middlename,
//            String lastname, String email) throws RegisterException {
//
//        String[] arguments = new String[2];
//        // checks:
//        // no spaces (trimmed)
//        // ascii only
//        // aselect: ....
//        if (isEmpty(username)) {
//            arguments[0] = TextMapper.getText(TextMapper.GUIR_USERNAME);
//            arguments[1] = TextMapper.getText(TextMapper.GUIR_REGISTERINFO);
//            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
//        } else if (!isValid(username)) {
//            arguments[0] = TextMapper.getText(TextMapper.GUIR_USERNAME);
//            arguments[1] = TextMapper.getText(TextMapper.GUIR_REGISTERINFO);
//            throw new RegisterException(RegisterException.RE_WRONG_FORMAT, arguments);
//        } else if (isEmpty(password)) {
//            arguments[0] = TextMapper.getText(TextMapper.GUIR_PASSWORD);
//            arguments[1] = TextMapper.getText(TextMapper.GUIR_REGISTERINFO);
//            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
//        } else if (isEmpty(firstname)) {
//            arguments[0] = TextMapper.getText(TextMapper.GUIR_FIRSTNAME);
//            arguments[1] = TextMapper.getText(TextMapper.GUIR_PERSONALINFO);
//            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
//        } else if (isEmpty(lastname)) {
//            arguments[0] = TextMapper.getText(TextMapper.GUIR_LASTNAME);
//            arguments[1] = TextMapper.getText(TextMapper.GUIR_PERSONALINFO);
//            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
//        } else if (isEmpty(email)) {
//            arguments[0] = TextMapper.getText(TextMapper.GUIR_EMAIL);
//            arguments[1] = TextMapper.getText(TextMapper.GUIR_PERSONALINFO);
//            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
//        } else if (!isValidEmail(email)) {
//            arguments[0] = TextMapper.getText(TextMapper.GUIR_EMAIL);
//            arguments[1] = TextMapper.getText(TextMapper.GUIR_PERSONALINFO);
//            throw new RegisterException(RegisterException.RE_WRONG_EMAILFORMAT, arguments);
//        }
//        if (!password.equals(rePassword)) {
//            throw new RegisterException(RegisterException.RE_WRONG_SECOND_PASSWORD);
//        } else {
//            return PersistenceFacade.instance().register(username, password, firstname, middlename, lastname, email);
//        }
//    }

    /**
     * Test of we een echt e-mail adres hebben. Test of het ASCII is, minus
     * spatie en del.
     *
     * @param email de email om te testen
     * @return true indien valide
     */
    private static boolean isValidEmail(String email) {
        char[] chars = email.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c <= 0x20 || c >= 0x7F) {
                return false;
            }
        }
        if (DwoHelper.isApplication()) {
            return new CheckEmail().check(email);
        }
        CheckEmail checkEmail;
        try {
            checkEmail = new CheckEmail(DwoHelper.getApplet().getCodeBase());
            return checkEmail.check(email);
        } catch (MalformedURLException ex) {
            return true;
        }
    }

//    // checks:
//    // no spaces (trimmed)
//    // ascii only
//    // aselect: ....
//    /**
//     * Test username op illegale characters. Alleen ASCII is toegestaan, echter
//     * geen =?* en geen geen ( ) , of \ spaties zijn wel toegestaan, maar niet
//     * aan begin of eind.
//     *
//     * @param username String
//     * @return true als username voldoet
//     * @see org.aselect.server.udb.jndi.JNDIConnector#getUserProfile(String)
//     */
//    public static boolean isValid(String username) {
//
//        if (!username.trim().equals(username)) {
//            return false;
//        }
//        char[] chars = username.toCharArray();
//        for (int i = 0; i < chars.length; i++) {
//            char c = chars[i];
//            if (c < 0x20 || c >= 0x7F // ascii, no space?, no delete?
//                    || c == '(' // aselect verbiedt =*?
//                    || c == ')' // maar ook , \ ( en ) mogen niet
//                    || c == '*' || c == '?' || c == '=' || c == '\\' || c == ',' || c == ';' // beter
//                    // van
//                    // niet
//                    // in
//                    // LDAP
//                    || c == '+' || c == '#' // nieuw, werkt niet in PHP
//                    ) {
//                return false;
//            }
//        }
//
//        for (String realm : realms) {
//            if (username.endsWith(realm)) {
//                return false;
//            }
//        }
//        return true;
//    }

    /**
     * Lijst met realms die niet in een te registreren username mogen voorkomen.
     * Zij komen wel in de lijst van users voor, maar dan alleen via
     * getInitialUser
     *
     * @see #getInitialUser()
     */
//    private static final String[] realms = {"@kennisnet.org", "@fi.uu.nl", "@w2k3.fi.uu.nl", "@soliscom.uu.nl"};

    private static final String LEARNER_ID = "cmi.learner_id";
    private static final String LEARNER_NAME = "cmi.learner_name";

    public static boolean SEQUENCE = true;
    public static boolean RUNNER; // running onder dworunner

//    /**
//     * Register a user in the system. Als links a user to a school.
//     *
//     * @param username The username of the user.
//     * @param password The password of the user.
//     * @param rePassword The re-password for the user. It is used to check for a
//     * typing error.
//     * @param firstname The firstname of the user.
//     * @param middlename The middlename of the user. <br>
//     * e.g: <code>Van</code>
//     * @param lastname The lastname (familyname) of the user.
//     * @param email The e-mail address of the user.
//     * @param schoolLogin The schoolloginname of the school of the user.
//     * @param group The group from the user.
//     * @param groupPassword The password corresponding with the specified group
//     * and the school.
//     * @return If the user was successfully registered true is returned.
//     * Otherwise false is returned.
//     * @throws fi.dwo.commons.exceptions.RegisterException
//     *
//     */
//    public boolean register(String username, String password, String rePassword, String firstname, String middlename,
//            String lastname, String email, String schoolLogin, Group group, String groupPassword)
//            throws RegisterException {
//
//        String[] arguments = new String[2];
//        // checks:
//        // no spaces (trimmed)
//        // ascii only
//        // aselect: ....
//        if (isEmpty(username)) {
//            arguments[0] = TextMapper.getText(TextMapper.GUIR_USERNAME);
//            arguments[1] = TextMapper.getText(TextMapper.GUIR_REGISTERINFO);
//            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
//        } else if (!isValid(username)) {
//            arguments[0] = TextMapper.getText(TextMapper.GUIR_USERNAME);
//            arguments[1] = TextMapper.getText(TextMapper.GUIR_REGISTERINFO);
//            throw new RegisterException(RegisterException.RE_WRONG_FORMAT, arguments);
//        } else if (isEmpty(password)) {
//            arguments[0] = TextMapper.getText(TextMapper.GUIR_PASSWORD);
//            arguments[1] = TextMapper.getText(TextMapper.GUIR_REGISTERINFO);
//            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
//        } else if (isEmpty(firstname)) {
//            arguments[0] = TextMapper.getText(TextMapper.GUIR_FIRSTNAME);
//            arguments[1] = TextMapper.getText(TextMapper.GUIR_PERSONALINFO);
//            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
//        } else if (isEmpty(lastname)) {
//            arguments[0] = TextMapper.getText(TextMapper.GUIR_LASTNAME);
//            arguments[1] = TextMapper.getText(TextMapper.GUIR_PERSONALINFO);
//            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
//        } else if (isEmpty(email)) {
//            arguments[0] = TextMapper.getText(TextMapper.GUIR_EMAIL);
//            arguments[1] = TextMapper.getText(TextMapper.GUIR_PERSONALINFO);
//            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
//        } else if (!isValidEmail(email)) {
//            arguments[0] = TextMapper.getText(TextMapper.GUIR_EMAIL);
//            arguments[1] = TextMapper.getText(TextMapper.GUIR_PERSONALINFO);
//            throw new RegisterException(RegisterException.RE_WRONG_EMAILFORMAT, arguments);
//        } else if (isEmpty(schoolLogin)) {
//            arguments[0] = TextMapper.getText(TextMapper.GUIR_SCHOOLLOGIN);
//            arguments[1] = TextMapper.getText(TextMapper.GUIR_SCHOOLINFO);
//            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
//        } else if (group == null) {
//            arguments[0] = TextMapper.getText(TextMapper.GUIR_SCHOOLGROUP);
//            arguments[1] = TextMapper.getText(TextMapper.GUIR_SCHOOLINFO);
//            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
//        } else if (isEmpty(groupPassword)) {
//            arguments[0] = TextMapper.getText(TextMapper.GUIR_SCHOOLPASSWORD);
//            arguments[1] = TextMapper.getText(TextMapper.GUIR_SCHOOLINFO);
//            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
//        }
//
//        if (!password.equals(rePassword)) {
//            throw new RegisterException(RegisterException.RE_WRONG_SECOND_PASSWORD);
//        } else {
//            return PersistenceFacade.instance().register(username, password, firstname, middlename, lastname, email,
//                    schoolLogin, group, groupPassword);
//        }
//    }

//    /**
//     * Returns all the available groups.
//     *
//     * @return An array of all the available groups.
//     *
//     */
//    public Group[] getGroups() {
//        try {
//            return PersistenceFacade.instance().getGroup();
//        } catch (PersistenceException e) {
//            JOptionPane.showMessageDialog(this, e.getMessage());
//            return null;
//        }
//    }

    /**
     * Returns the current user who is logged in. If the user is logged in as a
     * guest, Guest.instance is returned.
     *
     * @return the current user who is logged in. If the user is logged in as a
     * guest, Guest.instance is returned.
     *
     */
    public User getUser() {
        return DwoHelper.getCurrentFacadeUser();
    }

    /**
     * Returns all the courses available for the user. If some courses are
     * available for the users school, they are also returned.
     *
     * @return An sorted array of all the courses for the current user.
     *
     */
    public Course[] getCourses() {
        try {
            courseList = PersistenceFacade.instance().getCoursesJS(DwoHelper.getCurrentFacadeUser()); // was getCourses
            if(true)
            	return (courseList); // Sorted by server
            return ((courseList));
        } catch (PersistenceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return null;
        }
    }

    /**
     * Sorteer course op class (is nu school).
     *
     * @param courses
     * @param inclass
     * @return
     * @deprecated er is geen class meer, altijd school
     */
    public Course[] sequence(Course[] courses, SchoolClass inclass) {
        if (!SEQUENCE) {
            return courses;
        }
        return (courses);
    }

//    /**
//     * Returns all the courses available for the user. If some courses are
//     * available for the users school, they are also returned.
//     *
//     * @param schoolClass
//     * @deprecated not used?
//     * @return An array of all the courses for the current user.
//     *
//     */
//    private Course[] getCourses(SchoolClass schoolClass) {
//        try {
//            courseList = PersistenceFacade.instance().getCourses(schoolClass);
//            return selectDwoProfileCourses(courseList);
//        } catch (PersistenceException e) {
//            JOptionPane.showMessageDialog(this, e.getMessage());
//            return null;
//        }
//    }

    // courses no folders, no timelimits. profile restricted.
    public Course[] getSelectedCourses(SchoolClass schoolClass) {
        Course[] courses;
        courses = schoolClass.getSelectedSchoolCourses();
        return (courses);
    }

    public static DomDwoProfileFull getDwoProfile() {
        return dwoProfile;
    }
    
    public static int getDwoProfileID() {
    	return dwoProfileID;
    }
 
    /**
     * Log the user off of the system. Sets all the data to null.
     *
     */
    public void logoff() {
        idleOff();
        try {
            if(DwoHelper.getCurrentUser()!=null) SecureUserAccountManager.logoutUser(DwoHelper.getCurrentLoginContext());
        } catch (Dwo2Exception ex) {
            Logger.getLogger(DWO.class.getName()).log(Level.SEVERE, "", ex);
        }
        try {
            DwoHelper.setCurrentUser(null,null);
        } catch (Dwo2Exception ex) {
            Logger.getLogger(DWO.class.getName()).log(Level.SEVERE, "", ex);
        }
        currentCourse = null;
        courseList = null;
        resultsModule = null;
        // MapperCreator.instance(Applet.class).removeAllObjects();

        // TODO NOW do a clear cache function.
        MethodsProperties.unset();
        PersistenceFacade.instance().clearCurrentScoDataCache();
        PersistenceFacade.instance().clearCurrentCourseDataCache();
    }

    /**
     * Sets a course to the current course. Loads the course his sco's and
     * returns a panel representing the course.
     *
     * @param course The course to select.
     * @return A panel representing the course.
     *
     */
    public CenterSubPanel loadCourse(Course course) {
        currentCourse = course;
        return course.getCoursePanel();
    }

    /**
     * Sets a sco to the course current sco. Returns a panel with the
     * sco-applet.
     *
     * @param sco The sco to select.
     * @return A panel with the sco-applet.
     *
     */
    public CenterSubPanel loadSco(Sco sco) {
        if (currentCourse != null) {
            currentCourse.setCurrentSco(sco);
        }
        return sco.getScoPanel(this, DwoHelper.getCurrentFacadeUser(),null);
    }
    // TODO DONE MANY TO MANY : obsolete
    // /**
    // * Change the current user his account.
    // *
    // * @param password The current password of the user. It will be used to
    // * validate the current user.
    // * @param newPassword The new password of the user.
    // * @param reNewPassword The re-password for the user. It is used to check
    // * for a typing error.
    // * @param firstName The firstname of the user.
    // * @param middleName The middlename of the user. <br>
    // * e.g: <code>Van</code>
    // * @param lastName The lastname (familyname) of the user.
    // * @param email The e-mail address of the user.
    // * @param c The new SchoolClass of the user.
    // * @throws fi.dwo.commons.exceptions.RegisterException
    // *
    // */
    // @Override
    // public void changeAccount(String password, String newPassword,
    // String reNewPassword, String firstName, String middleName,
    // String lastName, String email)
    // throws RegisterException {
    //
    // validateAccount(password, firstName, lastName, email);
    //
    // if (!newPassword.equals(reNewPassword)) {
    // throw new RegisterException(RegisterException.RE_WRONG_SECOND_PASSWORD);
    // } else {
    // PersistenceFacade.instance().changeAccount(User.getCurrentFacadeUser(),
    // password, newPassword, firstName, middleName, lastName, email);
    // }
    //
    // }

//    /**
//     * Change the current user his account.
//     *
//     * @param password The current password of the user. It will be used to
//     * validate the current user.
//     * @param newPassword The new password of the user.
//     * @param reNewPassword The re-password for the user. It is used to check
//     * for a typing error.
//     * @param firstName The firstname of the user.
//     * @param middleName The middlename of the user. <br>
//     * e.g: <code>Van</code>
//     * @param lastName The lastname (familyname) of the user.
//     * @param email The e-mail address of the user.
//     * @param schoolLogin The schoolloginname of the school of the user.
//     * @param group The group from the user.
//     * @param groupPassword The password corresponding with the specified group
//     * and the school.
//     * @throws fi.dwo.commons.exceptions.RegisterException
//     *
//     */
//    public void changeAccount(String password, String newPassword, String reNewPassword, String firstName,
//            String middleName, String lastName, String email, String schoolLogin, Group group, String groupPassword)
//            throws RegisterException {
//
//        validateAccount(password, firstName, lastName, email);
//
//        String[] arguments = new String[2];
//        if (isEmpty(schoolLogin)) {
//            arguments[0] = TextMapper.getText(TextMapper.GUIP_SCHOOLLOGIN);
//            arguments[1] = TextMapper.getText(TextMapper.GUIP_SCHOOLINFO);
//            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
//        } else if (group == null) {
//            arguments[0] = TextMapper.getText(TextMapper.GUIP_SCHOOLGROUP);
//            arguments[1] = TextMapper.getText(TextMapper.GUIP_SCHOOLINFO);
//            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
//        } else if (isEmpty(groupPassword)) {
//            arguments[0] = TextMapper.getText(TextMapper.GUIP_SCHOOLPASSWORD);
//            arguments[1] = TextMapper.getText(TextMapper.GUIP_SCHOOLINFO);
//            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
//        }
//
//        if (!newPassword.equals(reNewPassword)) {
//            throw new RegisterException(RegisterException.RE_WRONG_SECOND_PASSWORD);
//        } else {
//            PersistenceFacade.instance().addToSchool(DwoHelper.getCurrentFacadeUser(), schoolLogin, group,
//                    groupPassword);
//            PersistenceFacade.instance().changeAccount(DwoHelper.getCurrentFacadeUser(), password, newPassword,
//                    firstName, middleName, lastName, email);
//        }
//
//    }

//    /**
//     * Change the current user his account.
//     *
//     * @param password The current password of the user. It will be used to
//     * validate the current user.
//     * @param newPassword The new password of the user.
//     * @param reNewPassword The re-password for the user. It is used to check
//     * for a typing error.
//     * @param firstName The firstname of the user.
//     * @param middleName The middlename of the user. <br>
//     * e.g: <code>Van</code>
//     * @param lastName The lastname (familyname) of the user.
//     * @param email The e-mail address of the user.
//     * @throws fi.dwo.commons.exceptions.RegisterException
//     *
//     */
//    public void changeAccount(String password, String newPassword, String reNewPassword, String firstName,
//            String middleName, String lastName, String email) throws RegisterException {
//
//        validateAccount(password, firstName, lastName, email);
//
//        if (!newPassword.equals(reNewPassword)) {
//            throw new RegisterException(RegisterException.RE_WRONG_SECOND_PASSWORD);
//        } else {
//            PersistenceFacade.instance().changeAccount(DwoHelper.getCurrentFacadeUser(), password, newPassword,
//                    firstName, middleName, lastName, email);
//        }
//
//    }

    /**
     * Common code voor changeAccount 1, 2 en 3.
     *
     * @param password
     * @param firstName
     * @param lastName
     * @param email
     * @throws RegisterException
     */
    private void validateAccount(String password, String firstName, String lastName, String email)
            throws RegisterException {
        String[] arguments = new String[2];
        if (isEmpty(password)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIP_OLD_PASSWORD);
            arguments[1] = TextMapper.getText(TextMapper.GUIP_REGISTERINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(firstName)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIP_FIRSTNAME);
            arguments[1] = TextMapper.getText(TextMapper.GUIP_REGISTERINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(lastName)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIP_LASTNAME);
            arguments[1] = TextMapper.getText(TextMapper.GUIP_PERSONALINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(email)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIP_EMAIL);
            arguments[1] = TextMapper.getText(TextMapper.GUIP_PERSONALINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (!isValidEmail(email)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIP_EMAIL);
            arguments[1] = TextMapper.getText(TextMapper.GUIP_PERSONALINFO);
            throw new RegisterException(RegisterException.RE_WRONG_EMAILFORMAT, arguments);
        }
    }

//    /**
//     * Adds a class to the school of the current user. The current user will
//     * also be the teacher. The operation is only carried out if the user is a
//     * teacher.
//     *
//     * @param className The name of the new class.
//     * @return boolean If the class is successfully inserted it returns true.
//     * Otherwise it returns false.
//     * @throws fi.dwo.commons.exceptions.ClassException
//     *
//     */
//    public boolean addClass(String className) throws ClassException {
//        if (DwoHelper.getCurrentFacadeUser() instanceof Teacher) {
//            SchoolClass sc = PersistenceFacade.instance().addClass((Teacher) DwoHelper.getCurrentFacadeUser(),
//                    className);
//            ((Teacher) DwoHelper.getCurrentFacadeUser()).addClass(sc);
//
//            if (DwoHelper.getCurrentFacadeUser().getSchool() != null) {
//                DwoHelper.getCurrentFacadeUser().getSchool().addClass(sc);
//            }
//        }
//        return false;
//    }

//    /**
//     * Deletes the current user.
//     *
//     */
//    public void deleteUser() {
//        try {
//            PersistenceFacade.instance().deleteUser(DwoHelper.getCurrentFacadeUser());
//        } catch (RegisterException e) {
//            JOptionPane.showMessageDialog(this, e.getMessage());
//        }
//    }

//    /**
//     * Deletes the specified class from the system.
//     *
//     * @param c The class to delete.
//     * @return boolean If the class was successfully deleted it returns true.
//     * Otherwise it returns false.
//     *
//     */
//    @Override
//    public boolean deleteClass(SchoolClass c) {
//        boolean returnvalue = false;
//        try {
//            if (!PersistenceFacade.instance().deleteClass(c, true)) {
//                if (JOptionPane.showConfirmDialog(this, TextMapper.getText(TextMapper.GUIC_CLASS_NOT_EMPTY) + "?",
//                        TextMapper.getText(TextMapper.GUIC_CLASS_NOT_EMPTY_TITLE),
//                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//                    returnvalue = PersistenceFacade.instance().deleteClass(c, false);
//                }
//            } else {
//                returnvalue = true;
//            }
//        } catch (ClassException e) {
//            JOptionPane.showMessageDialog(this, e.getMessage());
//        }
//
//        if (returnvalue) {
//            if (DwoHelper.getCurrentFacadeUser() instanceof Teacher) {
//                ((Teacher) DwoHelper.getCurrentFacadeUser()).deleteClass(c);
//            }
//            if (DwoHelper.getCurrentFacadeUser().getSchool() != null) {
//                DwoHelper.getCurrentFacadeUser().getSchool().deleteClass(c);
//            }
//        }
//
//        return returnvalue;
//    }

    /**
     * Returns the current resultsmodule.
     *
     * @return The current results module.
     *
     */
    public ResultsModule getResultsModule() {
        return getResultsModule(getCourses(), false);
    }

    /**
     * Returns the current resultsmodule with the selected courses.
     *
     * @param courses The courses default selected.
     * @return The current results module.
     *
     */
    public ResultsModule getResultsModule(Course[] courses) {
        return getResultsModule(courses, true);
    }

    /**
     * Returns the current resultsmodule with the results of the specified
     * class.
     *
     * @param schoolClass The SchoolClass to show the results from.
     * @return The current results module.
     *
     */
    public ResultsModule getResultsModule(SchoolClass schoolClass) {
      Course[] selection;
      if (resultsModule == null) {
         try {
            DomSchoolClass sc = new DomSchoolClass();
            sc.setSchoolClassName(schoolClass.getName());
            sc.setIconizer(schoolClass.hasIconizer());
            sc.setId(PersistentSchoolClass.buildPersistenceId(Long.valueOf(schoolClass.getID())));
long w = System.currentTimeMillis();
            DomCoursesOfSchoolClass4Teacher result = 
                SecureTeacherSchoolClassManager.getModules(sc, DWO.getDwoProfile());
w -= System.currentTimeMillis();
LOG.info("time selected courses " + (-w) + " ms");
            Map<PersistenceId, DomCourse> allcourses = insertCache(result.getCourses());
            List<DomCourse> course = result.getClassCourses().stream().parallel()
            .map(DomMapEntry::getValue)
            .filter(cc -> cc.getViewState() == ViewState.studentsAndTeachers)
            .map(cc -> allcourses.get(cc.getCourseId()))
            .filter(course1 -> !course1.getWithChildren().booleanValue())
            .collect(Collectors.toList());
            DomResultsPerTeacher results, source;
            source = new DomResultsPerTeacher();
            
            selection = PersistenceFacade.instance().toCourse(course);
            
            source.setCourses(course
              .stream()
              .map(t -> new DomMapEntry<>(t.getId(), t))
              .collect(Collectors.toList()
            ));
            source.setSchoolClasses(Collections.singletonList(new DomMapEntry<>(sc.getId(), sc)));
long t = System.currentTimeMillis();
            results = SecuredTeacherResultsManager.selectedTeacherResults(getDwoProfile(), source);
t -= System.currentTimeMillis();
LOG.info("time results = " + (-t) + " ms");
            return new ResultsModule(results, this, sc, selection);
        } catch (Dwo2Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
          return null;
        } catch (PersistenceException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
          return null;
        }

        } else {
          selection = resultsModule.getSelectedCourse();
        }

        resultsModule.reset();
        resultsModule.selectCourses(selection, false);
        resultsModule.zoomIn(schoolClass);
        return resultsModule;
    }

    private static Map<PersistenceId, DomCourse> insertCache(
        List<DomMapEntry<PersistenceId, DomCourse>> all) {
        Map<PersistenceId, DomCourse> allcourses = new HashMap<>();
        all.forEach(e -> allcourses.put(e.getKey(), e.getValue()));
      return allcourses;
    }

    /**
     * Returns the current resultsmodule with the selected courses.
     *
     * @param courses The courses default selected.
     * @return The current results module.
     *
     */
    public ResultsModule getResultsModule(Course[] courses, boolean showSco) {
        if (resultsModule == null) {
          try {
            DomResultsPerTeacher source = new DomResultsPerTeacher();
            source.setCourses(
              Arrays.asList(courses).stream().map((Course c) -> {               
                PersistenceId id = (PersistentCourse.buildPersistenceId(Long.valueOf(c.getID())));
                return new DomMapEntry<PersistenceId, DomCourse>(id, null);
              }).collect(Collectors.toList()));
              DomResultsPerTeacher results = SecuredTeacherResultsManager.selectedTeacherResults(getDwoProfile(), source);
              resultsModule = new ResultsModule(results, this);
          } catch (Dwo2Exception e) {
            LOG.log(Level.SEVERE, "get results module", e);
            return null;
          }

          
          
          
///            resultsModule = new ResultsModule(new Course[0], (Teacher) DwoHelper.getCurrentFacadeUser(), this);
        }

        resultsModule.reset();
        resultsModule.selectCourses(courses, false);
        if (showSco && (courses.length == 1)) {
            resultsModule.zoomIn(courses[0]);
        }
        return resultsModule;
    }

    /**
     * This methods preconfigures any statics in a class that needs to be set
     * before their static methods are accessed.
     */
    public void boot() {

    }

    private boolean isRunningJavaWebStart() {
        boolean hasJNLP = false;
        try {
            Class.forName("javax.jnlp.ServiceManager");
            hasJNLP = true;
        } catch (ClassNotFoundException ex) {
            hasJNLP = false;
        }
        return hasJNLP;
    }

    /**
     * First phase of the applet life-cycle, {@Link start} is called immediately
     * after it.
     */
    @Override
    public void init() {
        if (!DwoHelper.setApplet(this)) {
            return;
        }
// remove security manager completely obsolete
//        try {
//			System.setSecurityManager(null);
//		} catch (Exception e1) {
//			LOG.log(Level.WARNING, "running with securitymanager",e1);
//		}
        
        if (DwoHelper.isApplication() == false || isRunningJavaWebStart()) {
            Authenticator.setDefault(null);
        }
        try {
            ReadConfigProperties();
        } catch (MalformedURLException ex) {
            LOG.log(Level.SEVERE, "", ex);
        }
        Clipboard.initialize();
        // It we started from the command line then the
        // DwoHelper.getServletConnectString()
        // has been intialized. Otherwise we set it to the server where we
        // downloaded from
        if (DwoHelper.getServerUrlPath() == null) {
            URL url = DwoHelper.getApplet().getCodeBase();
            try {
                Loader.setPrefix(url.toString());
                // applet was loaded from the subdir jars
                DwoHelper.setServerUrlPath((new URL(url, ".."))); // denotes the
                // base
                // servlet url
            } catch (MalformedURLException ex) {
                LOG.log(Level.SEVERE, "", ex);
            }
        } else 
        	Loader.setPrefix(DwoHelper.getJarUrlPath().toExternalForm());

        // TODO make it configurable in the servlet via a attribute in the jsp
        // initialized via the tomcat context.xml
                String softwareVersion = BUILD.version;
                String svnRevision = BUILD.buildNumber;
                String buildTimeStamp = BUILD.timeStamp;
                LOG.log(Level.INFO, "Software version {0}, revision {1}, build timestamp {2}",
                        new Object[]{softwareVersion, svnRevision,buildTimeStamp});
        DwoHelper.setAu(new AppletUtil(this));
        delegate = getFocusTraversalPolicy();
        if (delegate != null) {
            setFocusTraversalPolicy(CATCH_POLICY);
        }
        WiskOpdrCache.init();

        // override van swing properties...
        // TODO dit ook testen in een applet omgeving!
        UIDefaults defaults;
        defaults = UIManager.getDefaults();
        defaults.addResourceBundle("fi/dwo/dwojapplet/gui/resources/swing");
        // standaard Tooltip geel
        UIManager.put("ToolTip.background", new ColorUIResource(255, 247, 200));

        {
            String mode = getParameter(Sco.LESSON_MODE);
            if (mode != null) {
                Sco.setDefaultLessonMode(mode);
            }
        }

        String lang = getParameter("language");
        logoutURL = getParameter("logoutURL");
        // System.out.println(logoutURL);
        if ((lang != null) && (!lang.equals(""))) {
            TextMapper.setLanguage(lang);
            fi.dwo.dwojapplet.parameters.system.TextMapper.setLanguage(lang);
        }

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        final boolean cookies = false;
        String cookiesString = getParameter("cookies");
        if (cookiesString != null && cookiesString.equals("true")) {
            //cookies = true;
        }

        boolean guestUser = false; // Wim: teruggezet
        String guestUserString = getParameter("guestUser");
        if (guestUserString != null && guestUserString.equals("true")) {
            guestUser = true;
        }

        String scoViewNrString = getParameter("scoViewNr");
        if (scoViewNrString != null && (!scoViewNrString.equals(""))) {
            try {
                scoViewNr = Integer.parseInt(scoViewNrString);
            } catch (Exception e) {
            }
        }
        // scoViewNr = 58010;
        String courseViewNrString = getParameter("courseViewNr");
        if (courseViewNrString != null && (!courseViewNrString.equals(""))) {
            try {
                courseViewNr = Integer.parseInt(courseViewNrString);
            } catch (Exception e) {
            }
        }
        // courseViewNr = 13916;
        boolean umpc = false;
        String umpcString = getParameter("umpc");
        if (umpcString != null && umpcString.equals("true")) {
            umpc = true;
        }
        DwoHelper.setUmpc(umpc);

        boolean testView = false;
        String testViewString = getParameter("testView");
        if (testViewString != null && testViewString.equals("true")) {
            testView = true;
        }

        if (testView) {
            String testViewPropertiesString = getParameter("testViewProperties");

            Properties testProperties;

            try {
                URL url = new URL(getDocumentBase(), testViewPropertiesString);
                InputStream in = url.openStream();
                testProperties = new Properties();
                testProperties.load(in);
                testViewKeys = new Hashtable();
                int number = Integer.parseInt(testProperties.getProperty("number"));
                for (int i = 1; i < number + 1; i++) {
                    String classNumber = testProperties.getProperty("class." + i);
                    String testNumber = testProperties.getProperty("test." + i);
                    testViewKeys.put(classNumber, testNumber);
                }
            } catch (Exception e) {
                testViewKeys = null;
                testView = false;
                LOG.log(Level.SEVERE, "", e);
            }
        }
        if (!DwoHelper.isApplication()) {
            dwoProfileID = 1;
            String dwoProfileString = getParameter("profile");
            if ((dwoProfileString != null) && (!dwoProfileString.equals(""))) {
                try {
                	dwoProfile = PublicProfileCache.get(dwoProfileString);
                	dwoProfileID = MySQLPersistenceId.getNativeId(dwoProfile).intValue();
                } catch (Exception e) {
                }
            }
        }
//        JVMChecker jvmChecker = new JVMChecker(this);
//        jvmChecker.check();

        JXBChecker jxbChecker = new JXBChecker(this);
        DwoHelper.noJXB = jxbChecker.check();
        
        
        try {
            if (dwoProfile==null)
            {
            	if (dwoProfileID > 0)
            		dwoProfile = PublicProfileCache.get(dwoProfileID);
            	else
            		dwoProfile = PublicProfileCache.get(dwoProfileKey);
            }
            dwoProfileID = MySQLPersistenceId.getNativeId(dwoProfile).intValue();
            dwoProfileKey = dwoProfile.getDwoProfileName();
            if (languageOveride == null) 
            	languageOveride = dwoProfile.getLanguage();
    		DwoHelper.setProfileRights(dwoProfile.getDwoProfileRights());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, TextMapper.getText(TextMapper.DLG_SERVER_OUT), e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }

        boolean limitedSchoolAccess = initLimitedProfile();

        GuiConstants.setDwoProfile(dwoProfile, getParameter(PROFILE_EXTENSION));
        ModuleTreePanel.initialize(dwoProfile);
        // Hier fixen we nog de iconizer
        GuiConstants.fixIconizer(scoViewNr, courseViewNr);

        initWaitLabel(); // wim: GuiConstants nu actief en correct!

        /*
	 * ToolTipManager ttm = new ToolTipManager(this); Wim: wordt niet meer
	 * gebruikt, alleen swing
         */
        GuiCreator gc = new GuiCreator(this);
        // this.setSize(GuiConstants.DWO_WIDTH, GuiConstants.DWO_HEIGHT);
        this.setBackground(GuiConstants.MAIN_BACKGROUND);

        if (userName == null) {
            String lclUserName = getParameter("userName");
            if ("".equals(lclUserName)) {
                lclUserName = null;
            } else if (lclUserName != null) {
                this.userName = lclUserName;
            }
        }
        if (passWord == null) {
            String lclPassword = getParameter("passWord");
            if ("".equals(lclPassword)) {
                lclPassword = null;
            } else if (lclPassword != null) {
                this.passWord = lclPassword;
            }
        }
        refreshToken = getParameter("refreshToken");
        if (refreshToken != null && ! refreshToken.isEmpty()) {
        	try {
        		GuiCreator.instance().loginWithRefreshToken(refreshToken);
        		return;
        	} catch (Exception exc) {
        		setRefreshToken("");
        		LOG.log(Level.WARNING, "login with refreshToken", exc);
        	}
        }

        if (userName != null && passWord != null) {
            try {
                GuiCreator.instance().login(userName, passWord);
                return;
            } catch (LoginException exc) {
                LOG.log(Level.SEVERE, "", exc);
                JOptionPane.showMessageDialog(this, exc.getMessage(), TextMapper.getText(TextMapper.GUIW_ERR_LOGIN),
                        JOptionPane.ERROR_MESSAGE);
            } catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, "", ex);
                JOptionPane.showMessageDialog(this, ex.getMessage(), TextMapper.getText(TextMapper.GUIW_ERR_LOGIN),
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (guestUser) {
            try {
                GuiCreator.instance().login();
                return;
            } catch (LoginException exc) {
                JOptionPane.showMessageDialog(this, exc.getMessage(), TextMapper.getText(TextMapper.GUIW_ERR_LOGIN),
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (cookies) {
            userName = null;
            passWord = null;
            if (userName != null && passWord != null) {
                try {
                    GuiCreator.instance().login(userName, passWord);
                    return;
                } catch (Exception ex) {
                }
            }
        }
        DwoHelper.setCurrentFacadeUser(null);

        if (dwo_env.contains("entree") && DwoHelper.isTest()) {
        	samlData = new HashMap<>();
        	samlData.put("IDP", "entree");
        	if (DwoHelper.hasProfileRight('H'))
            	samlData.put("IDP", "conext");       		
        	samlData.put("endpoint", "/dwo/oauth2/entree");
        }
        
        
        panel = gc.getWelcomePanel(testView || limitedSchoolAccess, samlData);
        panel.setVisible(false);
        panel.setSize(this.getSize());
        panel.setLocation(0, 0);
        setContentPane(panel);// , BorderLayout.CENTER);
        panel.setVisible(true);

    }

	public boolean initLimitedProfile() {
		boolean limitedSchoolAccess = false;
        if (!DwoHelper.isApplication()) {
            limitedSchoolAccessString = getParameter("limitedSchoolAccess");
        }
        if (limitedSchoolAccessString != null && limitedSchoolAccessString.equals("true")) {
            limitedSchoolAccess = true;
        } else {
        	limitedSchoolAccess = DwoHelper.hasProfileRight(DwoHelper.LIMITED); // Haal LIMITED op uit profiel
        }

        if (limitedSchoolAccess) {
        	// vaste string, as RESOURCE
        	schoolAccessPropertiesString = "resources/schools-" + dwoProfileID + ".properties"; 
        	
//            if (!DwoHelper.isApplication()) {
//                schoolAccessPropertiesString = getParameter("schoolAccessProperties"); // TODO wegwerken: database of resource
//            }
            
            Properties schoolAccessProperties;

            try {
//                URL url = new URL(getDocumentBase(), schoolAccessPropertiesString);
            	URL url = DwoHelper.getResourceUrlPath();
            	url = new URL(url, schoolAccessPropertiesString);
                InputStream in = url.openStream();
                schoolAccessProperties = new Properties();
                schoolAccessProperties.load(in);

                schoolAccessKeys = new Properties();
                int number = Integer.parseInt(schoolAccessProperties.getProperty("number"));
                for (int i = 1; i < number + 1; i++) {
                    String schoolNumber = schoolAccessProperties.getProperty("school." + i);
                    String access = schoolAccessProperties.getProperty("access." + i);
                    String rights = schoolAccessProperties.getProperty("rights." + i);
                    schoolAccessKeys.put(schoolNumber, access);
                    if (rights != null) {
                        schoolAccessKeys.put("rights." + schoolNumber, rights);
                    }
                }
            } catch (Exception e) {
                schoolAccessKeys = new Properties();
                //limitedSchoolAccess = false; // FIXME Security scan
                LOG.log(Level.SEVERE, "", e);
            }

        } else {
        	schoolAccessKeys = null; // everyone can log in.
        }
		return limitedSchoolAccess;
	}

    public void setWelcomePanel() {
        setPanel(GuiCreator.instance().getWelcomePanel(testViewKeys != null || schoolAccessKeys != null));
    }

    public void setWelcomePanel(String username) {
        WelcomePanel p = GuiCreator.instance().getWelcomePanel(testViewKeys != null || schoolAccessKeys != null);
        p.setUsername(username);
        setPanel(p);
    }

    public int getCourseViewNr() {
        return courseViewNr;
    }

    public int getScoViewNr() {
        return scoViewNr;
    }

//    /**
//     * Overides the Applet.paint method. Draws a wait string, and calls the
//     * super. If the mainpanel is made invisible, nothing is showed above the
//     * wait string, so the wait string is showed.
//     *
//     * @param g
//     */
//    public void paintx(Graphics g) {
//        g.setColor(getBackground());
//        g.fillRect(0, 0, getSize().width, getSize().height);
//        String text = waitText;
//        text += "...";
//        g.setFont(GuiConstants.HEADER_TEXT);
//        g.setColor(Color.black);
//        FontMetrics fm = this.getFontMetrics(GuiConstants.HEADER_TEXT);
//        int x = (getSize().width / 2) - (fm.stringWidth(text) / 2);
//        int y = (getSize().height / 2) - (fm.getHeight() / 2);
//        g.drawString(text, x, y);
//        super.paint(g);
//
//    }

    /**
     * Sets the current panel of the applet.
     *
     * @param p The panel to set.
     */
    public void setPanel(Container p) {
        if (panel != null) {
            panel.setVisible(false);
            this.remove(panel);
            // panel.setVisible(true);
        }
        this.panel = p;

        // FIXME dit moet beter, maar hoe?
        if (logoutURL != null && p instanceof MainPanel) {
            ((MainPanel) p).setLogoutURL(logoutURL);
        }

        panel.setVisible(false);
        setContentPane(panel);
        invalidate();
        panel.setVisible(true);
        panel.requestFocus();
        validate();
    }

    /**
     * Returns the LMS value for the specified sco and the specified user.
     *
     * @param sco The sco wherefrom the LMS value is asked.
     * @param user The user wherefrom the LMS value is asked.
     * @param iDataModelElement The parameter to ask for.
     * @return The value representing for the specified sco, user and parameter.
     */
    public String LMSGetValue(ScoBase sco, User user, SchoolClass cls, String iDataModelElement) {
        if (LEARNER_ID.equals(iDataModelElement)) {
            return getLearnerId(user, cls);
    }
        if (LEARNER_NAME.equals(iDataModelElement)) {
            if (user == null) {
                return Guest.instance().getUsername();
            }
            return user.getUniqueDisplayName();
        }

        if (iDataModelElement.equals(SCORM12APIInterface.USER_GROUP)) {
            if (DwoHelper.getCurrentFacadeUser() == null || DwoHelper.getCurrentFacadeUser() instanceof Guest) {
                return SCORM12APIInterface.UG_GUEST;
            } else if (DwoHelper.getCurrentFacadeUser() instanceof Teacher) {
                return SCORM12APIInterface.UG_TEACHER;
            } else {
                return SCORM12APIInterface.UG_STUDENT;
            }

        } else {
            try {
                return PersistenceFacade.instance().LMSGetValue(sco, user, cls, iDataModelElement);
            } catch (PersistenceException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
                return "";
            }
        }

    }

	protected String getLearnerId(User user, SchoolClass cls) {
		if (user == null) {
		    return Guest.instance().getUsername();
		}
		String value;
		if (cls != null)
		  value = String.format("2-%020d-%020d",user.getID(),cls.getID());
		else
		  value = String.format("1-%020d-%s", user.getID(), user.getSchoolGroupID().getIdString().substring(28));
		return value;
	}

    /**
     * Sets the LMS value for the specified sco for the current user.
     *
     * @param sco The sco wherefrom the LMS value is set.
     * @param user
     * @param iDataModelElement The dataModeElement to set.
     * @param iValue The new value for the dataModeElement.
     * @return String representing a boolean
     * <ul>
     * <li><code>true</code> result indicates that the LMSSetValue() was
     * successful</li>
     * <li><code>false</code> result indicates that the LMSSetValue() was
     * unsuccessful</li>
     * </ul>
     */
    public String LMSSetValue(ScoBase sco, User user, SchoolClass sc, String iDataModelElement, String iValue) {
        try {
            return PersistenceFacade.instance().LMSSetValue(sco, user, sc, iDataModelElement, iValue);
        } catch (PersistenceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return "false";
        }

    }

    /**
     * 2nd applet life cycle phase. Called automatically after the method
     * {@link #init}.
     */
    @Override
    public void start() {
        this.getRootPane().setDoubleBuffered(true);
        IdleDetect.instance.start();
    }

    /**
     * <p>
     * Third phase in an applet life-cycle. Is called when a user moves off the
     * page on which the applet resides. It can be called repeatedly in the same
     * applet.
     * </p>
     *
     * <p>
     * Closes...
     * </p>
     */
    @Override
    public void stop() {
        // TODO: Question to Wim - Why this?
        if (DwoHelper.getApplet() != this) {
            return;
        }
        IdleDetect.instance.stop();
        this.setWait();
        super.stop();
        if (currentCourse != null) {
            currentCourse.end();
        } else if (currentSco != null) {
            currentSco.end();
        }
        logoff();
        // TODO NOW
        StoreCreator.destroy();
        this.setReady();
    }

    /**
     * Fourth phase in an applet life-cycle. Is called when a user closes a
     * browser. Usually resources are released in phase {@Link stop}.
     *
     * <p>
     * Destroys the clipboard an clears the reference to this Applet in the
     * {@Link DwoHelper}.
     * </p>
     */
    @Override
    public void destroy() {
        Clipboard.destroy();
        DwoHelper.clrApplet(this);
    }

    public void setCurrentSco(Sco sco) {
        currentSco = sco;
    }

    static {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
    }

    /**
     * The main method of the class.
     *
     * @param args
     * @throws Exception
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    public static void main(String[] args) throws Exception {

        LOG.log(Level.INFO, "Starting the DWO as an application.");
        int width = GuiConstants.DWO_WIDTH;
        int height = GuiConstants.DWO_HEIGHT;
        RestAuthenticator.getInstance().setServerUrlPath(null); // the old default!

        DWO dwo = new DWO(args);
        // Configure the applet
        DWO.ReadLoggingProperties();
        //Put applet in a frame.
        MainFrame mf = new MainFrame( dwo, width, height) {

			@Override
			public URL getCodeBase() {
				return DwoHelper.getURL("");
			}

      @Override
      public URL getDocumentBase() {
        if (RUNNER) return getCodeBase();
        return super.getDocumentBase();
      }
			
        	
        };
        mf.setTitle("Numworx Author");
        mf.pack();
        // Start applet.
        mf.setVisible(true);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(GuiConstants.DWO_WIDTH, GuiConstants.DWO_HEIGHT);
    }

    /**
     * Shows a wait cursor and the default wait message to indicate that the
     * user must wait for a while.
     *
     */
    public void setWait() {
        setWait(TextMapper.dwo2Message().NUM_TBL_FETCHINGDATA()); // uit numteacher
    }

    private JLabel waitLabel = new JLabel("Even geduld");

    private void initWaitLabel() {
        waitLabel.setFont(new Font("SansSerif", Font.BOLD, 26));

        waitLabel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        waitLabel.setHorizontalAlignment(JLabel.CENTER);
        waitLabel.setVerticalAlignment(JLabel.CENTER);
        waitLabel.setBackground(Constants.COLOR14);// GuiConstants.MAIN_BACKGROUND);
        waitLabel.setForeground(Color.WHITE);
        waitLabel.setOpaque(true);
        waitLabel.setBorder(BorderFactory.createEmptyBorder(20, 130, 20, 130));
        // {
        // Image img;
        // img = DwoHelper.getImage(GuiConstants.RESOURCES +
        // GuiConstants.GUI_BGIMAGE_MENU);
        // Border border = new DWOBorder(img, GuiConstants.GUI_INSETS_MENU,
        // GuiConstants.GUI_9PATCH_MENU);
        // waitLabel.setBorder(border);
        // }

        waitLabel.setVisible(true);
        // Center....
        Box lclPanel = Box.createHorizontalBox();
        lclPanel.setOpaque(false);
        lclPanel.add(Box.createGlue());
        lclPanel.add(waitLabel);
        lclPanel.add(Box.createGlue());
        Box xbox = Box.createVerticalBox();
        xbox.add(Box.createGlue());
        xbox.add(lclPanel);
        xbox.add(Box.createGlue());
        setGlassPane(xbox);
        xbox.setVisible(false);
    }

    /**
     * Shows a wait cursor and the specified wait message to indicate that the
     * user must wait for a while.
     *
     * @param waitText
     */
    public synchronized void setWait(String waitText) {
        nestedWait++;
        if (nestedWait == 1) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            waitLabel.setText(" " + waitText + " ");
            getGlassPane().setVisible(true);
            validate();
            if ( waitLabel.getGraphics() != null) {
                waitLabel.paint(waitLabel.getGraphics());
            } else {
               this.repaint();
            }
        } else {
          LOG.fine("recurse " + waitText);
        }
    }

    /**
     * Hides the wait cursor and the message what was showed up with
     * <code>setWait()</code>
     *
     */
    public synchronized void setReady() {
        if (nestedWait == 1) {
            getGlassPane().setVisible(false);
            if (panel != null) {
                panel.requestFocus();
            }
            setCursor(Cursor.getDefaultCursor());
        } else { 
          LOG.fine("recurse ready");
        }
        if ( nestedWait > 0 ) nestedWait--;
        else LOG.severe("Too much ready");
    }

    /**
     * Clears all the information of the current user out of the memory, so no
     * caching problems can appear.
     *
     */
    public void clearCurrentUserData(int uid) {
        PersistenceFacade.instance().clearCurrentUserDataCache(uid);
        // DwoHelper.setCurrentFacadeUser(null);
        currentCourse = null;
        courseList = null;
        resultsModule = null;
    }

    /*
     * (non-Javadoc)
     * 
     */
    public Course addCourse(String name, String description, Course parent, boolean isMap, boolean notVisible, CourseManager manager) {
    	PersistentCourse pc = new PersistentCourse();
    	try {
// if Course extends persistentCourse
    		pc.setName(name);
    		pc.setWithChildren(Boolean.valueOf(isMap));
    		pc.setDescription(description);    		
    		pc.setDwoProfileID(Long.valueOf(getDwoProfileID()));
// defaults:
    		pc.setNotVisible(notVisible);
// special cases...
    		pc.setParentID(parent == null ? 0L : parent.getID()); // NPE?
    		pc.setSchoolID(parent == null ? 
    				MySQLPersistenceId.getNativeId(DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getSchool()) :
    				Long.valueOf(parent.getSchoolID()));
    		if(parent != null && parent.getChildren() != null)
    			pc.setSequencenr(Long.valueOf(parent.getChildren().length));
    		else 
    			pc.setSequencenr(0L); // TODO from root
    		
    		DomCourseFull edit = pc.buildDomCourseFull();
			edit = manager.add(edit);
// legacy
			Course c = PersistenceFacade.instance().toCourse(Collections.singletonList(edit))[0];
			return c;
    	} catch (Dwo2Exception e) {
          LOG.log(Level.SEVERE, "add course", e);
          GuiCreator.instance().ShowErrorDialog(this, e);
          return null;
    	} catch(Exception e) {
    		LOG.log(Level.SEVERE, "add course", e);
            JOptionPane.showMessageDialog(this, e.getMessage());
    		return null;
    	}
    }

    /*
     * (non-Javadoc)
     * 
     */
    public boolean updateCourse(Course course, CourseManager manager) {
    	PersistentCourse pc = new PersistentCourse();
    	try {
 // if Course extends persistentCourse
    		pc.setCourseID(Long.valueOf(course.getID()));
    		buildPersistentCourse(course, pc);
 // should work with DomCourseFull
    		DomCourseFull edit = pc.buildDomCourseFull();
    		if (edit.getImageData() != CourseManagementPanel.IMAGEURL && !"".equals(edit.getImage())) 
    		  edit.setImage(null);

    		edit = manager.update(edit);
    		return true;
    	} catch (Dwo2Exception e) {
    	  GuiCreator.instance().ShowErrorDialog(this, e);
    	  return false;
    	} catch(Exception e) {
    		LOG.log(Level.SEVERE, "update course", e);
            JOptionPane.showMessageDialog(this, e.getMessage());
    		return false;
    	}
    }

	private void buildPersistentCourse(Course course, PersistentCourse pc) {
		pc.setDescription(course.getDescription());
		pc.setName(course.getName());
		pc.setDwoProfileID(Long.valueOf(getDwoProfileID()));
		pc.setExport(course.getExport());
		pc.setImage(course.getImageUrl());
		pc.setImageData(course.getImageData());
		pc.setParentID(Long.valueOf(course.getParentID()));
		pc.setSchoolID(Long.valueOf(course.getSchoolID()));
		pc.setNotVisible(course.isNotVisible());
		if(course.sequencenr != null)
			pc.setSequencenr(Long.valueOf(course.sequencenr.longValue()));
		else 
			pc.setSequencenr(null);

		pc.setTreeIndex(null);
		pc.setWithChildren(null); // not updatable
	}
    
    
    /*
     * (non-Javadoc)
     * 
     */
    public Sco addSco(Course course, AppletConfig appletConfig, String name, String description, boolean showScore, boolean showDocent, byte[] imageData, AbstractScoContextManager manager) {
		try {
    		return addScoWithExceptions(course, appletConfig, name, description, showScore, showDocent, imageData, manager);
		} catch (Dwo2Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getLocalizedCodeExplanation(DwoHelper.getLocale()), null, JOptionPane.ERROR_MESSAGE);
            return null;
		} catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return null;
		}
    	
    }

	public Sco addScoWithExceptions(Course course, AppletConfig appletConfig, String name, String description,
			boolean showScore, boolean showDocent, byte[] imageData, AbstractScoContextManager manager) throws Dwo2Exception, PersistenceException {
		DomScoContextFull scoContext = new DomScoContextFull();
		DomScoData scoData = new DomScoData();
		scoContext.setImageData(imageData);
		scoContext.setScoName(name);
		scoContext.setDescription(description);
		scoContext.setShowScore(!showScore); // reverse logic hier, of in de server?
		scoContext.setShowDocent(showDocent);
		scoContext.setAppletId(PersistentApplet.buildPersistenceId((long)appletConfig.getAppletID()));
		scoContext.setCourseId(PersistentCourse.buildPersistenceId((long)course.getID()));
		scoContext.setSequencenr((long)course.getScoList().length+1); // vanaf 1, niet vanaf 0
		scoContext.setUrnId(appletConfig.getImageSource());
// scodata
		final String launchdata = appletConfig.getLaunchdata();
		Sco sco = new Sco();
		sco.setAppletID(appletConfig.getAppletID());
		sco.setLaunchdataString(launchdata);
		sco.loadApplet();
		Map<?, ?> m = sco.getLaunchdata();
		Object mode = m.get("mode");
		int value = mode == null ? 0 : Integer.parseInt(mode.toString());
		scoContext.setScoType(ScoType.values()[value]);
		extractStudentModel(scoContext, sco, m);
		scoData.setLaunchdata(launchdata);
		if (sco.hasFeature(Sco.JSON_OUT))
			scoData.setLaunchdatabytes(sco.getLaunchdataBytes());
		scoContext = manager.add(scoContext, scoData, getDwoProfile());
// legacy
		sco = PersistenceFacade.instance().toSco(scoContext); 
		return sco;
	}

	public void extractStudentModel(DomScoContextFull scoContext, Sco sco, Map<?, ?> m) {
		Object instellingenStr = m.get("instellingen");
		try {
			Map map = (Map) StringCodeObject.decodeStringToObject(instellingenStr.toString(), sco.getApplet().getClass().getClassLoader());
			String model = (String) map.get("studentModelId");
			if(model != null) {
				scoContext.setStudentModelContext(new PersistenceId(model));
			} else {
				scoContext.setStudentModelContext(null);
			}
		} catch(Exception e) {		
			scoContext.setStudentModelContext(null);
			LOG.log(Level.WARNING, "instellingen/model failed", e);
		};
	}

    /*
     * (non-Javadoc)
     * 
     */
    public boolean updateSco(Sco sco, AbstractScoContextManager manager) {
		DomScoContextFull scoContext = new DomScoContextFull();
		scoContext.setId(PersistentScoContext.buildPersistenceId((long)sco.getScoID()));
		DomScoData scoData = null;

		if (sco.getImageData() != null) {
    		scoContext.setImageData(sco.getImageData());
    	}
    	scoContext.setScoName(sco.getScoName());
    	scoContext.setShowScore(!sco.isShowScore()); // reverse logic
    	scoContext.setShowDocent(sco.getShowDocent());
    	scoContext.setDescription(sco.getDescription());
    	if (sco.isCourseChanged()) {
    		scoContext.setCourseId(PersistentCourse.buildPersistenceId((long)sco.getCourse().getID()));
    		scoContext.setSequencenr((long) sco.getSequencenr());
    	}
    	
    	
    	if (sco.isDataChanged()) {
   		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> m = sco.getLaunchdata();
			Object mode = m.get("mode");
			int value = mode == null ? 0 : Integer.parseInt(mode.toString());
// bepaal maxscore FIXME ook bij toevoegen!!!!
			// if maxscore == 0, value = ScoType.INFO.ordinal();
			Object n = m.get("aantalOpdrachten_1");
			int aantal = n == null ? 0 : Integer.parseInt(n.toString());
			int max = 0;
			for(int i = 1; i <= aantal; i++) {
			  n = m.get("opdracht_1_" + i);
			  n = StringCodeObject.decodeStringToObject(n.toString(), sco.getApplet().getClass().getClassLoader());
			  max += ((Map<Object,Number>) n).get("scoreMax").intValue();
			}
			if (max == 0) 
			  value = ScoType.INFO.ordinal();
			
			scoContext.setScoType(ScoType.values()[value]);
			scoData = new DomScoData();
			scoData.setLaunchdata(sco.getLaunchdataString());
			extractStudentModel(scoContext, sco, m);
	  		if (sco.hasFeature(Sco.JSON_OUT))
	  			scoData.setLaunchdatabytes(sco.getLaunchdataBytes());
			StoreCreator.instance().uncache(sco, true);
		} catch (Exception e) {
			LOG.log(Level.WARNING, "incompatibel", e);
		}
    	}
		try {
// TODO this needs refactoring
            PersistenceFacade.instance().clearObjectInScoCache(sco.getID()); // ons kent ons, zowel de oude als de nieuwe course cache moet worden gecleart
		    manager.update(scoContext, scoData, getDwoProfile());
			sco.setImageData(null);
			sco.setCourseChanged(false);
			if (sco.isDataChanged()) {
			  sco.setSaved();
	          sco.setDataChanged(false);
			}
		} catch (Dwo2Exception e) {
            GuiCreator.instance().ShowErrorDialog(this, e);
            return false;
		}
			return true;
     }

    /*
     * (non-Javadoc)
     * 
     */
  public boolean deleteCourse(Course course, CourseManager manager, boolean trash) {
    try {
      DomCourse c = new DomCourse();
      c.setId(PersistentCourse.buildPersistenceId(Long.valueOf(course.getID())));
      boolean r = 
          trash ?
              manager.trash(c, getDwoProfile()).booleanValue() :
          manager.remove(c, getDwoProfile()).booleanValue();
      if (r) {
        PersistenceFacade.instance().removeObjectCourse(course.getID());
      }

      return r;
      // return PersistenceFacade.instance().deleteCourse(course);
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, e.getMessage());
      return false;
    }
  }

    /*
     * (non-Javadoc)
     * 
     */
  public boolean deleteSco(Sco sco, AbstractScoContextManager manager, boolean trash) {
    try {
      DomScoContext scoContext = new DomScoContext();
      scoContext.setId(PersistentScoContext.buildPersistenceId(Long.valueOf(sco.getScoID())));
      boolean returnValue = 
          trash ?
          manager.trash(scoContext, getDwoProfile()) :
          manager.remove(scoContext, getDwoProfile());
      if (returnValue) {
        PersistenceFacade.instance().removeObjectSco(sco.getID());
       
        /*
         * Delete the sco in the course, and reset all the sequencenrs.
         * not if sco is in trash!
         */
        Sco[] scos = sco.getCourse().getScoList();
        if (sco.getSequencenr() <= scos.length)
        { Sco[] tmp = new Sco[scos.length - 1];
          int div = 0;
          for (int i = 0; i < scos.length; i++) {
            if (scos[i] != sco) {
              if (scos[i].getSequencenr() > sco.getSequencenr()) {
                scos[i].setSequencenr(scos[i].getSequencenr() - 1);
                scos[i].setCourseChanged(false);
              } 
              tmp[i + div] = scos[i];
            } else {
              div--;
            }
          }
          sco.getCourse().setScoList(tmp);
        }
      }
      return returnValue;
    } catch (Dwo2Exception e) {
      JOptionPane.showMessageDialog(this, e.getMessage());
      return false;
    }
  }

    /*
     * (non-Javadoc)
     * 
     */
    public AppletConfig[] getAppletConfig() {
        try {
            AppletConfig[] ac = PersistenceFacade.instance().getAppletConfig(getLocale());
            return ac;
        } catch (PersistenceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     */
    public ScoPanel previewSco(AppletConfig appletConfig) {
        Sco dummy = new Sco();
        dummy.setAppletID(appletConfig.getAppletID());
        dummy.setName(appletConfig.getName());
        dummy.setLaunchdata((Hashtable) new StringCodeObject(appletConfig.getLaunchdata()).toObject());
        dummy.setLessonMode(Sco.BROWSE);
        return dummy.getScoPanel(this, null,null);
    }

    /*
     * (non-Javadoc)
     * 
     */
    public ScoPanel previewSco(Sco sco) {
        sco.setLessonMode(Sco.BROWSE);
        return sco.getScoPanel(this, null,null);
    }

    @Override
    public String LMSInitialize(String iParam) {
        if (currentSco != null) {
            currentSco.LMSInitialize(iParam);
            return "true";
        } else if (currentCourse != null && currentCourse.getCurrentSco() != null) {
            currentCourse.getCurrentSco().LMSInitialize(iParam);
            return "true";
        } else {
            return "false";
        }
    }

    public String Initialize(String iParam) {
        return LMSInitialize(iParam);
    }

    @Override
    public String LMSFinish(String iParam) {
        if (currentSco != null) {
            return currentSco.LMSFinish(iParam);
        } else if (currentCourse != null && currentCourse.getCurrentSco() != null) {
            return currentCourse.getCurrentSco().LMSFinish(iParam);
        } else {
            return "false";
        }
    }

    public String Terminate(String iParam) {
        return LMSFinish(iParam);
    }

    @Override
    public String LMSGetValue(String iDataModelElement) {
        if (currentSco != null) {
            return currentSco.LMSGetValue(iDataModelElement);
        } else if (currentCourse != null && currentCourse.getCurrentSco() != null) {
            return currentCourse.getCurrentSco().LMSGetValue(iDataModelElement);
        } else {
            return "";
        }
    }

    public String GetValue(String iDataModelElement) {
        if (currentSco != null) {
            return currentSco.GetValue(iDataModelElement);
        } else if (currentCourse != null && currentCourse.getCurrentSco() != null) {
            return currentCourse.getCurrentSco().GetValue(iDataModelElement);
        } else {
            return "";
        }
    }

    @Override
    public String LMSSetValue(String iDataModelElement, String iValue) {
        if (currentSco != null) {
            return currentSco.LMSSetValue(iDataModelElement, iValue);
        } else if (currentCourse != null && currentCourse.getCurrentSco() != null) {
            return currentCourse.getCurrentSco().LMSSetValue(iDataModelElement, iValue);
        } else {
            return "false";
        }
    }

    public String SetValue(String iDataModelElement, String iValue) {
        if (currentSco != null) {
            return currentSco.SetValue(iDataModelElement, iValue);
        } else if (currentCourse != null && currentCourse.getCurrentSco() != null) {
            return currentCourse.getCurrentSco().SetValue(iDataModelElement, iValue);
        } else {
            return "false";
        }
    }

    @Override
    public String LMSCommit(String iParam) {
        if (currentSco != null) {
            return currentSco.LMSCommit(iParam);
        } else if (currentCourse != null && currentCourse.getCurrentSco() != null) {
            return currentCourse.getCurrentSco().LMSCommit(iParam);
        } else {
            return "false";
        }
    }

    public String Commit(String iParam) {
        return LMSCommit(iParam);
    }

    public String LMSCommit(ScoBase sco, String param) {
        try {
            return PersistenceFacade.instance().LMSCommit(sco, getUser(), param);
        } catch (PersistenceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return "false";
        }
    }

    @Override
    public String LMSGetLastError() {
        if (currentSco != null) {
            return currentSco.LMSGetLastError();
        } else if (currentCourse != null && currentCourse.getCurrentSco() != null) {
            return currentCourse.getCurrentSco().LMSGetLastError();
        } else {
            return "101";
        }
    }

    public String GetLastError() {
        return LMSGetLastError();
    }

    @Override
    public String LMSGetErrorString(String iErrorCode) {
        if (currentSco != null) {
            return currentSco.LMSGetErrorString(iErrorCode);
        } else if (currentCourse != null && currentCourse.getCurrentSco() != null) {
            return currentCourse.getCurrentSco().LMSGetErrorString(iErrorCode);
        } else {
            return null;
        }
    }

    public String GetErrorString(String iErrorCode) {
        return LMSGetErrorString(iErrorCode);
    }

    @Override
    public String LMSGetDiagnostic(String iErrorCode) {
        if (currentSco != null) {
            return currentSco.LMSGetDiagnostic(iErrorCode);
        } else if (currentCourse != null && currentCourse.getCurrentSco() != null) {
            return currentCourse.getCurrentSco().LMSGetDiagnostic(iErrorCode);
        } else {
            return null;
        }
    }

    public String GetDiagnostic(String iErrorCode) {
        return LMSGetDiagnostic(iErrorCode);
    }

    HashMap<String,String> samlData;

	private String dwo_env;

    public void loginViaSaml(Properties p) throws Exception {
//       String samlUserID = p.getProperty(DWO_SAML_USER_ID);
//       String samlOrgID = p.getProperty(DWO_SAML_ORGANIZATION_ID);
       String authToken = p.getProperty("dwoSAMLAuthToken");
//       LOG.log(Level.INFO,"Cookies: dwoSAMLUserID {0} dwoSAMLOrganizationID {1} dwoSAMLAuthToken {2}", new Object[]{samlUserID, samlOrgID, authToken});
		CookieHandler handler = CookieHandler.getDefault();
		  Map<String, List<String>> responseHeaders = new HashMap<>();
		  List<String> cookies = new ArrayList<>();
		  for(Map.Entry<Object, Object> entry: p.entrySet()) {
		    if (entry.getKey().toString().startsWith("dwo")) {
		      String value = entry.getValue().toString();
		      if (value.contains(":")) // need escape?
		        value = "\"" + value + "\""; // ons kent ons
		      cookies.add( ( entry.getKey() + "=" + value));
		    }
		  }
		  responseHeaders.put("Set-Cookie", cookies); // inject DWO cookies.
		  handler.put(DwoHelper.getServerUrlPath().toURI(), responseHeaders);
		
		  String token = p.getProperty("dme.oauth.code");
		  String clientId = p.getProperty("dme.oauth.client_id");
		  String verifier = p.getProperty("dme.oauth.code_verifier");
		  String redirectUri = p.getProperty("dme.oauth.redirect_uri");
		  GuiCreator.instance().loginWithToken(token, clientId, verifier, redirectUri);
    }
    
    
//    public void linkViaSAML() {
//        try {
//          SecureUserAccountManager.link_saml(samlData.get(DWO_SAML_USER_ID),
//                  samlData.get(DWO_SAML_ORGANIZATION_ID), (String) samlData.get("dwoSAMLAuthToken"));
//        } catch (Dwo2Exception e) {
//            LOG.log(Level.SEVERE, "linkViaSaml", e);
//            /// popup
//        }
//
//    }

//    /**
//     * Geef mij een gebruiker buitenom.
//     *
//     * @return a user.
//     */
//    private User getInitialUser() {
//        return null;
//    }

//    /**
//     * Zet een gebruiker in een klas.
//     *
//     * @param className naam van klas
//     * @param u de gebruiker
//     * @param school die van u
//     */
//    private void setInitialUserInClass(String className, User u, School school) {
//        SchoolClass schoolClass = u.getInClass();
//        if (className != null && (schoolClass == null || !schoolClass.getName().equals(className))) {
//            SchoolClass[] classes = school.getClassList();
//            for (SchoolClass classe : classes) {
//                if (className.equals(classe.getName())) {
//                    u.setInClass(classe);
//                    try {
//                        PersistenceFacade.instance().changeAccount(u, null, null, u.getFirstname(), u.getMiddleName(),
//                                u.getLastName(), u.getEmail());
//                        PersistenceFacade.instance().addStudentToClass(u.getInClass(), u.getID());
//                    } catch (PersistenceException e) {
//                        LOG.log(Level.SEVERE, "", e);
//                    } catch (RegisterException ex) {
//                        LOG.log(Level.SEVERE, "", ex);
//                    }
//                }
//            }
//        }
//    }

//    /**
//     * Converteer een role naar een Group. De namen van de group zijn niet
//     * gelijk aan die van de 'role' (entree.kennisnet.nl). Er moet daarom
//     * gemapped worden.
//     *
//     * @param role
//     * @return de Group die role representeerd.
//     */
//    private Group findGroup(String role) {
//        if (role == null) {
//            return null;
//        }
//        // TODO is deze mapping compleet?
//        // if (false) // docent en contactdocent
//        // {
//        // role = "TEACHER";
//        // }
//        // if (false) {
//        // role = "STUDENT";
//        // }
//        Group[] groups = getGroups();
//        for (Group group : groups) {
//            if (role.equalsIgnoreCase(group.getName())) {
//                return group;
//            }
//        }
//        return null;
//    }

    public boolean deleteSchool(School sc, SecureDwoAdminSchoolManager schoolManager) {
        try {
            DomSchool4DwoAdmin submit = new DomSchool4DwoAdmin();
            submit.setId(PersistentSchool.buildPersistenceId(Long.valueOf(sc.getSchoolID())));
            return schoolManager.removeSchool(submit);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return false;
        }
    }

    /**
     * Verwissel de sequencenrs van twee Sco's. De Sco's moeten tot dezelfde
     * Course behoren.
     *
     * @param sco1 Sco
     * @param sco2 Sco
     * @param manager 
     * @return boolean: succes of gefaald
     */
    public boolean swapSco(Sco sco1, Sco sco2, AbstractScoContextManager manager) {
        try {
            if (sco1.getCourse() != sco2.getCourse()) {
                throw new ScoException(ScoException.EX_DB);
            }
            int nr1 = (int) sco1.getSequencenr();
            int nr2 = (int) sco2.getSequencenr();
            sco1.setSequencenr(nr2);
            updateSco(sco1, manager);
            sco2.setSequencenr(nr1);
            updateSco(sco2, manager);
            Sco[] scos = sco1.getCourse().getScoList();
            scos[nr2 - 1] = sco1;
            scos[nr1 - 1] = sco2;
            return true;
        } catch (ScoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return false;
        }
    }

    public ResultsModuleIF getUserResultsModule(Course course) {
        if (DwoHelper.getCurrentFacadeUser() instanceof Guest) {
            return null;
        }
        return new UserResultsModule(course, this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.applet.Applet#getParameter(java.lang.String)
     */
    @Override
    public String getParameter(String name) {
        if ("language".equals(name) && languageOveride != null) {
            return languageOveride;
        }
        if (PROFILE_EXTENSION.equals(name) && extensionOverride != null) {
            return extensionOverride;
        }
        if(DWO_ENV.equals(name) && dwo_env != null)
        	return dwo_env;
        if ("learner_id".equals(name)) {
        	return getLearnerId(DwoHelper.getCurrentFacadeUser(), null); // no fake, always self
        }
        if ("oauth_token".equals(name)) {
        	return StoredRestManager.getInstance().getBasicAuthString();
        }
        if ("serverUrlPath".equals(name)) {
        	return StoredRestManager.getInstance().getAuthenticator().getServerUrlPath().toExternalForm();
        }
        if ("appUrlPath".equals(name)) {
        	return DwoHelper.getAppURLPath().toString();
        }
        return super.getParameter(name);
    }

    /**
     * Adds a school to the database.
     *
     * @param id
     * @param schoolName The name of the new school.
     * @param schoolLogin The configurePanelsForUser name of the new school.
     * @param schoolPasswdMap
     * @param date
     * @param aboType 
     * @param schoolManager 
     * @return
     * @pschool is successfully inserted it returns true. Otherwise it returns
     * false.
     * @throws fi.dwo.commons.exceptions.SchoolException
     *
     */
    public School addSchool(int id, String schoolName, String schoolLogin, SchoolPasswdMap schoolPasswdMap, Date date, AboType aboType, SecureDwoAdminSchoolManager schoolManager)
            throws SchoolException {
        
        DomSchoolFull school = new DomSchoolFull();
        school.setExpire(date);
        school.setSchoolLogin(schoolLogin);
        school.setSchoolName(schoolName);
        school.setAboType(aboType);
        ArrayList<DomMapEntry<RoleType,String>> passwords = new ArrayList<>();
        for(Map.Entry<String, String> entry: schoolPasswdMap.entrySet()) {
          RoleType role = RoleType.values()[Integer.parseInt(entry.getKey())];
          String pw = entry.getValue();
          passwords.add(new DomMapEntry<RoleType, String>(role, pw));
        }
        school.setPasswords(passwords);
        try {
          school = schoolManager.addSchool(school);
          School s = new School();
          s.setDomSchool(school); // DUMMY, null/nonnull
          return s;
        } catch (Dwo2Exception e) {
           int code = SchoolException.EX_UNKNOWN_ERROR;
           if (e.getDwo2Code() == Dwo2ExceptionCode.Rest_ObjectAlreadyExists)
        	   code = SchoolException.SE_SCHOOL_EXISTS;
          throw new SchoolException(code);
        }
     }

    /**
     * Edit a school to the database.
     *
     * @param schoolID The ID of the school.
     * @param schoolName The new name of the school.
     * @param schoolLogin The new configurePanelsForUser name of the school.
     * @param schoolPasswdMap
     * @param date
     * @param aboType 
     * @return school
     * @throws fi.dwo.commons.exceptions.SchoolException
     *
     */
    @SuppressWarnings("deprecation")
    public School editSchool(int schoolID, String schoolName, String schoolLogin, SchoolPasswdMap schoolPasswdMap,
            Date date, AboType aboType, SecureDwoAdminSchoolManager schoolManager) throws SchoolException {
        // String studentPassw = schoolPasswdMap.getPasswd(SchoolGroup.STUDENT);
        // String teacherPassw = schoolPasswdMap.getPasswd(SchoolGroup.TEACHER);
        // return PersistenceFacade.instance().editSchool(schoolID, schoolName,
        // schoolLogin, studentPassw, teacherPassw);
      DomSchoolFull school = new DomSchoolFull();
// safe the date in UTC:
      Date date0 = null;
      if(date != null)
        date0 = new Date(Date.UTC(date.getYear(), date.getMonth(), date.getDate(), 0, 0, 0));

      school.setExpire(date0);
      school.setSchoolLogin(schoolLogin);
      school.setSchoolName(schoolName);
      school.setAboType(aboType);
      ArrayList<DomMapEntry<RoleType,String>> passwords = new ArrayList<>();
      for(Map.Entry<String, String> entry: schoolPasswdMap.entrySet()) {
        RoleType role = RoleType.values()[Integer.parseInt(entry.getKey())];
        String pw = entry.getValue();
        passwords.add(new DomMapEntry<RoleType, String>(role, pw));
      }
      school.setPasswords(passwords);
      school.setId(PersistentSchool.buildPersistenceId(Long.valueOf(schoolID)));
      try {
        schoolManager.updateSchool(school);
        school.setExpire(date);
        School newSchool = PersistenceFacade.instance().toSchool(Collections.singleton(school))[0];
        newSchool.setExpire(date);
        newSchool.setName(schoolName);
        newSchool.setSchoolLogin(schoolLogin);
        newSchool.setAboType(aboType);
//        SchoolGroup[] pw = newSchool.getSchoolGroupList();
//        HashMap<Integer,String> hash = new HashMap<>();
//        passwords.stream().forEach(e -> hash.put(e.getKey().ordinal(), e.getValue()));
//        for(SchoolGroup p : pw) {
//          String value = hash.get(p.getGroupID());
//          if(value != null)
//            p.setPasswd(value);
//        }
        return newSchool;
      } catch (Dwo2Exception e) {
         throw new SchoolException(SchoolException.EX_UNKNOWN_ERROR);
      }
      //  return PersistenceFacade.instance().editSchool(schoolID, schoolName, schoolLogin, schoolPasswdMap, date);
    }

    @Deprecated /* use update Course */
    public boolean updateLogo(Course c, CourseManager manager) {
        return updateCourse(c,manager); // TODO optimize, zie code updateLogo(c)
        //return PersistenceFacade.instance().updateLogo(c);
    }

//    public String selectSco(String scoid) {
//        int id = Integer.parseInt(scoid); // In dwo-appengine id = scoid
//        try {
//            Sco sco = (Sco) PersistenceFacade.instance().getSco(id);
//            GuiCreator.instance().getMainPanel().getCenter().select(sco);
//            return "true";
//        } catch (Exception e) {
//            LOG.log(Level.SEVERE, "", e);
//            return "false";
//        }
//    }
//
//    private static final String SELECT = "select:";
//
//    public String interpret(String command) {
//        if (command.startsWith(SELECT)) {
//            return selectSco(command.substring(SELECT.length()));
//        }
//
//        return "false";
//    }

    public void switchProfile(int p, String lang) {
      try {
        String old = dwoProfile.getDwoProfileName();
        dwoProfile = PublicProfileCache.get(p);
        dwoProfileID = p;
        dwoProfileKey = dwoProfile.getDwoProfileName();
        firePropertyChange("profile", old, dwoProfile.getDwoProfileName());
        old = getLocale().getLanguage();
        Locale locale = Locale.forLanguageTag(lang);
        languageOveride = lang;
        DwoHelper.setLocale(new DwoLocale(lang));
        DwoHelper.setAu(new AppletUtil(this));
        setLocale(locale);
        JComponent.setDefaultLocale(locale);
        DwoHelper.setProfileRights(dwoProfile.getDwoProfileRights());
        GuiConstants.setDwoProfile(dwoProfile, getParameter(PROFILE_EXTENSION));
        TextMapper.setLanguage(lang);
        fi.dwo.dwojapplet.parameters.system.TextMapper.setLanguage(lang);
        firePropertyChange("language", old, lang);
        ModuleTreePanel.initialize(dwoProfile);
        initLimitedProfile();
      } catch(Exception oops) {
        LOG.log(Level.WARNING, "switch to " + p + " " + lang, oops);
      }
      
    }

    public void clearResultsModule() {
      resultsModule = null;
      PersistenceFacade.instance().setResultsModule(null);
      
    }

    void idleOn() {
      noAttn();
      IdleDetect.instance.addIdleListener(this);
    }
    void idleOff() {
      noAttn();
      IdleDetect.instance.removeIdleListener(this);
    }
     
    private void noAttn() {
      if (attnDialog != null) {
        attnDialog.dispose(); attnDialog = null;
      }
    }

    private JDialog attnDialog;    
    @Override
    public void onIdle(IdleEvent ev) {
      if (DwoHelper.isSamlLogin() && ev.isSlow()) { // HOTFIX
        slowIdleEvent();
        return;
      } else {
        if (ev.getCnt() >= 2 && attnDialog != null) {
          noAttn();
          stop();
          destroy();
          System.exit(2);         
        }
      }
      
    }

    private void slowIdleEvent() { 
      attnDialog = new JDialog((JFrame)null); attnDialog.setModal(true);
      attnDialog.setAlwaysOnTop(true);
      attnDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
      attnDialog.addWindowListener(new WindowAdapter() {

        public void windowClosing(WindowEvent e) {
          noAttn();
        }
        
      });
      Box pane = Box.createVerticalBox();
      attnDialog.setBackground(Constants.COLOR21);
      
      JLabel comp = new JLabel(TextMapper.dwo2Message().NUM_LBL_LOGGEDIN());
      comp.setOpaque(true);
      comp.setAlignmentX(CENTER_ALIGNMENT);
      comp.setFont(new Font("Ubuntu", Font.PLAIN, 24));
      comp.setHorizontalAlignment(JLabel.CENTER);
      comp.setBackground(GuiConstants.HEADER_COLOR);
      comp.setForeground(Constants.COLOR21);
      comp.setBorder(BorderFactory.createEmptyBorder(7,7,7,7));
      pane.add(comp);
      JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER,20,10));
      footer.setOpaque(true);
      footer.setBackground(Constants.COLOR21);
      JButton yes = new JButton(TextMapper.getText(TextMapper.BTN_OK));
      yes.setAlignmentX(CENTER_ALIGNMENT);
      yes.addActionListener(e -> {
        noAttn();
      });
      yes.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          noAttn();
        }});
      footer.add(yes);
      pane.add(footer);
      attnDialog.setContentPane(pane);
      attnDialog.pack();
      Dimension r = attnDialog.getPreferredSize();
      
      //attnDialog.setSize(300,200);
      Dimension d  = Toolkit.getDefaultToolkit().getScreenSize();
      attnDialog.setLocation(d.width/2-attnDialog.getWidth()/2, d.height/2-attnDialog.getHeight()/2);
      attnDialog.requestFocusInWindow();
      attnDialog.setVisible(true);
    }

}
