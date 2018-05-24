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
 * public void selectColumnZoom(int col); // Select for  tableheader col clicks (zoom in and out)
 * public void selectRowAndCol(int row, int col); // Selects for class/student result fields. rows starts with 0.
 * public String getExportString(); //Returns tsv result data to be copied to clipboard.
 * 
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "jsResultsSelectsDisplay", namespace = JsPackage.GLOBAL)
public class JsResultsSelectsDisplay{
    /** Clears the results in the ui. */
    public static native void clear();
    /** Fills the result table with the results.
     * @param data a double map with string data to show */
    public static native void plot(JavaScriptObject data, boolean zoomedClass, boolean zoomedCourse);    
    /** setEmptyTableMessage show an indicator that the table is empty. */
    public static native void setEmptyTableMessage();
    /** setEmptyTableMessage show an indicator that we are fetching data. */
    public static native void setLoadingTableMessage();
}
