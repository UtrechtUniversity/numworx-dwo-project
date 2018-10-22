package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results;

import com.google.gwt.core.client.JavaScriptObject;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * StudentScoResult UI interface. The interface should be
 * available as a JavaScript object named "jsStudentScoResultDisplay".
 * 
 * @author W.P.G. van Velthoven
 */
@JsType(isNative = true, name = "jsLogResultsDisplay", namespace = JsPackage.GLOBAL)
public class JsLogResultsDisplay{
     public static native void init0();
   /** Clears the results in the ui. */
    public static native void clear();
    public static native void setHelp(String url);
    /** Inits the view */
    public static native void init(JavaScriptObject resultState);
    /** Fills the result table with the results.*/
     public static native void updateResultTree(JavaScriptObject resultsTree, JavaScriptObject studentsTree);    
    /** setEmptyTableMessage show an indicator that the table is empty. */
    public static native void setEmptyTableMessage();
    /** setEmptyTableMessage show an indicator that we are fetching data. */
    public static native void setLoadingTableMessage();
    /** load frame with player */
    public static native void openUrl(String url);
    public static native void hide();
}
