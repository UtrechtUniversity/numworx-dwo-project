package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.ui;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * JsAlertDialogWithOKDisplay UI interface. the interface should be
 * available as a JavaScript object named "jsAlertDialogWithConfirmDisplay".
 * There are no call-backs from the display. The dialog has an
 * OK-button, that when clicked calls the hide() function in the 
 * presenter. Desired state changes are executed after which
 * hideDialog is called. Localization of the text is done in the Presenter
 * calling the View.
 * 
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "jsAlertDialogWithConfirmDisplay", namespace = JsPackage.GLOBAL)
//@JsType(isNative = false, namespace = JsPackage.GLOBAL)
public class JsAlertDialogWithOKDisplay{
    /** clear the state of the dialog **/
    public static native void clear();
    /** Initialize the Dialog for use **/
    public static native void init();
    /** Show the dialog **/
    public static native void showDialog(String text);
    /** Hide the dialog **/
    public static native void hideDialog();
}
