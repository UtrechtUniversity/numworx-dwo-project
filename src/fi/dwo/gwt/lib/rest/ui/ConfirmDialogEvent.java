package fi.dwo.gwt.lib.rest.ui;

import com.google.web.bindery.event.shared.Event;

/**
 * GWTEvent that notifies of a login action.
 *
 * @author Gert van der Plas
 */
public class ConfirmDialogEvent extends Event<ConfirmDialogEventHandler> {


    public static Type<ConfirmDialogEventHandler> TYPE = new Type<ConfirmDialogEventHandler>();
    public static EventType eventValue;
    ConfirmDialogPromise promise;

    public ConfirmDialogEvent(ConfirmDialogEvent.EventType type, ConfirmDialogPromise aPromise) {
        eventValue = type;
        promise = aPromise;
        
//        message = localizedMessage;
    }

    @Override
    public Type<ConfirmDialogEventHandler> getAssociatedType() {
        return TYPE;
    }
    
    public enum EventType {
        ConfirmDialog
    }
    
    @Override
    protected void dispatch(ConfirmDialogEventHandler handler) {
        handler.onDialogEvent(this);
    }

    public void setEventValue(EventType view) {
        eventValue = view;
    }

    public EventType getEventValue() {
        return eventValue;
    }
    
    public ConfirmDialogPromise getPromise(){
        return promise;
    }
}
