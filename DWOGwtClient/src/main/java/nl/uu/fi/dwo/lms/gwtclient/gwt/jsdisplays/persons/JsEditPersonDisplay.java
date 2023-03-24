package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.persons;

import com.google.gwt.core.client.JavaScriptObject;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * JsPersonsDisplay UI interface.
 * 
 * Filtering of people happens in the ui.
 * 
 * call back: 
 * 
 * 
 * 
 * if singleSchoolStudent:
 * 
 * public void saveUser(String givenName, String insertion, String familyName, String email, String curPassword, String newPassword, String newPasswordAgain)    
 *
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "jsEditPersonDisplay", namespace = JsPackage.GLOBAL)
public class JsEditPersonDisplay {
    public static native void init();

    /**
     * Clears all UI states
     */
    public static native void clear();
    public static native void setHelp(String url);
    /** User data in case one may not change settings */
    public static native void setUser(String role, JavaScriptObject data);

    /** User data in case one may edit settings */
    public static native void setSingleSchoolStudent(JavaScriptObject data);
    
     public static native void setSchoolClasses(JavaScriptObject data);

     /**
     * setEmptyTableMessage show an indicator that the table is empty.
     */
    public static native void setEmptyTableMessage();

    /**
     * setEmptyTableMessage show an indicator that we are fetching data.
     */
    public static native void setLoadingTableMessage();

}
