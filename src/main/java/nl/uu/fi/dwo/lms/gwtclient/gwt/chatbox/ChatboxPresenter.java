package nl.uu.fi.dwo.lms.gwtclient.gwt.chatbox;

import javax.inject.Inject;

import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;

public class ChatboxPresenter {

	public interface Display extends BasicDisplay {
		
	}
	
	private Display view;
	
	@Inject ChatboxPresenter() {
		
	}
	
	@Inject void setView(Display view) {
		this.view = view;
	}

	public void init() {
		
		
	}
}
