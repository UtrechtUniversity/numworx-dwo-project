package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

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
 *         LOGIN, 
 *         WELCOME, 
 *         ACCOUNT, 
 *         PEOPLE, 
 *         SCHOOLCLASSES, 
 *         EDITSCHOOLCLASS,
 *         ADDSTUDENTTOSCHOOLCLASS, 
 *         COPYORMOVESTUDENTTOSCHOOLCLASS, 
 *         ADDTEACHERTOSCHOOLCLASS, 
 *         EDITCOURSESOFSCHOOLCLASS,
 *         RESULTS, 
 *         RESULTSSTUDENT,
 *         MODULES, (Library menu)
 *         ORGANISATION
 * 
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "jsMainDisplay", namespace = JsPackage.GLOBAL)
public class JsMainDisplay {
    /** Set the school name in the header of the main panel. */
    public static native void setSchoolName(String schoolName);
    /** Set the user role in the header of the main panel, teacher, student or school admin. */
    public static native void setUserRole(String userRole);
    /** Set the user's presentation name in the header of the main panel, i.e. G.A.J. van der Plas */
    public static native void setPresentationName(String presentationName);    
    /** Sets the name of current panel shown in the main area in the header of the main panel. */
    public static native void  setCurrentPanelName();
    /** States if the account menu is visible. */
    public static native boolean isMenuVisible();
    /** Show the account panel. */
    public static native void showAccountView();
    /** Show the welcome panel. */
    public static native void showWelcomeView();
    /** Show the login panel. */
    public static native void showLoginView();
    /** Show the results panel. */
    public static native void  showResultsView();
    /** Show the selected results panel. */
    public static native void  showSelectedResultsView();
    /** Show the results of student panel. */
    public static native void  showStudentResultsView();
    /** Show the selected select the result for a student panel. */
    public static native void  showSelectStudentResultsView();
    /** Show the student sco (activity by a student) panel. */
    public static native void  showStudentScoResultView();
    /** Show the school classes panel. */
    public static native void  showSchoolclassesView();
    /** Show the edit school class panel. */
    public static native void  showEditSchoolclassView();
//    /** Show the students in school class panel. */
//    public static native void  showStudentsInSchoolclassView();
//    /** Show the teachers in school class panel. */
//    public static native void  showTeachersInSchoolclassView();
//    /** Show the teachers in school class panel. */
//    public static native void  showCoursesOfSchoolclassView();
    /** Show the add student to school class panel. */
    public static native void  showAddStudentToSchoolClassView();
    /** Show the copy or move student to school class panel. */
    public static native void  showCopyOrMoveStudentToSchoolClassView();
    /** Show the add teacher to school class panel. */
    public static native void  showAddTeacherToSchoolClassView();
    /** Show the add students panel. */
    public static native void  showEditCoursesOfSchoolClassView();
    /** Show the persons panel. */
    public static native void  showPersonsView();
    /** Show the add person panel. */
    public static native void  showAddPersonView();
    /** Show the edit person panel. */
    public static native void  showEditPersonView();
    /** Show the import persons panel. */
    public static native void  showImportPersonsView();
    /** Show the modules panel (course library). */
    public static native void  showModulesView();
    /** Show the organization panel. */
    public static native void  showOrganisationView();
    /** Show the log results panel */
    public static native void showLogResultsView();
    /** show student's schoolclass view */
    public static native void showStudentSchoolclassView();
    /** show teacher studentmodel view */
    public static native void showTeacherStudentModelView();
    
    static native String getSearchInput();
    
    static native void setTrails(JavaScriptObject javaScriptObject);
    static native void setSearchBox(boolean on);
    
    static native void selectView(String view);
    static native void setPremium(boolean set);
    
    static native void setIdleTimeout(int millis);
    static native void unsetIdleTimeout();
    
    static native void showStudentResultsGraphView();
    static native void showTeacherSMClassResultsView();
    static native void showTeacherClassFilterView();
    
    static native void showChatboxView();
}
