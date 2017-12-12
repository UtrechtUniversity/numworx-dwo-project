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
    /** Clears Welcome text. */
    public static native void clear();
    /** Sets the welcome text. */
    public static native void setWelcomeText(String html);
}
