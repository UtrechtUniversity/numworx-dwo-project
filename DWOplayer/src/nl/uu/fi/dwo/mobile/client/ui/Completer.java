package nl.uu.fi.dwo.mobile.client.ui;

import java.util.Collections;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;

import org.osgi.util.promise.Promise;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import dagger.Lazy;
import fi.dwo.gwt.lib.rest.CallManagers.XapiManager;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.sco.SMLogger.LogStrategy;
import nl.uu.fi.dwo.mobile.client.ui.views.XapiWrapper;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.CheckButton;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.xapi.Account;
import nl.uu.fi.dwo.rest.dom.xapi.Activity;
import nl.uu.fi.dwo.rest.dom.xapi.ActivityDefinition;
import nl.uu.fi.dwo.rest.dom.xapi.Context;
import nl.uu.fi.dwo.rest.dom.xapi.Group;
import nl.uu.fi.dwo.rest.dom.xapi.Statement;
import nl.uu.fi.dwo.rest.dom.xapi.Verb;

public class Completer implements CBookEventListener {
	final static HandlerRegistration NULL = () -> {};

	private static final Verb COMPLETED = new Verb();
	static {
		COMPLETED.id = "http://adlnet.gov/expapi/verbs/completed";
		COMPLETED.display = Collections.singletonMap("en-US", "completed");
	}
	
	final DwoGlobalVars vars;
	@Inject Lazy<RPCHandler> rpc;
	@Inject Provider<Optional<XapiWrapper>> xw;
	private Activity activity;
	private OpdrNavIF memento;

	@Inject Completer(DwoGlobalVars vars) {
		this.vars = vars;
	}

	
	public HandlerRegistration start(EventBus eventbus, SelectModuleItem sco, OpdrNavIF comRoot) {
		if (vars.getRoleType() == RoleType.STUDENT && vars.isPremium()) {
			activity = new Activity();
			activity.id = "pid:" + sco.original().getId();
			activity.definition = new ActivityDefinition();
			activity.definition.name = Collections.singletonMap("unk", sco.getName());
			activity.definition.type = "http://www.dwo.nl/type/" + sco.original().getId().getType();
			memento = comRoot;
			return eventbus.addHandler(CBookEvent.TYPE, this);
		} else {
			return NULL;
		}
	}
	
	Promise<String> send (Promise<XapiManager> p) {
		Statement s = new Statement();
		s.verb = COMPLETED;
		s.object = activity;		
		s.context = new Context();
		s.actor = p.getValue().getAgent();
		DomSchoolClass team = vars.getCurrentSchoolClass();
		if  (team != null) {
			Group group = new Group();
			s.context.team = group;
	    	group.account = new Account();
	    	group.account.name = "pid:" +team.getId().getIdString();
	    	group.name = team.getSchoolClassName();
		}
		s.context.registration = memento.getContext().getString("registration");
		final LogStrategy delegate = p.getValue()::saveStatement;
		LogStrategy m = xw.get().map(w -> {
			return w.wrap(delegate);
		}).orElse(delegate);
		return m.saveStatement(s);
	}
	
	
	@Override
	public void acceptCBookEvent(CBookEvent event) {
		if (CheckButton.AFRONDEN == event.getCommand()) {
			rpc.get().getLRS().then(this::send);
		}

	}

}
