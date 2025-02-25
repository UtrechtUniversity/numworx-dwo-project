package nl.uu.fi.dwo.mobile.client.ui;

import java.util.Optional;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.place.shared.PlaceHistoryHandler.DefaultHistorian;
import com.google.gwt.place.shared.PlaceHistoryHandler.Historian;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window.Location;
import com.google.web.bindery.event.shared.EventBus;

import dagger.Lazy;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.client.sco.SMLogger;
import nl.uu.fi.dwo.mobile.client.sco.SMLogger.LogStrategy;
import nl.uu.fi.dwo.mobile.client.ui.views.XapiWrapper;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.xapi.Activity;
import nl.uu.fi.dwo.rest.dom.xapi.Result;
import nl.uu.fi.dwo.rest.dom.xapi.Statement;
import nl.uu.fi.dwo.rest.dom.xapi.Verb;

@Singleton
public class PageTracker implements ValueChangeHandler<String>, Historian {
	
	private static Logger LOG = Logger.getLogger(PageTracker.class.getName());
	private HandlerRegistration reg;
	private Historian historian;
	private DwoGlobalVars vars;
	@Inject Lazy<RPCHandler> rpc;
	private Promise<LogStrategy> log;
	@Inject Provider<Optional<XapiWrapper>> xw;
	private UrlBuilder home;
	
	private class Tracker implements HasValueChangeHandlers<String>, ValueChangeHandler<String> {
		private final EventBus bus; 

		private Tracker(EventBus bus) {
			this.bus = bus;
		}

		@Override
		public void fireEvent(GwtEvent<?> event) {
			bus.fireEventFromSource(event, this);			
		}

		@Override
		public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
			return bus.addHandlerToSource(ValueChangeEvent.getType(), this, handler)::removeHandler;
		}

		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
			//bus.fireEventFromSource(event, this); 
			String value = event.getValue(); // use copy.
			ValueChangeEvent.fire(this,  value);
		}
		
	}
	private Tracker tracker;
	
	public HandlerRegistration onTrack(ValueChangeHandler<String> handler) {
		return tracker.addValueChangeHandler(handler);
	}

	private void fireTrack(String track) {
		ValueChangeEvent.fire(tracker, track);
	}
	

	@Inject PageTracker(DwoGlobalVars vars, Historian historian, EventBus bus) {
		this.historian = historian;
		this.vars = vars;
		this.tracker = new Tracker(bus);
		home = Location.createUrlBuilder().setHash(null).removeParameter("a");
		addValueChangeHandler(tracker);
		//logon();
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		String value = event.getValue();
		track(value);
	}

	protected void track(String value) {
		LOG.info("page tracker " + value);
		if(log != null) {
			log.flatMap( (LogStrategy x) -> x.saveStatement(build(value)))
			.then(p -> { LOG.info("statement " + p.getValue()); return p;});
		}
	}
	
	private Statement build(String value) {
		Statement s = new Statement();
		s.verb = new Verb();
		s.verb.id = "http://id.tincanapi.com/verb/viewed";
		s.object = new Activity();
		s.object.id = home.setHash(value).buildString();
		return s;
	}

	public void logon() {
		RoleType role = vars.getRoleType();
		if (role == RoleType.STUDENT) {
		DomSchool school = vars.getSchool();
		String rights = school.getSchoolRights();
		String token = historian.getToken();
		LOG.warning(" started logging for " + role + " " + rights + " " + token);
		// het idee is student + t in rights, de t van track
		if (rights.contains("t")) {
			if (reg == null) reg = tracker.addValueChangeHandler(this);
			log = rpc.get().getLRS().map(x -> x::saveStatement);
	        if (xw != null) {
				Optional<XapiWrapper> oxw = xw.get();
	        	log = log.map(m -> oxw.map(w -> w.wrap(m)).orElse(m));
	        }
	        return;
		} }
		log = null;
	}
	
	public void logoff() {
		log = null;
		if (reg != null) {
			reg.removeHandler();
			reg = null;
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> valueChangeHandler) {
		return historian.addValueChangeHandler(valueChangeHandler);
	}

	@Override
	public String getToken() {
		return historian.getToken();
	}

	@Override
	public void newItem(String token, boolean issueEvent) {
		historian.newItem(token, issueEvent);
		if (!issueEvent) { fireTrack(token); } // zelfs met !issueEvent toch tracking
		
	}

	public void back() {
		History.back();
	}

	public void replaceItem(String token, boolean b) {
		History.replaceItem(token, b);		
	}
	
}
