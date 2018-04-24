package fi.dwo.gwt.lib.rest.ui;

import com.google.web.bindery.event.shared.EventBus;
import java.util.logging.Logger;

/**
 * Handler for for ok/cancel dialogs.
 *
 * @author Gert van der Plas
 */
public class MsgClickedDialogPresenter implements MsgClickedDialogEventHandler {

    private static final Logger LOG = Logger.getLogger(MsgClickedDialogPresenter.class.getName());
    private EventBus eventBus;
    private Display view;
    private MsgClickedDialogPromise promise;

    public interface Display {

        void clear();
        void init();
        void showDialog(String text);
        void hideDialog();
    }

    public MsgClickedDialogPresenter(EventBus anEventBus) {
        eventBus = anEventBus;
        eventBus.addHandler(MsgClickedDialogEvent.TYPE, this);

    }

    @Override
    public void onDialogEvent(MsgClickedDialogEvent dialogEvent) {
        if (dialogEvent.getEventValue()==MsgClickedDialogEvent.EventType.MsgClickedDialog){
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
