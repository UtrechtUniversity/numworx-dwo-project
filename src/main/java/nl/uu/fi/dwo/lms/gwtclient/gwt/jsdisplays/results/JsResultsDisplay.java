package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONValue;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * WelcomeDisplay UI interface. the interface should be
 * available as a JavaScript object named "jsResultsDisplay". Calls the
 * presenter function with select(key) to select a school and switchSchool()
 * to switch to that school.
 * 
 * The callbacks are:
 * 
 *  public void showSelectedResults(String schoolClassId, boolean showOpenModules, boolean showClosedModules, JSONObject courseIds)
 * 
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "jsResultsDisplay", namespace = JsPackage.GLOBAL)
public class JsResultsDisplay{
    /** Clears the results in the ui. */
    public static native void clear();
    public static native void setResultTree(JavaScriptObject resultTree);    
    /** setEmptyTableMessage show an indicator that the table is empty. */
    public static native void setEmptyTableMessage();
    /** setEmptyTableMessage show an indicator that we are fetching data. */
    public static native void setLoadingTableMessage();
}
