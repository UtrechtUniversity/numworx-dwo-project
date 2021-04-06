package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.HandlerRegistrations;

import dagger.Lazy;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results.JsStudentResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.AbstractResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategoryScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelMethodInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObjectiveScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

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
	@Inject Lazy<StudentResultsGraph> graph;
	@Inject StudentResults service;
	@Inject DescriptionPresenter description;
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
		showGraph = false;
		service.getModels().then(this::getModels, FAILURE);
	}
	
	private static final DomStudentModelScore NULLSCORE = new DomStudentModelScore();
	{
		NULLSCORE.setScore(0, 0, 0, 0);
	}

	List<DomStudentModelContext4Student> list;
	DomStudentModelContext4Student current;

	class ModelChange implements ChangeHandler, ClickHandler {
		
		
		@Override
		public void onChange(ChangeEvent event) {
			final StudentResultsWidget w = widget.get();
			int selection = w.models.getSelectedIndex();
			LOG.info("selection = " + selection);
			w.tree.removeItems();
			w.title.setText("");
			w.filter.setText("");
			w.description.clear();
			w.setPerc(NULLSCORE);
			w.east.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
			current = null;
			if (selection == 0) return;
			DomStudentModelContext4Student item = list.get(selection-1);
			w.setFilter(item.getFilter());
			service.getModel(item).then(p -> {
				current = p.getValue();
				insertTree(item);
				return p;
			}, FAILURE);
		}

		private ModelChange(List<DomStudentModelContext4Student> list) {
			StudentResultsPresenter.this.list = list;
		}

		@Override
		public void onClick(ClickEvent event) {
			if (current != null) showHideGraph(current);			
		}
		
	}

	private void insertTree(DomStudentModelContext4Student item) {
		Tree tree = widget.get().tree;
		tree.removeItems();
		DomStudentModelStructure structure = item.getModelStructure();
		String title = structure.getInfo().getTitle().getOrDefault(lang, "");
		Widget html = Util.summaryItem(title, NULLSCORE ,0);
        TreeItem ti = tree.addItem(html);
		ti.setUserObject(item);
		service.getScore(item).then(s -> {
          DomStudentModelStructureScore score = s.getValue().getDomStudentModelStructureScore();
          ti.setWidget(Util.summaryItem(title, score ,0));
          //ti.setSelected(true);
          addToTree(ti, item);
          ti.setState(true);
		  return s;
		});
	}
	
	boolean showGraph;
	private Map<String,Map<String, Set<Integer>>> filter = Collections.emptyMap();
	void showHideGraph(DomStudentModelContext4Student item) {
		JSONObject json = new JSONObject();
		json.put("title", new JSONString(item.getModelStructure().getInfo().getTitle().get(lang)));
		json.put("id", new JSONString(item.getId().getIdString()));
		SwitchViewEvent ev = new SwitchViewEvent(SwitchViewEvent.SelectedView.STUDENTRESULTSGRAPH, json.getJavaScriptObject());
		eventBus.fireEvent(ev);
	}
	
	Promise<?> getModels(Promise<List<DomStudentModelContext4Student>> p) {
		StudentResultsWidget w = widget.get();
		List<DomStudentModelContext4Student> list = p.getValue();
		ModelChange changes = new ModelChange(list);
		Tree tree = w.tree;
		w.description.clear();
		w.title.setText("");
		w.east.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
		tree.removeItems();
		String first = w.models.getItemText(0);
		w.models.clear();
		w.models.addItem(first);
		for (DomStudentModelContext4Student item : list) {
			DomStudentModelStructure structure = item.getModelStructure();
			String title = structure.getInfo().getTitle().getOrDefault(lang, "");
			w.models.addItem(title);
		}

		ref = HandlerRegistrations.compose(
				eventBus.addHandlerToSource(ChangeEvent.getType(), w, changes),
				eventBus.addHandlerToSource(ClickEvent.getType(), w, changes),				
				tree.addSelectionHandler(this)
		);
		
		root.add(w);
		
		if (list.size() == 1) {
			w.models.setSelectedIndex(1);
			changes.onChange(null);
		}
		return null;
	}

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		TreeItem item = event.getSelectedItem();
		widget.get().east.getElement().getStyle().clearVisibility();
		LOG.info("selected " + item);
		Object userObject = item.getUserObject();
		if (userObject instanceof DomStudentModelContext4Student) {
			DomStudentModelContext4Student model = (DomStudentModelContext4Student) userObject;
			addToTree(item, model);
			
		} else if (userObject instanceof Integer) {
			DomStudentModelContext4Student model = (DomStudentModelContext4Student) item.getParentItem().getUserObject();
			DomStudentModelStructure structure = model.getModelStructure();
			DomStudentModelCategory o = structure.getCategories().get(((Integer) userObject).intValue());
			setDescription(o.getInfo());
            String text = o.getInfo().getTitle().get(lang);
            widget.get().title.setText(text);
			service.getScore(model).then(p -> { 
				DomStudentModelCategoryScore score = p.getValue().getDomStudentModelStructureScore().getCategories().get(((Integer) userObject).intValue());
				setPerc(score);
				return p; }, FAILURE)
			.then(p -> {
                return addToTree(item, userObject, o, p, filter); },  p -> item.removeItems() );			
		} else if (userObject instanceof int[]) {
			int[] elems = (int[]) userObject;
			int cat = elems[0], obj = elems[1];
			TreeItem top = item;
			for (int i = 0; i < elems.length; i++ ) top = top.getParentItem();
			DomStudentModelContext4Student model = (DomStudentModelContext4Student) top.getUserObject();
			DomStudentModelStructure structure = model.getModelStructure();
			DomStudentModelCategory o = structure.getCategories().get(cat);

			DomStudentModelObj o0 = o.getObjectives().get(obj);
			for (int i = 2; i < elems.length; i++ ) {
				o0 = o0.getObjectives().get(elems[i]);
			}
			final DomStudentModelObj oo = o0;
			String text;
			setDescription(oo.getInfo());
	        text = oo.getInfo().getTitle().get(lang);
            widget.get().title.setText(text);

            service.getScore(model).then( p -> { 
				DomStudentModelObjectiveScore score = p.getValue().getDomStudentModelStructureScore().getCategories().get(cat).getObjectives().get(obj);
				for (int i = 2; i < elems.length; i++ ) score = score.getChildren().get(elems[i]);
				setPerc(score);
				return p; }, FAILURE)
			.then (p -> {
				return addToTree(item, elems, cat, obj, oo, p, filter);
			}, p-> item.removeItems());
		}
		
	}

	private Promise<DomStudentModelDataScore> addToTree(TreeItem item, int[] elems, int cat, int obj,
			final DomStudentModelObj oo, Promise<DomStudentModelDataScore> p, Map<String, Map<String, Set<Integer>>> filter) {
		if (oo.getObjectives() != null && oo.getObjectives().size() != item.getChildCount()) {
			DomStudentModelObjectiveScore score = p.getValue().getDomStudentModelStructureScore().getCategories().get(cat).getObjectives().get(obj);
			for (int i = 2; i < elems.length; i++ ) score = score.getChildren().get(elems[i]);
			int oobj = 0;
			for (DomStudentModelObj ooo: oo.getObjectives()) {
				DomStudentModelObjectiveScore s = score.getChildren().get(oobj);
				TreeItem tt;
				if (s.getChildren() != null)
					tt = item.addItem(Util.summaryItem(ooo.getInfo().getTitle().getOrDefault(lang, ""), s,3));
				else
				{	boolean add = inFilter(filter, ooo);
					tt = item.addItem(Util.scoreItem(ooo.getInfo().getTitle().getOrDefault(lang, ""), s,3));
					tt.setVisible(add);
				}
				int[] oelems = new int[elems.length+1];
				System.arraycopy(elems, 0, oelems, 0, elems.length);
				oelems[elems.length] = oobj;
				{
					tt.setUserObject(oelems);
					addToTree(tt, oelems, cat, obj, ooo, p, filter);
					if (s.getChildren() != null && getVisibleChildCount(tt) == 0) 
						tt.setVisible(false);
				}
				oobj++;
			}
		}
		return p;
	}

	private int getVisibleChildCount(TreeItem tt) {
		int cnt = 0;
		int len = tt.getChildCount();
		for (int i = 0; i < len; i++) {
			if (tt.getChild(i).isVisible()) cnt++;
		}
		return cnt;
	}

	private Promise<DomStudentModelDataScore> addToTree(TreeItem item, Object userObject, DomStudentModelCategory o,
			Promise<DomStudentModelDataScore> p, Map<String, Map<String, Set<Integer>>> filter) {
		DomStudentModelCategoryScore score = p.getValue().getDomStudentModelStructureScore().getCategories().get(((Integer) userObject).intValue());
		if (item.getChildCount() != o.getObjectives().size()) {
			item.removeItems();
			int cat = ((Integer) userObject).intValue();
			int obj = 0;
			for( DomStudentModelObj oo : o.getObjectives()) {
			    float ppp;
			    DomStudentModelObjectiveScore s = score.getObjectives().get(obj);
				TreeItem tt;
				if (s.getChildren() != null)
					tt = item.addItem(Util.summaryItem(oo.getInfo().getTitle().getOrDefault(lang, ""), s,2));
				else
				{	boolean add = inFilter(filter, oo);
					tt = item.addItem(Util.scoreItem(oo.getInfo().getTitle().getOrDefault(lang, ""), s,2));
					tt.setVisible(add);
				}
				int[] elems = new int[] { cat, obj };
				{
					tt.setUserObject(elems );
					addToTree(tt, elems, cat, obj, oo, p, filter);
					if (s.getChildren() != null && getVisibleChildCount(tt) == 0) 
						tt.setVisible(false);
				}

				obj++;
			}
		}
		return p;
	}
	static boolean contains(Map<String, Map<String, Set<Integer>>> filter,
			Map<String, Map<String, Set<Integer>>> methodes) {
		for (Map.Entry<String, Map<String, Set<Integer>>> entry : filter.entrySet()) {
		    if (entry.getKey().isEmpty()) {
		      if (methodes.values().stream().allMatch(Map::isEmpty)) return true;
		      continue;
		    }		  
			Map<String, Set<Integer>> map = methodes.getOrDefault(entry.getKey(), Collections.emptyMap());
			if (map.isEmpty())
			{ 
			  continue;
			}
			if (entry.getValue().isEmpty()) return true;
			for (Map.Entry<String, Set<Integer>> m : entry.getValue().entrySet()) {
				Set<Integer> chapters = map.getOrDefault(m.getKey(), Collections.emptySet());
				if (chapters.isEmpty()) continue;
				if (m.getValue().isEmpty()) return true;
				chapters = new TreeSet<>(chapters);
				chapters.retainAll(m.getValue());
				if (!chapters.isEmpty())
					return true;
			}
		}
		return false;
	}

	static boolean inFilter(Map<String, Map<String, Set<Integer>>> filter, DomStudentModelObj oo) {
		Map<String, Map<String, Set<Integer>>> methods = oo.getInfo().getMethods();
		return contains(filter, methods);
	}

	static boolean inFilter(Map<String, Map<String, Set<Integer>>> filter, DomStudentModelMethodInfo info) {
		if (info.getMethod() == null)
			return contains(filter, Collections.emptyMap());
		return contains(filter, Collections.singletonMap(info.getMethod(), Collections.singletonMap(info.getBook(), Collections.singleton(info.getChapter()))));
	}

	private void addToTree(TreeItem item, DomStudentModelContext4Student model) {
		DomStudentModelStructure structure = model.getModelStructure();
		Map<String, Map<String, Set<Integer>>> filter = model.getFilter();
		String text;
		setDescription(structure.getInfo());
		text = structure.getInfo().getTitle().get(lang);
		widget.get().title.setText(text);
		service.getScore(model).then ( p -> {
			DomStudentModelStructureScore score = p.getValue().getDomStudentModelStructureScore();
			setPerc(score);
			return p;
		}, FAILURE)
		.then(p -> { 
			if (item.getChildCount() != structure.getCategories().size()) {
				item.removeItems();
				int cat = 0;
				for (DomStudentModelCategory o : structure.getCategories()) {
		            DomStudentModelCategoryScore score = p.getValue().getDomStudentModelStructureScore().getCategories().get(cat);
					TreeItem tt = item.addItem(
					  Util.summaryItem(o.getInfo().getTitle().getOrDefault(lang, ""), (score),1));
					tt.setUserObject(cat);
					addToTree(tt, cat, o, p, filter);
					if (getVisibleChildCount(tt) == 0) 
						tt.setVisible(false);
					cat++;
				}
			}
			return p; })
		.then(null, FAILURE);
	}

	private void setDescription(DomStudentModelContextInfo info) {
		showGraph = false;
		description.get(current, info)
		.then(p -> { Widget description = p.getValue();
			widget.get().description.setWidget(description);
			return null;
		});
	}

	private void setPerc(DomStudentModelScore<?> score) {
		widget.get().setPerc(score);
	}

  private JSONObject resultState;
    
  

	public void init(JavaScriptObject resultState) {
		init();
		if (resultState != null) {
			this.resultState = new JSONObject(resultState);
			String id = this.resultState.get("id").isString().stringValue();
			PersistenceId pid = new PersistenceId(id);
			DomStudentModelContextId cid = new DomStudentModelContextId(pid);
			int index = 0;
			for(int i = 0; i < list.size(); i++) {
				if(pid .equals (list.get(i).getId())) { index = i+1; break; }
			}
			widget.get().models.setSelectedIndex(index);
			service.getModel(cid).then(p -> {
				current = p.getValue();
				insertTree(current);
				return p;
			}, FAILURE);
		
		}
		
	}


}
