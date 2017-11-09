package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * LoginDisplay UI interface. the interface should be
 * available as a JavaScript object named "jsLoginDisplay".
 * Callback from the LoginDisplay UI to the presenter occurs
 * via 
 * 
 * public void loginClicked(String user, String password, final Boolean switchRole);
 * 
 * where switchRole is always set to false;
 * 
 * There are two relevant presenter functions.
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "jsLoginDisplay", namespace = JsPackage.GLOBAL)
//@JsType(isNative = false, namespace = JsPackage.GLOBAL)
public class JsLoginDisplay{
    /** Clears username and password fields of values. */
    public static native void clear();
    /** Sets the username field. */
    public static native void setUsername(String username);
    /** Returns the username field value. */
    public static native String getUsername();
    /** Sets the password field. */
    public static native void setPassword(String password);
    /** Returns the password field value. */
    public static native String getPassword();

}
