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
@JsType(isNative = true, name = "jsTeacherStudentModelDisplay", namespace = JsPackage.GLOBAL)
class JsTeacherStudentModelDisplay {
    static native void clear();
    static native void setHelp(String url);
    static native void init();
    static native void showSchoolClasses(JavaScriptObject data);
	static native void showModels(JavaScriptObject data);
	static native void showTree(JavaScriptObject javaScriptObject);
	static native String getTreeId();
	static native String getDescriptionId();
	static native void setTitle(String title);
	static native void setMethodLabel(String label);
	native static void setModelSelect(String id);
	static native boolean isMethod();
}
