package nl.uu.fi.dwo.lms.gwtclient.gwt.ui;

import com.google.web.bindery.event.shared.EventBus;
import java.util.logging.Level;
import java.util.logging.Logger;
import jsinterop.annotations.JsMethod;

/**
 * Subclassing for project.
 * 
 * @author plas0006
 */
public class AlertDialogWithConfirmCancelPresenter implements AlertDialogWithConfirmCancelEventHandler{

    private static final Logger LOG = Logger.getLogger(AlertDialogWithConfirmCancelPresenter.class.getName());
    
    private EventBus eventBus;
    private Display view;
    private AlertDialogWithConfirmCancelPromise promise;

    public interface Display {

        void clear();
        void init();
        void showDialog(String text);
        void hideDialog();
    }

    public AlertDialogWithConfirmCancelPresenter(EventBus anEventBus) {
        eventBus = anEventBus;
        eventBus.addHandler(AlertDialogWithConfirmCancelEvent.TYPE, this);
    }

    @Override
    public void onDialogEvent(AlertDialogWithConfirmCancelEvent aDialogEvent) {
        LOG.log(Level.INFO,"AlertDialogWithConfirmCancelEvent does: "+aDialogEvent.getEventValue());

        if (aDialogEvent.getEventValue()==AlertDialogWithConfirmCancelEvent.EventType.ConfirmDialog){
            promise = aDialogEvent.getPromise();
            view.showDialog(promise.getMsg());
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
    
    @JsMethod
    public void confirm(){
        view.hideDialog();
        promise.resolve(Boolean.TRUE);
    }

    @JsMethod
    public void cancel(){
        view.hideDialog();
        promise.resolve(Boolean.FALSE);       
    }    
}
