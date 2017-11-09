package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * AccountDisplay UI interface. the interface should be
 * available as a JavaScript object named "jsAccountDisplay".
 * Callback from the MainDisplay UI to the presenter occurs
 * via 
 * 
 * There are two relevant presenter functions.
 * 
 * To save account data:
 * 
 * public void saveUser(String givenName, String insertion, String familyName, 
 *   String email, String curPassword, String newPassword, String newPasswordAgain);
 * 
 * and to reset the data in the Account display.
 * 
 * public void updateUserDataInView();
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
    public static native void updateView(String username, String firstName, String insertion, String familyName, String email);
}
