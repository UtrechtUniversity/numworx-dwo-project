package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.chatbox;

import javax.inject.Inject;
import nl.uu.fi.dwo.lms.gwtclient.gwt.chatbox.*;

public class JsChatboxView implements ChatboxPresenter.Display {

	@Inject JsChatboxView() {}

	@Override
	public void init() {
		JsChatboxDisplay.init();
		
	}

	@Override
	public void clear() {
		JsChatboxDisplay.clear();
	}

	@Override
	public void setHelp(String url) {
	}
	
}
