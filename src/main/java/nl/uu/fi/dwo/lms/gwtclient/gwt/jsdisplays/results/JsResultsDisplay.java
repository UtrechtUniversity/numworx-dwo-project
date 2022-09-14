package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results;

import com.google.gwt.core.client.JavaScriptObject;
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
    public static native void init();
    /** Clears the results in the ui. */
    public static native void clear();
    public static native void setHelp(String url);
    public static native void setResultTree(JavaScriptObject resultTree, JavaScriptObject studentTree);    
    public static native void setResultTreeWithContext(JavaScriptObject resultTree, JavaScriptObject studentTree, JavaScriptObject context);    
    /** setEmptyTableMessage show an indicator that the table is empty. */
    public static native void setEmptyTableMessage();
    /** setEmptyTableMessage show an indicator that we are fetching data. */
    public static native void setLoadingTableMessage();
    
    public static native void setChooseModulesTable();
	public static native void setRemedialView(boolean set);
}
