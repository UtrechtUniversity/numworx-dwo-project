package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.ui;

import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelPresenter;

/**
 * Mapper to allow java interface implementation.
 *
 * @author G.A.J. van der Plas
 */
public class JsAlertDialogWithConfirmCancelView implements AlertDialogWithConfirmCancelPresenter.Display {

    @Override
    public void clear() {
        JsAlertDialogWithConfirmCancelDisplay.clear();
    }

    @Override
    public void init() {
        JsAlertDialogWithConfirmCancelDisplay.init();
    }

    @Override
    public void showDialog(String text, String ok, String cancel) {
        JsAlertDialogWithConfirmCancelDisplay.showDialog(text, ok, cancel);
    }

    @Override
    public void hideDialog() {
        JsAlertDialogWithConfirmCancelDisplay.hideDialog();
    }

}
