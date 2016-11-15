package nl.uu.fi.dwo.mobile.client.ui;

import java.util.Map;

import nl.uu.fi.dwo.mobile.client.sco.SCORM_guest;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;
import nl.uu.fi.dwo.mobile.client.ui.views.SelectModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @see GWT
 * @author Danny Hendrix
 * 
 */
public interface ClientFactory
{
	EventBus getEventBus();

	PlaceController getPlaceController();

	ViewModuleView getEntryView();

	SelectModuleView getHomeView();

	LoginView getLoginView();

	TreeModuleView getTreeModuleView();
	
	RPCHandler getRPCHandler();
	
	IsWidget getMenuWidget();
	IsWidget getLogoutWidget();
	void setLogoutWidget(IsWidget widget);
	
	/**
	 * FIXME deze moet weer weg als TreeModuleView één view gebruikt in plaats van steeds een nieuwe.
	 * @param view
	 */
	@Deprecated
	void setEntryView(ViewModuleView view);
	
	public SCORM_guest setupAPI();

	void logout();
	
	boolean withUser();
// Low level functions	
	Object getSchoolID();
	Object getClassID();
	boolean isIconizer();
	RoleType getRoleType();
	
	Object getUserID();
	Object getSchoolName();

// High level functions
/*	Full varianten?
	DomUserFull getUser();
	DomSchoolFull   getSchool();
	DomSchoolClassFull getSchoolClass();
	
*/	
}
