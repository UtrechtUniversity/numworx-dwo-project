/*
 * Created on Feb 25, 2005
 *
 */
package fi.dwo.dwojapplet.domain;

import fi.beans.appletutil.AppletUtil;
import fi.beans.mainframe.MainFrame;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.commons.exceptions.LoginException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.rest.SecureUserAccountLoginsManager;
import fi.dwo.dwojapplet.gui.GuiCreator;
import fi.dwo.dwojapplet.gui.MainPanel;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import nl.uu.fi.dwo.rest.DwoLocale;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;

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
 * 
 * DwoHelper is expected to be accessed by multiple threads. Synchronization is there
 * for mandatory. Note that reference changes are atomic, primitives however not always. 
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
     * DWO boot property attributes, set before calling init() in DWO.main()
     */
    private static DwoLocale locale = new DwoLocale("nl-NL"); //runtime property for locale.

    private static String defaultUsername = "";
    private static String defaultPassword = "";
    private static URL serverUrlPath;
    private static URL resourceUrlPath; // required null if to use the default
    private static URL appUrlPath;
    private static URL jarUrlPath;
    private static HttpAuthenticationType httpAuthenticationType;
    private static DomSchool nullSchool;

    /**
     * Properties set on DwoHelper.init()
     */
   private static DomSchoolsRolesAndClassesV2 schoolLogins;
    
    /**
     * Properties that are set on user login.
     */
    private static DomUserFull currentUser; // null if none available.
    private static DomLoginContext currentLoginContext;
 
    // ********deprecated attributes **********
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
    public static DwoLocale getLocale() {
        return locale;
    }

    /**
     * @param aLocale the locale to set
     */
    public static void setLocale(DwoLocale aLocale) {
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
            schoolLogins = SecureUserAccountLoginsManager.getSchoolLogins();
            //TODO should set relevant properties when calling init using REST-interface: school, hasRole etc...

        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
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
    public static void userInit(DomUserFull aCurrentUser) throws Dwo2Exception {
        currentUser = aCurrentUser;
        //Fetch all the login roles from the server for the current roles
        try {
            if (aCurrentUser != null) {
                SecureUserAccountLoginsManager.getSchoolLogins();//updates DwoHelper
//                nullSchool = SecureUserAccountManager.getNullSchool();
            } else {
                schoolLogins = null;
            }
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
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
        } catch (Exception e) {
            // expect JSException of ClassNotFoundException
        } catch (NoClassDefFoundError e) {
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
        if(image.startsWith("resources/"))
			try {
				url = new URL (getResourceUrlPath(), image);
			} catch (MalformedURLException e) {
			}
        if (url == null) {
            return null;
        }
        Image im;
        im = applet.getImage(url);
        return loadImage(image, im);
    }

    /**
     * get URL relative to /dwo. codebase is /dwo/jars/ documentbase = /dwo/(?)
     * applicationbase = /dwo/
     *
     * @param resource
     * @return URL
     */
    public static URL getURL(String resource) {
        URL url = null;
        if (isApplication) {
            if (applicationBase == null) {
                    try {
                        applicationBase = new URL("https://app.dwo.nl/dwo/");
                    } catch (MalformedURLException e) {
                    }
            }
            try {
                url = new URL(applicationBase, resource);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            try {
                //applicationBase = applet.getDocumentBase(); // insecure
                if (applicationBase == null) {
                    applicationBase = new URL(applet.getCodeBase(), "../"); // secure
                }
                url = new URL(applicationBase, resource);
            } catch (MalformedURLException e) {
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
        } catch (Exception e) {
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
        } catch (Throwable ex) {
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
        } catch (Throwable ex) {
        }
    }

    public static void deleteCookie(String name) {
        if (isApplication()) {
            return;
        }
        try {
            JSObject.getWindow(applet).eval("document.cookie ='" + name + "=dummy" + "';");
        } catch (Throwable ex) {
        }
    }

    /**
     * Returns the url path of the resource location where the resources are
     * stored. Changes must be synchronized.
     *
     * @return the resourceUrlPathString
     */
    public static URL getResourceUrlPath() {
        return resourceUrlPath;
    }

    /**
     * Sets the url path of the resource location where the resources are
     * stored. Changes must be synchronized.
     *
     * @param aGetResourceURLPath
     */
    public static void setResourceUrlPath(URL aGetResourceURLPath) {
        resourceUrlPath = aGetResourceURLPath;
    }

    /**
     * Sets the url path of the jars location where the resources are
     * stored. Changes must be synchronized.
     *
     * @param aJarURLPath
     */
    public static void setJarUrlPath(URL aJarURLPath) {
        jarUrlPath = aJarURLPath;
    }

    /**
     * Returns the url path for the jars location where the resources are
     * stored. Changes must be synchronized.
     *
     * @return
     */
    public static URL getJarUrlPath() {
        return jarUrlPath;
    }

    /**
     * Sets the url path of the server. Changes must be synchronized.
     * 
     * @param aServerUrlPath
     */
    public static void setServerUrlPath(URL aServerUrlPath) {
        serverUrlPath = aServerUrlPath;
    }

   /**
     * Returns the url of the server. Changes must be synchronized.
     *
     * @return
     */    public static URL getServerUrlPath() {
        return serverUrlPath;
    }

     /**
     * Assisting function for hybrid code between old and new.
     *
     * @return
     * @deprecated
     */
    @Deprecated
    public static int getActiveSchoolClassId() {
        try {
            return MySQLPersistenceId.getNativeId(schoolLogins.getActiveSchoolRoleAndClass().getSchoolClass()).intValue();
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    /**
     * Assisting function for hybrid code between old and new.
     *
     * @return
     * @deprecated
     */
    @Deprecated
    public static int getActiveSchoolId() {
        try {
            return  MySQLPersistenceId.getNativeId(schoolLogins.getActiveSchoolRoleAndClass().getSchool()).intValue();
        } catch (Dwo2Exception ex) {
           LOG.log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    /**
     * @return the current User
     */
    public static DomUserFull getCurrentUser() {
        return currentUser;
    }

    /**
     * Only updates the user not its login roles for efficiency.
     *
     * @param aCurrentUser the current User to set
     */
    public static void updateCurrentUser(DomUserFull aCurrentUser) {
        if (aCurrentUser.getId() == currentUser.getId()) {
            currentUser = aCurrentUser;
            try {
                currentFacadeUser = (User) PersistenceFacade.instance().login(aCurrentUser.getUserName());
            } catch (LoginException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        } else {
            currentFacadeUser = null;
        }
    }

    /**
     * @param aCurrentUser the current User to set
     */
    public static void setCurrentUser(DomUserFull aCurrentUser) throws Dwo2Exception {
        userInit(aCurrentUser);
        if (aCurrentUser != null) {
            try {
                GuiCreator.instance().clearCurrentUserData((int) MySQLPersistenceId.getNativeId(aCurrentUser).intValue());
                currentFacadeUser = (User) PersistenceFacade.instance().login(aCurrentUser.getUserName());
            } catch (LoginException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        } else {
            if (currentFacadeUser != null) {
                GuiCreator.instance().clearCurrentUserData(currentFacadeUser.getID());
            }
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
    public static DomSchoolsRolesAndClassesV2 getSchoolLogins() {
        return schoolLogins;
    }

    /**
     * @param aSchoolLogins
     */
    public static void setSchoolLogins(DomSchoolsRolesAndClassesV2 aSchoolLogins) {
        schoolLogins = aSchoolLogins;
    }

    /**
     * @return the httpAuthenticationTYPE
     */
    public static HttpAuthenticationType getHttpAuthentication() {
        return httpAuthenticationType;
    }

    /**
     * @param aHttpAuthentication the httpAuthenticationTYPE to set
     */
    public static void setHttpAuthentication(HttpAuthenticationType aHttpAuthentication) {
        httpAuthenticationType = aHttpAuthentication;
    }

    /**
     * @return the defaultUsername
     */
    public static String getDefaultUsername() {
        return defaultUsername;
    }

    /**
     * @param aDefaultUsername the defaultUsername to set
     */
    public static void setDefaultUsername(String aDefaultUsername) {
    	if(aDefaultUsername == null)
    		aDefaultUsername = "";
    	defaultUsername = aDefaultUsername;
    }

    /**
     * @return the defaultPassword
     */
    public static String getDefaultPassword() {
        return defaultPassword;
    }

    /**
     * @param aDefaultPassword a password
     */
    public static void setDefaultPassword(String aDefaultPassword) {
    	if(aDefaultPassword == null) aDefaultPassword = "";
    	defaultPassword = aDefaultPassword;
    }

    public static void setAppURLPath(URL url) {
        appUrlPath = url;
    }

    public static URL getAppURLPath() {
        return appUrlPath;
    }

    public static void setCurrentLoginContext(DomLoginContext domLoginContext) {
        currentLoginContext = domLoginContext;
    }

    /**
     * @return the currentLoginContext
     */
    public static DomLoginContext getCurrentLoginContext() {
        return currentLoginContext;
    }

	//options voor de rights string.
	public static char READONLY = 'r';

	public static char PREVIEW = 'p';

	// Limited is dat je niet als gast en alleen met "goedgekeurde" schoolid's er in mag.
	// Goedgekeurd is een "school.properties" bestand.
	public static char LIMITED = 'l'; // goed voor rekenwise en consorten.

	private static String rights = "";

	/**
	 * @param rights the rights to set
	 */
	public static void setProfileRights(String rights) {
	    DwoHelper.rights = rights;
	}

	/**
	 * @return the rights
	 */
	public static String getProfileRights() {
	    return rights;
	}

	public static boolean hasProfileRight(char right) {
	    return rights.indexOf(right) >= 0;
	}

}
