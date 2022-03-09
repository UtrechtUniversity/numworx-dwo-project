package nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.web.bindery.event.shared.EventBus;

import fi.dwo.gwt.lib.rest.util.StringFormatter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsPresenter;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class SMStudentResultsPresenter extends StudentResultsPresenter {

	private final SingleStudentResults service;

	@Inject SMStudentResultsPresenter(EventBus bus, DwoGlobalVars vars, SingleStudentResults service) {
		super(bus, vars, service);
		this.service = service;
	}
	
	public void init(DomUser user, DomSchoolClass schoolClass, JavaScriptObject resultState) {
		service.setUser(user);
		service.setSchoolClass(schoolClass);
		service.setState(resultState);
		super.init(resultState);
		view.setTitle(StringFormatter.format(DwoLocalesForGWT.instance.NUM_LBL_KNOWLEDGE_OF_(), user.getDisplayName()));
		setBackVisible(true);
	}
	
	public void init(DomUser user, DomSchoolClass schoolClass, DomStudentModelContext4Student context, JavaScriptObject jso) {
		service.setContext(context);
		init(user, schoolClass, jso);
	}

	@Override
	protected SwitchViewEvent onGraphEvent(JSONObject json) {
		json.put("user", new JSONString(service.user.getDisplayName()));
		return new SwitchViewEvent(SwitchViewEvent.SelectedView.SMSTUDENTRESULTSGRAPH, service.user, service.schoolClass, service.context, json.getJavaScriptObject());
	}

	
	
	
	private Map<PersistenceId, Promise<FilterMethodDialog>> filterDialogs = new HashMap<>();

	@Override
	protected void doFilter(DomStudentModelContext4Student item) {
		PersistenceId key = item.getModelStructure().getActiveMethod();
		if (key == null) return;
		Promise<FilterMethodDialog> p;
		p = filterDialogs.computeIfAbsent(key, k -> service.getActiveMethod(item.getModelStructure()).map(FilterMethodDialog::new));
		p.then( q -> {		
			FilterMethodDialog d = q.getValue();
			d.setValue(filter);
			d.addCloseHandler(ev -> {
				filter = d.getValue();
				item.setFilter(d.getValue());
				setupTree(item);
				
			});
			d.show();
			return q;
		});
	}

	@Override
	protected Map<String, Map<String, Set<Integer>>> getCurrentFilter(DomStudentModelContext4Student item) {
		PersistenceId key = item.getModelStructure().getActiveMethod();
		Promise<FilterMethodDialog> obj = filterDialogs.get(key);
		if (obj != null && obj.isDone() && obj.getFailure() == null) {
			return obj.getValue().getValue();
		}
		return super.getCurrentFilter(item);
	}

	@Override
	protected void doBack(DomStudentModelContext4Student item) {
		super.doBack(item);
		JSONObject state = new JSONObject();
		state.put("id", new JSONString(item.getId().getIdString()));
		state.put("method", JSONBoolean.getInstance(isMethod()));

		SwitchViewEvent event = new SwitchViewEvent(SwitchViewEvent.SelectedView.SMCLASSRESULTS, item, state.getJavaScriptObject());
		eventBus.fireEvent(event);
	}
	
	
}
