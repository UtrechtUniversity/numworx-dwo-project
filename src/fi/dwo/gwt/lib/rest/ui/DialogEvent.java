package fi.dwo.gwt.lib.rest.ui;

import com.google.web.bindery.event.shared.Event;

import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

/**
 * GWTEvent that notifies of a login action.
 *
 * @author Gert van der Plas
 */
public class DialogEvent extends Event<DialogEventHandler> {

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the exception
     */
    public Dwo2Exception getException() {
        return exception;
    }

    public enum Dialogs {
        Message,
        Dwo2MessageDialog,
        Dwo2ExceptionDialog,
        
    }
    
    private String message;
    private Dwo2Exception exception;

    public static Type<DialogEventHandler> TYPE = new Type<DialogEventHandler>();
    public static Dialogs eventValue;

    public DialogEvent(String localizedMessage) {
        this.setEventValue(Dialogs.Message);
        message = localizedMessage;
    }

    public DialogEvent(Dwo2Exception e) {
        this.setEventValue(Dialogs.Dwo2ExceptionDialog);
        exception = e;
    }
    
    public DialogEvent(Dwo2ExceptionCode code, String msg) {
      this(new Dwo2Exception(code, msg));
    }
    public DialogEvent(Dwo2ExceptionCode code) {
      this(code, "");
    }   
    
    @Override
    public Type<DialogEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DialogEventHandler handler) {
        handler.onDialogEvent(this);
    }

    public void setEventValue(Dialogs view) {
        eventValue = view;
    }

    public Dialogs getEventValue() {
        return eventValue;
    }
}
