package nl.uu.fi.dwo.lms.gwtclient.gwt.ui;

import com.google.web.bindery.event.shared.Event;

import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

/**
 * GWTEvent that notifies of a login action.
 *
 * @author Gert van der Plas
 */
public class AlertDialogWithOKEvent extends Event<AlertDialogWithOKEventHandler> implements Runnable {

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
    private Runnable runner = this;

    public static Type<AlertDialogWithOKEventHandler> TYPE = new Type<AlertDialogWithOKEventHandler>();
    public static Dialogs eventValue;

    public AlertDialogWithOKEvent(String localizedMessage) {
        this.setEventValue(Dialogs.Message);
        message = localizedMessage;
    }

    public AlertDialogWithOKEvent(Dwo2Exception e) {
        this.setEventValue(Dialogs.Dwo2ExceptionDialog);
        exception = e;
    }
    
    public AlertDialogWithOKEvent(Dwo2Exception e, Runnable callback) {
    	this(e);
    	runner = callback==null?this:callback;
    }
    
    public void callback() {
    	runner.run();
    }
    
    public AlertDialogWithOKEvent(Dwo2ExceptionCode code, String msg) {
      this(new Dwo2Exception(code, msg));
    }
    public AlertDialogWithOKEvent(Dwo2ExceptionCode code) {
      this(code, "");
    }   
    
    @Override
    public Type<AlertDialogWithOKEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AlertDialogWithOKEventHandler handler) {
        handler.onDialogEvent(this);
    }

    public void setEventValue(Dialogs view) {
        eventValue = view;
    }

    public Dialogs getEventValue() {
        return eventValue;
    }

	@Override
	public void run() {}
}
