package nl.uu.fi.dwo.lms.gwtclient.gwt;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Logger;
import org.osgi.util.promise.Promise;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class MsgConfirmDialogPresenter implements ConfirmDialogEventHandler {

    private static final Logger LOG = Logger.getLogger(MsgConfirmDialogPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private Display view;
    private ConfirmDialogPromise promise;

    public interface Display {

        Widget asWidget();
        void clear();
        void init();
//        void showDialog(String text);
        void showConfirmDialog(String text);
    }

    public MsgConfirmDialogPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        eventBus.addHandler(ConfirmDialogEvent.TYPE, this);

    }

    @Override
    public void onDialogEvent(ConfirmDialogEvent dialogEvent) {
        if (dialogEvent.getEventValue()==ConfirmDialogEvent.EventType.ConfirmDialog){
            promise = dialogEvent.getPromise();
            view.showConfirmDialog(promise.getMsg());
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
        promise.resolve(Boolean.TRUE);
    }

    public void cancel(){
        promise.resolve(Boolean.FALSE);       
    }

}
