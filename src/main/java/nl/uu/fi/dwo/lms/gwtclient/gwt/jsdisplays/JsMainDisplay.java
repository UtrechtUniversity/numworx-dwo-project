package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 *
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "dwo", namespace = JsPackage.GLOBAL)
//@JsType(isNative = false, namespace = JsPackage.GLOBAL)
public class JsMainDisplay {
    public static native void hideMenu();
    public static native void showMenu();
    public static native void showLoginView();
    public static native void showWelcomeView();
    public static native void showAccountView();
    public static native void setSchoolName(String schoolName);
    public static native void setUserRole(String userRole);
    public static native void setPresentationName(String presentationName);    
    public static native boolean isMenuVisible();
}
