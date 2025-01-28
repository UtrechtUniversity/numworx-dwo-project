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
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.StudentModelPresenter;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategoryScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelMethodInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObjectiveScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;

public class StudentResultsTree extends Composite {

	private final String lang = LocaleInfo.getCurrentLocale().getLocaleName();
	private static final Logger LOG = Logger.getLogger(StudentResultsTree.class.getName());

	Tree tree;
	private final LoggingFailure FAILURE;
	public DomMethod method;
	public Map<String, DomStudentModelContextInfo> currentInfo = new HashMap<String, DomStudentModelContextInfo>();

	class Strategy {
		Widget html(DomStudentModelContextInfo info, DomStudentModelScore<?> s, int level) {
	  		String title = StudentModelPresenter.getTitle(info,lang);
	  		if (s.getChildren() != null)
	  			return Util.summaryItem(title, s, level);
	  		return Util.scoreItem(title, s, level, Optional.empty());
		}
		
		Widget root (DomStudentModelContextInfo info) {
	  		String title = StudentModelPresenter.getTitle(info,lang);
			return Util.summaryItem(title, StudentResultsPresenter.NULLSCORE, 0);
		}

		Widget summary(String title, DomStudentModelScore<?> score, int level) {
	  		return Util.summaryItem(title, score, level);
		}

		Widget score(String title, DomStudentModelScore<?> s, int level) {
			return Util.scoreItem(title, s, level, Optional.empty());
		}

		public Widget score(String title, DomStudentModelScore<?> s, int level, Optional<String> variant) {
			return Util.scoreItem(title, s, level, variant);
		}
	}

	class ZonderTitel extends Strategy {
		Widget html(DomStudentModelContextInfo info, DomStudentModelScore<?> s, int level) {
	  		String title = StudentModelPresenter.getTitle(info,lang);
	  		if (s.getChildren() != null)
	  			return new SummaryIcon(title);
	  		return new ScoreIcon(title);
		}
		Widget root (DomStudentModelContextInfo info) {
	  		String title = StudentModelPresenter.getTitle(info,lang);
			return new SummaryIcon(title);
		}
		Widget summary(String title, DomStudentModelScore<?> score, int level) {
	  		return new SummaryIcon(title);
		}
		Widget score(String title, DomStudentModelScore<?> s, int level) {
			return new ScoreIcon(title);
		}

	}
	
	Strategy to = new Strategy();
	
	public void enableScore(boolean b) {
		if (b)
			to = new Strategy();
		else
			to = new ZonderTitel();
	}
	
	public void setMethod(DomMethod method) {
		this.method = method;
	}

	public StudentResultsTree(EventBus bus) {
		tree = new Tree();
		initWidget(tree);
		FAILURE = new LoggingFailure(LOG, bus);
	}

	public void removeItems() {
		tree.removeItems();
	}

	public TreeItem addItem(Widget html) {
		return tree.addItem(html);
	}

	public HandlerRegistration addSelectionHandler(SelectionHandler<TreeItem> handler) {
		return tree.addSelectionHandler(handler);
	}

	public void insertTree(DomStudentModelContext4Student item, Promise<DomStudentModelDataScore> ps) {
		TreeItem ti = getRoot(item);
		ps.then(s -> {
          DomStudentModelStructureScore score = s.getValue().getDomStudentModelStructureScore();
          StudentResultsPresenter.applyFilter(score, item.getFilter(), currentInfo, method);
          ti.setWidget(to.html(item.getModelStructure().getInfo(), score, 0));
          addToTree(ti, item, ps, item.getFilter());
          ti.setState(true);
		  return s;
		});

		
	}
// tree manipulation: 
	boolean inFilter(Map<String, Map<String, Set<Integer>>> filter, DomStudentModelObj oo) {
		Map<String, Map<String, Set<Integer>>> methods = oo.getInfo().getMethods();
		return StudentResultsPresenter.contains(filter, methods, method);
	}

	private int getVisibleChildCount(TreeItem tt) {
		int cnt = 0;
		int len = tt.getChildCount();
		for (int i = 0; i < len; i++) {
			if (tt.getChild(i).isVisible()) cnt++;
		}
		return cnt;
	}
	public void updateToTree(TreeItem item, int[] elems, int cat, int obj,
			final DomStudentModelObj oo, DomStudentModelObjectiveScore score, Map<String, Map<String, Set<Integer>>> filter) {
		int count = item.getChildCount();
		for (int i = 0; i < count; i++) {
			TreeItem tt = item.getChild(i);
			int[] oelems = (int[]) tt.getUserObject();
			int oobj = oelems[elems.length];
			DomStudentModelObj ooo = oo.getObjectives().get(oobj);
			DomStudentModelObjectiveScore s = score.getChildren().get(oobj);
			Widget html = to.html(ooo.getInfo(), s, 3);
			tt.setWidget(html);
			updateToTree(tt, oelems, cat, obj, ooo, s, filter);
		}
	}

	
	public Promise<DomStudentModelDataScore> addToTree(TreeItem item, int[] elems, int cat, int obj,
			final DomStudentModelObj oo, Promise<DomStudentModelDataScore> p, Map<String, Map<String, Set<Integer>>> filter) {
		if (oo.getObjectives() != null && oo.getObjectives().size() != item.getChildCount()) {
			DomStudentModelObjectiveScore score = p.getValue().getDomStudentModelStructureScore().getCategories().get(cat).getObjectives().get(obj);
			for (int i = 2; i < elems.length; i++ ) score = score.getChildren().get(elems[i]);
			int oobj = 0;
			for (DomStudentModelObj ooo: oo.getObjectives()) {
				DomStudentModelObjectiveScore s = score.getChildren().get(oobj);
				TreeItem tt;
				Widget html = to.html(ooo.getInfo(), s, 3);
				tt = item.addItem(html);
				if (s.getChildren() == null)
				{	boolean add = inFilter(filter, ooo);
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

	public void updateToTree(TreeItem item, int cat, DomStudentModelCategory o,
			DomStudentModelCategoryScore score, Map<String, Map<String, Set<Integer>>> filter) {
		int count = item.getChildCount();
		for (int i = 0; i < count; i++) { 
			TreeItem tt = item.getChild(i);
			int[] elems = (int[]) tt.getUserObject();
			int obj = elems[1];
		    DomStudentModelObjectiveScore s = score.getObjectives().get(obj);
		    DomStudentModelObj oo = o.getObjectives().get(obj);
			Widget html = to.html(oo.getInfo(), s, 2);
			tt.setWidget(html);
			updateToTree(tt, elems, cat, obj, oo, s, filter);
		}
		
	}
	
	
	public Promise<DomStudentModelDataScore> addToTree(TreeItem item, Object userObject, DomStudentModelCategory o,
			Promise<DomStudentModelDataScore> p, Map<String, Map<String, Set<Integer>>> filter) {
		DomStudentModelCategoryScore score = p.getValue().getDomStudentModelStructureScore().getCategories().get(((Integer) userObject).intValue());
		if (item.getChildCount() != o.getObjectives().size()) {
			item.removeItems();
			int cat = ((Integer) userObject).intValue();
			int obj = 0;
			for( DomStudentModelObj oo : o.getObjectives()) {
			    DomStudentModelObjectiveScore s = score.getObjectives().get(obj);
				TreeItem tt;
				Widget html = to.html(oo.getInfo(), s, 2);
				tt = item.addItem(html);
				if (s.getChildren() == null)
				{	boolean add = inFilter(filter, oo);
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
	public void updateToTree(TreeItem item, DomStudentModelContext4Student model, Promise<DomStudentModelDataScore> promisedScore, Map<String, Map<String, Set<Integer>>> filter2) {
		promisedScore.then( p -> {
			DomStudentModelStructure structure = model.getModelStructure();
			int count = item.getChildCount();
			List<DomStudentModelCategoryScore> cats = p.getValue().getDomStudentModelStructureScore().getCategories();
			List<DomStudentModelCategory> categories = structure.getCategories();
			for (int i = 0; i < count; i++) {
				TreeItem tt = item.getChild(i);
				int cat = (Integer) tt.getUserObject();
	            DomStudentModelCategoryScore score = cats.get(cat);
	            DomStudentModelCategory o = categories.get(cat);				
				Widget w = to.html(o.getInfo(), score, 1);
				tt.setWidget(w);
				updateToTree(tt, cat, o, score, filter2);
			}
			return p;
		});
	}
	
	public void addToTree(TreeItem item, DomStudentModelContext4Student model, Promise<DomStudentModelDataScore> promisedScore, Map<String, Map<String, Set<Integer>>> filter2) {
		promisedScore
		.then(p -> {
			DomStudentModelStructure structure = model.getModelStructure();
			if (item.getChildCount() != structure.getCategories().size()) {
				item.removeItems();
				int cat = 0;
				for (DomStudentModelCategory o : structure.getCategories()) {
		            DomStudentModelCategoryScore score = p.getValue().getDomStudentModelStructureScore().getCategories().get(cat);
					TreeItem tt = item.addItem(
					  to.html(o.getInfo(), score,1));
					tt.setUserObject(cat);
					addToTree(tt, cat, o, p, filter2);
					if (getVisibleChildCount(tt) == 0) 
						tt.setVisible(false);
					cat++;
				}
			}
			return p; })
		.then(null, FAILURE);

	}

	public TreeItem getCurrentRoot() {
		return tree.getItem(0);
	}
	
	public TreeItem getRoot(DomStudentModelContext4Student item) {
		tree.removeItems();
		Widget html = to.root(item.getModelStructure().getInfo());
        TreeItem ti = tree.addItem(html);
		ti.setUserObject(item);
		return ti;
	}

	
// method tree
	void addToMethodTree2(List<DomStudentModelObj> objs, List<DomStudentModelObjectiveScore> objscores, Map<String, Holder> holderMap) {
		Iterator<DomStudentModelObj> iobjs = objs.iterator();
		Iterator<DomStudentModelObjectiveScore> iobjscores = objscores.iterator();
		while (iobjs.hasNext()) {
			DomStudentModelObj obj = iobjs.next();
			if (obj.getObjectives() == null) {
				// leaf
				Holder h = new Holder(obj, iobjscores.hasNext()?iobjscores.next():StudentResultsPresenter.NULLSCORE);
				holderMap.put(obj.getInfo().getId(), h);
			
			} else {
				addToMethodTree2(obj.getObjectives(), iobjscores.next().getChildren(), holderMap);
			}
 		}
		

		
	}

	private void addToMethodTree(TreeItem item, DomStudentModelObj obj, DomStudentModelScore<?> s, DomMethod method) {
		Map<String, Map<String, Set<Integer>>> map = obj.getInfo().getMethods();
		if (StudentResultsPresenter.contains(filter, map, method)) {
			String title = StudentModelPresenter.getTitle(obj.getInfo(),lang);
			Map<String, Set<Integer>> books = map.getOrDefault(method.key(), Collections.emptyMap());
			for( Map.Entry<String, Set<Integer>> entry: books.entrySet()) {
				String book = entry.getKey();
				for (Integer chapter: entry.getValue()) {
					// met method, book, chapter vind methodinfo uit obj
					List<DomStudentModelMethodInfo> mi = obj.getInfo().getMethodInfo();
					Optional<DomStudentModelMethodInfo> opt = mi.stream()
							.filter(m -> book.equals(m.getBook()) && chapter.equals(m.getChapter()) && method.key().equals(m.getMethod()))
							.findAny();
					Optional<String> variant = opt.map(DomStudentModelMethodInfo::getVariant);
					addToMethodTree(item, book, chapter, to.score(title, s, 3, variant), method, s, variant);
				}
			}
		}
	}

	private void addToMethodTree(TreeItem item, String book, Integer chapter, Widget scoreItem, DomMethod method, DomStudentModelScore<?> score, Optional<String> variant) {
		int kidscount = item.getChildCount();
		for (int i = 0; i < kidscount; i++) {
			TreeItem bookitem = item.getChild(i);
			int index = ((Number) bookitem.getUserObject()).intValue();
			String titlebook = method.books.get(index);
			if (titlebook.equals(book)) {
				int chaptercount = bookitem.getChildCount();
				for (int j = 0; j < chaptercount; j++) {
					TreeItem chapitem = bookitem.getChild(j);
					int chapnr = ((int[])chapitem.getUserObject())[1];
					if (chapnr+1 == chapter.intValue()) {
						TreeItem obj = insertMethodTree(chapitem, scoreItem);
						obj.setUserObject(score);
						insertMethodMap(obj.getParentItem(), score, variant);
						break;
					}
				}
				break;
			}
		}	
	}

	private void insertMethodMap(TreeItem chapitem, DomStudentModelScore<?> score, Optional<String> variant) {
		DomStudentModelScore<?> summary = scoreMap.computeIfAbsent(chapitem, k -> {
			DomStudentModelScore<?> r = new DomStudentModelScore();
			r.setScore(0, 0, 0, 0, 0);
			return r;
		});
		long gc = score.getGreenCount();
		if (gc != 0L) {
			double gs = metVariant(score, score.getGreenScore(), variant);
			summary.setGreenCount(summary.getGreenCount() + gc);
			summary.setGreenScore(summary.getGreenScore() + gs);
		}
		long rc = score.getRedCount();
		if (rc != 0L) {
			summary.setRedCount(summary.getRedCount() + rc);
			summary.setRedScore(summary.getRedScore() + score.getRedScore());
		}
		summary.setTotalCount(summary.getTotalCount() + score.getTotalCount());
		TreeItem parent = chapitem.getParentItem();
		if (parent != null) insertMethodMap(parent, score, variant);
	}

	/* 
	 * Dit is de plek waar leerdoelen aan de methodetree worden toegevoegd.
	 * moet topologisch gesorteerd zijn. Hier verder geen sortering meer mogelijk.
	 */
	
	private double metVariant(DomStudentModelScore<?> s, double greenScore, Optional<String> variant) {
		if (variant.isPresent() && s instanceof DomStudentModelObjectiveScore) {
			DomStudentModelObjectiveScore sv = (DomStudentModelObjectiveScore) s;
			greenScore = variant.map(v -> sv.getVariants().get(v)).orElse(greenScore);
		}
		return greenScore;
	}

	TreeItem insertMethodTree(TreeItem item, Widget scoreItem) {
		if (scoreItem instanceof HasText) {
			int count = item.getChildCount()-1;
			String text = ((HasText) scoreItem).getText();
			if (text.startsWith("W:")) {
				item = item.getChild(count); /// Dit is "Begrippen en vaktaal"
				count = item.getChildCount();
			}
			return item.insertItem(count, scoreItem);
		}
		return item.addItem(scoreItem);
	}
	void addToMethodTree2(TreeItem item, DomStudentModelContext4Student model, DomStudentModelStructureScore score, DomMethod method) {
		scoreMap.clear();
		Map<String, Holder> holderMap = setupHolderMap(model, score);

		closure(holderMap);
		List<Holder> holderList = new LinkedList<>(holderMap.values());

		sort(holderList, (Holder a, Holder b) -> {
		      int result = 0;
		        String ida = a.obj.getInfo().getId(); Collection<String> sa = a.foreknowledge;
		        String idb = b.obj.getInfo().getId(); Collection<String> sb = b.foreknowledge;
		        if (sa.contains(idb)) 
		          result = +1;
		        if (sb.contains(ida))
		          result = -1;
		      return result;
		    });
		
		holderList.forEach(h -> {
			addToMethodTree(item, h.obj, h.s, method);
		});
		updateScoreMap();	 
	}

	protected void updateScoreMap() {
		scoreMap.forEach( (key, value) -> 
			{	String t = ((HasText) key.getWidget()).getText();
				int i = key.getParentItem() == null ? 0 : 1;
	 			key.setWidget(to.summary(t, value, i));
			}
		);
	}

	protected Map<String, Holder> setupHolderMap(DomStudentModelContext4Student model,
			DomStudentModelStructureScore score) {
		Map<String, Holder> holderMap = new LinkedHashMap<>();
		DomStudentModelStructure structure = model.getModelStructure();
		List<DomStudentModelCategory> cats = structure.getCategories();
		List<DomStudentModelCategoryScore> catscores = score.getCategories();
		Iterator<DomStudentModelCategory> icats = cats.iterator();
		Iterator<DomStudentModelCategoryScore> icatscores = catscores.iterator();
		while( icats.hasNext()) {
			List<DomStudentModelObj> objs = icats.next().getObjectives();
			List<DomStudentModelObjectiveScore> objscores = icatscores.hasNext() ? icatscores.next().getObjectives() : Collections.emptyList();
			addToMethodTree2(objs, objscores, holderMap);
		}
		return holderMap;
	} 

	protected Map<String,Map<String, Set<Integer>>> filter;
	public Map<TreeItem, DomStudentModelScore<?>> scoreMap = new HashMap<>();

	private void addToMethodTree(TreeItem item, List<DomStudentModelObj> objs, List<DomStudentModelObjectiveScore> objscores, DomMethod method) {
		Iterator<DomStudentModelObj> iobjs = objs.iterator();
		Iterator<DomStudentModelObjectiveScore> iobjscores = objscores.iterator();
		while (iobjs.hasNext()) {
			DomStudentModelObj obj = iobjs.next();
			if (obj.getObjectives() == null) {
				// leaf
				addToMethodTree(item, obj, iobjscores.hasNext()?iobjscores.next():StudentResultsPresenter.NULLSCORE, method);
			} else {
				addToMethodTree(item, obj.getObjectives(), iobjscores.next().getChildren(), method);
			}
 		}
		

		
	}
	private void closure(Map<String, Holder> sets) {
	    Holder NULL = new Holder();
	    boolean done;
	    do { done = true;
	      for(Map.Entry<String, Holder> entry: sets.entrySet()) {
	        boolean added = false;
	        if (! entry.getValue().foreknowledge.isEmpty()) 
	        for(String i: new HashSet<>(entry.getValue().foreknowledge)) {
	          Holder h = sets.getOrDefault(i, NULL);		          
	          Collection<String> extra = h.foreknowledge;
	          added = entry.getValue().foreknowledge.addAll(extra) || added;
	        }
	        if (added)
	          done = false;
	      }
	    } while(!done);
	    
	  }

  private void sort(List<Holder> list, Comparator<Holder> compare) {
	    List<Holder> ordered = new ArrayList<>(list.size());
	    while( ! list.isEmpty()) {
	      int node = 0;
	      Holder candidate = list.get(0);
	      for(int i = 1; i < list.size(); i++) {
	    	  Holder n = list.get(i);
	        if (compare.compare(n, candidate)<0) {
	          node = i;
	          candidate = n;
	        }
	      }
	      ordered.add(candidate); list.remove(node);
	    }
	    list.addAll(ordered);  
	  }  
  
  Promise<DomStudentModelDataScore> updateMethodTree(DomStudentModelContext4Student item,	Promise<DomStudentModelDataScore> promisedScore)
  {
	  //Map<String, Set<Integer>> bookfilter = filter.getOrDefault(method.key(), Collections.emptyMap());
	  
	  return promisedScore.then(s -> {
		  scoreMap.clear();
		  String title = method.getMethod();
		  DomStudentModelStructureScore score = s.getValue().getDomStudentModelStructureScore();
		  Widget html = to.summary(title, score, 0);
		  TreeItem ti = getCurrentRoot();
		  ti.setWidget(html);
          //addToMethodTree2(ti, item, score, method); // hier moet er worden ingebroken
          Map<String, Holder> holdermap = setupHolderMap(item, score);
          updateMethodTree(ti, holdermap);
          updateScoreMap();
          
          trimMethodTree(ti);
	  
		  
		  return s;
	  });
  }
  
  
  private void updateMethodTree(TreeItem ti, Map<String, Holder> holdermap) {
	int bookcount = ti.getChildCount();
	for(int b=0 ; b < bookcount; b++) {
		TreeItem book = ti.getChild(b);
		String bookstr = method.books.get(b); // klopt dit?
		int chapcount = book.getChildCount();
		for( int c = 0; c < chapcount; c++) {
			TreeItem chap = book.getChild(c);
			Object cu = chap.getUserObject(); // wat is dit? [b, c] misschien?
			GWT.log(cu.toString());
			Integer chapter = Integer.valueOf(c+1); // denken we
			
			int count = chap.getChildCount() - 1;
			for( int i = 0; i <= count; i++) {
				TreeItem item = chap.getChild(i);
				updateMethodTreeItem(holdermap, bookstr, chapter, item);
			}
			TreeItem weetjes = chap.getChild(count);
			count = weetjes.getChildCount();
			for( int i = 0; i < count; i++) {
				TreeItem item = weetjes.getChild(i);
				updateMethodTreeItem(holdermap, bookstr, chapter, item);
			}
		}
		
		
	}
	
}

protected void updateMethodTreeItem(Map<String, Holder> holdermap, String bookstr, Integer chapter, TreeItem item) {
	Object o = item.getUserObject();
	if (o instanceof DomStudentModelObjectiveScore) {
		String id = ((DomStudentModelObjectiveScore) o).getId();
		Holder h = holdermap.get(id);
		if (h != null) {
			item.setUserObject(h.s);
			DomStudentModelObj obj = h.obj;
			String title = StudentModelPresenter.getTitle(obj.getInfo(),lang);
			List<DomStudentModelMethodInfo> mi = obj.getInfo().getMethodInfo();
			Optional<DomStudentModelMethodInfo> opt = mi.stream()
					.filter(m -> bookstr.equals(m.getBook()) && chapter.equals(m.getChapter()) && method.key().equals(m.getMethod()))
					.findAny();
			Optional<String> variant = opt.map(DomStudentModelMethodInfo::getVariant);
			// DEBUG h.s.setScore(1.0 - h.s.getScore());
			item.setWidget(to.score(title, h.s, 3, variant));
			insertMethodMap(item.getParentItem(), h.s, variant);
			
		}
	}
}

public Promise<DomStudentModelDataScore> insertMethodTree(DomStudentModelContext4Student item,	Promise<DomStudentModelDataScore> promisedScore) {
		removeItems();
		String title = method.getMethod();
		Map<String, Set<Integer>> bookfilter = filter.getOrDefault(method.key(), Collections.emptyMap());
		Widget html = to.summary(title, StudentResultsPresenter.NULLSCORE, 0);
		TreeItem ti = addItem(html);
		List<String> books = method.books;
		for(int i = 0; i < books.size(); i++) {
			String booktitle = books.get(i);
			if (! filter.isEmpty() && !bookfilter.containsKey(booktitle)) continue;
			Set<Integer> chapterfilter = bookfilter.getOrDefault(booktitle, Collections.emptySet());
			html = to.summary(booktitle, StudentResultsPresenter.NULLSCORE, 1);
			TreeItem bi = ti.addItem(html);
			bi.setUserObject(Integer.valueOf(i));
			List<String> chapters = method.chapters.get(i);
			for(int j = 0; j < chapters.size(); j++) {
				if ( !chapterfilter.isEmpty() && !chapterfilter.contains(Integer.valueOf(j+1))) continue;
				html = to.summary(chapters.get(j), StudentResultsPresenter.NULLSCORE, 2);
				TreeItem ci = bi.addItem(html);
				ci.setUserObject(new int[] {i, j});
				html = to.summary(StudentResultsPresenter.BEGRIPPEN_EN_VAKTAAL, StudentResultsPresenter.NULLSCORE, 3);
				TreeItem wi = ci.addItem(html);
				wi.setUserObject("W:");
			}
		}
		ti.setUserObject(item);
		return promisedScore.then(s -> {
	          DomStudentModelStructureScore score = s.getValue().getDomStudentModelStructureScore();
	          ti.setWidget(to.summary(title, score ,0));
	          //ti.setSelected(true);
	          addToMethodTree2(ti, item, score, method); // hier moet er worden ingebroken
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

	
}
