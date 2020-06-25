package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results;

import com.google.gwt.core.client.JavaScriptObject;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * WelcomeDisplay UI interface. the interface should be
 * available as a JavaScript object named "jsResultsSelectsDisplay". Calls the
 * presenter function with select(key) to select a school and switchSchool()
 * to switch to that school.
 * 
 * The callbacks are:
 * 
 *  public void showSelectedResults(String schoolClassId, boolean showOpenModules, boolean showClosedModules, JSONObject courseIds)
 * 
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "jsSelectedResultsDisplay", namespace = JsPackage.GLOBAL)
public class JsSelectedResultsDisplay{
     public static native void init0();
   /** Clears the results in the ui. */
    public static native void clear();
    /** Inits the view */
    public static native void init(JavaScriptObject resultState);
    /** Fills the result table with the results.*/
     public static native void updateResultTree(JavaScriptObject resultsTree, JavaScriptObject studentsTree);  
     public static native void showPages(JavaScriptObject resultsTree);
    /** setEmptyTableMessage show an indicator that the table is empty. */
    public static native void setEmptyTableMessage();
    /** setEmptyTableMessage show an indicator that we are fetching data. */
    public static native void setLoadingTableMessage();
    /** set Help */
    public static native void setHelp(String url);
    
    public static native void backtoCurrentActivitiesStudents();
}
