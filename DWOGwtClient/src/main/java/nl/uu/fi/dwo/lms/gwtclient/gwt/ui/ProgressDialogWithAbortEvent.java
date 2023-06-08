package nl.uu.fi.dwo.lms.gwtclient.gwt.ui;

import com.google.web.bindery.event.shared.Event;
import fi.dwo.gwt.lib.rest.ui.ProgressDialogEvent;
import static fi.dwo.gwt.lib.rest.ui.ProgressDialogEvent.eventType;

/**
 * GWTEvent that notifies of a login action.
 *
 * @author Gert van der Plas
 */
public class ProgressDialogWithAbortEvent extends Event<ProgressDialogWithAbortEventHandler> {


    public static Type<ProgressDialogWithAbortEventHandler> TYPE = new Type<ProgressDialogWithAbortEventHandler>();
    public static EventType eventType;
    private static int progress = 0;
    private static String activityMsg = "";
    ProgressDialogWithAbortDeferred deferred;
    
    public enum EventType {
        Init,
        Update,
        Complete
    }
    
    public ProgressDialogWithAbortEvent(ProgressDialogWithAbortEvent.EventType type, int aProgress, String actMsg, ProgressDialogWithAbortDeferred aPromise) {
        eventType = type;
        progress = aProgress;
        activityMsg=actMsg;
        deferred = aPromise;
    }

    @Override
    public Type<ProgressDialogWithAbortEventHandler> getAssociatedType() {
        return TYPE;
    }
    
    @Override
    protected void dispatch(ProgressDialogWithAbortEventHandler handler) {
        handler.onDialogEvent(this);
    }

    public void setEventType(EventType view) {
        eventType = view;
    }

    public EventType getEventType() {
        return eventType;
    }
    
    public ProgressDialogWithAbortDeferred getDeferred(){
        return deferred;
    }
    
    
    /**
     * @return the progress
     */
    public int getProgress() {
        return progress;
    }

    /**
     * @return the activityMsg
     */
    public String getActivityMsg() {
        return activityMsg;
    }
}
