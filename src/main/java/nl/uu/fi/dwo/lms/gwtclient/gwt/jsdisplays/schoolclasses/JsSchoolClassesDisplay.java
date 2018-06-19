package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import com.google.gwt.core.client.JavaScriptObject;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * WelcomeDisplay UI interface. the interface should be
 * available as a JavaScript object named "jsSchoolClassesDisplay".
 * 
 * The callbacks are:
 * 
 *  public void addSchoolClass(); //switches to addSchoolClass panel
 *  public void editSchoolClass(String key); switches to the panel.
 *  public void editStudents(String key); switches to the panel.
 *  public void editTeachers(String key); switches to the panel.
 *  public void removeSchoolClass(String key); switches to the panel.
 * 
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "jsSchoolClassesDisplay", namespace = JsPackage.GLOBAL)
//@JsType(isNative = false, namespace = JsPackage.GLOBAL)
public class JsSchoolClassesDisplay{
    /** Clears the list of school classes in the ui. */
    public static native void clear();
    public static native void setHelp(String url);
    /** Initialize the ui to the default state with an empty list of school classes. */
    public static native void init();
    /** Fills the school classes view with the list of school classes. It requires a map
     * with a string as a key, and an object 'ClassItem'. ClassItem contains two string values
     * key and schoolclassName.
     * @param data a map with a string as key and value ClassItem. */
//    public static native void updateView(Map<String, SchoolclassesPresenter.ClassItem> data);    
    public static native void updateView(JavaScriptObject data);    
        /** setEmptyTableMessage show an indicator that the table is empty.
     */
    public static native void setEmptyTableMessage();
    /** setEmptyTableMessage show an indicator that we are fetching data.
     */
    public static native void setLoadingTableMessage();
}
