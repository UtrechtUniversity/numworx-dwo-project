package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import dagger.Lazy;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results.JsStudentResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.AbstractResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategoryScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObjectiveScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;

public class StudentResultsPresenter extends AbstractResultsPresenter implements SelectionHandler<TreeItem> {

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
	private HandlerRegistration ref;
	
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
		if (ref != null) {ref.removeHandler(); ref = null;}
		root.clear();
		service.clear();
		view.setHelp(dwoGlobalVars.buildHelpUrl("#studentresults"));
		service.getModels().then(this::getModels, FAILURE)
		;
	}
	
	Promise<?> getModels(Promise<List<DomStudentModelContext>> p) {
		StudentResultsWidget w = widget.get();
		List<DomStudentModelContext> list = p.getValue();
		Tree tree = w.tree;
		for (DomStudentModelContext item : list) {
			DomStudentModelStructure structure = item.getModelStructure();
			TreeItem ti = tree.addTextItem(structure.getInfo().getTitle().getOrDefault(lang, ""));
			ti.setUserObject(item);
		}
		ref = tree.addSelectionHandler(this);
		
		root.add(w);
		return null;
	}

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		TreeItem item = event.getSelectedItem();
		widget.get().title.setText(item.getText());
		LOG.info("selected " + item);
		Object userObject = item.getUserObject();
		if (userObject instanceof DomStudentModelContext) {
			DomStudentModelContext model = (DomStudentModelContext) userObject;
			DomStudentModelStructure structure = model.getModelStructure();
			String text = structure.getInfo().getDescription().get(lang);
			Widget description = createDescription(text);
			widget.get().description.setWidget(description);
			service.getScore(model).then ( p -> {
				DomStudentModelStructureScore score = p.getValue().getDomStudentModelStructureScore();
				widget.get().perc.setText(Math.round(score.getScore()*100) + "/" + score.getCount() + "%");
				return p;
			}, FAILURE)
			.then(p -> { 
				if (item.getChildCount() != structure.getCategories().size()) {
					item.removeItems();
					int cat = 0;
					for (DomStudentModelCategory o : structure.getCategories()) {
						TreeItem tt = item.addTextItem(o.getInfo().getTitle().getOrDefault(lang, ""));
						tt.setUserObject(cat);
						cat++;
					}
				}
				return p; });
			
		} else if (userObject instanceof Integer) {
			DomStudentModelContext model = (DomStudentModelContext) item.getParentItem().getUserObject();
			DomStudentModelStructure structure = model.getModelStructure();
			DomStudentModelCategory o = structure.getCategories().get(((Integer) userObject).intValue());
			String text = o.getInfo().getDescription().get(lang);
			Widget description = createDescription(text);
			widget.get().description.setWidget(description);
			service.getScore(model).then(p -> { 
				DomStudentModelCategoryScore score = p.getValue().getDomStudentModelStructureScore().getCategories().get(((Integer) userObject).intValue());
				widget.get().perc.setText(Math.round(score.getScore()*100) + "/" + score.getCount() + "%");
			
				return p; }, FAILURE)
			.then(p -> {
				if (item.getChildCount() != o.getObjectives().size()) {
					item.removeItems();
					int cat = ((Integer) userObject).intValue();
					int obj = 0;
					for( DomStudentModelObj oo : o.getObjectives()) {
						TreeItem tt = item.addTextItem(oo.getInfo().getTitle().getOrDefault(lang, ""));
						tt.setUserObject(new int[] { cat, obj } );
						obj++;
					}
				}
				return p; },  p -> item.removeItems() );			
		} else if (userObject instanceof int[]) {
			int[] elems = (int[]) userObject;
			int cat = elems[0], obj = elems[1];
			TreeItem top = item.getParentItem().getParentItem();
			DomStudentModelContext model = (DomStudentModelContext) top.getUserObject();
			DomStudentModelStructure structure = model.getModelStructure();
			DomStudentModelCategory o = structure.getCategories().get(cat);
			DomStudentModelObj oo = o.getObjectives().get(obj);
			String text = oo.getInfo().getDescription().get(lang);
			widget.get().description.setWidget(createDescription(text));
			service.getScore(model).then( p -> { 
				DomStudentModelObjectiveScore score = p.getValue().getDomStudentModelStructureScore().getCategories().get(cat).getObjectives().get(obj);
				widget.get().perc.setText(Math.round(score.getScore()*100) + "/" + score.getCount() + "%");			
				return p; }, FAILURE);
		}
		
	}

	private Widget createDescription(String text) {
		Widget description;
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		if (text == null) text = "";
		builder.appendEscapedLines(text);
		description = new HTML(builder.toSafeHtml());
		return description;
	}
}
