package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.ui;

import fi.dwo.gwt.lib.rest.ui.MsgDialogPresenter;

/**
 * Mapper to allow java interface implementation.
 *
 * @author G.A.J. van der Plas
 */
public class JsMsgDialogView implements MsgDialogPresenter.Display {

    @Override
    public void clear() {
        JsMsgDialogDisplay.clear();
    }

    @Override
    public void init() {
        JsMsgDialogDisplay.init();
    }

    @Override
    public void showDialog(String text) {
        JsMsgDialogDisplay.showDialog(text);
    }

    @Override
    public void hideDialog() {
        JsMsgDialogDisplay.hideDialog();
    }

}
