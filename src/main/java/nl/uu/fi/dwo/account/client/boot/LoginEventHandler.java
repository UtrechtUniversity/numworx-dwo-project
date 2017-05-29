package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.event.shared.EventHandler;

/**
 *
 * @author Gert van der Plas
 */
public interface LoginEventHandler extends EventHandler {
    void onLoginEvent(LoginEvent loginEvent);
}

