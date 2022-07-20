package nl.uu.fi.dwo.lms.gwtclient.gwt.chatbox;

import javax.inject.Inject;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;

import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.util.RestAuthenticator;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;

public class ChatboxPresenter implements ValueChangeHandler<String> {

	public interface Display extends BasicDisplay {

		void setLogin(String user, String password);

		void openUrl(String url);
		
	}
	
	final private DwoGlobalVars vars;
	
	private Display view;
	private EventBus bus;
	
	
	@Inject ChatboxPresenter(DwoGlobalVars vars) {
		this.vars = vars;
		RestAuthenticator.instance.addValueChangeHandler(this);
		init();
	}
	
	@Inject void setView(Display view) {
		this.view = view;
	}

	public void init() {
		String user = vars.getCurrentUser().getUserName();
		String password = RestAuthenticator.instance.getAuthorization(); // access token of so
		view.setLogin(user, strip(password));
		
		view.openUrl("chatbox.jsp");
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		String password = event.getValue();
		view.setLogin(vars.getCurrentUser().getUserName(), strip(password));		
	}

	private String strip(String password) {
		if (password.toLowerCase().startsWith("basic ")) return password.substring(6);
		if (password.toLowerCase().startsWith("bearer ")) return password.substring(7);
		return "None";
	}
}
