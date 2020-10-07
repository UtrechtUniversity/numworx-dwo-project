package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.organisation;

import com.google.gwt.core.client.JavaScriptObject;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, name = "jsOrganisationDisplay", namespace = JsPackage.GLOBAL)
public class JsOrganisationDisplay {

  private JsOrganisationDisplay() {
  }
  /**
   * Clears all UI states
   */
  public static native void clear();

  public static native void setHelp(String url);
  /**
   * Initializes the ui, puts all students in the list.
   */
  public static native void init();
  /**
   * Fills the list view with the list of persons. It requires a JSONObject
   * with each field the item key, and a converted TaggedDomUser as value.
   *
   * @param data a map with a string as key.
   */
  public static native void showPersons(JavaScriptObject data, String role);

  /**
   * setEmptyTableMessage show an indicator that the table is empty.
   */
  public static native void setEmptyTableMessage();

  /**
   * setLoadingTableMessage show an indicator that we are fetching data.
   */
  public static native void setLoadingTableMessage();

  /**
   * init "editModules"
   */
  public static native void initEditModules(boolean on, boolean access, boolean premium);

  /**
   * init "chooseClass"
   */
  public static native void initChooseClass(boolean on);
  
  /**
   * show schoolClassess
   */
  public static native void showSchoolClasses(JavaScriptObject json);
  
}
