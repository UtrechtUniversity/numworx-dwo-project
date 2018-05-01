package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.ui;

import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKPresenter;

/**
 * Mapper to allow java interface implementation.
 *
 * @author G.A.J. van der Plas
 */
public class JsAlertDialogWithOKView implements AlertDialogWithOKPresenter.Display {

    @Override
    public void clear() {
        JsAlertDialogWithOKDisplay.clear();
    }

    @Override
    public void init() {
        JsAlertDialogWithOKDisplay.init();
    }

    @Override
    public void showDialog(String text) {
        JsAlertDialogWithOKDisplay.showDialog(text);
    }

    @Override
    public void hideDialog() {
        JsAlertDialogWithOKDisplay.hideDialog();
    }

}
