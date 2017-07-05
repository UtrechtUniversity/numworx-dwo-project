package nl.uu.fi.dwo.lms.gwtclient.gwt;

import com.google.gwt.event.shared.GwtEvent;

/**
 * GWTEvent that notifies of a login action.
 *
 * @author Gert van der Plas
 */
public class ConfirmDialogEvent extends GwtEvent<ConfirmDialogEventHandler> {


    public static Type<ConfirmDialogEventHandler> TYPE = new Type<ConfirmDialogEventHandler>();
    public static EventType eventValue;
    ConfirmDialogPromise promise;
//    private Promise<T extends DwoConfirmDialogStatus> promise;
//    private Dwo2Exception exception;

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
