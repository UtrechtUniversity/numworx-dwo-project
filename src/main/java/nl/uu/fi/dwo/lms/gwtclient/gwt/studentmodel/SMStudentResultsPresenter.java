package nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel;

import javax.inject.Inject;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.web.bindery.event.shared.EventBus;

import dagger.Lazy;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsPresenter;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;

public class SMStudentResultsPresenter extends StudentResultsPresenter {

	private final SingleStudentResults service;

	@Inject SMStudentResultsPresenter(EventBus bus, DwoGlobalVars vars, SingleStudentResults service) {
		super(bus, vars, service);
		this.service = service;
	}

	@Inject Lazy<FilterDialog> filterDialog;
	
	public void init(DomUser user, DomSchoolClass schoolClass, JavaScriptObject resultState) {
		service.setUser(user);
		service.setSchoolClass(schoolClass);
		service.setState(resultState);
		super.init(resultState);
	}

	@Override
	protected SwitchViewEvent onGraphEvent(JSONObject json) {
		json.put("user", new JSONString(service.user.getDisplayName()));
		return new SwitchViewEvent(SwitchViewEvent.SelectedView.SMSTUDENTRESULTSGRAPH, service.user, service.schoolClass, json.getJavaScriptObject());
	}

	@Override
	protected void doFilter(DomStudentModelContext4Student item) {
		FilterDialog d = filterDialog.get();
		d.setValue(filter);
		d.addCloseHandler(ev -> {
			filter = d.getValue();
			item.setFilter(d.getValue());
			setupTree(item);
			
		});
		d.show();
	}

	
}
