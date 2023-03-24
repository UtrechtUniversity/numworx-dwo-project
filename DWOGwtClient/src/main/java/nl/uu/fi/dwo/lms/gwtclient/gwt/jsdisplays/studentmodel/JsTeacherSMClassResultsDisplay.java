package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.studentmodel;

import com.google.gwt.core.client.JavaScriptObject;

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
@JsType(isNative = true, name = "jsTeacherSMClassResultsDisplay", namespace = JsPackage.GLOBAL)
class JsTeacherSMClassResultsDisplay {
    static native void clear();
    static native void setHelp(String url);
    static native void init();
    static native void showSchoolclasses(JavaScriptObject data);
	static native void setTitle(String title);
	native static String getTreeId();
	native static void setScore(JavaScriptObject students, JavaScriptObject gemiddelde);
	static native void setMethodLabel(String label);
    static native boolean isMethod();
}
