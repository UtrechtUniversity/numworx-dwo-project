package fi.dwo.dwojapplet.domain;

import fi.beans.appletutil.AppletUtil;
import fi.beans.base64code.StringCodeObject;
import fi.beans.jvmchecker.JVMChecker;
import fi.beans.mainframe.MainFrame;
import fi.beans.scorm.SCORM12APIInterface;
import fi.dwo.commons.exceptions.ClassException;
import fi.dwo.commons.exceptions.CourseException;
import fi.dwo.commons.exceptions.LoginException;
import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.exceptions.RegisterException;
import fi.dwo.commons.exceptions.SchoolException;
import fi.dwo.commons.exceptions.ScoException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.utils.CheckEmail;
import fi.dwo.dwojapplet.gui.CenterSubPanel;
import fi.dwo.dwojapplet.gui.GuiConstants;
import fi.dwo.dwojapplet.gui.GuiCreator;
import fi.dwo.dwojapplet.gui.MainPanel;
import fi.dwo.dwojapplet.gui.ModuleTreePanel;
import fi.dwo.dwojapplet.gui.ScoPanel;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import fi.dwo.dwojapplet.persistence.StoreCreator;
import fi.dwo.dwojapplet.system.Loader;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Window;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.ColorUIResource;

/**
 * This is the main applet class of the DWO.<br>
 * At the start, a WelcomePanel is showed.<br>
 *
 * @author M.J.B. Kupers
 *
 */
public class DWO extends JApplet implements SCORM12APIInterface, DwoIF {

    private static final Logger log = Logger.getLogger("fi.dwo");

    private static final String DWO_SAML_ORGANIZATION_ID = "dwoSAMLOrganizationID";

    private static final String DWO_SAML_USER_ID = "dwoSAMLUserID";

    private static final String PROFILE_EXTENSION = "profileExtension";

    private Course currentCourse;

    //alleen nodig indien scoViewNr>0
    private Sco currentSco;

    private Course courseList[];

    private ResultsModule resultsModule;

    private Container panel;

    private String waitText;

    private int nestedWait;

    private DwoProfile dwoProfile;

    private int dwoProfileID;

    private String userName;

    private String passWord;

    private int courseViewNr;

    private int scoViewNr;

    private Hashtable testViewKeys;

    private Properties schoolAccessKeys;

    private String languageOveride, extensionOverride;

    private String limitedSchoolAccessString;

    private String schoolAccessPropertiesString;

    FocusTraversalPolicy delegate;

    private static void ReadLoggingProperties() {
        try {
            FileInputStream file;
            //folder relative to the current directory
            String path = "./logging.properties";
            //file handle for main.properties
            file = new FileInputStream(path);

            LogManager.getLogManager().readConfiguration(file);
            log.log(Level.INFO, "Using external logging.properties file.");
        } catch (final Exception e) {
            Logger.getAnonymousLogger().log(Level.INFO, "No logging.properties file found in current directory. Using default.");
            try {
                final InputStream inputStream2 = DWO.class.getResourceAsStream("logging.properties");
                LogManager.getLogManager().readConfiguration(inputStream2);
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
     * Reads a config file if it exists.
     *
     */
    private static void ReadConfigProperties() {

        try {
            Properties properties = new Properties();

            FileInputStream file;

            //folder relative to the current directory
            String path = "./DWO.properties";

            //file handle for main.properties
            file = new FileInputStream(path);

            //load the properties
            properties.load(file);
            log.log(Level.INFO, "Loaded external DWO.property file");

            //done with file
            file.close();

            //assign properties to static value.

            String servletConnectStringProperty = properties.getProperty("servletConnectString");
            //if(servletConnectStringProperty==null){
               // servletConnectStringProperty="http://ws.fisme.science.uu.nl/";
            //}
            DwoHelper.setServletConnectString(servletConnectStringProperty);
            log.log(Level.FINE, "Property {0} is value: {1}", new Object[]{"setServletConnectString",
                DwoHelper.getServletConnectString()});

            String resourceURLPathStringProperty = properties.getProperty("resourceURLPath");
            DwoHelper.setGetResourceURLPathString(resourceURLPathStringProperty);
            log.log(Level.FINE, "Property {0} is value: {1}", new Object[]{"resourceURLPathStringProperty",
                DwoHelper.getGetResourceURLPathString()});
        } catch (FileNotFoundException ex) {
            log.log(Level.FINE, "No external resource connection defined");
        } catch (IOException ex) {
            log.log(Level.FINE, "IO error reading DWO.properties file.");
        }
    }

    /**
     * Java 7 throws exceptions, catch them. Deze "catch" policy catch ze en
     * doet een default actie.
     *
     */
    final private FocusTraversalPolicy CATCH_POLICY = new FocusTraversalPolicy() {

        @Override
        public Component getComponentAfter(Container focusCycleRoot,
                Component aComponent) {
            try {
                return delegate.getComponentAfter(focusCycleRoot, aComponent);
            } catch (Exception e) {
                recover(e);
            }
            return getFirstComponent(focusCycleRoot);  // don't crash
        }

        @Override
        public Component getComponentBefore(Container focusCycleRoot,
                Component aComponent) {
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
        dwoProfileID = 1;
    }

    /**
     * Creates a new DWO object with an argument.
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

        nestedWait = 0;
        dwoProfileID = 1;
        int o = 0;

        while (args != null && args.length > 1 + o
                && args[0].length() > 1
                && '-' == args[o].charAt(0)
                && "rlsxb".indexOf(args[0].charAt(1)) >= 0) {
            if ("-b".equals(args[o])) {
                Sco.setDefaultLessonMode(Sco.BROWSE);
                o += 1;
            }

            // allow update van SERVLET
            if ("-s".equals(args[o])) {
                DwoHelper.setServletConnectString(args[1 + o]);
                o += 2;
            }
            // initialize applicationBase
            if (args.length > 1 + o && "-r".equals(args[o])) {
                try {
                    DwoHelper.applicationBase = new URL(args[o + 1]);
                } catch (MalformedURLException e) {
                    System.err.println("-r option: " + e);
                }
                o += 2;
            }
            // allow definitie van Locale.
            if (args.length > 1 + o && "-l".equals(args[o])) {
                languageOveride = args[o + 1];
                o += 2;
            }
            if (args.length > 1 + o && "-x".equals(args[o])) {
                extensionOverride = args[o + 1];
                o += 2;
            }

        }
        if (args != null && args.length > o && args[o] != null) {
            try {
                dwoProfileID = Integer.parseInt(args[o]);
            } catch (NumberFormatException e) {
            }
            if (args.length > 2 + o && args[1 + o] != null && args[2 + o] != null) {
                limitedSchoolAccessString = args[1 + o];
                schoolAccessPropertiesString = args[2 + o];
                o += 2;
            }
            if (args.length > 2 + o && args[1 + o] != null && args[2 + o] != null) {
                userName = args[1 + o];
                passWord = args[2 + o];
                o += 2;
            }

        }

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
    @Override
    public boolean login(String username, String password)
            throws LoginException {
        if (password == null) {
            User.setCurrentUser(PersistenceFacade.instance().login(username));
        } else {
            User.setCurrentUser(PersistenceFacade.instance().login(username, password));
        }

        return setExtraRights(User.getCurrentUser());
    }

    /**
     * @param currentUser
     * @return
     */
    //@SuppressWarnings("null")
    private boolean setExtraRights(final User currentUser) {
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
    @Override
    public boolean login() throws LoginException {
        User.setCurrentUser(Guest.instance());

        /*Object[] args = new Object[5];
         args[0] = "http://www.fi.uu.nl/wisweb/scorm/scos/nabouwenaanzichten/NabouwenAanzichten1.htm";
         args[1] = "name";
         args[2] = "800";
         args[3] = "600";
         args[4] = "yes";
         String result = (String) window.call("NewWindow", args);
        
         System.out.println("Aanroep NewWindow:"+ result);*/
        return true;
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
    @Override
    public boolean register(String username, String password,
            String rePassword, String firstname, String middlename,
            String lastname, String email) throws RegisterException {

        String[] arguments = new String[2];
// checks:
// no spaces (trimmed)
// ascii only
// aselect: ....
        if (isEmpty(username)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_USERNAME);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_REGISTERINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (!isValid(username)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_USERNAME);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_REGISTERINFO);
            throw new RegisterException(RegisterException.RE_WRONG_FORMAT, arguments);
        } else if (isEmpty(password)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_PASSWORD);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_REGISTERINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(firstname)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_FIRSTNAME);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_PERSONALINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(lastname)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_LASTNAME);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_PERSONALINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(email)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_EMAIL);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_PERSONALINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (!isValidEmail(email)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_EMAIL);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_PERSONALINFO);
            throw new RegisterException(RegisterException.RE_WRONG_EMAILFORMAT, arguments);
        }
        if (!password.equals(rePassword)) {
            throw new RegisterException(RegisterException.RE_WRONG_SECOND_PASSWORD);
        } else {
            return PersistenceFacade.instance().register(username, password, firstname, middlename, lastname, email);
        }
    }

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

//  checks:
//  no spaces (trimmed)
//  ascii only
//  aselect: ....
    /**
     * Test username op illegale characters. Alleen ASCII is toegestaan, echter
     * geen =?* en geen geen ( ) , of \ spaties zijn wel toegestaan, maar niet
     * aan begin of eind.
     *
     * @param username String
     * @return true als username voldoet
     * @see org.aselect.server.udb.jndi.JNDIConnector#getUserProfile(String)
     */
    public static boolean isValid(String username) {

        if (!username.trim().equals(username)) {
            return false;
        }
        char[] chars = username.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c < 0x20 || c >= 0x7F // ascii, no space?, no delete?
                    || c == '(' // aselect verbiedt =*? 
                    || c == ')' // maar ook , \ ( en ) mogen niet
                    || c == '*'
                    || c == '?'
                    || c == '='
                    || c == '\\'
                    || c == ','
                    || c == ';' // beter van niet in LDAP
                    || c == '+'
                    || c == '#' // nieuw, werkt niet in PHP
                    ) {
                return false;
            }
        }

        for (String realm : realms) {
            if (username.endsWith(realm)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Lijst met realms die niet in een te registreren username mogen voorkomen.
     * Zij komen wel in de lijst van users voor, maar dan alleen via
     * getInitialUser
     *
     * @see #getInitialUser()
     */
    private static final String[] realms = {
        "@kennisnet.org",
        "@fi.uu.nl",
        "@w2k3.fi.uu.nl",
        "@soliscom.uu.nl"
    };

    private static final String LEARNER_ID = "cmi.learner_id";
    private static final String LEARNER_NAME = "cmi.learner_name";

    public static boolean SEQUENCE = true;

    /**
     * Register a user in the system. Als links a user to a school.
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
     * @param schoolLogin The schoolloginname of the school of the user.
     * @param group The group from the user.
     * @param groupPassword The password corresponding with the specified group
     * and the school.
     * @return If the user was successfully registered true is returned.
     * Otherwise false is returned.
     * @throws fi.dwo.commons.exceptions.RegisterException
     *
     */
    @Override
    public boolean register(String username, String password,
            String rePassword, String firstname, String middlename,
            String lastname, String email, String schoolLogin, Group group,
            String groupPassword) throws RegisterException {

        String[] arguments = new String[2];
//      checks:
//      no spaces (trimmed)
//      ascii only
//      aselect: ....
        if (isEmpty(username)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_USERNAME);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_REGISTERINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (!isValid(username)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_USERNAME);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_REGISTERINFO);
            throw new RegisterException(RegisterException.RE_WRONG_FORMAT, arguments);
        } else if (isEmpty(password)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_PASSWORD);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_REGISTERINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(firstname)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_FIRSTNAME);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_PERSONALINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(lastname)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_LASTNAME);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_PERSONALINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(email)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_EMAIL);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_PERSONALINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (!isValidEmail(email)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_EMAIL);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_PERSONALINFO);
            throw new RegisterException(RegisterException.RE_WRONG_EMAILFORMAT, arguments);
        } else if (isEmpty(schoolLogin)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_SCHOOLLOGIN);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_SCHOOLINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (group == null) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_SCHOOLGROUP);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_SCHOOLINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(groupPassword)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_SCHOOLPASSWORD);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_SCHOOLINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        }

        if (!password.equals(rePassword)) {
            throw new RegisterException(RegisterException.RE_WRONG_SECOND_PASSWORD);
        } else {
            return PersistenceFacade.instance().register(username, password, firstname, middlename, lastname, email, schoolLogin, group, groupPassword);
        }
    }

    /**
     * Returns all the available groups.
     *
     * @return An array of all the available groups.
     *
     */
    @Override
    public Group[] getGroups() {
        try {
            return (Group[]) PersistenceFacade.instance().get(Group.class);
        } catch (PersistenceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return null;
        }
    }

    /**
     * Returns the current user who is logged in. If the user is logged in as a
     * guest, Guest.instance is returned.
     *
     * @return the current user who is logged in. If the user is logged in as a
     * guest, Guest.instance is returned.
     *
     */
    @Override
    public User getUser() {
        return User.getCurrentUser();
    }

    private Course[] selectDwoProfileCourses(Course[] completeList) {
        if (completeList == null) {
            return null;
        }
        Vector v = new Vector();
        for (Course completeList1 : completeList) {
            if (completeList1.getDwoProfile() == dwoProfile.getID()) {
                v.addElement(completeList1);
            }
        }
        Course[] selectedCourses = new Course[v.size()];
        for (int i = 0; i < selectedCourses.length; i++) {
            selectedCourses[i] = (Course) v.elementAt(i);
        }
        return selectedCourses;
    }

    /**
     * Returns all the courses available for the user. If some courses are
     * available for the users school, they are also returned.
     *
     * @return An array of all the courses for the current user.
     *
     */
    @Override
    public Course[] getCourses() {
        try {
            courseList = PersistenceFacade.instance().getCourses(User.getCurrentUser());
            return PersistenceFacade.instance().sequence(selectDwoProfileCourses(courseList));
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
    @Override
    public Course[] sequence(
            Course[] courses, SchoolClass inclass) {
        if (!SEQUENCE) {
            return courses;
        }
        return PersistenceFacade.instance().sequence(courses);
    }

    /**
     * Returns all the courses available for the user. If some courses are
     * available for the users school, they are also returned.
     *
     * @param schoolClass
     * @deprecated not used?
     * @return An array of all the courses for the current user.
     *
     */
    private Course[] getCourses(SchoolClass schoolClass) {
        try {
            courseList = PersistenceFacade.instance().getCourses(schoolClass);
            return selectDwoProfileCourses(courseList);
        } catch (PersistenceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return null;
        }
    }

    // courses no folders, no timelimits. profile restricted.
    public Course[] getSelectedCourses(SchoolClass schoolClass) {
        Course[] courses;
        courses = schoolClass.getSelectedSchoolCourses();
        return selectDwoProfileCourses(courses);
    }

    @Override
    public DwoProfile getDwoProfile() {
        return dwoProfile;
    }

    /**
     * Log the user off of the system. Sets all the data to null.
     *
     */
    @Override
    public void logoff() {
        User.setCurrentUser(null);
        currentCourse = null;
        courseList = null;
        resultsModule = null;
        // MapperCreator.instance(Applet.class).removeAllObjects();

        //TODO NOW do a clear cache function.
        PersistenceFacade.instance().clearCurrentMapperDataCache(Sco.class);
        PersistenceFacade.instance().clearCurrentMapperDataCache(Course.class);
    }

    /**
     * Sets a course to the current course. Loads the course his sco's and
     * returns a panel representing the course.
     *
     * @param course The course to select.
     * @return A panel representing the course.
     *
     */
    @Override
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
    @Override
    public CenterSubPanel loadSco(Sco sco) {
        if (currentCourse != null) {
            currentCourse.setCurrentSco(sco);
        }
        return sco.getScoPanel(this, User.getCurrentUser());
    }
//TODO MANY TO MANY DONE: obsolete
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
//     * @param c The new SchoolClass of the user.
//     * @throws fi.dwo.commons.exceptions.RegisterException
//     *
//     */
//    @Override
//    public void changeAccount(String password, String newPassword,
//            String reNewPassword, String firstName, String middleName,
//            String lastName, String email)
//            throws RegisterException {
//
//        validateAccount(password, firstName, lastName, email);
//
//        if (!newPassword.equals(reNewPassword)) {
//            throw new RegisterException(RegisterException.RE_WRONG_SECOND_PASSWORD);
//        } else {
//            PersistenceFacade.instance().changeAccount(User.getCurrentUser(), password, newPassword, firstName, middleName, lastName, email);
//        }
//
//    }

    /**
     * Change the current user his account.
     *
     * @param password The current password of the user. It will be used to
     * validate the current user.
     * @param newPassword The new password of the user.
     * @param reNewPassword The re-password for the user. It is used to check
     * for a typing error.
     * @param firstName The firstname of the user.
     * @param middleName The middlename of the user. <br>
     * e.g: <code>Van</code>
     * @param lastName The lastname (familyname) of the user.
     * @param email The e-mail address of the user.
     * @param schoolLogin The schoolloginname of the school of the user.
     * @param group The group from the user.
     * @param groupPassword The password corresponding with the specified group
     * and the school.
     * @throws fi.dwo.commons.exceptions.RegisterException
     *
     */
    @Override
    public void changeAccount(String password, String newPassword,
            String reNewPassword, String firstName, String middleName,
            String lastName, String email, String schoolLogin, Group group,
            String groupPassword) throws RegisterException {

        validateAccount(password, firstName, lastName, email);

        String[] arguments = new String[2];
        if (isEmpty(schoolLogin)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIP_SCHOOLLOGIN);
            arguments[1] = TextMapper.getText(TextMapper.GUIP_SCHOOLINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (group == null) {
            arguments[0] = TextMapper.getText(TextMapper.GUIP_SCHOOLGROUP);
            arguments[1] = TextMapper.getText(TextMapper.GUIP_SCHOOLINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(groupPassword)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIP_SCHOOLPASSWORD);
            arguments[1] = TextMapper.getText(TextMapper.GUIP_SCHOOLINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        }

        if (!newPassword.equals(reNewPassword)) {
            throw new RegisterException(RegisterException.RE_WRONG_SECOND_PASSWORD);
        } else {
            PersistenceFacade.instance().addToSchool(User.getCurrentUser(), schoolLogin, group, groupPassword);
            PersistenceFacade.instance().changeAccount(User.getCurrentUser(), password, newPassword, firstName, middleName, lastName, email);
        }

    }

    /**
     * Change the current user his account.
     *
     * @param password The current password of the user. It will be used to
     * validate the current user.
     * @param newPassword The new password of the user.
     * @param reNewPassword The re-password for the user. It is used to check
     * for a typing error.
     * @param firstName The firstname of the user.
     * @param middleName The middlename of the user. <br>
     * e.g: <code>Van</code>
     * @param lastName The lastname (familyname) of the user.
     * @param email The e-mail address of the user.
     * @throws fi.dwo.commons.exceptions.RegisterException
     *
     */
    @Override
    public void changeAccount(String password, String newPassword,
            String reNewPassword, String firstName, String middleName,
            String lastName, String email) throws RegisterException {

        validateAccount(password, firstName, lastName, email);

        if (!newPassword.equals(reNewPassword)) {
            throw new RegisterException(RegisterException.RE_WRONG_SECOND_PASSWORD);
        } else {
            PersistenceFacade.instance().changeAccount(User.getCurrentUser(), password, newPassword, firstName, middleName, lastName, email);
        }

    }

    /**
     * Common code voor changeAccount 1, 2 en 3.
     *
     * @param password
     * @param firstName
     * @param lastName
     * @param email
     * @throws RegisterException
     */
    private void validateAccount(String password, String firstName,
            String lastName, String email) throws RegisterException {
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

    /**
     * Adds a class to the school of the current user. The current user will
     * also be the teacher. The operation is only carried out if the user is a
     * teacher.
     *
     * @param className The name of the new class.
     * @return boolean If the class is successfully inserted it returns true.
     * Otherwise it returns false.
     * @throws fi.dwo.commons.exceptions.ClassException
     *
     */
    @Override
    public boolean addClass(String className) throws ClassException {
        if (User.getCurrentUser() instanceof Teacher) {
            SchoolClass sc = PersistenceFacade.instance().addClass((Teacher) User.getCurrentUser(), className);
            ((Teacher) User.getCurrentUser()).addClass(sc);

            if (User.getCurrentUser().getSchool() != null) {
                User.getCurrentUser().getSchool().addClass(sc);
            }
        }
        return false;
    }

    /**
     * Deletes the current user.
     *
     */
    @Override
    public void deleteUser() {
        try {
            PersistenceFacade.instance().deleteUser(User.getCurrentUser());
        } catch (RegisterException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    /**
     * Deletes the specified class from the system.
     *
     * @param c The class to delete.
     * @return boolean If the class was successfully deleted it returns true.
     * Otherwise it returns false.
     *
     */
    @Override
    public boolean deleteClass(SchoolClass c) {
        boolean returnvalue = false;
        try {
            if (!PersistenceFacade.instance().deleteClass(c, true)) {
                if (JOptionPane.showConfirmDialog(this, TextMapper.getText(TextMapper.GUIC_CLASS_NOT_EMPTY)
                        + "?", TextMapper.getText(TextMapper.GUIC_CLASS_NOT_EMPTY_TITLE), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    returnvalue = PersistenceFacade.instance().deleteClass(c, false);
                }
            } else {
                returnvalue = true;
            }
        } catch (ClassException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

        if (returnvalue) {
            if (User.getCurrentUser() instanceof Teacher) {
                ((Teacher) User.getCurrentUser()).deleteClass(c);
            }
            if (User.getCurrentUser().getSchool() != null) {
                User.getCurrentUser().getSchool().deleteClass(c);
            }
        }

        return returnvalue;
    }

    /**
     * Returns the current resultsmodule.
     *
     * @return The current results module.
     *
     */
    @Override
    public ResultsModuleIF getResultsModule() {
        return getResultsModule(getCourses(), false);
    }

    /**
     * Returns the current resultsmodule with the selected courses.
     *
     * @param courses The courses default selected.
     * @return The current results module.
     *
     */
    @Override
    public ResultsModuleIF getResultsModule(Course[] courses) {
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
    @Override
    public ResultsModuleIF getResultsModule(SchoolClass schoolClass) {
        if (resultsModule == null) {
            resultsModule = new ResultsModule(new Course[0], (Teacher) User.getCurrentUser(), this);
        }

        resultsModule.reset();
        resultsModule.selectCourses(getSelectedCourses(schoolClass), false);
        resultsModule.zoomIn(schoolClass);
        return resultsModule;
    }

    /**
     * Returns the current resultsmodule with the selected courses.
     *
     * @param courses The courses default selected.
     * @return The current results module.
     *
     */
    @Override
    public ResultsModuleIF getResultsModule(Course[] courses, boolean showSco) {
        if (resultsModule == null) {
            resultsModule = new ResultsModule(new Course[0], (Teacher) User.getCurrentUser(), this);
        }

        resultsModule.reset();
        resultsModule.selectCourses(courses, false);
        if (showSco && (courses.length == 1)) {
            resultsModule.zoomIn(courses[0]);
        }
        return resultsModule;
    }

    /**
     * Initializes the applet.
     */
    @Override
    public void init() {
// This order
        if (!DwoHelper.setApplet(this)) {
            return;
        }
        //It we started from the command line then the DwoHelper.getServletConnectString()
        //has been intialized. Otherwise we set it to the server where we downloaded from
        if(DwoHelper.getServletConnectString()==null){
            URL url = DwoHelper.getApplet().getCodeBase();
            try {
                Loader.setPrefix(url.toString());
                URL xmlRpcUrl = new URL(url, "../xmlrpc");
                DwoHelper.setServletConnectString(xmlRpcUrl.toString());
            } catch (MalformedURLException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
        //TODO make it configurable in the servlet via a attribute in the jsp
        //initialized via the tomcat context.xml
        
        //Read the versioning from the MANIFEST
        URLClassLoader cl = (URLClassLoader) DWO.class.getClassLoader();
        if (cl == null) {
            cl = (URLClassLoader) getClass().getClassLoader();
        }
        try {
            //TODO FIX Broken Manifest reading in Application mode and perhaps Applet mode.
            URL url = cl.findResource("META-INF/MANIFEST.MF");
            Manifest manifest = new Manifest(url.openStream());
            // code picks the manifest of the first jar loaded. 
            // It works only correct if started as stand-alone application. 
            // 
            String mainClass = manifest.getMainAttributes().getValue("Main-Class");
            if (mainClass != null && mainClass.matches("fi.dwo.dwojapplet.domain.DWO")) {
                String softwareVersion = manifest.getMainAttributes().getValue("DWOJApplet-Implementation-Version");
                String svnRevision = manifest.getMainAttributes().getValue("DWOJApplet-Implementation-Build");
                log.log(Level.INFO, "Software version {0},  subversion revision {1}", new Object[]{softwareVersion, svnRevision});
            } else {
                if (mainClass == null) {
                    mainClass = "";
                }
                log.log(Level.INFO, "No version numbering available. Possible running from IDE. Wrong 'Main-Class' value in MANIFEST-MF: '{0}'.", new Object[]{mainClass});
            }
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Can't open /META-INF/MANIFEST.MF", ex);
        }
        DwoHelper.setAu(new AppletUtil(this));
        delegate = getFocusTraversalPolicy();
        if (delegate != null) {
            setFocusTraversalPolicy(CATCH_POLICY);
        }

        // override van swing properties... 
        // TODO dit ook testen in een applet omgeving!
        UIDefaults defaults;
        defaults = UIManager.getDefaults();
        defaults.addResourceBundle("fi/dwo/client/gui/resources/swing");
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
        //System.out.println(logoutURL);
        if ((lang != null) && (!lang.equals(""))) {
            TextMapper.setLanguage(lang);
            fi.dwo.dwojapplet.parameters.system.TextMapper.setLanguage(lang);
        }

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        boolean cookies = false;
        String cookiesString = getParameter("cookies");
        if (cookiesString != null && cookiesString.equals("true")) {
            cookies = true;
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
//scoViewNr = 58010;       
        String courseViewNrString = getParameter("courseViewNr");
        if (courseViewNrString != null && (!courseViewNrString.equals(""))) {
            try {
                courseViewNr = Integer.parseInt(courseViewNrString);
            } catch (Exception e) {
            }
        }
//courseViewNr = 13916;
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
                log.log(Level.SEVERE, null, e);
            }
        }

        boolean limitedSchoolAccess = false;
        if (!DwoHelper.isApplication()) {
            limitedSchoolAccessString = getParameter("limitedSchoolAccess");
        }
        if (limitedSchoolAccessString != null && limitedSchoolAccessString.equals("true")) {
            limitedSchoolAccess = true;
        }

        if (limitedSchoolAccess) {
            if (!DwoHelper.isApplication()) {
                schoolAccessPropertiesString = getParameter("schoolAccessProperties");
            }

            Properties schoolAccessProperties;

            try {
                URL url = new URL(getDocumentBase(), schoolAccessPropertiesString);
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
                schoolAccessKeys = null;
                limitedSchoolAccess = false;
                log.log(Level.SEVERE, null, e);
            }

        }

        // deprecated
//        String key = getParameter("key");
//        if(key == null) {
//            key = "";
//        }
//        DwoHelper.setKey(key);
        if (!DwoHelper.isApplication()) {
            dwoProfileID = 1;
            String dwoProfileString = getParameter("profile");
            if ((dwoProfileString != null) && (!dwoProfileString.equals(""))) {
                try {
                    dwoProfileID = Integer.parseInt(dwoProfileString);
                } catch (Exception e) {
                }
            }
            JVMChecker jvmChecker = new JVMChecker(this);
            jvmChecker.check();
        }

        try {
            dwoProfile = (DwoProfile) PersistenceFacade.instance().get(dwoProfileID, DwoProfile.class);
        } catch (PersistenceException e) {
        }
        GuiConstants.setDwoProfile(dwoProfileID, getParameter(PROFILE_EXTENSION));
        ModuleTreePanel.initialize(dwoProfile);
// Hier fixen we nog de iconizer 
        GuiConstants.fixIconizer(scoViewNr, courseViewNr);

        initWaitLabel(); // wim: GuiConstants nu actief en correct!

        /* ToolTipManager ttm =  new ToolTipManager(this); Wim: wordt niet meer gebruikt, alleen swing */
        GuiCreator gc = new GuiCreator(this);
        try {
            PersistenceFacade.instance().reConnect();
        } catch (PersistenceException e) {
        }
        //this.setSize(GuiConstants.DWO_WIDTH, GuiConstants.DWO_HEIGHT);
        this.setBackground(GuiConstants.MAIN_BACKGROUND);

        //this.setLayout(new BorderLayout());
        //this.setLayout(null);
        if (userName == null) {
            String lclUserName = getParameter("userName");
            if (lclUserName != null && "".equals(lclUserName)) {
                lclUserName = null;
            } else if (lclUserName != null) {
                this.userName = lclUserName;
            }
        }
        if (passWord == null) {
            String lclPassword = getParameter("passWord");
            if (lclPassword != null && "".equals(lclPassword)) {
                lclPassword = null;
            } else if (lclPassword != null) {
                this.passWord = lclPassword;
            }
        }

        if (userName != null && passWord != null) {
            try {
                GuiCreator.instance().login(userName, passWord);
                return;
            } catch (LoginException exc) {
                JOptionPane.showMessageDialog(this, exc.getMessage(), TextMapper.getText(TextMapper.GUIW_ERR_LOGIN), JOptionPane.ERROR_MESSAGE);
            }
        } else if (guestUser) {
            try {
                GuiCreator.instance().login();
                return;
            } catch (LoginException exc) {
                JOptionPane.showMessageDialog(this, exc.getMessage(), TextMapper.getText(TextMapper.GUIW_ERR_LOGIN), JOptionPane.ERROR_MESSAGE);
            }
        } else if (cookies) {
            userName = DwoHelper.getCookie("dwoUserName");
            passWord = DwoHelper.getCookie("dwoPassWord");
            if (userName != null && passWord != null) {
                try {
                    GuiCreator.instance().login(userName, passWord);
                    return;
                } catch (Exception ex) {
                }
            }
        }
// Inloggen met ENTREE/OpenID (SAML)
        User.setCurrentUser(getSAMLUser());
        if (User.getCurrentUser() == null) // Hier wordt A-Select in DWO actief
        {
            User.setCurrentUser(getInitialUser());
        }
        if (User.getCurrentUser() != null) // Dit is de enige plaats waar op null
        // getest mag worden!
        {
            gc.login(User.getCurrentUser());
            return;
        }
// einde

        panel = gc.getWelcomePanel(testView || limitedSchoolAccess, samlData);
        panel.setVisible(false);
        panel.setSize(this.getSize());
        panel.setLocation(0, 0);
        setContentPane(panel);//, BorderLayout.CENTER);
        panel.setVisible(true);

    }

    @Override
    public void setWelcomePanel() {
        setPanel(GuiCreator.instance().getWelcomePanel(testViewKeys != null || schoolAccessKeys != null));
    }

    @Override
    public int getCourseViewNr() {
        return courseViewNr;
    }

    @Override
    public int getScoViewNr() {
        return scoViewNr;
    }

    /**
     * Overides the Applet.paint method. Draws a wait string, and calls the
     * super. If the mainpanel is made invisible, nothing is showed above the
     * wait string, so the wait string is showed.
     *
     * @param g
     */
    public void paintx(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getSize().width, getSize().height);
        String text = waitText;
        text += "...";
        g.setFont(GuiConstants.HEADER_TEXT);
        g.setColor(Color.black);
        FontMetrics fm = this.getFontMetrics(GuiConstants.HEADER_TEXT);
        int x = (getSize().width / 2) - (fm.stringWidth(text) / 2);
        int y = (getSize().height / 2) - (fm.getHeight() / 2);
        g.drawString(text, x, y);
        super.paint(g);

    }

    /**
     * Sets the current panel of the applet.
     *
     * @param p The panel to set.
     * @see fi.dwo.client.domain.DwoIF#setPanel(java.awt.Panel)
     */
    @Override
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
     * @param user The usere wherefrom the LMS value is asked.
     * @param iDataModelElement The parameter to ask for.
     * @return The value representing for the specified sco, user and parameter.
     */
    @Override
    public String LMSGetValue(ScoBase sco, User user, String iDataModelElement) {
        if (LEARNER_ID.equals(iDataModelElement)) {
            if (user == null) {
                return Guest.instance().getUsername();
            }
            return user.getUsername();
        }
        if (LEARNER_NAME.equals(iDataModelElement)) {
            if (user == null) {
                return Guest.instance().getStudentName();
            }
            return user.getStudentName();
        }

        if (iDataModelElement.equals(SCORM12APIInterface.USER_GROUP)) {
            if (User.getCurrentUser() == null || User.getCurrentUser() instanceof Guest) {
                return SCORM12APIInterface.UG_GUEST;
            } else if (User.getCurrentUser() instanceof Teacher) {
                return SCORM12APIInterface.UG_TEACHER;
            } else {
                return SCORM12APIInterface.UG_STUDENT;
            }

        } else {
            try {
                return PersistenceFacade.instance().LMSGetValue(sco, user, iDataModelElement);
            } catch (PersistenceException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
                return "";
            }
        }

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
    @Override
    public String LMSSetValue(ScoBase sco, User user, String iDataModelElement, String iValue) {
        try {
            return PersistenceFacade.instance().LMSSetValue(sco, user, iDataModelElement, iValue);
        } catch (PersistenceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return "false";
        }

    }

//    /**
//     * Indicate that a sco has been ended, so the data can be cleared.
//     * 
//     * @param sco The sco that is ended.
//     */
//    public void endSco(Sco sco) {
//        //MapperCreator.instance(Applet.class).removeObject(sco.getAppletID());
//    }
    @Override
    public void start() {
        this.getRootPane().setDoubleBuffered(true);
    }

    /**
     * Stops the current applet. Indicates at the current course that the applet
     * will be stopped.
     */
    @Override
    public void stop() {
        if (DwoHelper.getApplet() != this) {
            return;
        }

        this.setWait();
        super.stop();
        if (currentCourse != null) {
            currentCourse.end();
        } else if (currentSco != null) {
            currentSco.end();
        }
        logoff();
        //TODO NOW
        StoreCreator.destroy();
        this.setReady();
    }

    @Override
    public void destroy() {
        DwoHelper.clrApplet(this);
    }

    @Override
    public void setCurrentSco(Sco sco) {
        currentSco = sco;
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
        //String  lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
        //lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
        //lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
        //lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        //UIManager.setLookAndFeel(lookAndFeel);
        int width = GuiConstants.DWO_WIDTH;
        int height = GuiConstants.DWO_HEIGHT;
        DWO dwo = new DWO(args);
        DWO.ReadLoggingProperties();
        DWO.ReadConfigProperties();
        MainFrame mf = new MainFrame(dwo, width, height);
        mf.setTitle("DWO");
        mf.pack();
        mf.show();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(GuiConstants.DWO_WIDTH, GuiConstants.DWO_HEIGHT);
    }

    /**
     * Renames the specified class.
     *
     * @param schoolClass The class to rename.
     * @param newName The new name for the class.
     * @return If the class is successfully renamed it returns true. Otherwise
     * it returns false.
     * @see
     * fi.dwo.client.domain.DwoIF#renameClass(fi.dwo.client.domain.SchoolClass,
     * java.lang.String)
     */
    @Override
    public boolean renameClass(SchoolClass schoolClass, String newName, boolean iconizer) {
        try {
            PersistenceFacade.instance().renameClass(schoolClass, newName, iconizer);
            schoolClass.setClassName(newName);
            schoolClass.setIconizer(iconizer);
            return true;
        } catch (ClassException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return false;
        }
    }

    /**
     * Shows a wait cursor and the default wait message to indicate that the
     * user must wait for a while.
     *
     * @see fi.dwo.client.domain.DwoIF#setWait()
     */
    @Override
    public void setWait() {
        setWait(TextMapper.getText(TextMapper.GUI_WAIT_A_MOMENT));
    }

    private JLabel waitLabel = new JLabel("Even geduld");

    private void initWaitLabel() {
        waitLabel.setFont(GuiConstants.HEADER_TEXT);

        waitLabel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        waitLabel.setHorizontalAlignment(JLabel.CENTER);
        waitLabel.setVerticalAlignment(JLabel.CENTER);
        waitLabel.setBackground(new Color(218, 238, 249));//GuiConstants.MAIN_BACKGROUND);
        waitLabel.setOpaque(true);
        waitLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
//    	{
//    		Image img;
//    		img = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_BGIMAGE_MENU);
//    		Border border = new DWOBorder(img, GuiConstants.GUI_INSETS_MENU, GuiConstants.GUI_9PATCH_MENU);
//    		waitLabel.setBorder(border);   		
//    	}

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
     * @see fi.dwo.client.domain.DwoIF#setWait()
     */
    @Override
    public void setWait(String waitText) {
        nestedWait++;
        if (nestedWait == 1) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            this.waitText = waitText;
            waitLabel.setText(" " + waitText + " ");
            getGlassPane().setVisible(true);
            validate();
            if (this.getGraphics() != null) {
                paint(this.getGraphics());
            }
        }
    }

    /**
     * Hides the wait cursor and the message what was showed up with
     * <code>setWait()</code>
     *
     * @see fi.dwo.client.domain.DwoIF#setReady()
     */
    @Override
    public void setReady() {
        if (nestedWait == 1) {
            getGlassPane().setVisible(false);
            if (panel != null) {
                panel.requestFocus();
            }
            setCursor(Cursor.getDefaultCursor());
        }
        nestedWait--;
    }

    /**
     * Clears all the information of the current user out of the memory, so no
     * cashing problems can appear.
     *
     * @see fi.dwo.client.domain.DwoIF#clearCurrentUserData()
     */
    @Override
    public void clearCurrentUserData() {
        //MapperCreator.instance(User.class).removeObject(User.getCurrentUser().getUserID());
        PersistenceFacade.instance().clearCurrentUserDataCache(User.getCurrentUser().getUserID());
        User.setCurrentUser(null);
        currentCourse = null;
        courseList = null;
        resultsModule = null;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#getEditableCourses()
     */
    @Override
    public Course[] getEditableCourses() {
        try {
            courseList = PersistenceFacade.instance().getEditableCourses(User.getCurrentUser());
            return PersistenceFacade.instance().sequence(selectDwoProfileCourses(courseList));
        } catch (PersistenceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return null;
        }
    }

    /**
     * Alle aangepaste sco's van een school binnen dit profiel. TODO als de
     * docent profiel-rechten heeft, wat dan?
     *
     * @return
     */
    @Override
    public Sco[] getEditableScos() {
        return PersistenceFacade.instance().getEditableScos(getUser().getSchool(), getDwoProfile());
    }
    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#addCourse(java.lang.String, java.lang.String)
     */

    @Override
    public Course addCourse(String name, String description, Course parent, boolean isMap) {
        try {
            return PersistenceFacade.instance().addCourse(User.getCurrentUser().getSchool(), name, description, dwoProfile, parent, isMap);
        } catch (CourseException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return null;
        }
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#updateCourse(fi.dwo.client.domain.Course)
     */
    @Override
    public boolean updateCourse(Course course) {
        try {
            return PersistenceFacade.instance().updateCourse(course);
        } catch (CourseException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return false;
        }
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#addSco(int, java.lang.String, java.lang.String)
     */
    @Override
    public Sco addSco(Course course, AppletConfig appletConfig, String name, String description, boolean showScore) {
        try {
            return PersistenceFacade.instance().addSco(course, appletConfig, name, description, showScore);
        } catch (ScoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return null;
        }
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#updateSco(fi.dwo.client.domain.Sco)
     */
    @Override
    public boolean updateSco(Sco sco) {
        try {
            return PersistenceFacade.instance().updateSco(sco);
        } catch (ScoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return false;
        }
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#deleteCourse(fi.dwo.client.domain.Course)
     */
    @Override
    public boolean deleteCourse(Course course) {
        try {
            return PersistenceFacade.instance().deleteCourse(course);
        } catch (CourseException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return false;
        }
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#deleteSco(fi.dwo.client.domain.Sco)
     */
    @Override
    public boolean deleteSco(Sco sco) {
        try {
            return PersistenceFacade.instance().deleteSco(sco);
        } catch (ScoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return false;
        }
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#getAppletConfig()
     */
    @Override
    public AppletConfig[] getAppletConfig() {
        try {
            AppletConfig[] ac = (AppletConfig[]) PersistenceFacade.instance().get(AppletConfig.class, getLocale());
//            for(int i = 0; i < ac.length; i++) {
//                System.out.println("AppletConfig: " + ac[i].getAppletID() + "; " + ac[i].getName() + "; " + ac[i].getLaunchdata());
//            }
            return ac;
        } catch (PersistenceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return null;
        }
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#getSchools()
     */
    @Override
    public School[] getSchool() {
        try {
            School[] sc = (School[]) PersistenceFacade.instance().get(School.class);
            return sc;
        } catch (PersistenceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return null;
        }
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#previewSco(fi.dwo.client.domain.AppletConfig)
     */
    @Override
    public ScoPanel previewSco(AppletConfig appletConfig) {
        Sco dummy = new Sco();
        dummy.setAppletID(appletConfig.getAppletID());
        dummy.setName(appletConfig.getName());
        dummy.setLaunchdata((Hashtable) new StringCodeObject(appletConfig.getLaunchdata()).toObject());
        dummy.setLessonMode(Sco.BROWSE);
        return dummy.getScoPanel(this, null);
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#previewSco(fi.dwo.client.domain.Sco)
     */
    @Override
    public ScoPanel previewSco(Sco sco) {
        sco.setLessonMode(Sco.BROWSE);
        return sco.getScoPanel(this, null);
        //return sco.getNewScoPanel(this, null);
    }

    @Override
    public String LMSInitialize(String iParam) {
        if (currentSco != null) {
            return currentSco.LMSInitialize(iParam);
        } else if (currentCourse != null && currentCourse.getCurrentSco() != null) {
            return currentCourse.getCurrentSco().LMSInitialize(iParam);
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

    @Override
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

    HashMap samlData;

    private static String getDecodedCookie(String cookie) {
        String value = DwoHelper.getCookie(cookie);
        try {
            if (value != null) {
                value = URLDecoder.decode(value, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
        }
        return value;
    }

    /*
     * Beste Wim uit SURFnet Instelling,
     * je gebruikersid: c31d3bbc5b214528b90a0c72ce0240da11588d60@uu.nl,
     * je schoolid: SURFIN
     * @return
     */
    private User getSAMLUser() {
        String samlUserID = getDecodedCookie(DWO_SAML_USER_ID);
        String samlOrgID = getDecodedCookie(DWO_SAML_ORGANIZATION_ID);
        String samlOrg = getDecodedCookie("dwoSAMLOrganization");
//    	if(false)
//    	{
//    		samlUserID = "c31d3bbc5b214528b90a0c72ce0240da11588d60@uu.nl";
//    		samlOrgID = "SURFIN";
//    		samlOrg   = "SURFnet Instelling";
//    	}

        if (samlUserID != null && samlOrgID != null) {
            try {
                User u = PersistenceFacade.instance().loginViaSAML(samlUserID, samlOrgID);
                return u;
            } catch (LoginException e) {
            }
            samlData = new HashMap();
            samlData.put(DWO_SAML_USER_ID, samlUserID);
            samlData.put(DWO_SAML_ORGANIZATION_ID, samlOrgID);
            samlData.put("dwoSAMLOrganization", samlOrg);
        }
        return null;
    }

    @Override
    public void linkViaSAML() {
        PersistenceFacade.instance().linkViaSAML(getUser(), samlData.get(DWO_SAML_USER_ID).toString(),
                samlData.get(DWO_SAML_ORGANIZATION_ID).toString());

    }

    /**
     * Geef mij een gebruiker buitenom.
     *
     * @return a user.
     */
    private User getInitialUser() {
        return null;
    }

    /**
     * Zet een gebruiker in een klas.
     *
     * @param className naam van klas
     * @param u de gebruiker
     * @param school die van u
     */
    private void setInitialUserInClass(String className, User u, School school) {
        SchoolClass schoolClass = u.getInClass();
        if (className != null
                && (schoolClass == null || !schoolClass.getName().equals(className))) {
            SchoolClass[] classes = school.getClassList();
            for (SchoolClass classe : classes) {
                if (className.equals(classe.getName())) {
                    u.setInClass(classe);
                    try {
                        PersistenceFacade.instance().changeAccount(u, null, null, u.getFirstname(), u.getMiddleName(), u.getLastName(), u.getEmail());
                        PersistenceFacade.instance().addStudentToClass(u.getInClass(), u.getID());
                    } catch (PersistenceException e) {
                        log.log(Level.SEVERE, null, e);
                    } catch (RegisterException ex) {
                        log.log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    /**
     * Converteer een role naar een Group. De namen van de group zijn niet
     * gelijk aan die van de 'role' (entree.kennisnet.nl). Er moet daarom
     * gemapped worden.
     *
     * @param role
     * @return de Group die role representeerd.
     */
    private Group findGroup(String role) {
        if (role == null) {
            return null;
        }
        // TODO is deze mapping compleet?
//        if (false) // docent en contactdocent
//        {
//            role = "TEACHER";
//        }
//        if (false) {
//            role = "STUDENT";
//        }
        Group[] groups = getGroups();
        for (Group group : groups) {
            if (role.equalsIgnoreCase(group.getName())) {
                return group;
            }
        }
        return null;
    }

    @Override
    public boolean deleteSchool(School sc) {
        try {
            return PersistenceFacade.instance().deleteSchool(sc);
        } catch (SchoolException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return false;
        }
    }

    /**
     * Verwissel de sequencenrs van twee Sco's. De Sco's moeten tot dezelfe
     * Course behoren.
     *
     * @param sco1 Sco
     * @param sco2 Sco
     * @return boolean: succes of gefaald
     */
    @Override
    public boolean swapSco(Sco sco1, Sco sco2) {
        try {
            return PersistenceFacade.instance().swapScoSequenceNr(sco1, sco2);
        } catch (ScoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return false;
        }
    }

    @Override
    public ResultsModuleIF getUserResultsModule(Course course) {
        if (User.getCurrentUser() instanceof Guest) {
            return null;
        }
        return new UserResultsModule(course, User.getCurrentUser(), this);
    }

    /* (non-Javadoc)
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

        return super.getParameter(name);
    }

    /**
     * Adds a school to the database.
     *
     * @param id
     * @param schoolName The name of the new school.
     * @param schoolLogin The login name of the new school.
     * @param schoolPasswdMap
     * @param date
     * @return
     * @pschool is successfully inserted it returns true. Otherwise it returns
     * false.
     * @throws fi.dwo.commons.exceptions.SchoolException
     *
     */
    @Override
    public School addSchool(int id, String schoolName, String schoolLogin,
            SchoolPasswdMap schoolPasswdMap, Date date) throws SchoolException {

//    	String studentPassw = schoolPasswdMap.getPasswd(SchoolGroup.STUDENT);
//		String teacherPassw = schoolPasswdMap.getPasswd(SchoolGroup.TEACHER);
//		return PersistenceFacade.instance().addSchool(id, schoolName, schoolLogin, studentPassw, teacherPassw);
        return PersistenceFacade.instance().addSchool(id, schoolName, schoolLogin, schoolPasswdMap, date);
    }

    /**
     * Edit a school to the database.
     *
     * @param schoolID The ID of the school.
     * @param schoolName The new name of the school.
     * @param schoolLogin The new login name of the school.
     * @param schoolPasswdMap
     * @param date
     * @return school
     * @throws fi.dwo.commons.exceptions.SchoolException
     *
     */
    @Override
    public School editSchool(int schoolID, String schoolName,
            String schoolLogin, SchoolPasswdMap schoolPasswdMap, Date date) throws SchoolException {
//		String studentPassw = schoolPasswdMap.getPasswd(SchoolGroup.STUDENT);
//		String teacherPassw = schoolPasswdMap.getPasswd(SchoolGroup.TEACHER);		
//		return PersistenceFacade.instance().editSchool(schoolID, schoolName, schoolLogin, studentPassw, teacherPassw);
        return PersistenceFacade.instance().editSchool(schoolID, schoolName, schoolLogin, schoolPasswdMap, date);
    }

    @Override
    public boolean updateLogo(Course c) {
        return PersistenceFacade.instance().updateLogo(c);
    }

    public String selectSco(String scoid) {
        int id = Integer.parseInt(scoid); // In dwo-appengine id = scoid
        try {
            Sco sco = (Sco) PersistenceFacade.instance().get(id, Sco.class);
            GuiCreator.instance().getMainPanel().getCenter().select(sco);
            return "true";
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
            return "false";
        }
    }

    private static final String SELECT = "select:";

    public String interpret(String command) {
        if (command.startsWith(SELECT)) {
            return selectSco(command.substring(SELECT.length()));
        }

        return "false";
    }

}
