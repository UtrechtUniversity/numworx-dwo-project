package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONValue;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * JsAddTeacherToSchoolclassDisplay UI interface.
 * 
 * Filtering of teacher happens in the ui.
 * 
 * call back: 
 * 
 * public void AddTeacherToSchoolClass(String teacherId);
 * 
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "jsAddTeacherToSchoolclassDisplay", namespace = JsPackage.GLOBAL)
//@JsType(isNative = false, namespace = JsPackage.GLOBAL)
public class JsAddTeacherToSchoolclassDisplay {

    /**
     * Clears the ui.
     */
    public static native void clear();
    public static native void setHelp(String url);

    /**
     * Initializes the ui, puts all students in the list.
     */
    public static native void init();
    
    public static native void setSchoolClass(JSONValue schoolClass);

    /**
     * Fills the list view with the list of teachers students. It requires a JSONObject
     * with each field the item key, and a String for the student name as value.
     *
     * @param data a map with a string as key and value the full student name.
     */
     public static native void showTeachers(JavaScriptObject data);

             /**
     * setEmptyTableMessage show an indicator that the table is empty.
     */
    public static native void setEmptyTableMessage();

    /**
     * setEmptyTableMessage show an indicator that we are fetching data.
     */
    public static native void setLoadingTableMessage();
}
