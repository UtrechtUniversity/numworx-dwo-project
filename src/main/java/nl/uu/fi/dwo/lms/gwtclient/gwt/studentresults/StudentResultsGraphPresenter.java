package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.logging.Logger;

import javax.inject.Inject;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;

import dagger.Lazy;
import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.AbstractResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
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
	@Inject StudentResults service;
	@Inject Lazy<StudentResultsGraph> graph;
	
	@Inject StudentResultsGraphPresenter(EventBus bus, DwoGlobalVars vars, Display view) {
		super(bus, vars);
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
	    eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.STUDENTRESULTS, resultState));
	  }

	public void init(JavaScriptObject resultState) {
		root.clear();
		view.clear();
		view.init(resultState);
		this.resultState = new JSONObject(resultState);
		String id = this.resultState.get("id").isString().stringValue();
		PersistenceId pid = new PersistenceId(id);
		DomStudentModelContextId cid = new DomStudentModelContextId(pid);
		service.getModel(cid).then(p -> {
			root.add(main);
			main.setWidget(graph.get());
			DomStudentModelContext item = p.getValue();
			graph.get().setModelScore(item, service.getScore(item));
			
			return p;
		});
		
	}
}
