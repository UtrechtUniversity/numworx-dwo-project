package nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsutil.ValueSortedMap;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.FilterUtil;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.DomTree;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;

abstract class AbstractStudentModelPresenter {

  interface AbstractDisplay extends BasicDisplay {
    void showTree(DomTree<String> tree);
    void setTitle(String title);
    boolean isMethod();

  }
  
  public static String getTitle(DomStudentModelContextInfo info, String locale) {
  	return getTitle(info.getTitle(), locale);
  }

  private static String getTitle(Map<String, String> title, String locale) {
  	String language = title.getOrDefault(locale, "");
  	if (language.isEmpty()) 
  		language = title.getOrDefault("en", "");
  	if (language.isEmpty() || "Untitled".equals(language))
  		language = title.getOrDefault("nl", "Untitled");
  	if (language.isEmpty())
  		language = "Untitled";
  	return language;
  }

  protected static boolean contains(Map<String, Map<String, Set<Integer>>> filter, Map<String, Map<String, Set<Integer>>> methodes, DomMethod method) {
  	for (Map.Entry<String, Map<String, Set<Integer>>> entry : filter.entrySet()) {
  	    if (entry.getKey() == null || entry.getKey().isEmpty()) {
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
  		for (Map.Entry<String, Set<Integer>> m : entry.getValue().entrySet()) {
  			Set<Integer> chapters = new TreeSet<>(map.getOrDefault(m.getKey(), Collections.emptySet()));
  			if(!m.getValue().isEmpty()) chapters.retainAll(m.getValue());
  			if (!chapters.isEmpty())
  				return true;
  		}
  	}
  	return false;
  }

  private AbstractDisplay view;
  protected final LoggingFailure FAILURE;
  protected final String lang;
  protected final EventBus bus;
  protected Map<String,Map<String,Set<Integer>>> filter = Collections.emptyMap();
  protected Map<DomTree<String>, Integer> treeOrder;
  protected DwoGlobalVars dwoGlobalVars;
  @Inject
  protected StudentModelService service;
  @Inject
  protected PersonsService persons;
  
  AbstractStudentModelPresenter(EventBus bus, Logger LOG, DwoGlobalVars vars) {
    this.bus = bus;
    this.FAILURE = new LoggingFailure(LOG, bus);
    lang = LocaleInfo.getCurrentLocale().getLocaleName();
    this.dwoGlobalVars = vars;
    this.treeOrder = new IdentityHashMap<DomTree<String>, Integer>();
  }

  protected String getTitle(DomStudentModelContextInfo info) {
  	return getTitle(info, lang);
  }

  AbstractDisplay getView() {
    return view;
  }

  protected boolean checkFilter(Map<String, Map<String, Set<Integer>>> methods, DomMethod method) {
  	if (methods == null) return true;
  	return contains(filter, methods, method);
  }

  void setView(AbstractDisplay view) {
    this.view = view;
  }

  protected Map<String, DomTree<String>> children(List<DomStudentModelObj> objectives, DomMethod method) {
  	if (objectives == null) 
  		return null;
  	Map<String, DomTree<String>> map = new LinkedHashMap<>();
  	for( DomStudentModelObj obj : objectives) {
  		if (! filter.isEmpty()) {
  			if (!checkFilter( obj.getInfo().getMethods(), method ) )
  					continue;
  		}
  		DomTree<String> tobj = new DomTree<>(getTitle(obj.getInfo()));
  		tobj.setChildren(children(obj.getObjectives(), method));
  		if (tobj.getChildren() == null || ! tobj.getChildren().isEmpty())
  			map.put(obj.getInfo().getId(), tobj);
  	}
  	return map;
  }

  protected Promise<DomMethod> studentModelMethodTree(DomStudentModelStructure struc) {
    return service.getActiveMethod(struc.getActiveMethod()).then(m -> {
    		DomMethod method = m.getValue();
    		String t = method.getMethod();
    		Map<String, Set<Integer>> mf = filter.getOrDefault(method.key(), Collections.emptyMap());
    		DomTree<String> tree = new DomTree<>(t);
    		Map<String, DomTree<String>> map = new LinkedHashMap<>(), all = new HashMap<>();
    		tree.setChildren(map);
    		int bsize = method.books.size();
    		for(int i = 0; i < bsize; i++) {
    			String book = method.books.get(i);
    			if (! filter.isEmpty() && ! mf.containsKey(book) ) continue;
    			Set<Integer> mc = mf.getOrDefault(book, Collections.emptySet());
    			DomTree<String> tbook = new DomTree<>(book);
    			map.put(method.key() + "-" + book, tbook);
    			Map<String, DomTree<String>> bmap = new LinkedHashMap<>();
    			tbook.setChildren(bmap);
    			List<String> chapters = method.chapters.get(i);
    			int csize = chapters.size();
    			for (int j = 0; j < csize; j++) {
    				if (!mc.isEmpty() && !mc.contains(Integer.valueOf(j+1))) continue;
    				String chapter = chapters.get(j);
    				DomTree<String> ctree = new DomTree<>(chapter);
    				ctree.setChildren(new ValueSortedMap<String, DomTree<String>>(this::methodOrder));
    				String key = method.key() + "-" + book + "-" + (j+1);
    				bmap.put(key, ctree);
    				DomTree<String> weetjes = new DomTree<>(StudentResultsPresenter.BEGRIPPEN_EN_VAKTAAL);
    				weetjes.setChildren(new ValueSortedMap<String, DomTree<String>>(this::methodOrder));
    				ctree.getChildren().put(key + "-W", weetjes);
    				all.put(key, ctree);
    				all.put(key + "-W", weetjes);
    			}
    		}
    		insertChildren(all, struc.getCategories());
    		treeOrder.clear();
    		view.showTree(tree);
    		view.setTitle(FilterUtil.setFilter(filter, method));
    		return m;
    	});
  }

  int methodOrder(DomTree<String> a, DomTree<String> b) {
	Integer ia = treeOrder.get(a);
	Integer ib = treeOrder.get(b);
	if (ia != null && ib != null) return ia.compareTo(ib);
	  
  	String as = a.getObject();
  	String bs = b.getObject();
  	boolean ab = as.equals(StudentResultsPresenter.BEGRIPPEN_EN_VAKTAAL);
  	boolean bb = bs.equals(StudentResultsPresenter.BEGRIPPEN_EN_VAKTAAL);
  	if (ab && !bb) return +1;
  	if (bb && !ab) return -1;
  	return as.compareTo(bs);
  }

  void insertChildren(Map<String, DomTree<String>> all, List<DomStudentModelCategory> categories) {
  	for(DomStudentModelCategory cat: categories) {
  		insertChildrenObj(all, cat.getObjectives());
  	}	
  }

  void insertChildrenObj(Map<String, DomTree<String>> all, List<DomStudentModelObj> objectives) {
  for (DomStudentModelObj obj: objectives) {
  	if (obj.getObjectives() == null) { // leave
  		Map<String, Map<String, Set<Integer>>> methods = obj.getInfo().getMethods();
  		String title = getTitle(obj.getInfo());
  		String ext = title.startsWith("W:") ? "-W" : "";
  		methods.forEach(
  				(key, books) -> {
  					books.forEach( (book, chapters) -> chapters.forEach(chap -> {
  						String item = key + "-" + book + "-" + chap + ext;
  						all.computeIfPresent(item, (k, v) -> { 
  							DomTree<String> vv = new DomTree<>(title); vv.setChildren(null);
  							treeOrder.put(vv, treeOrder.size());
  							v.getChildren().put(obj.getInfo().getId(), vv);							
  						return v;});
  					}));});
  				
  					
  	} else {
  		insertChildrenObj(all, obj.getObjectives());
  	}}
  }

}
