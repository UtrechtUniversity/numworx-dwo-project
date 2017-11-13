package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays;

import java.util.Map;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesPresenter;

/**
 * WelcomeDisplay UI interface. the interface should be
 * available as a JavaScript object named "jsSchoolClassesDisplay".
 * 
 * The callback is
 * 
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "jsSchoolClassesDisplay", namespace = JsPackage.GLOBAL)
//@JsType(isNative = false, namespace = JsPackage.GLOBAL)
public class JsSchoolClassesDisplay{
    /** Clears the list of school classes in the ui. */
    public static native void clear();
    /** Initialize the ui to the default state with an empty list of school classes. */
    public static native void init();
    /** Fills the school classes view with the list of school classes. It requires a map
     * with a string as a key, and an object 'ClassItem'. ClassItem contains two string values
     * key and schoolclassName.
     * @param data a map with a string as key and value ClassItem. */
    public static native void updateView(Map<String, SchoolclassesPresenter.ClassItem> data);    
}
