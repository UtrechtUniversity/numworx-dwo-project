package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.studentmodel;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * StudentModel UI interface. the interface should be
 * available as a JavaScript object named "jsTeacherStudentModelDisplay".
 * 
 * The callbacks are:
 * 
 *  
 * @author Wim van Velthoven
 */
@JsType(isNative = true, name = "jsTeacherStudentModelDisplay", namespace = JsPackage.GLOBAL)
public class JsTeacherStudentModelDisplay {
    public static native void clear();
    public static native void setHelp(String url);
    public static native void init();
}
