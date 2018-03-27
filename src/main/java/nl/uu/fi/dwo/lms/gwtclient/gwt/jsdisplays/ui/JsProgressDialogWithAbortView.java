package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.ui;

import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.ProgressDialogWithAbortPresenter;

/**
 * Mapper to allow java interface implementation.
 *
 * @author G.A.J. van der Plas
 */
public class JsProgressDialogWithAbortView implements ProgressDialogWithAbortPresenter.Display {

    @Override
    public void clear() {
        JsProgressDialogWithAbortDisplay.clear();
    }

    @Override
    public void init() {
        JsProgressDialogWithAbortDisplay.init();
    }

    @Override
    public void showDialog(String text) {
        JsProgressDialogWithAbortDisplay.showDialog(text);
    }

    @Override
    public void hideDialog() {
        JsProgressDialogWithAbortDisplay.hideDialog();
    }

    @Override
    public void updateDialog(int progress, String actMsg) {
        JsProgressDialogWithAbortDisplay.updateDialog(progress, actMsg);

    }

}
