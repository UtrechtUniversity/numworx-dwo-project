package nl.uu.fi.dwo.mobile.client.ui;

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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

@Singleton
public class DummyClientFactory implements ClientFactory {

	final private EventBus eventBus;
	final private RPCHandler handler;
	final private boolean premium;
	private ViewModuleView entryView;
	private IsWidget logoutWidget;
	private TrafficAgent agent;
	
	public DummyClientFactory() {
	  this(new SimpleEventBus(),new DummyRPCHandler(), new TrafficAgent(), false);
	}

	@Inject DummyClientFactory(EventBus eventBus, RPCHandler handler, TrafficAgent agent, @Named("premium") boolean premium) {
      this.eventBus = eventBus;
      this.handler = handler;
      this.agent = agent;
      this.premium = premium;
    }

  @Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public PlaceController getPlaceController() {
		return null;
	}

	@Override
	public ViewModuleView getEntryView() {
		return entryView;
	}

	@Override
	public SelectModuleView getHomeView() {
		return null;
	}

	@Override
	public LoginView getLoginView() {
		return null;
	}

	@Override
	public TreeModuleView getTreeModuleView() {
		return null;
	}

	@Override
	public RPCHandler getRPCHandler() {
		return handler;
	}

	@Inject
	public void setEntryView(ViewModuleView entryView) {
		this.entryView = entryView;
	}

	@Override
	public SCORM_guest setupAPI() {
		return GWT.create(SCORM_guest.class);
	}

//	@Override
//	public IsWidget getLogoutWidget() {
//		return logoutWidget;
//	}
//
//	@Override
//	public void setLogoutWidget(IsWidget widget) {
//		logoutWidget = widget;
//	}

	@Override
	public Promise<Void> logout() {
		return null;
	}

	public boolean withUser() {
		return false;
	}
	
	public Object getSchoolID() {
		return "";
	}

	public Object getClassID() {
		return "";
	}

	@Override
	public boolean isIconizer() {
		return true;
	}

	@Override
	public RoleType getRoleType() {
		return RoleType.ANONYMOUS;
	}

	@Override
	public Object getUserID() {
		return null;
	}

	public Object getSchoolName() {
		return "";
	}

	@Override
	public DomSchool getSchool() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DomSchoolClass getSchoolClass() {
		return null;
	}

	@Override
	public Promise<Void> barrier() {
		return agent.barrier();
	}

	@Override
	public void addBarrier(Promise<?> p) {
		agent.addBarrier(p);
	}

	@Override
	public Promise<Void> startExam(DomClassCourse classCourse, String password) {
		return barrier();
	}

	@Override
	public boolean inExam(DomClassCourse classCourse) {
		return true;
	}

	@Override
	public HeaderView getHeaderView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NavigationView getNavigationView() {
		// TODO Auto-generated method stub
		return null;
	}

  @Override
  public Provider<NoCourseView> getNoCourseView() {
    // TODO Auto-generated method stub
    return null;
  }
  
  
	@Override
	public boolean isPremium() {
		return premium; // FIXME komt van buitenaf.
	}

  @Override
  public PlaceHistoryHandler getPlaceHistoryHandler() {
    // TODO Auto-generated method stub
    return null;
  }
}
