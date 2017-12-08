package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.place.shared.Place;

import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

public interface HeaderView {

	void setUserAndRole(DomUserFull currentUser, RoleType roleType);

	Place getHomePlace();

	void setUpPlace(Place homePlace);

	void setPresenter(GotoController presenter);

}
