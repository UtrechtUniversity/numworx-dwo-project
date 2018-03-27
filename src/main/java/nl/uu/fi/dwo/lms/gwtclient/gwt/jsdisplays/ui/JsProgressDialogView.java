package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.ui;

import fi.dwo.gwt.lib.rest.ui.ProgressDialogPresenter;

/**
 * Mapper to allow java interface implementation.
 *
 * @author G.A.J. van der Plas
 */
public class JsProgressDialogView implements ProgressDialogPresenter.Display {

    @Override
    public void clear() {
        JsProgressDialogDisplay.clear();
    }

    @Override
    public void init() {
        JsProgressDialogDisplay.init();
    }

    @Override
    public void showDialog(String text) {
        JsProgressDialogDisplay.showDialog(text);
    }

    @Override
    public void hideDialog() {
        JsProgressDialogDisplay.hideDialog();
    }

    @Override
    public void updateDialog(int progress, String actMsg) {
        JsProgressDialogDisplay.updateDialog(progress, actMsg);

    }

}
