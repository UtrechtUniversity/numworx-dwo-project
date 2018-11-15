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
public class PromisedDialogWithOKPresenter implements PromisedDialogWithConfirmEventHandler {

    private static final Logger LOG = Logger.getLogger(PromisedDialogWithOKPresenter.class.getName());
    
    private EventBus eventBus;
    private Display view;
    private PromisedDialogWithConfirmDeferred promise;

    public interface Display {

        void clear();
        void init();
        void showDialog(String text);
        void hideDialog();
    }

    @Inject public PromisedDialogWithOKPresenter(EventBus anEventBus) {
        eventBus = anEventBus;
        eventBus.addHandler(PromisedMessageDialogWithConfirmEvent.TYPE, this);
    }

    @Override
    public void onDialogEvent(PromisedMessageDialogWithConfirmEvent aDialogEvent) {
        LOG.log(Level.INFO,"AlertDialogWithConfirmCancelEvent does: "+aDialogEvent.getEventValue()+ " with "+aDialogEvent.getPromise().getMsg());
        if (aDialogEvent.getEventValue()==PromisedMessageDialogWithConfirmEvent.EventType.ConfirmDialog){
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
LOG.log(Level.INFO, "do it");
        promise.resolve(Boolean.TRUE);
    }

    @JsMethod
    public void cancel(){
        view.hideDialog();
//        promise.fail(null);
LOG.log(Level.INFO, "cancel");
        promise.resolve(Boolean.FALSE);       
    }    
}
