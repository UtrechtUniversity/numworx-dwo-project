package nl.uu.fi.dwo.mobile.client.ui;

import nl.uu.fi.dwo.mobile.client.sco.SCORM_guest;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.mobile.client.ui.views.NavigationView;
import nl.uu.fi.dwo.mobile.client.ui.views.NoCourseView;
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
import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.web.bindery.event.shared.EventBus;
import dagger.Lazy;

@Singleton
public class DummyClientFactory implements ClientFactory {

	final private EventBus eventBus;
	final private RPCHandler handler;
	final private boolean premium;
	private Lazy<ViewModuleView> entryView;
	private TrafficAgent agent;
	
	@Inject DummyClientFactory(EventBus eventBus, RPCHandler handler, TrafficAgent agent, @Named("premium") boolean premium) {
      this.eventBus = eventBus;
      this.handler = handler;
      this.agent = agent;
      this.premium = premium;
      java.util.logging.Logger.getLogger("DummyClientFactory " + premium);
    }

  @Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public ViewModuleView getEntryView() {
		return entryView.get();
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
	public void setEntryView(Lazy<ViewModuleView> entryView) {
		this.entryView = entryView;
	}

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
	public boolean isPremium() {
		return premium; // FIXME komt van buitenaf.
	}

}
