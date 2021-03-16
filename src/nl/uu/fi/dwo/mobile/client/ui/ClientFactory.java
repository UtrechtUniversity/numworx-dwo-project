package nl.uu.fi.dwo.mobile.client.ui;

import javax.inject.Provider;

import org.osgi.util.promise.Promise;

import nl.uu.fi.dwo.mobile.client.sco.SCORM_guest;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;
import nl.uu.fi.dwo.mobile.client.ui.views.NavigationView;
import nl.uu.fi.dwo.mobile.client.ui.views.NoCourseView;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @see GWT
 * @author Danny Hendrix
 * 
 */
public interface ClientFactory
{
	EventBus getEventBus();


	ViewModuleView getEntryView();


	TreeModuleView getTreeModuleView();
	
	RPCHandler getRPCHandler();
			
	Promise<Void> logout();	
	Promise<Void> barrier();
	void addBarrier(Promise<?> p);
	
	boolean withUser();
	DomSchool getSchool();
	DomSchoolClass getSchoolClass();
	
	boolean isIconizer();
	RoleType getRoleType();
	
	Object getUserID();

	Promise<Void> startExam(DomClassCourse classCourse, String password);

	boolean inExam(DomClassCourse classCourse);

	boolean isPremium();


}
