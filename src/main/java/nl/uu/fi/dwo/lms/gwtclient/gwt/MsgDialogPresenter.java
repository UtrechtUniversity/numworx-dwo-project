package nl.uu.fi.dwo.lms.gwtclient.gwt;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Logger;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class MsgDialogPresenter implements DialogEventHandler {

    private static final Logger LOG = Logger.getLogger(MsgDialogPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
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
    public void onDialogEvent(DialogEvent dialogEvent) {
        if (dialogEvent.getEventValue()==DialogEvent.Dialogs.Message) {
            view.showDialog(dialogEvent.getMessage());
        }else if (dialogEvent.getEventValue()==DialogEvent.Dialogs.Dwo2ExceptionDialog){
            view.showDialog(dialogEvent.getException().getLocalizedCodeExplanation(null));
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
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SCHOOLCLASSES));
    }
}
