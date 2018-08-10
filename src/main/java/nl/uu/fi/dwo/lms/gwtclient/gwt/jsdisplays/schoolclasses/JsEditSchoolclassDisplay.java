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
@JsType(isNative = true, name = "jsEditSchoolclassDisplay", namespace = JsPackage.GLOBAL)
public class JsEditSchoolclassDisplay{
    /** Clears all the edit schoolclass fields of their values. */
    public static native void clear();
    public static native void setHelp(String url);    
    /** Clears all the edit schoolclass  field of its value. */
    public static native void init();
    /**  Sets the values for the schoolclass */
    public static native void showSchoolClass(JavaScriptObject schoolClass);
    /**  Shows a list of students */
    public static native void showStudents(JavaScriptObject schoolClass);
    public static native void setEmptyStudentTableMessage();
    public static native void setLoadingStudentTableMessage();
    /**  Shows a list of teachers */
    public static native void showTeachers(JavaScriptObject schoolClass);
    public static native void setEmptyTeacherTableMessage();
    public static native void setLoadingTeacherTableMessage();
    /**  Shows a list of modules */    
    public static native void showShowModels(JavaScriptObject schoolClass);
    public static native void setEmptyModulesTableMessage();
    public static native void setLoadingModulesTableMessage();
    
}
