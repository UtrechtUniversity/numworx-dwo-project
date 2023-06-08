package nl.uu.fi.dwo.lms.gwtclient.gwt.ui;

import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.ui.MsgDialogPresenter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.osgi.util.promise.Deferred;

import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.PromisedMessageDialogWithConfirmEvent.EventType;

/**
 * Subclassing for project.
 *
 * @author plas0006
 */
@RoleScope
public class MessageDialogWithOKPresenter  implements MessageDialogWithOKEventHandler, PromisedDialogWithConfirmEventHandler {

    private static final Logger LOG = Logger.getLogger(MsgDialogPresenter.class.getName());
    private EventBus eventBus;
    private Display view;
    private Deferred<Boolean> promise;

    public interface Display {
        void clear();

        void init();

        void showDialog(String text);
        void hideDialog();
    }

    @Inject MessageDialogWithOKPresenter(EventBus anEventBus) {
        eventBus = anEventBus;
        eventBus.addHandler(MessageDialogWithOKEvent.TYPE, this);
        eventBus.addHandler(PromisedMessageDialogWithConfirmEvent.TYPE, this);

    }

    @Override
    public void onDialogEvent(MessageDialogWithOKEvent aDialogEvent) {
        LOG.log(Level.INFO,"MessageDialogWithOKPresenter does: "+aDialogEvent.getEventValue());
        if (aDialogEvent.getEventValue()==MessageDialogWithOKEvent.Dialogs.Message) {
           promise = new Deferred<>();
           view.showDialog(aDialogEvent.getMessage());
        }else if (aDialogEvent.getEventValue()==MessageDialogWithOKEvent.Dialogs.Dwo2ExceptionDialog){
          promise = new Deferred<>();
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
      view.hideDialog();
      promise.resolve(Boolean.TRUE);
    }
    
    @JsMethod
    public void hide() {
        view.hideDialog();
        promise.resolve(Boolean.FALSE);       
    }
    
    @JsMethod
    public void cancel(){
        view.hideDialog();
        promise.resolve(Boolean.FALSE);       
    }

    @Override
    public void onDialogEvent(PromisedMessageDialogWithConfirmEvent event) {
      if (event.getEventValue() == EventType.ConfirmDialog) {
        promise = event.getPromise();
        view.showDialog(event.getPromise().getMsg());
      } else {
        event.getPromise().fail(new IllegalArgumentException());
      }
      
    }    

}
