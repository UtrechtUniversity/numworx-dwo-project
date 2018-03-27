package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.ui;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * ProgressDialogDisplay UI interface. the interface should be
 available as a JavaScript object named "JsProgressDialogWithAbortDisplay".
 There are no call-backs from the display. The dialog has an
 Abort-button, that when clicked calls the abort() function in the 
 presenter. Desired state changes are executed after which
 hideDialog() is called. Localization of the text is done in the Presenter
 calling the View.
 * 
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "jsProgressDialogWithAbortDisplay", namespace = JsPackage.GLOBAL)
//@JsType(isNative = false, namespace = JsPackage.GLOBAL)
public class JsProgressDialogWithAbortDisplay{
    /** clear the state of the dialog. **/
    public static native void clear();
    /** Initialize the Dialog for use. **/
    public static native void init();
    /** Show the dialog and its main message.**/
    public static native void showDialog(String text);
    /** Hide the dialog. **/
    public static native void hideDialog();
    /** Updates the dialog activity text and a progress ranging from 0-100. */
    public static native void updateDialog(int progress, String actMsg);
}
