/*
 * Created on Feb 25, 2005
 *
 */
package fi.dwo.dwojapplet.domain;

import fi.beans.appletutil.AppletUtil;
import fi.beans.mainframe.MainFrame;
import fi.dwo.commons.dom.entities.DomFullUser;
import fi.dwo.commons.dom.entities.DomSchool;
import fi.dwo.commons.dom.entities.DomSchoolsRolesAndClasses;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.exceptions.LoginException;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.rest.SecureUserAccountLoginsManager;
import fi.dwo.dwojapplet.gui.GuiCreator;
import fi.dwo.dwojapplet.gui.MainPanel;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import java.applet.Applet;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import netscape.javascript.JSObject;

/**
 * Static Helper class for the DWO. The DwoHelper has two startup phases. During
 * the boot phase some parameters can and must be set. Afterwards the init
 * method must be called which retrieves configuration of the application server
 * and initializes the class. Information can only be considered correct in the
 * DwoHelper after initialization has finished.
 */
public final class DwoHelper {

    private static final Logger LOG = Logger.getLogger(DwoHelper.class.getName());

    /**
     * DWO 1.0 properties
     */
    private static AppletUtil au;

    private static Hashtable loadedImages;

    private static Applet applet;

    private static GuiCreator guic;

    private static boolean isApplication = true; // default als je setApplet niet aanroept.

    public static boolean umpc, contact;

    private static boolean scormExportLoggedIn, appletExportLoggedIn, adminLoggedIn;

    /**
     * DWO boot property attributes, set before calling init in DWO.main()
     */
    //TODO fix locale to be set within DWO_main.
    private static Locale locale = new Locale.Builder().setLanguage("nl").setRegion("NL").build(); //runtime property for locale.

    private static URL serverUrlPath = null;
    private static URL resourceUrlPath = null; // required null if to use the default
    private static URL jarUrlPath;

    //depending on application or applet start.
    private static SchoolClass schoolClass;
    private static School school;

    /**
     * Boot properties that need to be set before calling init()
     */
    private static DomFullUser currentUser; // null if none available.
//    private static RoleType currentRole; // null if none available.

    /**
     * Init properties set by init() *
     */
    private static DomSchoolsRolesAndClasses schoolLogins;
    //      
    /**
     * ********deprecated attributes **********
     */
    private static User currentFacadeUser;
    private static String plainPassword;

    /**
     * @return the plainPassword
     */
    @Deprecated
    public static String getPlainPassword() {
        return plainPassword;
    }

    /**
     * @param aPlainPassword the plainPassword to set
     */
    @Deprecated
    public static void setPlainPassword(String aPlainPassword) {
        plainPassword = aPlainPassword;
    }

    /**
     * @return the locale
     */
    public static Locale getLocale() {
        return locale;
    }

    /**
     * @param aLocale the locale to set
     */
    public static void setLocale(Locale aLocale) {
        locale = aLocale;
    }

    public static boolean isSingleSchoolStudent() {
        return currentUser.getSingleSchool();
    }

    /**
     * Initialization function that retrieves some basic configuration data from
     * the server. DwoHelper is to be initialized after the DWO started and any
     * pre-initialization occurred specifying resource locations.
     *
     * @throws Dwo2Exception
     */
    public static void init() throws Dwo2Exception {
        //Fetch all the login roles from the server for the current roles
        try {
            DomSchoolsRolesAndClasses srcs = SecureUserAccountLoginsManager.getSchoolLogins();
            //TODO should set relevant properties when calling init using REST-interface: school, hasRole etc...

        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * User initialization. When ever the user changes or his active schoollogin
     * we reinitialize things.
     *
     *
     * @param aCurrentUser
     * @throws Dwo2Exception
     */
    public static void userInit(DomFullUser aCurrentUser) throws Dwo2Exception {
        //Fetch all the login roles from the server for the current roles
        try {
            if (aCurrentUser != null) {
                schoolLogins = SecureUserAccountLoginsManager.getSchoolLogins();
            } else {
                schoolLogins = null;
            }
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * ****************************************
     */
    /**
     * Returns the current AppletUtil.
     *
     * @return The AppletUtil.
     */
    public static AppletUtil getAu() {
        return au;
    }

    /**
     * Sets the AppletUtil.
     *
     * @param au The AppletUtil to set.
     */
    public static void setAu(AppletUtil au) {
        DwoHelper.au = au;
    }

    public static void setUmpc(boolean b) {
        DwoHelper.umpc = b;
    }

    /**
     * Returns the current Applet.
     *
     * @return The current Applet.
     */
    public static Applet getApplet() {
        return applet;
    }

    public static Object getJSObject() {
        if (applet == null) {
            return null;
        }
        try {
            return JSObject.getWindow(applet);
        }
        catch (Exception e) {
            // expect JSException of ClassNotFoundException
        }
        catch (NoClassDefFoundError e) {
            // expect NoClassDefFound ERROR
        }
        return null;
    }

    /**
     * Sets the current Applet.
     *
     * @param applet The applet to set.
     * @return
     */
    public static boolean setApplet(Applet applet) {
// Voor Peter: in comment zetten
        if (DwoHelper.applet != null) {
            JOptionPane.showMessageDialog(applet, TextMapper.getText(TextMapper.DWOAPPLET_EXISTS));
            return false;
        }
// Einde
        DwoHelper.applet = applet;
        isApplication = applet.getParent() instanceof MainFrame;
        return true;
    }

    public static void clrApplet(Applet applet) {
        if (applet == DwoHelper.applet) {
            DwoHelper.applet = null;
        }
    }

    public static boolean isApplication() {
        return isApplication;
    }

    public static boolean isSecure() {
        return true;
    }

    public static boolean isAdminLoggedIn() {
        return adminLoggedIn;
    }

    public static void setAdminLoggedIn(boolean b) {
        adminLoggedIn = b;
    }

    public static boolean isScormExportLoggedIn() {
        return scormExportLoggedIn;
    }

    public static void setScormExportLoggedIn(boolean b) {
        scormExportLoggedIn = b;
    }

    public static boolean isAppletExportLoggedIn() {
        return appletExportLoggedIn;
    }

    public static void setAppletExportLoggedIn(boolean b) {
        appletExportLoggedIn = b;
    }

    public static Frame getFrameForComponent(Component owner) {
        if (owner == null) {
            owner = applet;
        }
// Java 1.2
        return JOptionPane.getFrameForComponent(owner);
// Java 1.1 via Acme.GuiUtils, of zelf doen.
    }

    public static Point getComponentLocation(Component c) {
        int x = c.getLocation().x;
        int y = c.getLocation().y;
        Container parent;
        parent = c.getParent();
        for (int i = 0; parent != null && i < 30; i++) {
            if (parent instanceof MainPanel) {
                return new Point(x, y);
            } else {
                if (parent == null) {
                    return null;
                }
                x += parent.getLocation().x;
                y += parent.getLocation().y;
                parent = parent.getParent();
            }
        }
        return null;
    }

    public static Image getResourceImage(String image) {
        if (loadedImages == null) {
            loadedImages = new Hashtable();
        }
        if (loadedImages.containsKey(image)) {
            return (Image) loadedImages.get(image);
        }
        Image im = au.getImage(image);
        return loadImage(image, im);
    }

    static URL applicationBase;

    public static Image getImage(String image) {
        if (loadedImages == null) {
            loadedImages = new Hashtable();
        }
        if (loadedImages.containsKey(image)) {
            return (Image) loadedImages.get(image);
        }

        URL url = getURL(image);
        if (url == null) {
            return null;
        }
        Image im;
        im = applet.getImage(url);
        return loadImage(image, im);
    }

    public static URL getURL(String resource) {
        URL url = null;
        if (isApplication) {
            if (applicationBase == null) {
                try {
                    applicationBase = new URL("http://www.fisme.science.uu.nl/dwo/");
                    //TODO Gert: fix this to allow change of url by property file.
                }
                catch (MalformedURLException e) {
                }
            }
            try {
                url = new URL(applicationBase, resource);
            }
            catch (MalformedURLException e) {
                LOG.log(Level.SEVERE, null, e);
            }

        } else {
            try {
                url = new URL(applet.getCodeBase(), resource);
            }
            catch (MalformedURLException e) {
            }/**/

        }
        return url;
    }

    /**
     * @param image
     * @param im
     * @return
     */
    private static Image loadImage(String image, Image im) {
        MediaTracker tr = new MediaTracker(applet);
        tr.addImage(im, 0);
        try {
            tr.waitForAll();
        }
        catch (Exception e) {
        }
        if (im != null) {
            loadedImages.put(image, im);
        }
        return im;
    }

    /**
     * True if schoolAdmin.
     *
     * @return the contact
     */
    public static boolean isContact() {
        return contact;
    }

    /**
     * True if schoolAdmin.
     *
     * @param contact the contact to set
     */
    public static void setContact(boolean contact) {
        DwoHelper.contact = contact;
    }

    public static String getCookie() {
        if (isApplication()) {
            return null;
        }
        try {
            String cookie;
            cookie = (String) JSObject.getWindow(applet).eval("document.cookie");
            return cookie;
        }
        catch (Throwable ex) {
            return null;
        }
    }

    public static String getCookie(String name) {
        String cookie = getCookie();
        if (cookie == null) {
            return null;
        }

        String nameIs = name + "=";
        if (cookie.length() > 0) {
            int begin = cookie.indexOf(nameIs);
            if (begin != -1) {
                begin += nameIs.length();
                int einde = cookie.indexOf(";", begin);
                if (einde == -1) {
                    einde = cookie.length();
                }
                String value;
                value = cookie.substring(begin, einde);
                return value;
            }
        }
        return null;
    }

    public static void setCookie(String name, String value) {
        if (isApplication()) {
            return;
        }
        try {
            JSObject.getWindow(applet).eval("document.cookie ='" + name + "=" + value + "';");
        }
        catch (Throwable ex) {
        }
    }

    public static void deleteCookie(String name) {
        if (isApplication()) {
            return;
        }
        try {
            JSObject.getWindow(applet).eval("document.cookie ='" + name + "=dummy" + "';");
        }
        catch (Throwable ex) {
        }
    }

    /**
     * Returns the url path to the resource location where the resources are
     * stored.
     *
     * @return the resourceUrlPathString
     */
    public static URL getResourceUrlPath() {
        return resourceUrlPath;
    }

    /**
     * Sets the url path to the resource location where the resources are
     * stored.
     *
     * @param aGetResourceURLPath
     */
    public static void setResourceUrlPath(URL aGetResourceURLPath) {
        resourceUrlPath = aGetResourceURLPath;
    }

    /**
     * Sets the url path to the resource location where the resources are
     * stored.
     *
     * @param aJarURLPath
     */
    public static void setJarUrlPath(URL aJarURLPath) {
        jarUrlPath = aJarURLPath;
    }

    /**
     * Returns the url path to the resource location where the resources are
     * stored.
     *
     * @return
     */
    public static URL getJarUrlPath() {
        return jarUrlPath;
    }

    public static void setServerUrlPath(URL aServerUrlPath) {
        serverUrlPath = aServerUrlPath;
    }

    public static URL getServerUrlPath() {
        return serverUrlPath;
    }

    /**
     * @return the schoolClass
     */
    public static SchoolClass getSchoolClass() {
        return schoolClass;
    }

    /**
     * @param aSchoolClass the schoolClass to set
     */
    public static void setSchoolClass(SchoolClass aSchoolClass) {
        schoolClass = aSchoolClass;
    }

    /**
     * @return the school
     */
    public static School getSchool() {
        return school;
    }

    /**
     * @param aSchool the school to set
     */
    public static void setSchool(School aSchool) {
        school = aSchool;
    }

    /**
     * @return the current User
     */
    public static DomFullUser getCurrentUser() {
        return currentUser;
    }

    /**
     * Only updates the user not its login roles for efficiency.
     *
     * @param aCurrentUser the current User to set
     */
    public static void updateCurrentUser(DomFullUser aCurrentUser) {
        if (aCurrentUser.getId() == currentUser.getId()) {
            currentUser = aCurrentUser;
            try {
                currentFacadeUser = (User) PersistenceFacade.instance().login(aCurrentUser.getUsername());
            }
            catch (LoginException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        } else {
            currentFacadeUser = null;
        }
    }

    /**
     * @param aCurrentUser the current User to set
     */
    public static void setCurrentUser(DomFullUser aCurrentUser) throws Dwo2Exception {
        userInit(aCurrentUser);
        if (aCurrentUser != null) {
            try {
                currentFacadeUser = (User) PersistenceFacade.instance().login(aCurrentUser.getUsername());
            }
            catch (LoginException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        } else {
            currentFacadeUser = null;
        }
    }

    /**
     * @return the currentFacadeUser
     */
    @Deprecated
    public static User getCurrentFacadeUser() {
        return currentFacadeUser;
    }

    /**
     * @param aCurrentUser the currentFacadeUser to set
     */
    @Deprecated
    public static void setCurrentFacadeUser(User aCurrentUser) {
        currentFacadeUser = aCurrentUser;
    }

//    /**
//     * @return the currentRole
//     */
//    public static RoleType getCurrentRole() {
//                    return RoleType.valueOf(schoolLogins.getActiveSchoolRoleAndClass().getRoleName());
//    }
//
//    /**
//     * @param aCurrentRole the currentRole to set
//     */
//    public static void setCurrentRole(RoleType aCurrentRole) {
//        currentRole = aCurrentRole;
//    }
    public static RoleType[] getRoles() {
        RoleType[] list = new RoleType[5];
        list[0] = RoleType.ANONYMOUS;
        list[1] = RoleType.STUDENT;
        list[2] = RoleType.TEACHER;
        list[3] = RoleType.SCHOOLADMIN;
        list[4] = RoleType.ADMIN;
        return list;
    }

    /**
     * @return the srcs
     */
    public static DomSchoolsRolesAndClasses getSchoolLogins() {
        return schoolLogins;
    }

    /**
     * @param aSchoolLogins
     */
    public static void setSchoolLogins(DomSchoolsRolesAndClasses aSchoolLogins) {
        schoolLogins = aSchoolLogins;
    }
}
