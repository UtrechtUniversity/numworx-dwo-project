package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.Optional;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface NavigationView extends IsWidget {

	void setDisplay(Widget display, Optional<NavigationMenu> menu);

	void show();
	void hide();
	void wide();
	
	//void showIcon(boolean show);
}
