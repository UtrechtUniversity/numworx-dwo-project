package nl.uu.fi.dwo.mobile.client.ui;

import com.google.gwt.event.shared.EventHandler;

public interface MessageEventHandler extends EventHandler {
    void onMessage(MessageEvent event);
}
