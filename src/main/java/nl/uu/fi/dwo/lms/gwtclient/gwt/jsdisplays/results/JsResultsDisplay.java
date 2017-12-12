package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results;

import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.*;
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
public class JsResultsDisplay{
    /** Clears the results in the ui. */
    public static native void clear();
    /** Fills the result table with the results.
     * @param data a double map with string data to show */
    public static native void plot(JavaScriptObject data, boolean zoomedClass, boolean zoomedCourse);    
    /** setEmptyTableMessage show an indicator that the table is empty.
     */
    public static native void setEmptyTableMessage();
    /** setEmptyTableMessage show an indicator that we are fetching data.
     */
    public static native void setLoadingTableMessage();
}
