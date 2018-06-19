package nl.uu.fi.dwo.lms.gwtclient.gwt.ui;

import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.ui.ProgressDialogEvent;
import fi.dwo.gwt.lib.rest.ui.ProgressDialogEventHandler;
import fi.dwo.gwt.lib.rest.ui.ProgressDialogPresenter;
import fi.dwo.gwt.lib.rest.ui.ProgressDialogPromise;
import java.util.logging.Logger;
import jsinterop.annotations.JsMethod;

/**
 * Sub classing for project.
 *
 * @author plas0006
 */
public class ProgressDialogWithAbortPresenter implements ProgressDialogWithAbortEventHandler {

    private static final Logger LOG = Logger.getLogger(ProgressDialogPresenter.class.getName());
    private EventBus eventBus;
    private Display view;
    private ProgressDialogWithAbortDeferred deferred;

    public interface Display {
        /* clear progress and texts */
        void clear();
        void init();
        /* progress status becomes 0, current activity label is empty */ 
        void showDialog(String msg);
        /* complete status becomes 0, current activity label is actMsg, progress 0-100 */ 
        void updateDialog(int progress, String actMsg);
        void hideDialog();
    }

    
    public ProgressDialogWithAbortPresenter(EventBus anEventBus) {
        eventBus=anEventBus;
        eventBus.addHandler(ProgressDialogWithAbortEvent.TYPE, this);
    }
    
    @Override
    public void onDialogEvent(ProgressDialogWithAbortEvent dialogEvent) {
        switch(dialogEvent.getEventType()){
            case Init:
                deferred = dialogEvent.getDeferred();
                view.showDialog(dialogEvent.getActivityMsg() );
                view.updateDialog(dialogEvent.getProgress(), dialogEvent.getActivityMsg() );
                break;
            case Update:
                view.updateDialog(dialogEvent.getProgress(), dialogEvent.getActivityMsg() );
                break;
            case Complete:
                view.hideDialog();
                break;
        }
    }
    public void init() {
        
    }

    /**
     * @param view the view to set
     */
    public void setView(ProgressDialogWithAbortPresenter.Display view) {
        this.view = view;
    }    
   
    @JsMethod
    public void abort(){
        view.hideDialog();
        deferred.resolve(Boolean.TRUE);
    }    
    
}
