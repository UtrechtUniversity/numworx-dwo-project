package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.account;

import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountPresenter;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsAccountView implements AccountPresenter.Display{
    @Override
    public void clear() {
        JsAccountDisplay.clear();
    }

    @Override
    public void init() {
        JsAccountDisplay.init();
    }

    @Override
    public void updateView(String username, String firstName, String insertion, String familyName, String email) {
        JsAccountDisplay.updateView(username, firstName, insertion, familyName, email);
    }


}
