package fi.dwo.gwt.lib.rest.ui;

import com.google.web.bindery.event.shared.EventBus;
import java.util.logging.Logger;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class ProgressDialogPresenter implements ProgressDialogEventHandler {

    private static final Logger LOG = Logger.getLogger(ProgressDialogPresenter.class.getName());
    private EventBus eventBus;
    private Display view;
    private ProgressDialogPromise promise;

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

    public ProgressDialogPresenter(EventBus anEventBus) {
        eventBus = anEventBus;
        eventBus.addHandler(ProgressDialogEvent.TYPE, this);

    }

    @Override
    public void onDialogEvent(ProgressDialogEvent dialogEvent) {
        switch(dialogEvent.getEventType()){
            case Init:
                promise = dialogEvent.getPromise();
                view.showDialog(dialogEvent.getActivityMsg() );
                view.updateDialog(dialogEvent.getProgress(), dialogEvent.getActivityMsg() );
                break;
            case Update:
                view.updateDialog(dialogEvent.getProgress(), dialogEvent.getActivityMsg() );
            case Complete:
                view.hideDialog();
        }
    }
    public void init() {
        
    }

    /**
     * @param view the view to set
     */
    public void setView(ProgressDialogPresenter.Display view) {
        this.view = view;
    }    
   
    public void abort(){
        view.hideDialog();
        promise.resolve(Boolean.TRUE);
    }    
}
