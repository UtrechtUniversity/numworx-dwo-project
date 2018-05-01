package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.ui;

import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.MessageDialogWithOKPresenter;

/**
 * Mapper to allow java interface implementation.
 *
 * @author G.A.J. van der Plas
 */
public class JsMessageDialogWithOKView implements MessageDialogWithOKPresenter.Display {

    @Override
    public void clear() {
        JsMessageDialogWithOKDisplay.clear();
    }

    @Override
    public void init() {
        JsMessageDialogWithOKDisplay.init();
    }

    @Override
    public void showDialog(String text) {
        JsMessageDialogWithOKDisplay.showDialog(text);
    }

    @Override
    public void hideDialog() {
        JsMessageDialogWithOKDisplay.hideDialog();
    }

}
