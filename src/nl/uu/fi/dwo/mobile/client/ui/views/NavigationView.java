package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface NavigationView extends IsWidget {

	void setDisplay(Widget display);

	void show();
	void hide();
	
	void showIcon(boolean show);
}
