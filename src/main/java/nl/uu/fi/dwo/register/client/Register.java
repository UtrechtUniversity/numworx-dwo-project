package nl.uu.fi.dwo.register.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;
import fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class Register implements EntryPoint, Command {
	
	private RegisterPanel content;
	private String newURL = "about:blank";
	

	public void onModuleLoad() {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionGWTTranslator());
        String next = Window.Location.getParameter("next");
        if(next != null)
        	newURL = next;
        
		content = new RegisterPanel();
		content.getController().setNext(this);
		RootPanel.get().add(content);
		
	}


	@Override
	public void execute() {
		Window.Location.assign(newURL);
	}

}
