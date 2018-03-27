package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.ui;

import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.MessageDialogWithConfirmPresenter;

/**
 * Mapper to allow java interface implementation.
 *
 * @author G.A.J. van der Plas
 */
public class JsMessageDialogWithConfirmView implements MessageDialogWithConfirmPresenter.Display {

    @Override
    public void clear() {
        JsMessageDialogWithConfirmDisplay.clear();
    }

    @Override
    public void init() {
        JsMessageDialogWithConfirmDisplay.init();
    }

    @Override
    public void showDialog(String text) {
        JsMessageDialogWithConfirmDisplay.showDialog(text);
    }

    @Override
    public void hideDialog() {
        JsMessageDialogWithConfirmDisplay.hideDialog();
    }

}
