package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.persons;

import com.google.gwt.core.client.JavaScriptObject;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * JsImportPersonsDisplay UI interface.
 * 
 * @author G.A.J. van der Plas
 * 
 * Callbacks: 
 * 
 * importStudents(List<DomSingleSchoolStudent> students) 
 * importTeachers(List<DomSingleSchoolStudent> teachers) 
 * 
 */
@JsType(isNative = true, name = "jsImportPersonsDisplay", namespace = JsPackage.GLOBAL)
public class JsImportPersonsDisplay {

    /**
     * Clears all UI states
     */
    public static native void clear();
    public static native void init();
    public static native void setEmptyPeopleTableMessage();
    public static native void setLoadingPeopleTableMessage();
    public static native void setEmptySchoolClassesTableMessage();
    public static native void setLoadingSchoolClassesTableMessage();
    public static native void setHelp(String url);
    public static native void setPersonImportList(JavaScriptObject listObject);
    public static native void showSchoolClasses(JavaScriptObject mapObject);
    
}
