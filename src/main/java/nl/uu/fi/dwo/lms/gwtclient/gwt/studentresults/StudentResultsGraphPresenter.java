package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;

import dagger.Lazy;
import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.AbstractResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.SingleStudentResults;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class StudentResultsGraphPresenter extends AbstractResultsPresenter {

	private static final Logger LOG = Logger.getLogger(StudentResultsGraphPresenter.class.getName());

	public interface Display extends BasicDisplay {
		String getId();
		void hide();
		void init(JavaScriptObject resultState);
	}

	private Display view;
	private RootPanel root;
	private SimplePanel main;
	private JSONObject resultState;
	private StudentResults service, currentService;
	@Inject Lazy<SingleStudentResults> single;
	@Inject Lazy<StudentResultsGraph> graph;
	private DomSchoolClass schoolclass;
	private DomStudent user;
	private DomStudentModelContext4Student context;
	
	@Inject StudentResultsGraphPresenter(EventBus bus, DwoGlobalVars vars, Display view, StudentResults service) {
		super(bus, vars);
		this.service = service;
		this.view = view;
		root = RootPanel.get(view.getId());
		root.clear();
		main = new ResizeLayoutPanel();
		main.setHeight("100%");
	}

	@Override
	public void init() {
		view.clear();
		view.init();
	}

	  @JsMethod 
	  public void close(JavaScriptObject resultState) {
	    view.clear();
	    view.hide();
	    root.clear();
	    main.clear();
	    if (context != null) 
	    	context.setFilter(graph.get().filter);
	    if (service == currentService)
	    	eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.STUDENTRESULTS, context, resultState));
	    else 
	    	eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SMSTUDENTRESULTS, user, schoolclass, context, resultState));
	  }

	public void init(DomStudentModelContext4Student context, JavaScriptObject resultState) {
		currentService = service;
		this.context = context;
		service.setContext(context);
		init2(resultState);		
	}

	private void init2(JavaScriptObject resultState) {
		root.clear();
		view.clear();
		view.init(resultState);
		this.resultState = new JSONObject(resultState);
		String id = this.resultState.get("id").isString().stringValue();
		PersistenceId pid = new PersistenceId(id);
		DomStudentModelContextId cid = new DomStudentModelContextId(pid);
		currentService.getModel(cid).then(p -> {
			root.add(main);
			main.setWidget(graph.get());
			DomStudentModelContext4Student item = p.getValue();
			return currentService.getActiveMethod(item.getModelStructure()).then(q -> {
				graph.get().setModelScore(item, currentService.getScore(item), q.getValue());
				return q;
			});
			
		}).then(null, oops -> LOG.log(Level.SEVERE, "init state", oops.getFailure()));
	}

	public void init(DomUser user, DomSchoolClass schoolClass, DomStudentModelContext4Student context, JavaScriptObject resultState2) {
		currentService = single.get();
		this.user = new DomStudent(user);
		this.schoolclass = schoolClass;
		this.context = context;
		single.get().setContext(context);
		single.get().setUser(user);
		single.get().setSchoolClass(schoolClass);
		single.get().setState(resultState2);
		init2(resultState2);		
	}
}
