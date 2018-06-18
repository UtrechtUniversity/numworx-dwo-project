package nl.uu.fi.dwo.lms.gwtclient.gwt.ui;

import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.ui.DialogEvent;
import fi.dwo.gwt.lib.rest.ui.DialogEventHandler;
import fi.dwo.gwt.lib.rest.ui.MsgDialogPresenter;
import java.util.logging.Level;
import java.util.logging.Logger;
import jsinterop.annotations.JsMethod;

/**
 * Subclassing for project.
 *
 * @author plas0006
 */
public class MessageDialogWithOKPresenter  implements MessageDialogWithOKEventHandler {

    private static final Logger LOG = Logger.getLogger(MsgDialogPresenter.class.getName());
    private EventBus eventBus;
    private Display view;

    public interface Display {
        void clear();

        void init();

        void showDialog(String text);
        void hideDialog();
    }

    public MessageDialogWithOKPresenter(EventBus anEventBus) {
        eventBus = anEventBus;
        eventBus.addHandler(MessageDialogWithOKEvent.TYPE, this);

    }

    @Override
    public void onDialogEvent(MessageDialogWithOKEvent aDialogEvent) {
        LOG.log(Level.INFO,"MessageDialogWithOKPresenter does: "+aDialogEvent.getEventValue());
        if (aDialogEvent.getEventValue()==MessageDialogWithOKEvent.Dialogs.Message) {
            view.showDialog(aDialogEvent.getMessage());
        }else if (aDialogEvent.getEventValue()==MessageDialogWithOKEvent.Dialogs.Dwo2ExceptionDialog){
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

    @JsMethod
    public void confirm() {
        hide();
    }
    
    @JsMethod
    public void hide() {
        view.hideDialog();
    }
}
