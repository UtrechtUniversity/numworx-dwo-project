package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.roleswitch;

import com.google.gwt.core.client.JavaScriptObject;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * WelcomeDisplay UI interface. the interface should be
 * available as a JavaScript object named "jsSwitchSchoolDisplay". Calls the
 * presenter function with select(key) to select a school and switchSchool()
 * to switch to that school.
 * 
 * The callback is
 * 
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "jsSwitchSchoolDisplay", namespace = JsPackage.GLOBAL)
//@JsType(isNative = false, namespace = JsPackage.GLOBAL)
public class JsSwitchSchoolDisplay{
    /** Clears the schools in the ui. */
    public static native void clear();
    /** Initialize the ui to the default state with an empty list of schools. */
    public static native void init();
    /** Fills the schools view with the list of schools. It requires a map
     * with a string as a key, and an object 'SchoolItem'. SchoolItem contains two string values
     * key and schoolclassName.
     * @param data a map with a string as key and value SchoolItem. */
    public static native void updateView(JavaScriptObject data);    
}
