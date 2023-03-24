package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONValue;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * JsCopyOrMoveStudentToSchoolclassDisplay UI interface.
 *
 * Call backs, strings for students are userId's, strings for classes are classId's:
 * 
 * public void SelectClassB(String classId)
 * public void CopyStudentsToClassA(List<String> idList)
 * public void CopyStudentsToClassB(List<String> idList)
 * public void MoveStudentsToClassA(List<String> idList)
 * public void MoveStudentsToClassB(List<String> idList)
 * 
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "jsCopyOrMoveStudentToSchoolclassDisplay", namespace = JsPackage.GLOBAL)
//@JsType(isNative = false, namespace = JsPackage.GLOBAL)
public class JsCopyOrMoveStudentToSchoolclassDisplay {

    /**
     * Clears the ui.
     */
    public static native void clear();
    public static native void setHelp(String url);
    /**
     * Initializes the ui, puts all students in the list.
     */
    public static native void init();

    public static native void setSchoolClassA(JavaScriptObject schoolClass);

    public static native void setSchoolClassB(JavaScriptObject schoolClass);

    public static native void setEmptyTableMessageClasses();
    
    public static native void setLoadingTableMessageClasses();
    /**
     * setEmptyTableMessage show an indicator that the table is empty.
     */
    public static native void setEmptyTableMessageA();

    /**
     * setEmptyTableMessage show an indicator that we are fetching data.
     */
    public static native void setLoadingTableMessageA();
    
    /**
     * setEmptyTableMessage show an indicator that the table is empty.
     */
    public static native void setEmptyTableMessageB();

    /**
     * setEmptyTableMessage show an indicator that we are fetching data.
     */
    public static native void setLoadingTableMessageB();

    public static native void showStudentsClassA(JavaScriptObject data);

    public static native void showStudentsClassB(JavaScriptObject data);
    
    public static native void setClassList(JavaScriptObject data);

}
