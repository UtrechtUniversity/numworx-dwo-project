package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.account;

import com.google.gwt.core.client.JavaScriptObject;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * AccountDisplay UI interface. the interface should be available as a
 * JavaScript object named "jsAccountDisplay". Callback from the MainDisplay UI
 * to the presenter occurs via
 *
 * There are four relevant presenter functions.
 *
 * public void saveUser(String givenName, String insertion, String familyName, String email, String curPassword, String newPassword, String newPasswordAgain) 
 * 
 * public void switchSchoolLogin(String hasRoleId); 
 * 
 * public void addASchoolLogin(String role, String schoolLogin, String accessCode);
 * 
 * public void removeASchoolLogin(String hasRoleId);
 *
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "jsAccountDisplay", namespace = JsPackage.GLOBAL)
public class JsAccountDisplay {
    /**
     * Clears all the account fields of their values.
     */
    public static native void clear();
    /**
     * Set's help url. Supports hashtag.
     * 
     * @param url 
     */
    public static native void setHelp(String url);
    /**
     * Clears the password field of its value.
     */
    public static native void init();

    /**
     * Updates SchoolLogins table.
     */
    public static native void updateSchoolLoginsView(JavaScriptObject schoolLogins);

    /**
     * Updates user fields.
     */
    public static native void updateUserView(JavaScriptObject user);
    
    public static native void clearAddSchoolLogin();
    
    public static native void setLoadingTableMessage();

    public static native void setEmptyTableMessage();
}
