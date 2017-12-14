package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * AddSchoolclassDisplay UI interface. the interface should be
 * available as a JavaScript object named "jsAddSchoolclassDisplay".
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
@JsType(isNative = true, name = "jsAccountDisplay", namespace = JsPackage.GLOBAL)
public class JsAddSchoolclassDisplay{
    /** Clears all the add schoolclass fields of their values. */
    public static native void clear();
    /** Clears all the add schoolclass  field of its value. */
    public static native void init();
 
}
