package nl.uu.fi.dwo.lms.gwtclient.gwt;

import com.google.gwt.event.shared.GwtEvent;

/**
 * GWTEvent that notifies of a login action.
 *
 * @author Gert van der Plas
 */
public class DialogEvent extends GwtEvent<DialogEventHandler> {

    public enum Dialogs {
        Message,
        ErrorDialog,
    }

    public static Type<DialogEventHandler> TYPE = new Type<DialogEventHandler>();
    public static Dialogs eventValue;

    public DialogEvent(Dialogs aState) {
        this.setEventValue(aState);
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
