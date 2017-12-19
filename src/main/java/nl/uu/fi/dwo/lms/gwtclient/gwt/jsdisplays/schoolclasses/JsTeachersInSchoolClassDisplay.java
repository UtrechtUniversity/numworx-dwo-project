package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import com.google.gwt.core.client.JavaScriptObject;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * WelcomeDisplay UI interface. the interface should be available as a
 * JavaScript object named "JsTeachersInSchoolClassDisplay".
 *
 * The callback is
 *
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "JsTeachersInSchoolClassDisplay", namespace = JsPackage.GLOBAL)
//@JsType(isNative = false, namespace = JsPackage.GLOBAL)
public class JsTeachersInSchoolClassDisplay {

    /**
     * Clears the list of teachers in the ui.
     */
    public static native void clear();

    /**
     * Initialize the ui to the default state with an empty list of teachers.
     */
    public static native void init();

    /**
     * Fills the list view with the list of teachers. It requires a JSONObject
     * with each field the item key, and a String for the teacher name as value.
     *
     * @param data a map with a string as key and value the full teacher name.
     */
    public static native void updateView(JavaScriptObject data);

    /**
     * Provides a list of teachers in the school that may be assigned to this 
     * schoolclass. It requires a JSONObject with each field the item key, and a
     * String for the teacher name as value.
     *
     * @param data a map with a string as key and value the full teacher name.
     */
    public static native void updateTeacherList(JavaScriptObject data);

    /**
     * setEmptyTableMessage show an indicator that the table is empty.
     */
    public static native void setEmptyTableMessage();

    /**
     * setEmptyTableMessage show an indicator that we are fetching data.
     */
    public static native void setLoadingTableMessage();
}
