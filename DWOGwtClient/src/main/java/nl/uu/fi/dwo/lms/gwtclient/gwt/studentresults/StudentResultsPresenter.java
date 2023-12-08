package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.RootPanel;
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
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.StudentModelPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
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

	public static final String BEGRIPPEN_EN_VAKTAAL = "Begrippen en vaktaal";
	private static final Logger LOG = Logger.getLogger(StudentResultsPresenter.class.getName());

	public interface Display extends BasicDisplay {
		String getId();
		void setTitle(String title);
	}
	
	private final LoggingFailure FAILURE;
	protected Display view;
	private RootPanel root;
	private final String lang;
	
	@Inject Lazy<StudentResultsWidget> widget;
	@Inject Lazy<StudentResultsGraph> graph;
	final private StudentResults service;
	@Inject DescriptionPresenter description;
	private HandlerRegistration ref;
	
	@Inject
	protected StudentResultsPresenter(EventBus bus, DwoGlobalVars vars, StudentResults service) {
		super(bus, vars);
		FAILURE = new LoggingFailure(LOG, bus);
		lang = LocaleInfo.getCurrentLocale().getLocaleName();
		this.service = service;
	}
	
	protected void setBackVisible(Boolean b) {
		widget.get().setBackVisible(b);
	}
	
	protected boolean isMethod() {
		return widget.get().isMethod();
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
		view.init();
		root.clear();
		service.clear();
		view.setHelp(dwoGlobalVars.buildHelpUrl("#studentresults"));
		showGraph = false;
		service.getModels().then(this::getModels, FAILURE);
	}
	
	public static final DomStudentModelScore NULLSCORE = new DomStudentModelScore();
	{
		NULLSCORE.setScore(0, 0, 0, 0);
	}

	List<DomStudentModelContext4Student> list;
	DomStudentModelContext4Student current;
	Map<String, DomStudentModelContextInfo> currentInfo = new HashMap<String, DomStudentModelContextInfo>();

	protected void setupTree(DomStudentModelContext4Student item) {
		final StudentResultsWidget w = widget.get();
		w.tree.removeItems();
		w.setFilter(filter, method);
		setCurrentInfo(item.getModelStructure());
		if (w.isMethod()) insertMethodTree(item, method);
		else insertTree(item);
	}
	
	class ModelChange implements ChangeHandler, ClickHandler, ValueChangeHandler<Boolean> {
		
		
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
			currentInfo.clear();
			if (selection == 0) return;
			DomStudentModelContext4Student item = list.get(selection-1);
			service.getModel(item).then(p -> {
				current = p.getValue();
				return service.getActiveMethod(current.getModelStructure());
			}).then( p -> {
				method = p.getValue();
				filter = getCurrentFilter(current);
				w.setFilter(filter, method);
				setCurrentInfo(current.getModelStructure());
				if (w.isMethod()) insertMethodTree(item, method);
				else insertTree(item);
				return p;
			}, FAILURE);
		}

		private ModelChange(List<DomStudentModelContext4Student> list) {
			StudentResultsPresenter.this.list = list;
		}

		@Override
		public void onClick(ClickEvent event) {
			if (current != null) {
				if (widget.get().isBack()) {
					doBack(current);
				} else
				if (widget.get().isFilter()) {
					doFilter(current);
				} else
					showHideGraph(current);			
			}
		}

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			onChange(null);
		}
		
	}

	private void insertTree(DomStudentModelContext4Student item) {
		StudentResultsTree tree = widget.get().tree;
		Promise<DomStudentModelDataScore> promisedScore = service.getScore(item);
		tree.setMethod(method);
		TreeItem ti = tree.getRoot(item);
		promisedScore.then(s -> {
          DomStudentModelStructureScore score = s.getValue().getDomStudentModelStructureScore();
          applyFilter(score);
  		  String title = StudentModelPresenter.getTitle(item.getModelStructure().getInfo(),lang);
          ti.setWidget(Util.summaryItem(title, score ,0));
          addToTree(ti, item);
          ti.setState(true);
		  return s;
		});
	}

	
	private void insertMethodTree(DomStudentModelContext4Student item, DomMethod method) {
		StudentResultsTree tree = widget.get().tree;
		Promise<DomStudentModelDataScore> promisedScore = service.getScore(item);
		tree.setMethod(method);
		tree.filter = filter;
		tree.insertMethodTree(item, promisedScore);
	}


	public void setCurrentInfo(DomStudentModelStructure model) {
		setCurrentInfo(model.getCategories(), model.getInfo(), currentInfo);
	}
	
	public static void setCurrentInfo(List<DomStudentModelCategory> categories, DomStudentModelContextInfo info, Map<String, DomStudentModelContextInfo> currentInfo) {
		currentInfo.put(info.getId(), info);
		if (categories != null) {
			for (DomStudentModelCategory item: categories) {
				setCurrentInfoObj(item.getObjectives(), item.getInfo(), currentInfo);
			}
		}		
	}

	private static void setCurrentInfoObj(List<DomStudentModelObj> objectives, DomStudentModelContextInfo info, Map<String, DomStudentModelContextInfo> currentInfo) {
		currentInfo.put(info.getId(), info);
		if (objectives != null) {
			for (DomStudentModelObj item: objectives) {
				setCurrentInfoObj(item.getObjectives(), item.getInfo(), currentInfo);
			}
		}
	}

	private void applyFilter(DomStudentModelStructureScore score) {
		applyFilter(score, filter, currentInfo, method);
	}
	
	
	public static  void applyFilter(DomStudentModelStructureScore score, Map<String, Map<String, Set<Integer>>> filter, Map<String, DomStudentModelContextInfo> currentInfo, DomMethod method) {
		long greenCount = 0, redCount = 0, totalCount = 0;
		double greenScore = 0, redScore = 0;
		for (DomStudentModelCategoryScore cat: score.getCategories()) {
			applyFilter(cat, filter, currentInfo, method);
			greenCount += cat.getGreenCount();
			redCount += cat.getRedCount();
			totalCount += cat.getTotalCount();
			if (cat.getGreenCount() > 0) greenScore += cat.getGreenScore();
			if (cat.getRedCount() > 0) redScore += cat.getRedScore();
		}
		score.setScore(greenScore, greenCount, redScore, redCount, totalCount);		
	}

	private static  void applyFilter(DomStudentModelCategoryScore cat, Map<String, Map<String, Set<Integer>>> filter, Map<String, DomStudentModelContextInfo> currentInfo, DomMethod method) {
		long greenCount = 0, redCount = 0, totalCount = 0;
		double greenScore = 0, redScore = 0;
		for (DomStudentModelObjectiveScore obj : cat.getObjectives()) {
			if (applyFilter(obj, filter, currentInfo, method)) {
				greenCount += obj.getGreenCount();
				redCount += obj.getRedCount();
				totalCount += obj.getTotalCount();
				if (obj.getGreenCount() > 0) greenScore += obj.getGreenScore();
				if (obj.getRedCount() > 0) redScore += obj.getRedScore();				
			}
		}
		cat.setScore(greenScore, greenCount, redScore, redCount, totalCount);
	}

	private static boolean applyFilter(DomStudentModelObjectiveScore obj, Map<String, Map<String, Set<Integer>>> filter, Map<String, DomStudentModelContextInfo> currentInfo, DomMethod method) {
		if (obj.getChildren() == null) {
	// leaf
			DomStudentModelContextInfo info = currentInfo.get(obj.getId());
			if (info == null) return false;
			return contains(filter, info.getMethods(), method);		
		}
   // interior node
		long greenCount = 0, redCount = 0, totalCount = 0;
		double greenScore = 0, redScore = 0;
		for (DomStudentModelObjectiveScore child : obj.getChildren()) {
			if (applyFilter(child, filter, currentInfo, method)) {
				greenCount += child.getGreenCount();
				redCount += child.getRedCount();
				totalCount += child.getTotalCount();
				if (child.getGreenCount() > 0) greenScore += child.getGreenScore();
				if (child.getRedCount() > 0) redScore += child.getRedScore();				
			}
		}
		obj.setScore(greenScore, greenCount, redScore, redCount, totalCount);
		return true;
		
	}

	protected void doFilter(DomStudentModelContext4Student item) {
	}

	protected void doBack(DomStudentModelContext4Student item) {
		LOG.info("do back " + item);
	}
	
	boolean showGraph;
	protected Map<String,Map<String, Set<Integer>>> filter = Collections.emptyMap();
	protected DomMethod method;
	
	void showHideGraph(DomStudentModelContext4Student item) {
		JSONObject json = new JSONObject();
		json.put("title", new JSONString(StudentModelPresenter.getTitle(item.getModelStructure().getInfo(),lang)));
		json.put("id", new JSONString(item.getId().getIdString()));
		json.put("method", JSONBoolean.getInstance(isMethod()));
		SwitchViewEvent ev = onGraphEvent(json);
		eventBus.fireEvent(ev);
	}

	protected SwitchViewEvent onGraphEvent(JSONObject json) {
		return new SwitchViewEvent(SwitchViewEvent.SelectedView.STUDENTRESULTSGRAPH, json.getJavaScriptObject());
	}
	
	Promise<?> getModels(Promise<List<DomStudentModelContext4Student>> p) {
		StudentResultsWidget w = widget.get();
		List<DomStudentModelContext4Student> list = p.getValue();
		ModelChange changes = new ModelChange(list);
		StudentResultsTree tree = w.tree;
		w.description.clear();
		w.title.setText("");
		w.filter.setText("");
		w.east.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
		tree.removeItems();
		String first = w.models.getItemText(0);
		w.models.clear();
		w.models.addItem(first);
		for (DomStudentModelContext4Student item : list) {
			DomStudentModelStructure structure = item.getModelStructure();
			String title = StudentModelPresenter.getTitle(structure.getInfo(),lang);
			w.models.addItem(title);
		}

		ref = HandlerRegistrations.compose(
				eventBus.addHandlerToSource(ChangeEvent.getType(), w, changes),
				eventBus.addHandlerToSource(ClickEvent.getType(), w, changes),
				eventBus.addHandlerToSource(ValueChangeEvent.getType(), w, changes),
				tree.addSelectionHandler(this)
		);
		
		root.add(w);
		
		if (list.size() == 1) {
			w.models.setSelectedIndex(1);
			changes.onChange(null);
		}
		return null;
	}

	private void onMethodSelection(TreeItem item, Object userObject) {
		widget.get().east.clearVisibility();
		StudentResultsTree tree = widget.get().tree;
		DomStudentModelScore<?> score = tree.scoreMap.get(item);
		if ("W:".equals(userObject)) {
			userObject = item.getParentItem().getUserObject();
		}
		if (userObject instanceof DomStudentModelScore<?>) {
			score = (DomStudentModelScore<?>) userObject;
			String id = score.getId();
			DomStudentModelContextInfo info = currentInfo.get(id);
			String text;
			setDescription(info);
	        text = StudentModelPresenter.getTitle(info,lang);
            widget.get().title.setText(text);
		} else if (userObject instanceof Integer) {
			widget.get().title.setText(method.books.get(((Integer) userObject).intValue()));
			widget.get().description.clear();
		} else if (userObject instanceof int[]) {
			int[] arr = (int[]) userObject;
			String text = method.chapters.get(arr[0]).get(arr[1]);
			widget.get().title.setText(text);
			widget.get().description.clear();
		} else {
			widget.get().title.setText(method.getMethod());
			widget.get().description.clear();
		}	
		if (score != null) setPerc(score); else setPerc(NULLSCORE);
	}
	
	
	
	
	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		TreeItem item = event.getSelectedItem();
		LOG.info("selected " + item);
		Object userObject = item.getUserObject();
		if (widget.get().isMethod()) {
			onMethodSelection(item, userObject);
			return;
		}
		
		
		
		widget.get().east.clearVisibility();
		if (userObject instanceof DomStudentModelContext4Student) {
			DomStudentModelContext4Student model = (DomStudentModelContext4Student) userObject;
			addToTree(item, model);
			
		} else if (userObject instanceof Integer) {
			DomStudentModelContext4Student model = (DomStudentModelContext4Student) item.getParentItem().getUserObject();
			DomStudentModelStructure structure = model.getModelStructure();
			DomStudentModelCategory o = structure.getCategories().get(((Integer) userObject).intValue());
			setDescription(o.getInfo());
            String text = StudentModelPresenter.getTitle(o.getInfo(),lang);
            widget.get().title.setText(text);
			service.getScore(model).then(p -> { 
				DomStudentModelCategoryScore score = p.getValue().getDomStudentModelStructureScore().getCategories().get(((Integer) userObject).intValue());
				setPerc(score);
				return p; }, FAILURE)
			.then(p -> {
                return widget.get().tree.addToTree(item, userObject, o, p, filter); },  p -> item.removeItems() );			
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
	        text = StudentModelPresenter.getTitle(oo.getInfo(),lang);
            widget.get().title.setText(text);

            service.getScore(model).then( p -> { 
				DomStudentModelObjectiveScore score = p.getValue().getDomStudentModelStructureScore().getCategories().get(cat).getObjectives().get(obj);
				for (int i = 2; i < elems.length; i++ ) score = score.getChildren().get(elems[i]);
				setPerc(score);
				return p; }, FAILURE)
			.then (p -> {
				return widget.get().tree.addToTree(item, elems, cat, obj, oo, p, filter);
			}, p-> item.removeItems());
		}
		
	}


	public static boolean contains(Map<String, Map<String, Set<Integer>>> filter,
			Map<String, Map<String, Set<Integer>>> methodes, DomMethod method) {
		if (filter.isEmpty()) return true;
		for (Map.Entry<String, Map<String, Set<Integer>>> entry : filter.entrySet()) {
		    if (entry.getKey().isEmpty()) {
		      final String currentKey = method.key();
		      //if (methodes.values().stream().allMatch(Map::isEmpty)) return true;
		      if ( methodes.entrySet().stream().allMatch(e -> e.getValue().isEmpty()||!e.getKey().equals(currentKey))) return true;
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

	boolean inFilter(Map<String, Map<String, Set<Integer>>> filter, DomStudentModelObj oo) {
		Map<String, Map<String, Set<Integer>>> methods = oo.getInfo().getMethods();
		return contains(filter, methods, method);
	}

	 static boolean inFilter(Map<String, Map<String, Set<Integer>>> filter, DomStudentModelMethodInfo info, DomMethod method) {
		if (info.getMethod() == null)
			return contains(filter, Collections.emptyMap(), method);
		return contains(filter, Collections.singletonMap(info.getMethod(), Collections.singletonMap(info.getBook(), Collections.singleton(info.getChapter()))), method);
	}

	
	private void addToTree(TreeItem item, DomStudentModelContext4Student model) {
		DomStudentModelStructure structure = model.getModelStructure();
		setDescription(structure.getInfo());

		Promise<DomStudentModelDataScore> promisedScore = service.getScore(model).then ( p -> {
			DomStudentModelStructureScore score = p.getValue().getDomStudentModelStructureScore();
			setPerc(score);
			return p;
		}, FAILURE);
		widget.get().tree.addToTree(item, model, promisedScore, model.getFilter());
	}

	private void setDescription(DomStudentModelContextInfo info) {
		showGraph = false;
		widget.get().east.setDescription(current, info);
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
			if (list != null)
			for(int i = 0; i < list.size(); i++) {
				if(pid .equals (list.get(i).getId())) { index = i+1; break; }
			}
			widget.get().models.setSelectedIndex(index);
			service.getModel(cid).then(p -> {
				current = p.getValue();
				return service.getActiveMethod(current.getModelStructure());
			}).then( p -> {
				method = p.getValue();
				filter = current.getFilter();
				widget.get().setFilter(filter, method);
				currentInfo = widget.get().tree.currentInfo;
				setCurrentInfo(current.getModelStructure());
				if (widget.get().isMethod())
					insertMethodTree(current, method);
				else
					insertTree(current);
				return p;
			}, FAILURE);
		
		}
		
	}

	protected Map<String, Map<String, Set<Integer>>> getCurrentFilter(DomStudentModelContext4Student item) {
		return item.getFilter();
	}


}
