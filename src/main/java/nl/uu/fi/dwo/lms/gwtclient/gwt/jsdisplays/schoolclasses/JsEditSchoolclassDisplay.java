package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * EditSchoolclassDisplay UI interface. the interface should be
 * available as a JavaScript object named "JsEditSchoolclassDisplay".
 * 
 * There are two relevant presenter functions.
 * 
 * To add a new school class:
 * 
 * public void AddAndBack(String name, Boolean showTree, Boolean hasRegKey, String regKey) {
 * 
 * and to go back without adding a school class.
 * 
 * public void Back();
 * 
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "JsEditSchoolclassDisplay", namespace = JsPackage.GLOBAL)
public class JsEditSchoolclassDisplay{
    /** Clears all the edit schoolclass fields of their values. */
    public static native void clear();
    /** Clears all the edit schoolclass  field of its value. */
    public static native void init();
    /**  Sets the values for the dialog */
    public static native void showDialog(String name, Boolean showTree, Boolean hasRegKey, String regKey);
}
