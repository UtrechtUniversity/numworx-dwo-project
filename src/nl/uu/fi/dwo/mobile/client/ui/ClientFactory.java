package nl.uu.fi.dwo.mobile.client.ui;

import java.util.Map;

import javax.inject.Provider;

import org.osgi.util.promise.Promise;

import nl.uu.fi.dwo.mobile.client.sco.SCORM_guest;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;
import nl.uu.fi.dwo.mobile.client.ui.views.NavigationView;
import nl.uu.fi.dwo.mobile.client.ui.views.NoCourseView;
import nl.uu.fi.dwo.mobile.client.ui.views.SelectModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
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
	HeaderView getHeaderView();
	
	RPCHandler getRPCHandler();
	
//	IsWidget getLogoutWidget();
//	void setLogoutWidget(IsWidget widget);
	
	/**
	 * FIXME deze moet weer weg als TreeModuleView één view gebruikt in plaats van steeds een nieuwe.
	 * @param view
	 */
//	@Deprecated
//	void setEntryView(ViewModuleView view);
	
	public SCORM_guest setupAPI();

	Promise<Void> logout();	
	Promise<Void> barrier();
	void addBarrier(Promise<?> p);
	
	boolean withUser();
// Low level functions	
//	Object getSchoolID();
//	Object getClassID();
	DomSchool getSchool();
	DomSchoolClass getSchoolClass();
	
	boolean isIconizer();
	RoleType getRoleType();
	
	Object getUserID();
//	Object getSchoolName();

	Promise<Void> startExam(DomClassCourse classCourse, String password);

	boolean inExam(DomClassCourse classCourse);

	NavigationView getNavigationView();

    Provider<NoCourseView> getNoCourseView();

// High level functions
/*	Full varianten?
	DomUserFull getUser();
	DomSchoolFull   getSchool();
	DomSchoolClassFull getSchoolClass();
	
*/	
}
