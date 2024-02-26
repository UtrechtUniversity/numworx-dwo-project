package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.List;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

public interface HeaderView extends IsWidget {

	void setUserAndRole(DomUserFull currentUser, RoleType roleType);

	Place getHomePlace();
	Place getUpPlace();

	void setUpPlace(Place homePlace);
	void setHomePlace(Place homePlace);
	
	void hide();
	void show();
	void setDisplay(Widget display, NavigationView navigationView);
	
	void setPresenter(GotoController presenter);
	GotoController getPresenter();

	void setTrail(List<SelectModuleItem> trail);

	Widget getDisplay();

}
