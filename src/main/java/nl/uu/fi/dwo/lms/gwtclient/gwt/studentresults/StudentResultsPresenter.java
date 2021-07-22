package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.HasText;
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

	private static final Logger LOG = Logger.getLogger(StudentResultsPresenter.class.getName());

	public interface Display extends BasicDisplay {
		String getId();
	}
	
	private final LoggingFailure FAILURE;
	private Display view;
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
	Map<String, DomStudentModelContextInfo> currentInfo = new HashMap<String, DomStudentModelContextInfo>();

	protected void setupTree(DomStudentModelContext4Student item) {
		final StudentResultsWidget w = widget.get();
		w.tree.removeItems();
		w.setFilter(filter, method);
		setCurrentInfo(item.getModelStructure());
		insertTree(item);
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
				filter = current.getFilter();
				w.setFilter(item.getFilter(), method);
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
		Tree tree = widget.get().tree;
		tree.removeItems();
		DomStudentModelStructure structure = item.getModelStructure();
		String title = structure.getInfo().getTitle().getOrDefault(lang, "");
		Widget html = Util.summaryItem(title, NULLSCORE ,0);
        TreeItem ti = tree.addItem(html);
		ti.setUserObject(item);
		service.getScore(item).then(s -> {
          DomStudentModelStructureScore score = s.getValue().getDomStudentModelStructureScore();
          applyFilter(score);
          ti.setWidget(Util.summaryItem(title, score ,0));
          //ti.setSelected(true);
          addToTree(ti, item);
          ti.setState(true);
		  return s;
		});
	}
	
	private void insertMethodTree(DomStudentModelContext4Student item, DomMethod method) {
		Tree tree = widget.get().tree;
		tree.removeItems();
		String title = method.getMethod();
		Map<String, Set<Integer>> bookfilter = filter.getOrDefault(method.key(), Collections.emptyMap());
		Widget html = Util.summaryItem(title, NULLSCORE, 0);
		TreeItem ti = tree.addItem(html);
		List<String> books = method.books;
		for(int i = 0; i < books.size(); i++) {
			String booktitle = books.get(i);
			if (! filter.isEmpty() && !bookfilter.containsKey(booktitle)) continue;
			Set<Integer> chapterfilter = bookfilter.getOrDefault(booktitle, Collections.emptySet());
			html = Util.summaryItem(booktitle, NULLSCORE, 1);
			TreeItem bi = ti.addItem(html);
			bi.setUserObject(Integer.valueOf(i));
			List<String> chapters = method.chapters.get(i);
			for(int j = 0; j < chapters.size(); j++) {
				if ( !chapterfilter.isEmpty() && !chapterfilter.contains(Integer.valueOf(j+1))) continue;
				html = Util.summaryItem(chapters.get(j), NULLSCORE, 2);
				TreeItem ci = bi.addItem(html);
				ci.setUserObject(new int[] {i, j});
			}
		}
		ti.setUserObject(item);
		service.getScore(item).then(s -> {
	          DomStudentModelStructureScore score = s.getValue().getDomStudentModelStructureScore();
	          ti.setWidget(Util.summaryItem(title, score ,0));
	          //ti.setSelected(true);
	          addToMethodTree(ti, item, score, method);
	          trimMethodTree(ti);
	          ti.setState(true);
			  return s;
			});
	}
	
	

	private boolean trimMethodTree(TreeItem ti) {
		int count = ti.getChildCount();
		for (int i = 0; i < count; i++) {
			TreeItem kid = ti.getChild(i);
			if (kid.getUserObject() instanceof DomStudentModelObjectiveScore) continue;
			if (trimMethodTree(kid)) {
				i--; count--;
			}
		}
		if (count == 0 && ti.getParentItem() != null) { ti.getParentItem().removeItem(ti); return true; }
		return false;
		
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
		// TODO Auto-generated method stub
		
	}

	boolean showGraph;
	protected Map<String,Map<String, Set<Integer>>> filter = Collections.emptyMap();
	protected DomMethod method;
	
	void showHideGraph(DomStudentModelContext4Student item) {
		JSONObject json = new JSONObject();
		json.put("title", new JSONString(item.getModelStructure().getInfo().getTitle().get(lang)));
		json.put("id", new JSONString(item.getId().getIdString()));
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
		Tree tree = w.tree;
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
			String title = structure.getInfo().getTitle().getOrDefault(lang, "");
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

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		TreeItem item = event.getSelectedItem();
		if (widget.get().isMethod()) return;
		
		
		
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

	private void addToMethodTree(TreeItem item, DomStudentModelContext4Student model, DomStudentModelStructureScore score, DomMethod method) {
		DomStudentModelStructure structure = model.getModelStructure();
		List<DomStudentModelCategory> cats = structure.getCategories();
		List<DomStudentModelCategoryScore> catscores = score.getCategories();
		Iterator<DomStudentModelCategory> icats = cats.iterator();
		Iterator<DomStudentModelCategoryScore> icatscores = catscores.iterator();
		while( icats.hasNext()) {
			List<DomStudentModelObj> objs = icats.next().getObjectives();
			List<DomStudentModelObjectiveScore> objscores = icatscores.hasNext() ? icatscores.next().getObjectives() : Collections.emptyList();
			addToMethodTree(item, objs, objscores, method);
		}
		scoreMap.forEach( (key, value) -> 
		{	String t = ((HasText) key.getWidget()).getText();
			int i = key.getParentItem() == null ? 0 : 1;
 			key.setWidget(Util.summaryItem(t, value, i));
		}
		);
		scoreMap.clear();
		
	}

	Map<TreeItem, DomStudentModelScore<?>> scoreMap = new HashMap<>();
	
	
	
	private void addToMethodTree(TreeItem item, List<DomStudentModelObj> objs, List<DomStudentModelObjectiveScore> objscores, DomMethod method) {
		Iterator<DomStudentModelObj> iobjs = objs.iterator();
		Iterator<DomStudentModelObjectiveScore> iobjscores = objscores.iterator();
		while (iobjs.hasNext()) {
			DomStudentModelObj obj = iobjs.next();
			if (obj.getObjectives() == null) {
				// leaf
				addToMethodTree(item, obj, iobjscores.hasNext()?iobjscores.next():NULLSCORE, method);
			} else {
				addToMethodTree(item, obj.getObjectives(), iobjscores.next().getChildren(), method);
			}
 		}
		

		
	}

	private void addToMethodTree(TreeItem item, DomStudentModelObj obj, DomStudentModelScore<?> s, DomMethod method) {
		Map<String, Map<String, Set<Integer>>> map = obj.getInfo().getMethods();
		String title = obj.getInfo().getTitle().get(lang);
		Map<String, Set<Integer>> books = map.getOrDefault(method.key(), Collections.emptyMap());
		for( Map.Entry<String, Set<Integer>> entry: books.entrySet()) {
			String book = entry.getKey();
			for (Integer chapter: entry.getValue()) {
				if (contains(filter, obj.getInfo().getMethods(), method))
					addToMethodTree(item, book, chapter, Util.scoreItem(title, s, 3), method, s);
			}
		}
	}

	private void addToMethodTree(TreeItem item, String book, Integer chapter, Widget scoreItem, DomMethod method, DomStudentModelScore<?> score) {
		int kidscount = item.getChildCount();
		for (int i = 0; i < kidscount; i++) {
			TreeItem bookitem = item.getChild(i);
			String titlebook = method.books.get(i);
			if (titlebook.equals(book)) {
				int chaptercount = bookitem.getChildCount();
				for (int j = 0; j < chaptercount; j++) {
					TreeItem chapitem = bookitem.getChild(j);
					int chapnr = ((int[])chapitem.getUserObject())[1];
					if (chapnr+1 == chapter.intValue()) {
						insertMethodTree(chapitem, scoreItem).setUserObject(score);
						insertMethodMap(chapitem, score);
						break;
					}
				}
				break;
			}
		}	
	}

	private void insertMethodMap(TreeItem chapitem, DomStudentModelScore<?> score) {
		DomStudentModelScore<?> summary = scoreMap.computeIfAbsent(chapitem, k -> {
			DomStudentModelScore<?> r = new DomStudentModelScore();
			r.setScore(0, 0, 0, 0, 0);
			return r;
		});
		long gc = score.getGreenCount();
		if (gc != 0L) {
			summary.setGreenCount(summary.getGreenCount() + gc);
			summary.setGreenScore(summary.getGreenScore() + score.getGreenScore());
		}
		long rc = score.getRedCount();
		if (rc != 0L) {
			summary.setRedCount(summary.getRedCount() + rc);
			summary.setRedScore(summary.getRedScore() + score.getRedScore());
		}
		summary.setTotalCount(summary.getTotalCount() + score.getTotalCount());
		TreeItem parent = chapitem.getParentItem();
		if (parent != null) insertMethodMap(parent, score);
	}

	TreeItem insertMethodTree(TreeItem item, Widget scoreItem) {
		if (scoreItem instanceof HasText) {
			String text = ((HasText) scoreItem).getText();
			int count = item.getChildCount();
			for(int i = 0 ;i < count; i++) {
				Widget w = item.getChild(i).getWidget();
				if (w instanceof HasText) {
					String wt = ((HasText) w).getText();
					if (compareM(text,wt ) <= 0)
					{
						return item.insertItem(i, scoreItem);
					}
				}
			}
		}
		return item.addItem(scoreItem);
	}

	private int compareM(String as, String bs) {
		boolean ab = as.startsWith("W:");
		boolean bb = bs.startsWith("W:");
		if (ab && !bb) return -1;
		if (bb && !ab) return +1;
		return as.compareTo(bs);
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
			return p;
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
				setCurrentInfo(current.getModelStructure());
				insertTree(current);
				return p;
			}, FAILURE);
		
		}
		
	}


}
