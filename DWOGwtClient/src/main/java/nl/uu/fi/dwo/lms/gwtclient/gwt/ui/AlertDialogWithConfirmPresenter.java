package nl.uu.fi.dwo.lms.gwtclient.gwt.ui;

import javax.inject.Inject;

import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.ui.MsgDialogPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;

/**
 * Subclassing for project.
 *
 * @author plas0006
 */
@RoleScope
public class AlertDialogWithConfirmPresenter extends MsgDialogPresenter {
    
    @Inject public AlertDialogWithConfirmPresenter(EventBus anEventBus) {
        super(anEventBus);
    }
    
}
