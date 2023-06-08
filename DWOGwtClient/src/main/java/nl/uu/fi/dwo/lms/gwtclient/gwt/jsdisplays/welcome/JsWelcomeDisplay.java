package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.welcome;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * WelcomeDisplay UI interface. the interface should be
 * available as a JavaScript object named "jsWelcomeDisplay".
 * There are no call-backs from the display.
 * 
 * 
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "jsWelcomeDisplay", namespace = JsPackage.GLOBAL)
//@JsType(isNative = false, namespace = JsPackage.GLOBAL)
public class JsWelcomeDisplay{
    public static native void init();
    /** Clears Welcome text. */
    public static native void clear();
    /** Set the url for the welcome module */
    public static native void setHelp(String url);
    /** Sets the welcome text. */
    public static native void setWelcomeText(String html);
    /** Sets the default welcome text depending on nl or en language, hard-coded in the html/javascript code. */
    public static native void setDefaultText();
}
