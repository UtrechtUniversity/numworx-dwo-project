package nl.uu.fi.dwo.mobile.client.ui.views;

import nl.uu.fi.dwo.mobile.client.sco.SCORM_guest;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;

/**
 * 
 * @author Danny Hendrix
 * 
 */
public interface ViewModuleView extends IsWidget
{

	void setupModule(String name, String file);

	public HeaderButton getBackButton();

	void setApi(Scorm2004IF scorm_guest);

	void close();
	
	AnchorView.AnchorContext getAnchorContext();
	void setAnchorContext(AnchorView.AnchorContext context);
	
	 public interface Loader {
	        void viewModuleViewSetupDone();
	    }

}
