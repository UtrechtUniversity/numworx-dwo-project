package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.persons;

import com.google.gwt.core.client.JavaScriptObject;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * JsPersonsDisplay UI interface.
 * 
 * Filtering of people happens in the ui.
 * 
 * call back: 
 * 
 * public void addPerson(); user wants to navigate to addPerson
 *
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "jsAddPersonDisplay", namespace = JsPackage.GLOBAL)
//@JsType(isNative = false, namespace = JsPackage.GLOBAL)
public class JsAddPersonDisplay {

    /**
     * Clears all UI states
     */
    public static native void clear();

    public static native void setHelp(String url);
    
    /**
     * Initializes the UI, either for the TEACHER view or the SCHOOLADMIN view. Teacher
     * view allows adding a student and selecting a single class. The schooladmin
     * view extends this to adding a teacher and selecting a single class or none (null).
     * 
     */
    public static native void init(String role);
    /**
     * Fills the list view with a list of the teacher's schoolclasses. 
     *
     * @param data a map with a string as key.
     */
     public static native void showSchoolClasses(JavaScriptObject data);

     /**
     * setEmptyTableMessage show an indicator that the table is empty.
     */
    public static native void setEmptyTableMessage();

    /**
     * setEmptyTableMessage show an indicator that we are fetching data.
     */
    public static native void setLoadingTableMessage();

}
