package nl.uu.fi.dwo.lms.gwtclient.gwt.ui;

import com.google.gwt.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.ui.ProgressDialogPresenter;

/**
 * Subclassing for project.
 *
 * @author plas0006
 */
public class ProgressDialogWithAbortPresenter extends ProgressDialogPresenter {

    public ProgressDialogWithAbortPresenter(EventBus anEventBus) {
        super(anEventBus);
    }
}
