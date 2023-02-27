package fi.dwo.gwt.lib.rest.ui;

import com.google.web.bindery.event.shared.EventBus;
import java.util.logging.Logger;

/**
 * Handler for for message dialogs than require an 'ok' for read actions.
 *
 * @author Gert van der Plas
 */
public class MsgConfirmDialogPresenter implements ConfirmDialogEventHandler {

    private static final Logger LOG = Logger.getLogger(MsgConfirmDialogPresenter.class.getName());
    private EventBus eventBus;
    private Display view;
    private ConfirmDialogPromise promise;

    public interface Display {

        void clear();
        void init();
        void showDialog(String text);
        void hideDialog();
    }

    public MsgConfirmDialogPresenter(EventBus anEventBus) {
        eventBus = anEventBus;
        eventBus.addHandler(ConfirmDialogEvent.TYPE, this);

    }

    @Override
    public void onDialogEvent(ConfirmDialogEvent dialogEvent) {
        if (dialogEvent.getEventValue()==ConfirmDialogEvent.EventType.ConfirmDialog){
            promise = dialogEvent.getPromise();
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
    
    public void confirm(){
        view.hideDialog();
        promise.resolve(Boolean.TRUE);
    }

    public void cancel(){
        view.hideDialog();
        promise.resolve(Boolean.FALSE);       
    }

}
