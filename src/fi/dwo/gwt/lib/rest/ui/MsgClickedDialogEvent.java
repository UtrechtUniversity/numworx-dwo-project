package fi.dwo.gwt.lib.rest.ui;

import com.google.web.bindery.event.shared.Event;

/**
 * GWTEvent that notifies of a login action.
 *
 * @author Gert van der Plas
 */
public class MsgClickedDialogEvent extends Event<MsgClickedDialogEventHandler> {


    public static Type<MsgClickedDialogEventHandler> TYPE = new Type<MsgClickedDialogEventHandler>();
    public static EventType eventValue;
    MsgClickedDialogPromise promise;

    public MsgClickedDialogEvent(MsgClickedDialogEvent.EventType type, MsgClickedDialogPromise aPromise) {
        eventValue = type;
        promise = aPromise;
        
//        message = localizedMessage;
    }

    @Override
    public Type<MsgClickedDialogEventHandler> getAssociatedType() {
        return TYPE;
    }
    
    public enum EventType {
        MsgClickedDialog
    }
    
    @Override
    protected void dispatch(MsgClickedDialogEventHandler handler) {
        handler.onDialogEvent(this);
    }

    public void setEventValue(EventType view) {
        eventValue = view;
    }

    public EventType getEventValue() {
        return eventValue;
    }
    
    public MsgClickedDialogPromise getPromise(){
        return promise;
    }
}
