package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.event.shared.GwtEvent;

/**
 * GWTEvent that notifies of a login action.
 *
 * @author Gert van der Plas
 */
public class SwitchViewEvent extends GwtEvent<SwitchViewEventHandler> {
    public enum SelectedView {
        LOGIN,
        ACCOUNT,
        SWITCHSCHOOL,
        RESULTS,
        SCORESULTS,
        SCHOOLCLASSES,
        TEACHERSINSCHOOLCLASS,
        STUDENTSINSCHOOLCLASS
    }
    
    public static Type<SwitchViewEventHandler> TYPE = new Type<SwitchViewEventHandler>();
    public static SelectedView eventValue;

    public SwitchViewEvent(SelectedView aState){
        this.setEventValue(aState);
    }
    
    @Override
    public Type<SwitchViewEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SwitchViewEventHandler handler) {
        handler.onSwitchViewEvent(this);
    }
    
    public void setEventValue(SelectedView view){
        eventValue = view;
    }
    public SelectedView getEventValue(){
        return eventValue;
    }
}
