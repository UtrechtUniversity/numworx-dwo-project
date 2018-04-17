package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import com.google.gwt.core.client.JavaScriptObject;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * JsCopyOrMoveStudentToSchoolclassDisplay UI interface.
 *
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "JsCopyOrMoveStudentToSchoolclassDisplay", namespace = JsPackage.GLOBAL)
//@JsType(isNative = false, namespace = JsPackage.GLOBAL)
public class JsCopyOrMoveStudentToSchoolclassDisplay {

    /**
     * Clears the ui.
     */
    public static native void clear();

    /**
     * Initializes the ui, puts all students in the list.
     */
    public static native void init();

    /**
     * setEmptyTableMessage show an indicator that the table is empty.
     */
    public static native void setEmptyTableMessage();

    /**
     * setEmptyTableMessage show an indicator that we are fetching data.
     */
    public static native void setLoadingTableMessage();
    
    public static native void showStudentsClassA(JavaScriptObject data);

    public static native void showStudentsClassB(JavaScriptObject data);

}
