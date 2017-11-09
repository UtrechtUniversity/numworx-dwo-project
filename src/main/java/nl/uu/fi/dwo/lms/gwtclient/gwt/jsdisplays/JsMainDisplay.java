package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * MainDisplay UI interface. the interface should be
 * available as a JavaScript object named "jsMainDisplay".
 * Callback from the MainDisplay UI to the presenter occurs
 * via 
 * 
 * there are two presenter functions relevant. 
 * 
 * public void selectView(String selectedView);
 * with selected view one of 
 * 
 *      LOGIN,
 *      ACCOUNT,
 *      SWITCHSCHOOL,
 *      RESULTS,
 *      ACTIVERESULTS,
 *      SCORESULTS,
 *      SCHOOLCLASSES,
 *      COURSESOFSCHOOLCLASS,
 *      STUDENTSINSCHOOLCLASS,
 *      TEACHERSINSCHOOLCLASS,
 *      ADDSTUDENTS
 * 
 * and
 * 
 * public void menuButtonClicked();
 * 
 * Which shows and hides the Account popup menu. Selecting the popup menu option
 * edit account calls showAccountView(). 
 * 
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "jsMainDisplay", namespace = JsPackage.GLOBAL)
public class JsMainDisplay {
    /** Hide the account menu. */
    public static native void hideMenu();
    /** Show the account menu. */
    public static native void showMenu();
    /** Show the login panel. */
    public static native void showLoginView();
    /** Show the welcome panel. */
    public static native void showWelcomeView();
    /** Show the account panel. */
    public static native void showAccountView();
    /** Show the results panel. */
    public static native void  showResultsView();
    /** Show the school classes panel. */
    public static native void  showSchoolclassesView();
    /** Show the switch school panel. */
    public static native void  showSwitchSchoolView();
    /** Set the school name in the header of the main panel. */
    public static native void setSchoolName(String schoolName);
    /** Set the user role in the header of the main panel, teacher, student or school admin. */
    public static native void setUserRole(String userRole);
    /** Set the user's presentation name in the header of the main panel, i.e. G.A.J. van der Plas */
    public static native void setPresentationName(String presentationName);    
    /** States if the account menu is visible. */
    public static native boolean isMenuVisible();
    /** Turns the school name, user role and user presentation name visible in the header panel. */
    public static native void showPostLoginWidgets();
    /** Turns the school name, user role and user presentation name invisible in the header panel. */
    public static native void hidePostLoginWidgets();
    /** Sets the name of current panel shown in the main area in the header of the main panel. */
    public static native void  setCurrentPanelName();
    /** Show the account menu. */
    public static native void  showMenuView();
    /** Hide the account menu. */    
    public static native void  hideMenuView();
}
