package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.osgi.util.promise.Promise;

import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Composite;
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
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObjectiveScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;

public class StudentResultsTree extends Composite {

	private final String lang = LocaleInfo.getCurrentLocale().getLocaleName();
	private static final Logger LOG = Logger.getLogger(StudentResultsTree.class.getName());

	private Tree tree;
	private final LoggingFailure FAILURE;
	private DomMethod method;
	Map<String, DomStudentModelContextInfo> currentInfo = new HashMap<String, DomStudentModelContextInfo>();

	class Strategy {
		Widget html(DomStudentModelContextInfo info, DomStudentModelScore<?> s, int level) {
	  		String title = StudentModelPresenter.getTitle(info,lang);
	  		if (s.getChildren() != null)
	  			return Util.summaryItem(title, s, level);
	  		return Util.scoreItem(title, s, level);
		}
		
		Widget root (DomStudentModelContextInfo info) {
	  		String title = StudentModelPresenter.getTitle(info,lang);
			return Util.summaryItem(title, StudentResultsPresenter.NULLSCORE, 0);
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

	
	public TreeItem getRoot(DomStudentModelContext4Student item) {
		tree.removeItems();
		Widget html = to.root(item.getModelStructure().getInfo());
        TreeItem ti = tree.addItem(html);
		ti.setUserObject(item);
		return ti;
	}

}
