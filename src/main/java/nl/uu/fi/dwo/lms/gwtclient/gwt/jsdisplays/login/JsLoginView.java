package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.login;

import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginPresenter;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsLoginView implements LoginPresenter.Display{

    @Override
    public void clear(){
        JsLoginDisplay.clear();
    }
    @Override
    public void setUsername(String username){
        JsLoginDisplay.setUsername(username);
    }
    @Override
    public void setPassword(String password){
        JsLoginDisplay.setPassword(password);
    }

    @Override
    public void showMessage(String message) {
         JsLoginDisplay.showMessage(message);
    }

    @Override
    public void showWarning(String warning) {
         JsLoginDisplay.showWarning(warning);
    }

    @Override
    public void hideMsgBox() {
         JsLoginDisplay.hideMsgBox();
    }
}
