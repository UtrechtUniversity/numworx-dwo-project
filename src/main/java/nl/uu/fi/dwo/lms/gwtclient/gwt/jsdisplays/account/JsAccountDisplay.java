package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.account;

import com.google.gwt.json.client.JSONValue;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * AccountDisplay UI interface. the interface should be
 * available as a JavaScript object named "jsAccountDisplay".
 * Callback from the MainDisplay UI to the presenter occurs
 * via 
 * 
 * There are four relevant presenter functions.
 * 
 * public void changePasword(String curPassword, String newPassword, String newPasswordAgain)
 * public void switchSchoolLogin(String hasRoleId);
 * public void addASchoolLogin(String role, String schoolLogin, String accessCode);
 * public void removeASchoolLogin(String hasRoleId);
 * 
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "jsAccountDisplay", namespace = JsPackage.GLOBAL)
public class JsAccountDisplay{
    /** Clears all the account fields of their values. */
    public static native void clear();
    /** Clears the password field of its value. */
    public static native void init();
    /** Clears username and password fields of values. */
    public static native void updateView(JSONValue schoolLogins);
}
