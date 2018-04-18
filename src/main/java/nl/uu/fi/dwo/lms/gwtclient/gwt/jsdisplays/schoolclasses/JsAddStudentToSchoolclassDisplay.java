package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import com.google.gwt.core.client.JavaScriptObject;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * JsAddStudentToSchoolclassDisplay UI interface.

 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "JsAddStudentToSchoolclassDisplay", namespace = JsPackage.GLOBAL)
//@JsType(isNative = false, namespace = JsPackage.GLOBAL)
public class JsAddStudentToSchoolclassDisplay {

    /**
     * Clears the ui.
     */
    public static native void clear();

    /**
     * Initializes the ui, puts all students in the list.
     */
    public static native void init();

    /**
     * Fills the list view with the list of teachers students. It requires a JSONObject
     * with each field the item key, and a String for the student name as value.
     *
     * @param data a map with a string as key and value the full student name.
     */
     public static native void showStudents(JavaScriptObject data);

     /**
     * setEmptyTableMessage show an indicator that the table is empty.
     */
    public static native void setEmptyTableMessageA();

    /**
     * setEmptyTableMessage show an indicator that we are fetching data.
     */
    public static native void setLoadingTableMessageA();


     /**
     * setEmptyTableMessage show an indicator that the table is empty.
     */
    public static native void setEmptyTableMessageB();

    /**
     * setEmptyTableMessage show an indicator that we are fetching data.
     */
    public static native void setLoadingTableMessageB();
}
