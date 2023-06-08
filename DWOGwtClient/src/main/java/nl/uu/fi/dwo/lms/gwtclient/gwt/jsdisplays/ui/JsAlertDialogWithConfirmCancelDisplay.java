package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.ui;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * JsAlertDialogWithConfirmCancelDisplay UI interface. the interface should be
 * available as a JavaScript object named "JsAlertDialogWithConfirmCancelDisplay".
 * There are no call-backs from the display. The dialog has an
 * OK and CANCEL-button, that when clicked calls the hide() function in the 
 * presenter. Desired state changes are executed after which
 * hideDialog is called. Localization of the text is done in the Presenter
 * calling the View.
 * 
 * The viewer calls MsgConfirmDialogPresenter.cancel() on CANCEL and
 * MsgConfirmDialogPresenter.confirm() on OK.
 * 
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name = "jsAlertDialogWithConfirmCancelDisplay", namespace = JsPackage.GLOBAL)
public class JsAlertDialogWithConfirmCancelDisplay{
    /** clear the state of the dialog **/
    public static native void clear();
    /** Initialize the Dialog for use **/
    public static native void init();
    /** Show the dialog 
     * @param text the message
     * @param cancel "cancel"
     * @param ok  "ok" **/
    public static native void showDialog(String text, String ok, String cancel);
    /** Hide the dialog **/
    public static native void hideDialog();
}
