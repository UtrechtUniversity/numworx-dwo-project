package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import com.google.gwt.core.client.JavaScriptObject;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * AddStudentDisplay UI interface. the interface should be
 * available as a JavaScript object named "JsAddStudentsDisplay".
 * Callbacks from the MainDisplay UI to the presenter are:
 * 
 * public void addNewStudent(AddStudentsPresenter.StudentItem item);//Exceptions are thrown to dialogs.
 * 
 * Functions to verify input can be provide.
 * 
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "JsAddStudentsDisplay", namespace = JsPackage.GLOBAL)
public class JsAddStudentsDisplay{
    /** Clears all the account fields of their values. */
    public static native void clear();
    /** Clears the password field of its value. */
    public static native void init();
    /** Clears username and password fields of values. */
    public static native void updateView(JavaScriptObject data);
}
