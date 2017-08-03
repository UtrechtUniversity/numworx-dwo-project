package nl.uu.fi.dwo.lms.gwtclient.gwt;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class MsgDialogPresenter implements DialogEventHandler {

    private static final Logger LOG = Logger.getLogger(MsgDialogPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private DialogEvent dialogEvent;
    private Display view;

    public interface Display {

        Widget asWidget();

        void clear();

        void init();

        void showDialog(String text);
//        void showConfirmDialog(String text);
    }

    public MsgDialogPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        eventBus.addHandler(DialogEvent.TYPE, this);

    }

    @Override
    public void onDialogEvent(DialogEvent aDialogEvent) {
        if (aDialogEvent.getEventValue()==DialogEvent.Dialogs.Message) {
            view.showDialog(aDialogEvent.getMessage());
        }else if (aDialogEvent.getEventValue()==DialogEvent.Dialogs.Dwo2ExceptionDialog){
            view.showDialog(aDialogEvent.getException().getLocalizedCodeExplanation(null));
            dialogEvent = aDialogEvent;
//        }else if (dialogEvent.getEventValue()==DialogEvent.Dialogs.ConfirmDialog){
//            view.showConfirmDialog(dialogEvent.getMessage());
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
     * Go back to the schoolclasses presentation.
     */
    public void Back() {
        if(dialogEvent.getException().getDwo2Code().equals(Dwo2ExceptionCode.User_AuthenticationError)){
//          Window.Location.reload();
          Window.Location.replace(Window.Location.getHref());
        }
    }
}
