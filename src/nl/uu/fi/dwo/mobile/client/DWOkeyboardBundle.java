package nl.uu.fi.dwo.mobile.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;

public interface DWOkeyboardBundle extends ClientBundle {
	@Source("nl/uu/fi/dwo/mobile/client/resources/kb/mw_breuk.gif")
	ImageResource breuk();

	@Source("nl/uu/fi/dwo/mobile/client/resources/kb/mw_kwadraat.gif")
	ImageResource kwadraat();
	
	@Source("nl/uu/fi/dwo/mobile/client/resources/kb/mw_macht.gif")
	ImageResource macht();

	@Source("nl/uu/fi/dwo/mobile/client/resources/kb/mw_ndewortel.gif")
	ImageResource ndewortel();

	@Source("nl/uu/fi/dwo/mobile/client/resources/kb/mw_wortel.gif")
	ImageResource wortel();

	
}
