package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.ui;

import fi.dwo.gwt.lib.rest.ui.MsgClickedDialogPresenter;

/**
 * Mapper to allow java interface implementation.
 *
 * @author G.A.J. van der Plas
 */
public class JsClickedDialogView implements MsgClickedDialogPresenter.Display {

    @Override
    public void clear() {
        JsClickedDialogDisplay.clear();
    }

    @Override
    public void init() {
        JsClickedDialogDisplay.init();
    }

    @Override
    public void showDialog(String text) {
        JsClickedDialogDisplay.showDialog(text);
    }

    @Override
    public void hideDialog() {
        JsClickedDialogDisplay.hideDialog();
    }

}
