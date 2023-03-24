package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import com.google.gwt.core.client.JavaScriptObject;
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
 * Buttons actions: 
 * 
 * To copy or move a student to a class
 * 
 *   public void copyOrMoveStudents()
 * 
 * To add a student to a class
 * 
 *   public void connectStudents()
 * 
 * To add a teacher to a class
 * 
 *   public void connectTeachers()
 * 
 * To edit modules to a class
 *   public void editModules()
 * 
 * To remove a school class
 * 
 *   public void removeSchoolClass()
 * 
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "jsStudentSchoolclassDisplay", namespace = JsPackage.GLOBAL)
public class JsStudentSchoolclassDisplay{
    /** Clears all the edit schoolclass fields of their values. */
    public static native void clear();
    public static native void setHelp(String url);    
    /** Clears all the student schoolclass  field of its value. */
    public static native void init();
 
    public static native void setLoadingTableMessage();
    public static native void setEmptyTableMessage();

    public static native void setSchoolClasses(JavaScriptObject data);
    public static native void showSchoolClasses(JavaScriptObject data);

}
