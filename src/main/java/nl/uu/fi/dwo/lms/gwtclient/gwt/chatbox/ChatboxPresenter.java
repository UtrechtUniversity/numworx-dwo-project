package nl.uu.fi.dwo.lms.gwtclient.gwt.chatbox;

import javax.inject.Inject;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.web.bindery.event.shared.EventBus;

import fi.dwo.gwt.lib.rest.util.RestAuthenticator;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;

public class ChatboxPresenter implements ValueChangeHandler<String> {

	public interface Display extends BasicDisplay {

		void setLogin(String user, String password);

		void openUrl(String url);
		
	}
	
	final private DwoGlobalVars vars;
	
	private Display view;
	
	
	@Inject ChatboxPresenter(DwoGlobalVars vars, EventBus bus) {
		this.vars = vars;
		
		//RestAuthenticator.instance.addValueChangeHandler(this);
		bus.addHandlerToSource(ValueChangeEvent.getType(), RestAuthenticator.instance, this); // resettable eventbus, helaas werkt niet want Authenticator gebruikt andere bus
		view.openUrl("about:blank");
	}
	
	@Inject void setView(Display view) {
		this.view = view;
	}

	public void init() {
		DomUserFull u = vars.getCurrentUser();
		if (u == null) {
			view.setLogin("", "");
			view.openUrl("about:blank");
			return;
		}
		String user = u.getUserName();
		String password = RestAuthenticator.instance.getAuthorization(); // access token of so
		view.setLogin(user, strip(password));
		
		view.openUrl("chatbox.jsp");
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		DomUserFull u = vars.getCurrentUser();
		if (u != null) {
			String password = event.getValue();
			view.setLogin(u.getUserName(), strip(password));
		}
		else {
			view.setLogin("", "");
			view.openUrl("about:blank");			
		}
	}

	private String strip(String password) {
		if (password.toLowerCase().startsWith("basic ")) return password.substring(6);
		if (password.toLowerCase().startsWith("bearer ")) return password.substring(7);
		return "None";
	}
}
