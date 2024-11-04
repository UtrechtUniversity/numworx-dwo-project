package nl.uu.fi.dwo.lms.gwtclient.gwt;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import com.google.web.bindery.event.shared.EventBus;

import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent;

public class GuestMainPresenter extends MainPresenter {
    private static final Logger LOG = Logger.getLogger(GuestMainPresenter.class.getName());

	@Inject GuestMainPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars, BootPanelController source) {
		super(anEventBus, aDwoGlobalVars, source);
	}

	@Override @JsMethod
	public void logout() {
        LOG.log(Level.INFO, "Logging in");
        eventBus.fireEvent(new LoginEvent(LoginEvent.State.LOGOUT));
	}

}
