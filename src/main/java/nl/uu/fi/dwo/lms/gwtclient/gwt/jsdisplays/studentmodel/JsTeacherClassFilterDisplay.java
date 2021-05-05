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
@JsType(isNative = true, name = "jsTeacherClassFilterDisplay", namespace = JsPackage.GLOBAL)
public class JsTeacherClassFilterDisplay {
    static native void clear();
    static native void setHelp(String url);
    static native void init();
    public static native String getId();
}
