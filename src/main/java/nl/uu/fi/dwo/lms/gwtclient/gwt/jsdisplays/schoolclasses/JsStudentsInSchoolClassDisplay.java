package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import com.google.gwt.core.client.JavaScriptObject;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * WelcomeDisplay UI interface. the interface should be available as a
 * JavaScript object named "JsStudentsInSchoolClassDisplay".
 *
 * The callback is
 *
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "JsStudentsInSchoolClassDisplay", namespace = JsPackage.GLOBAL)
//@JsType(isNative = false, namespace = JsPackage.GLOBAL)
public class JsStudentsInSchoolClassDisplay {

    /**
     * Clears the list of students in the ui.
     */
    public static native void clear();

    /**
     * Initialize the ui to the default state with an empty list of students.
     */
    public static native void init();

    /**
     * Fills the list view with the list of teachers. It requires a JSONObject
     * with each field the item key, and a String for the student name as value.
     *
     * @param data a map with a string as key and value the full student name.
     */
     public static native void updateView(JavaScriptObject data);

    /**
     * setEmptyTableMessage show an indicator that the table is empty.
     */
    public static native void setEmptyTableMessage();

    /**
     * setEmptyTableMessage show an indicator that we are fetching data.
     */
    public static native void setLoadingTableMessage();
}
