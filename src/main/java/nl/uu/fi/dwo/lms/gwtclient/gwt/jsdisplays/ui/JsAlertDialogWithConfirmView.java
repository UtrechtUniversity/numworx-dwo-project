package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.ui;

import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmPresenter;

/**
 * Mapper to allow java interface implementation.
 *
 * @author G.A.J. van der Plas
 */
public class JsAlertDialogWithConfirmView implements AlertDialogWithConfirmPresenter.Display {

    @Override
    public void clear() {
        JsAlertDialogWithConfirmDisplay.clear();
    }

    @Override
    public void init() {
        JsAlertDialogWithConfirmDisplay.init();
    }

    @Override
    public void showDialog(String text) {
        JsAlertDialogWithConfirmDisplay.showDialog(text);
    }

    @Override
    public void hideDialog() {
        JsAlertDialogWithConfirmDisplay.hideDialog();
    }

}
