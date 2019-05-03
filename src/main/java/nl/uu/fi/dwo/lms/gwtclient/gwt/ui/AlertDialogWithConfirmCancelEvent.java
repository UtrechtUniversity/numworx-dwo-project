package nl.uu.fi.dwo.lms.gwtclient.gwt.ui;

import com.google.web.bindery.event.shared.Event;

/**
 * GWTEvent that notifies of a login action.
 *
 * @author Gert van der Plas
 */
public class AlertDialogWithConfirmCancelEvent extends Event<AlertDialogWithConfirmCancelEventHandler> {


    public static Type<AlertDialogWithConfirmCancelEventHandler> TYPE = new Type<AlertDialogWithConfirmCancelEventHandler>();
    public static EventType eventValue;
    AlertDialogWithConfirmCancelDeferred promise;

    public AlertDialogWithConfirmCancelEvent(AlertDialogWithConfirmCancelEvent.EventType type, AlertDialogWithConfirmCancelDeferred aPromise) {
        eventValue = type;
        promise = aPromise;
        
//        message = localizedMessage;
    }

    @Override
    public Type<AlertDialogWithConfirmCancelEventHandler> getAssociatedType() {
        return TYPE;
    }
    
    public enum EventType {
        ConfirmDialog,
        YesNoDialog
    }
    
    @Override
    protected void dispatch(AlertDialogWithConfirmCancelEventHandler handler) {
        handler.onDialogEvent(this);
    }

    public void setEventValue(EventType view) {
        eventValue = view;
    }

    public EventType getEventValue() {
        return eventValue;
    }
    
    public AlertDialogWithConfirmCancelDeferred getPromise(){
        return promise;
    }
}
