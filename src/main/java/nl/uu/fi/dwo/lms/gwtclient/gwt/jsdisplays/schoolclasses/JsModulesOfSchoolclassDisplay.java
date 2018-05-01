package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONValue;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * WelcomeDisplay UI interface. the interface should be
 * available as a JavaScript object named "jsModulesOfSchoolclassDisplay".
 * 
 * The callbacks are:
 * 
 * TODO
 * 
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "jsModulesOfSchoolclassDisplay", namespace = JsPackage.GLOBAL)
//@JsType(isNative = false, namespace = JsPackage.GLOBAL)
/**
 * Op dit moment is de implementatie nog onzeker. De vraag is of de setTree goed overkomt
 * Zo ja dan kan de classCourseItem worden aangepast.
 * 
 * void setSelectedItem(ClassCourseItem item) 
 * void setModuleSettings(String key, String typeString, String fromData, String toData, String accessKey)
 * void detachItemFromSchoolClass(ClassCourseItem classCourseItem)
 * void attachItemToSchoolClass(ClassCourseItem classCourseItem)
 * 
 * 
 */
public class JsModulesOfSchoolclassDisplay{
    public static native void clear();
    public static native void init();
    public static native void setEmptyTableMessageModules();
    public static native void setLoadingTableMessageModules();
    public static native void setEmptyTableMessageSelected();
    public static native void setLoadingTableMessageSelected();
    public static native void updateTable(JavaScriptObject jsObject);
    public static native void setTree(JSONValue jsNode);
}
