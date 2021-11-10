package nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.web.bindery.event.shared.EventBus;

import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent.SelectedView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.studentmodel.JsTeacherSMClassResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClass;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.FilterUtil;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.DomTree;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataStudentScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObjectiveScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScorePerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class SMClassResultsPresenter extends AbstractStudentModelPresenter implements SelectionHandler<TreeItem>{

	private static final Logger LOG = Logger.getLogger(SMClassResultsPresenter.class.getName());
	
	public interface Display extends AbstractDisplay {

		void showSchoolClasses(Map<String, TaggedDomSchoolClass> schoolClasses);

		void setLoadingTreeMessage();

		void setEmptyTreeMessage();

		void setScores(Map<String, DomStudentModelObjectiveScore> result, boolean leaf);

		void setMethod(String label);		
	}
	
	private Display view;
	private DomSchoolClass schoolClass;
	private JSONObject state;
	
	private DomStudentModelContext4Student currentModel;
	
	
	private Promise<DomStudentModelScorePerTeacher> scores;
	private Map<PersistenceId, DomStudent> students;
	private Map<String, DomStudentModelContextInfo> currentInfo = new HashMap<>();
	
	@Inject void setView(JsTeacherSMClassResultsView view) {
	    super.setView(view);
		this.view = view;
		bus.addHandlerToSource(SelectionEvent.getType(), view, this);
	}
	
	@Inject SMClassResultsPresenter(EventBus bus) {
	    super(bus, LOG, null);
	}

	public void init(DomSchoolClass domSchoolClass, JavaScriptObject javaScriptObject) {
		view.init();
		view.clear();
		state = new JSONObject(javaScriptObject);
		Promise<?> p1 = showSchoolClasses(domSchoolClass);	
		String id = state.get("id").isString().stringValue();
		PersistenceId pid = new PersistenceId(id);
		DomStudentModelContext cid = new DomStudentModelContext();
		cid.setId(pid);
		
		Promise<?> p2 = showSchoolModel(cid, domSchoolClass);
		scores = getResults(cid, domSchoolClass, p2);
		Promises.all(p1, p2, scores).then(null, FAILURE);
	}

	
	private Promise<DomStudentModelScorePerTeacher> getResults(DomStudentModelContext cid, DomSchoolClass domSchoolClass, Promise<?> p2) {
		DomStudentModelScorePerTeacher result = new DomStudentModelScorePerTeacher();
		result.setSchoolClasses(Collections.singletonList(new DomMapEntry<PersistenceId, DomSchoolClass>(domSchoolClass.getId(), domSchoolClass)));
		result.setStudentModelContexts(Collections.singletonList(new DomMapEntry<PersistenceId, DomStudentModelContext>(cid.getId(), cid)));
// p2 (stap2) moet klaar zijn voordat stap3 mag 
		return Promises.all(service.getScores(result), p2).map(list -> (DomStudentModelScorePerTeacher)list.get(0)).then(this::stap3);
	}

	private Promise<?> showSchoolModel(DomStudentModelContextId cid, DomSchoolClass domSchoolClass) {
		return service.getForClass(cid, domSchoolClass)
				.then(p -> service.stap0(p, cid, domSchoolClass))
				.then(this::stap2);		
	}

	private Promise<?> stap1(Promise<List<DomSchoolClass>> p) {
    	Map <String, TaggedDomSchoolClass> schoolClasses = new HashMap<>();
    	p.getValue().forEach(sc -> {
    		String key = sc.getId().getIdString();
    		TaggedDomSchoolClass value = new TaggedDomSchoolClass(sc);
    		schoolClasses.put(key, value);
    	});
    	schoolClasses.get(schoolClass.getId().getIdString()).setTag(true);
    	view.showSchoolClasses(schoolClasses);
    	return p;
    }

	
	
	
	private Promise<?> stap2(Promise<DomStudentModelContext4Student> p) {		
		currentModel = p.getValue();
		filter = currentModel.getFilter();
		List<DomStudentModelCategory> categories = currentModel.getModelStructure().getCategories();
		DomStudentModelContextInfo info = currentModel.getModelStructure().getInfo();
		StudentResultsPresenter.setCurrentInfo(categories, info, currentInfo);
		showModel();
		return p;
	}

	private Promise<DomStudentModelScorePerTeacher> stap3(Promise<DomStudentModelScorePerTeacher> scores) {
		students = scores.getValue().getStudents().stream().collect(Collectors.toMap(DomMapEntry<PersistenceId,DomStudent>::getKey, DomMapEntry<PersistenceId,DomStudent>::getValue));
		String uuid = scores.getValue().getStudentModelContexts().get(0).getValue().getModelStructure().getInfo().getId();
		PersistenceId pid = currentModel.getModelStructure().getActiveMethod();
		return service.getActiveMethod(pid).then( q -> {
			view.setMethod(q.getValue().getMethod());
			setScores(scores, uuid, false, q.getValue());		
			return scores;
		});
	}

	private void setScores(Promise<DomStudentModelScorePerTeacher> scores, String uuid, boolean leaf, DomMethod method) {
		Map<String, DomStudentModelObjectiveScore> result = new HashMap<>();
		
		List<DomStudentModelDataStudentScore> list = scores.getValue().getStudentScores();
		
		for(DomStudentModelDataStudentScore item: list) {
			PersistenceId sid = item.getStudentId();
			DomStudentModelStructureScore score;
			DomStudentModelScore<?> org = score = item.getDomStudentModelStructureScore();
			StudentResultsPresenter.applyFilter(score, filter, currentInfo, method);
			org = find(org, uuid);
			if (org == null) org = item.getDomStudentModelStructureScore();
			DomStudent student = students.get(sid);
			String name = student.getDisplayName();
			DomStudentModelObjectiveScore copy = new DomStudentModelObjectiveScore();
			copy.setId(name);
			copy.setScore(org.getGreenScore(),org.getGreenCount(), org.getRedScore(), org.getRedCount(), org.getTotalCount());
			result.put(sid.getIdString(), copy);
		}
		view.setScores(result, leaf);
	}
	
	
	
	private DomStudentModelScore<?> find(DomStudentModelScore<?> org, String uuid) {
		if (uuid.equals(org.getId()))
			return org;
		if (org.getChildren() != null)
		for (DomStudentModelScore<?> item: org.getChildren()) {
			item = find(item, uuid);
			if (item != null) return item;
		}
		return null;
	}

	private void showModel() {
        DomStudentModelStructure struc = currentModel.getModelStructure();
		studentModelTree(struc);
	}

  protected Promise<DomMethod> studentModelTree(DomStudentModelStructure struc) {
    DomTree<String> tree = new DomTree<>("");
	DomTree<String> root = new DomTree<>(getTitle(struc.getInfo()));
	tree.setChildren(Collections.singletonMap(struc.getInfo().getId(), root));
	Map<String, DomTree<String>> map = new LinkedHashMap<>();
	root.setChildren(map);
	return service.getActiveMethod(struc.getActiveMethod()).then(m -> {
			for ( DomStudentModelCategory cat : struc.getCategories()) {
				DomTree<String> tcat = new DomTree<>(getTitle(cat.getInfo()));
				tcat.setChildren(children(cat.getObjectives(), m.getValue()));
				if (!tcat.getChildren().isEmpty())
					map.put(cat.getInfo().getId(), tcat);
			}
			view.showTree(tree);
			view.setTitle(FilterUtil.setFilter(filter, m.getValue()));
			return m;
		});
  }
 	
	private Promise<?> showSchoolClasses(DomSchoolClass domSchoolClass) {
		schoolClass = domSchoolClass;
		return persons.getTeachersSchoolClasses().then(this::stap1);		
	}

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		boolean leaf = event.getSelectedItem().getChildCount() == 0;
		String id = (String) event.getSelectedItem().getUserObject();
		LOG.info("on selection " + id);
		PersistenceId pid = currentModel.getModelStructure().getActiveMethod();
		service.getActiveMethod(pid).then ( q -> {
		DomMethod method = q.getValue();
		scores.then(p -> { 
				setScores(p, id, leaf, method);
			return p;} );
		return q;
		} );
	}

	private Map<PersistenceId, Promise<FilterMethodDialog>> filterDialogs = new HashMap<>();

	@JsMethod
	public void onFilter() {
		if(currentModel == null) return;
		LOG.info("on filter click");
		Promise<FilterMethodDialog> p;
		PersistenceId key = currentModel.getModelStructure().getActiveMethod();
		if (key == null) return;
		p = filterDialogs.computeIfAbsent(key, k -> service.getActiveMethod(k).map(FilterMethodDialog::new));
		p.then( q -> {		
			FilterMethodDialog filterPanel = q.getValue();
			filterPanel.setValue(filter);
			filterPanel.addCloseHandler(ev -> { 
				LOG.info("filter settings closed");
				filter = filterPanel.getValue();
				showModel();
			});
			filterPanel.show();
			return null;
		});
	}
	
	@JsMethod
	public void onPerson(String id) {
		LOG.info("on Person " + id);
		SwitchViewEvent event = new SwitchViewEvent(SelectedView.SMSTUDENTRESULTS, students.get(new PersistenceId(id)), schoolClass, state.getJavaScriptObject());;
		bus.fireEvent(event);
	}
	
	@JsMethod
	public void onChange(String id) {
		LOG.info("on Change Class " + id);
		DomSchoolClass sc = new DomSchoolClass();
		sc.setId(new PersistenceId(id));
		SwitchViewEvent event = new SwitchViewEvent(SelectedView.SMCLASSRESULTS, sc, state.getJavaScriptObject());
		bus.fireEvent(event);
	}
	
	@JsMethod
	public void onMethod(boolean value) {
		LOG.info("on method " + value);
	}
}
