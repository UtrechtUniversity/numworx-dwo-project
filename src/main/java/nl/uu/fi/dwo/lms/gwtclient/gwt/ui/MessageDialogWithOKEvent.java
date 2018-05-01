package nl.uu.fi.dwo.lms.gwtclient.gwt.ui;

import com.google.web.bindery.event.shared.Event;

import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

/**
 * GWTEvent that notifies of a login action.
 *
 * @author Gert van der Plas
 */
public class MessageDialogWithOKEvent extends Event<MessageDialogWithOKEventHandler> {

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

    public static Type<MessageDialogWithOKEventHandler> TYPE = new Type<MessageDialogWithOKEventHandler>();
    public static Dialogs eventValue;

    public MessageDialogWithOKEvent(String localizedMessage) {
        this.setEventValue(Dialogs.Message);
        message = localizedMessage;
    }

    public MessageDialogWithOKEvent(Dwo2Exception e) {
        this.setEventValue(Dialogs.Dwo2ExceptionDialog);
        exception = e;
    }
    
    public MessageDialogWithOKEvent(Dwo2ExceptionCode code, String msg) {
      this(new Dwo2Exception(code, msg));
    }
    public MessageDialogWithOKEvent(Dwo2ExceptionCode code) {
      this(code, "");
    }   
    
    @Override
    public Type<MessageDialogWithOKEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(MessageDialogWithOKEventHandler handler) {
        handler.onDialogEvent(this);
    }

    public void setEventValue(Dialogs view) {
        eventValue = view;
    }

    public Dialogs getEventValue() {
        return eventValue;
    }
}
