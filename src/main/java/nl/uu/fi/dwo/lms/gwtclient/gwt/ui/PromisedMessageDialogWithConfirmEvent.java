package nl.uu.fi.dwo.lms.gwtclient.gwt.ui;

import com.google.web.bindery.event.shared.Event;

/**
 * GWTEvent that notifies of a login action.
 *
 * @author Gert van der Plas
 */
public class PromisedMessageDialogWithConfirmEvent extends Event<PromisedDialogWithConfirmEventHandler> {


    public static Type<PromisedDialogWithConfirmEventHandler> TYPE = new Type<PromisedDialogWithConfirmEventHandler>();
    public static EventType eventValue;
    PromisedDialogWithConfirmDeferred promise;

    public PromisedMessageDialogWithConfirmEvent(PromisedMessageDialogWithConfirmEvent.EventType type, PromisedDialogWithConfirmDeferred aPromise) {
        eventValue = type;
        promise = aPromise;
        
//        message = localizedMessage;
    }

    @Override
    public Type<PromisedDialogWithConfirmEventHandler> getAssociatedType() {
        return TYPE;
    }
    
    public enum EventType {
        ConfirmDialog
    }
    
    @Override
    protected void dispatch(PromisedDialogWithConfirmEventHandler handler) {
        handler.onDialogEvent(this);
    }

    public void setEventValue(EventType view) {
        eventValue = view;
    }

    public EventType getEventValue() {
        return eventValue;
    }
    
    public PromisedDialogWithConfirmDeferred getPromise(){
        return promise;
    }
}
