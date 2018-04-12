package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import com.google.gwt.json.client.JSONValue;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * EditSchoolclassDisplay UI interface. the interface should be
 * available as a JavaScript object named "JsEditSchoolclassDisplay".
 * 
 * There are two relevant presenter functions.
 * 
 * To update a school class:
 * 
 *   public void updateAndRefresh(String name, Boolean showTree, Boolean hasRegKey, String regKey) 
 * 
 * To remove a school class
 * 
 *   public void removeSchoolClass()
 * 
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "JsEditSchoolclassDisplay", namespace = JsPackage.GLOBAL)
public class JsEditSchoolclassDisplay{
    /** Clears all the edit schoolclass fields of their values. */
    public static native void clear();
    /** Clears all the edit schoolclass  field of its value. */
    public static native void init();
    /**  Sets the values for the schoolclass */
    public static native void showSchoolClass(JSONValue schoolClass);
}
