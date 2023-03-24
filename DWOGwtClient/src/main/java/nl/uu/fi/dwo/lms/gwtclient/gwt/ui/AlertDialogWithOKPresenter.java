package nl.uu.fi.dwo.lms.gwtclient.gwt.ui;

import com.google.web.bindery.event.shared.EventBus;
import java.util.logging.Logger;

import javax.inject.Inject;

import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;

/**
 * Subclassing for project.
 *
 * @author plas0006
 */
@RoleScope
public class AlertDialogWithOKPresenter  implements AlertDialogWithOKEventHandler {

    private static final Logger LOG = Logger.getLogger(AlertDialogWithOKPresenter.class.getName());
    private EventBus eventBus;
    private Display view;

    public interface Display {
        void clear();

        void init();

        void showDialog(String text);
        void hideDialog();
    }

    @Inject AlertDialogWithOKPresenter(EventBus anEventBus) {
        eventBus = anEventBus;
        eventBus.addHandler(AlertDialogWithOKEvent.TYPE, this);

    }

    private AlertDialogWithOKEvent last;
 
    @Override
    public void onDialogEvent(AlertDialogWithOKEvent aDialogEvent) {
    	last = aDialogEvent;
        if (aDialogEvent.getEventValue()==AlertDialogWithOKEvent.Dialogs.Message) {
            view.showDialog(aDialogEvent.getMessage());
        }else if (aDialogEvent.getEventValue()==AlertDialogWithOKEvent.Dialogs.Dwo2ExceptionDialog){
            view.showDialog(aDialogEvent.getException().getLocalizedCodeExplanation(null));
        }
    }

    public void init() {

    }

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

    /**
     * @param view the view to set
     */
    public Display getView() {
        return view;
    }
    
    @JsMethod
    public void confirm() {
        hide();
    }
    
    @JsMethod
    public void hide() {
        view.hideDialog();
        last.callback();
    }
}
