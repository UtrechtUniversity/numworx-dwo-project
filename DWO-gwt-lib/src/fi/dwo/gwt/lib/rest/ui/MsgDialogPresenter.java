package fi.dwo.gwt.lib.rest.ui;

import com.google.web.bindery.event.shared.EventBus;

import java.util.logging.Logger;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class MsgDialogPresenter implements DialogEventHandler {

    private static final Logger LOG = Logger.getLogger(MsgDialogPresenter.class.getName());
    private EventBus eventBus;
    private Display view;

    public interface Display {
        void clear();

        void init();

        void showDialog(String text);
        void hideDialog();
    }

    public MsgDialogPresenter(EventBus anEventBus) {
        eventBus = anEventBus;
        eventBus.addHandler(DialogEvent.TYPE, this);

    }

    @Override
    public void onDialogEvent(DialogEvent aDialogEvent) {
        if (aDialogEvent.getEventValue()==DialogEvent.Dialogs.Message) {
            view.showDialog(aDialogEvent.getMessage());
        }else if (aDialogEvent.getEventValue()==DialogEvent.Dialogs.Dwo2ExceptionDialog){
            view.showDialog(aDialogEvent.getException().getLocalizedCodeExplanation(null));
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
     * @param view the view to set
     */
    public Display getView() {
        return view;
    }
    
    public void hide() {
        view.hideDialog();
    }
}
