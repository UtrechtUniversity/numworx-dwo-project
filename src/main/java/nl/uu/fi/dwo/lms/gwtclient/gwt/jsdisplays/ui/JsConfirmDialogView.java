package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.ui;

import fi.dwo.gwt.lib.rest.ui.MsgConfirmDialogPresenter;

/**
 * Mapper to allow java interface implementation.
 *
 * @author G.A.J. van der Plas
 */
public class JsConfirmDialogView implements MsgConfirmDialogPresenter.Display {

    @Override
    public void clear() {
        JsConfirmDialogDisplay.clear();
    }

    @Override
    public void init() {
        JsConfirmDialogDisplay.init();
    }

    @Override
    public void showDialog(String text) {
        JsConfirmDialogDisplay.showDialog(text);
    }

    @Override
    public void hideDialog() {
        JsConfirmDialogDisplay.hideDialog();
    }

}
