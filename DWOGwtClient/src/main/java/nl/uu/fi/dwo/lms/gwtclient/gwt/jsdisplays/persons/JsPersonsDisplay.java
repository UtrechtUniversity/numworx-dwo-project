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
 * public void showTeacherList(); for updating teachers in the table. public
 * void showStudentsList(); for updating students in the table. public void
 * editPerson(String id); user wants to navigate to editPerson en edit the
 * person with given id; public void addPerson(); user wants to navigate to
 * addPerson public void importPersons(); user wants to navigate to
 * importPersons;
 *
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "jsPersonsDisplay", namespace = JsPackage.GLOBAL)
//@JsType(isNative = false, namespace = JsPackage.GLOBAL)
public class JsPersonsDisplay {
    /**
     * Clears the ui.
     */
    public static native void clear();

    public static native void setHelp(String url);

    /**
     * Initializes the ui, puts all students in the list.
     */
    public static native void init();

    /**
     * Fills the list view with the list of persons. It requires a JSONObject
     * with each field the item key, and a converted DomUser as value.
     *
     * @param data a map with a string as key.
     */
    public static native void showPersons(JavaScriptObject data);

    /**
     * setEmptyTableMessage show an indicator that the table is empty.
     */
    public static native void setEmptyTableMessage();

    /**
     * setEmptyTableMessage show an indicator that we are fetching data.
     */
    public static native void setLoadingTableMessage();

}
