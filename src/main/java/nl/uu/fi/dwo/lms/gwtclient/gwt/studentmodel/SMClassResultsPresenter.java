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
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.web.bindery.event.shared.EventBus;

import dagger.Lazy;
import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.studentmodel.JsTeacherSMClassResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClass;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.DomTree;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
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

public class SMClassResultsPresenter implements SelectionHandler<TreeItem>{

	private static final Logger LOG = Logger.getLogger(SMClassResultsPresenter.class.getName());
	
	public interface Display extends BasicDisplay {

		void showTree(DomTree<String> tree);

		void showSchoolClasses(Map<String, TaggedDomSchoolClass> schoolClasses);

		void setLoadingTreeMessage();

		void setEmptyTreeMessage();

		void setTitle(String title);

		void setScores(Map<String, DomStudentModelObjectiveScore> result);		
	}
	
	private Display view;
	@Inject StudentModelService service;
	@Inject PersonsService persons;
	final private EventBus bus;
	private DomSchoolClass schoolClass;
	private final LoggingFailure FAILURE;
	private final String lang;
	private JSONObject state;
	
	private Map<String, Map<String, Set<Integer>>> filter;
	@Inject Lazy<FilterSettings> filterPanel;
	private DomStudentModelContext4Student currentModel;
	
	@Inject void setView(JsTeacherSMClassResultsView view) {
		this.view = view;
		bus.addHandlerToSource(SelectionEvent.getType(), view, this);
	}
	
	@Inject SMClassResultsPresenter(EventBus bus) {
		this.bus = bus;
		FAILURE = new LoggingFailure(LOG, bus);
		lang = LocaleInfo.getCurrentLocale().getLocaleName();
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
		Promise<?> p3 = getResults(cid, domSchoolClass);
		Promises.all(p1, p2, p3).then(null, FAILURE);
	}

	
	private Promise<?> getResults(DomStudentModelContext cid, DomSchoolClass domSchoolClass) {
		DomStudentModelScorePerTeacher result = new DomStudentModelScorePerTeacher();
		result.setSchoolClasses(Collections.singletonList(new DomMapEntry<PersistenceId, DomSchoolClass>(domSchoolClass.getId(), domSchoolClass)));
		result.setStudentModelContexts(Collections.singletonList(new DomMapEntry<PersistenceId, DomStudentModelContext>(cid.getId(), cid)));
		return service.getScores(result).then(this::stap3);
	}

	private Promise<?> showSchoolModel(DomStudentModelContextId cid, DomSchoolClass domSchoolClass) {
		return service.getForClass(cid, domSchoolClass)
				.then(p -> stap0(p, cid, domSchoolClass))
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

	private Promise<DomStudentModelContext4Student> stap0(Promise<DomStudentModelContext4Student> p, DomStudentModelContextId cid, DomSchoolClassId schoolClass) {
		if (p.getValue() != null)		
			return p;
		return service.getStudentModel(cid.getId()).map( model -> {
			DomStudentModelContext4Student result = new DomStudentModelContext4Student();
			result.setFilter(Collections.emptyMap());
			result.setId(model.getId());
			result.setModelStructure(model.getModelStructure());
			result.setOptLock(model.getOptLock());
			result.setSchoolClass(schoolClass);
			return result;
		});
	}
	
	
	
	private Promise<?> stap2(Promise<DomStudentModelContext4Student> p) {		
		currentModel = p.getValue();
		filter = currentModel.getFilter();
		showModel();
		return p;
	}

	private Promise<?> stap3(Promise<DomStudentModelScorePerTeacher> scores) {
		Map<PersistenceId, DomStudent> students = scores.getValue().getStudents().stream().collect(Collectors.toMap(DomMapEntry<PersistenceId,DomStudent>::getKey, DomMapEntry<PersistenceId,DomStudent>::getValue));
		
		Map<String, DomStudentModelObjectiveScore> result = new HashMap<>();
		
		List<DomStudentModelDataStudentScore> list = scores.getValue().getStudentScores();
		
		for(DomStudentModelDataStudentScore item: list) {
			PersistenceId sid = item.getStudentId();
			DomStudentModelStructureScore org = item.getDomStudentModelStructureScore();
			DomStudent student = students.get(sid);
			String name = student.getDisplayName();
			DomStudentModelObjectiveScore copy = new DomStudentModelObjectiveScore();
			copy.setId(name);
			copy.setScore(org.getGreenScore(),org.getGreenCount(), org.getRedScore(), org.getRedCount(), org.getTotalCount());
			result.put(sid.getIdString(), copy);
		}
		view.setScores(result);
		
		return scores;
	}
	
	
	
	private void showModel() {
		DomTree<String> tree = new DomTree<>("");
		DomStudentModelStructure struc = currentModel.getModelStructure();
		DomTree<String> root = new DomTree<>(getTitle(struc.getInfo()));
		tree.setChildren(Collections.singletonMap(struc.getInfo().getId(), root));
		Map<String, DomTree<String>> map = new LinkedHashMap<>();
		root.setChildren(map);
		for ( DomStudentModelCategory cat : struc.getCategories()) {
			DomTree<String> tcat = new DomTree<>(getTitle(cat.getInfo()));
			tcat.setChildren(children(cat.getObjectives()));
			if (!tcat.getChildren().isEmpty())
				map.put(cat.getInfo().getId(), tcat);
		}
		view.showTree(tree);
	}
 	
	private Map<String, DomTree<String>> children(List<DomStudentModelObj> objectives) {
		if (objectives == null) 
			return null;
		Map<String, DomTree<String>> map = new LinkedHashMap<>();
		for( DomStudentModelObj obj : objectives) {
			if (! filter.isEmpty()) {
				if (!checkFilter( obj.getInfo().getMethods() ) )
						continue;
			}
			DomTree<String> tobj = new DomTree<>(getTitle(obj.getInfo()));
			tobj.setChildren(children(obj.getObjectives()));
			if (tobj.getChildren() == null || ! tobj.getChildren().isEmpty())
				map.put(obj.getInfo().getId(), tobj);
		}
		return map;
	}

	private boolean checkFilter(Map<String, Map<String, Set<Integer>>> methods) {
		if (methods == null) return true;
		return StudentModelPresenter.contains(filter, methods);
	}

	private String getTitle(DomStudentModelContextInfo info) {
		return info.getTitle().getOrDefault(lang, "");
	}

	private Promise<?> showSchoolClasses(DomSchoolClass domSchoolClass) {
		schoolClass = domSchoolClass;
		return persons.getTeachersSchoolClasses().then(this::stap1);		
	}

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		String id = (String) event.getSelectedItem().getUserObject();
		LOG.info("on selection " + id);	
	}

	@JsMethod
	public void onFilter() {
		if(currentModel == null) return;
		LOG.info("on filter click");
		PopupPanel popup = new PopupPanel(true,true);
		filterPanel.get().setValue(filter);
		popup.setWidget(filterPanel.get());
		popup.center();
		popup.addCloseHandler(ev -> { 
			LOG.info("filter settings closed");
			filter = filterPanel.get().getValue();
			showModel();
		});
	}
	
	@JsMethod
	public void onPerson(String id) {
		LOG.info("on Person " + id);
	}
}
