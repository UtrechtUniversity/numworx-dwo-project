package nl.uu.fi.dwo.register.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class Register implements EntryPoint {
	
	private RegisterPanel content;
	

	public void onModuleLoad() {
		content = new RegisterPanel();
		
		RootPanel.get().add(content);

	}

}
