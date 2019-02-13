package nl.uu.fi.dwo.lms.gwtclient.gwt.login;

import com.google.gwt.event.shared.GwtEvent;

/**
 * GWTEvent that notifies of a login action.
 *
 * @author Gert van der Plas
 */
public class LoginEvent extends GwtEvent<LoginEventHandler> {
    public enum State {
        FAIL,
        SUCCESS, // default post login panel should always be implemented. By default the welcome panel
        SUCCESS_ROLE, // role panel
        SUCCESS_WELCOME, // welcome panel
        SUCCESS_RESULTS, // result panel
        SUCCESS_GUEST,   // guest login
        SUCCESS_SCHOOLCLASSES, //schoolclass panel
        LOGOUT 
    }
    
    public static Type<LoginEventHandler> TYPE = new Type<LoginEventHandler>();
    public static State state;

    public LoginEvent(State aState){
        this.setState(aState);
    }
    
    @Override
    public Type<LoginEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(LoginEventHandler handler) {
        handler.onLoginEvent(this);
    }
    
    public void setState(State aState){
        state = aState;
    }
    public State getState(){
        return state;
    }
}
