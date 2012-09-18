package nl.uu.fi.dwo.mobile.client.ui.views;

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

}
