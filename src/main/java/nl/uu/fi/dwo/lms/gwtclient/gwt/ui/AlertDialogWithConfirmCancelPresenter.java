package nl.uu.fi.dwo.lms.gwtclient.gwt.ui;

import com.google.web.bindery.event.shared.EventBus;
import java.util.logging.Level;
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
public class AlertDialogWithConfirmCancelPresenter implements AlertDialogWithConfirmCancelEventHandler{

    private static final Logger LOG = Logger.getLogger(AlertDialogWithConfirmCancelPresenter.class.getName());
    
    private EventBus eventBus;
    private Display view;
    private AlertDialogWithConfirmCancelDeferred deferred;

    public interface Display {

        void clear();
        void init();
        void showDialog(String text);
        void hideDialog();
    }

    @Inject public AlertDialogWithConfirmCancelPresenter(EventBus anEventBus) {
        eventBus = anEventBus;
        eventBus.addHandler(AlertDialogWithConfirmCancelEvent.TYPE, this);
    }

    @Override
    public void onDialogEvent(AlertDialogWithConfirmCancelEvent aDialogEvent) {
        LOG.log(Level.INFO,"AlertDialogWithConfirmCancelEvent does: "+aDialogEvent.getEventValue()+ " with "+aDialogEvent.getPromise().getMsg());
        if (aDialogEvent.getEventValue()==AlertDialogWithConfirmCancelEvent.EventType.ConfirmDialog){
            deferred = aDialogEvent.getPromise();
            view.showDialog(deferred.getMsg());
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
LOG.log(Level.INFO, "do it");
        deferred.resolve(Boolean.TRUE);
    }

    @JsMethod
    public void cancel(){
        view.hideDialog();
//        promise.fail(null);
LOG.log(Level.INFO, "cancel");
        deferred.resolve(Boolean.FALSE);       
    }    
}
