package nl.uu.fi.dwo.lms.gwtclient.gwt.ui;

import com.google.gwt.event.shared.EventHandler;

/**
 *
 * @author Gert van der Plas
 */
public interface ProgressDialogWithAbortEventHandler extends EventHandler {
    void onDialogEvent(ProgressDialogWithAbortEvent dialogEvent);
}

