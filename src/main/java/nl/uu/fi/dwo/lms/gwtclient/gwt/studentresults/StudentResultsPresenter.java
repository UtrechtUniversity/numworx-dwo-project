package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.web.bindery.event.shared.EventBus;

import dagger.Lazy;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results.JsStudentResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.AbstractResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;

public class StudentResultsPresenter extends AbstractResultsPresenter {

	private static final Logger LOG = Logger.getLogger(StudentResultsPresenter.class.getName());

	public interface Display extends BasicDisplay {
		String getId();
	}

	private final LoggingFailure FAILURE;
	private Display view;
	private RootPanel root;
	private String lang;
	
	@Inject Lazy<StudentResultsWidget> widget;
	@Inject StudentResultsService service;
	
	@Inject StudentResultsPresenter(EventBus bus, DwoGlobalVars vars) {
		super(bus, vars);
		FAILURE = new LoggingFailure(LOG, bus);
		lang = LocaleInfo.getCurrentLocale().getLocaleName();
	}
	
	@Inject void setView(JsStudentResultsView view) {
		this.view = view;
		attachWidget(view);
	}

	private void attachWidget(Display view) {
		this.view = view;
		root = RootPanel.get(view.getId());
	}
	
	public void init() {
		root.clear();
		view.setHelp(dwoGlobalVars.buildHelpUrl("#studentresults"));
		StudentResultsWidget w = widget.get();		
		service.getModels().then(this::getModels, FAILURE)
		;
	}
	
	Promise<?> getModels(Promise<List<DomStudentModelContext>> p) {
		StudentResultsWidget w = widget.get();
		List<DomStudentModelContext> list = p.getValue();
		Tree tree = w.tree;
		for (DomStudentModelContext item : list) {
			DomStudentModelStructure structure = item.getModelStructure();
			tree.addTextItem(structure.getInfo().getTitle().getOrDefault(lang, ""));
		}
		root.add(w);
		return null;
	}
}
