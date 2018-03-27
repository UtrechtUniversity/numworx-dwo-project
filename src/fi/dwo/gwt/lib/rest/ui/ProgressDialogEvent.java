package fi.dwo.gwt.lib.rest.ui;

import com.google.web.bindery.event.shared.Event;

/**
 * GWTEvent that notifies of a login action.
 *
 * @author Gert van der Plas
 */
public class ProgressDialogEvent extends Event<ProgressDialogEventHandler> {

    public static Type<ProgressDialogEventHandler> TYPE = new Type<ProgressDialogEventHandler>();
    public static EventType eventType;
    private static int progress = 0;
    private static String activityMsg = "";
    ProgressDialogPromise promise;

    public ProgressDialogEvent(ProgressDialogEvent.EventType type, int aProgress, String actMsg, ProgressDialogPromise aPromise) {
        eventType = type;
        progress = aProgress;
        activityMsg=actMsg;
        promise = aPromise;
    }

    @Override
    public Type<ProgressDialogEventHandler> getAssociatedType() {
        return TYPE;
    }

    public enum EventType {
        Init,
        Update,
        Complete
    }

    @Override
    protected void dispatch(ProgressDialogEventHandler handler) {
        handler.onDialogEvent(this);
    }

    public void setEventType(EventType view) {
        eventType = view;
    }

    public EventType getEventType() {
        return eventType;
    }

    public ProgressDialogPromise getPromise() {
        return promise;
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
