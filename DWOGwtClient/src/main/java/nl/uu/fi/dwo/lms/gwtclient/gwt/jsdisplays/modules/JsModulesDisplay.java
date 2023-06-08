package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.modules;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * WelcomeDisplay UI interface. the interface should be available as a
 * JavaScript object named "jsWelcomeDisplay". There are no call-backs from the
 * display.
 *
 *
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "jsModulesDisplay", namespace = JsPackage.GLOBAL)
//@JsType(isNative = false, namespace = JsPackage.GLOBAL)
public class JsModulesDisplay {

    public static native void init();

    public static native void clear();

    public static native void openUrl(String url);

    public static native void setMainNavVisible(boolean b);

    public static native boolean isMainNavVisible();

    public static native void sendMessage(String message);
}
