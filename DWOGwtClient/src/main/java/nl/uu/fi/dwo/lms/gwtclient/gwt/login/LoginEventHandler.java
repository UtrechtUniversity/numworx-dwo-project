package nl.uu.fi.dwo.lms.gwtclient.gwt.login;

import com.google.gwt.event.shared.EventHandler;

/**
 *
 * @author Gert van der Plas
 */
public interface LoginEventHandler extends EventHandler {
    void onLoginEvent(LoginEvent loginEvent);
}

