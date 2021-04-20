package nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.web.bindery.event.shared.EventBus;

import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.studentmodel.JsTeacherStudentModelView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClass;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.DomTree;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class StudentModelPresenter implements Comparator<DomStudentModelContext>, SelectionHandler<TreeItem> {
	private static final Logger LOG = Logger.getLogger(StudentModelPresenter.class.getName());

    private Display view;
    private Map<String, TaggedDomSchoolClass> schoolClasses = new HashMap<>();
    private Map<String, DomStudentModelContext> models = new LinkedHashMap<>();
    
    public interface Display extends BasicDisplay {

		void showSchoolClasses(Map<String, TaggedDomSchoolClass> schoolClasses);
		void showStudentModels(Map<String, String> models);
		void showTree(DomTree<String> tree);
		void setLoadingTreeMessage();
		void setDescription(IsWidget w);
    }
    
    @Inject void setView(JsTeacherStudentModelView view) {
    	this.view = view;
    	bus.addHandlerToSource(SelectionEvent.getType(), view, this);
    }
    @Inject PersonsService persons;
    @Inject StudentModelService service;
    @Inject DescriptionPresenter description;

	private final LoggingFailure FAILURE;

	private final String lang;

	private EventBus bus;

	private Promise<DomStudentModelContext> currentModel;
    
    @Inject StudentModelPresenter(EventBus bus) {
    	this.bus = bus;
        this.FAILURE = new LoggingFailure(LOG, bus);
		lang = LocaleInfo.getCurrentLocale().getLocaleName();
		
    }
    
    public void init() {
    	view.clear();
    	view.init();
    	updateSchoolclasses();
    	updateStudentModels();
    }

    private void updateStudentModels() {
		service.getModels().then(this::stap2, FAILURE);
	}

	private Promise<?> stap1(Promise<List<DomSchoolClass>> p) {
    	schoolClasses.clear();
    	p.getValue().forEach(sc -> {
    		String key = sc.getId().getIdString();
    		TaggedDomSchoolClass value = new TaggedDomSchoolClass(sc);
    		schoolClasses.put(key, value);
    	});
    	view.showSchoolClasses(schoolClasses);
    	return p;
    }
	
	private Promise<?> stap2(Promise<List<DomStudentModelContext>> p) {
		models.clear();
		LinkedHashMap<String,String> titles = new LinkedHashMap<>();
		p.getValue().stream().sorted(this).forEach(model -> { 
			String key = model.getId().getIdString();
			String title = model.getModelStructure().getInfo().getTitle().getOrDefault(lang, "");
			titles.put(key, title);
			models.put(key, model);			
		});
		view.showStudentModels(titles);
		return p;
	}
    
	private void updateSchoolclasses() {
		persons.getTeachersSchoolClasses().then(this::stap1, FAILURE);
	}

	@Override
	public int compare(DomStudentModelContext o1, DomStudentModelContext o2) {
		String t1 = o1.getModelStructure().getInfo().getTitle().getOrDefault(lang, "");
		String t2 = o2.getModelStructure().getInfo().getTitle().getOrDefault(lang, "");
		return String.CASE_INSENSITIVE_ORDER.compare(t1, t2);
	}
	
	@JsMethod
	public void selectModel(String id) {
		view.setLoadingTreeMessage();
		LOG.info("select Model " + id);
		PersistenceId pid = new PersistenceId(id);
		this.currentModel = service.getStudentModel(pid).then(this::studentModel, FAILURE);
	}
	
	Promise<DomStudentModelContext> studentModel(Promise<DomStudentModelContext> p) {
		DomStudentModelStructure struc = p.getValue().getModelStructure();
		String t = getTitle(struc.getInfo());
		DomTree<String> tree = new DomTree<>(t);
		Map<String, DomTree<String>> map = new LinkedHashMap<>();
		tree.setChildren(map);
		for ( DomStudentModelCategory cat : struc.getCategories()) {
			DomTree<String> tcat = new DomTree<>(getTitle(cat.getInfo()));
			map.put(cat.getInfo().getId(), tcat);
			tcat.setChildren(children(cat.getObjectives()));
		}
		view.showTree(tree);
		return p;
		
	}
		
	private Map<String, DomTree<String>> children(List<DomStudentModelObj> objectives) {
		if (objectives == null) 
			return null;
		Map<String, DomTree<String>> map = new LinkedHashMap<>();
		for( DomStudentModelObj obj : objectives) {
			DomTree<String> tobj = new DomTree<>(getTitle(obj.getInfo()));
			map.put(obj.getInfo().getId(), tobj);
			tobj.setChildren(children(obj.getObjectives()));
		}
		return map;
	}

	private String getTitle(DomStudentModelContextInfo info) {
		return info.getTitle().getOrDefault(lang, "");
	}

	@JsMethod
	public void onGraph() {
		LOG.info("on graph click");
	}
	
	@JsMethod
	public void onFilter() {
		LOG.info("on filter click");
	}

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		String id = (String) event.getSelectedItem().getUserObject();
		LOG.info("on selection " + id);		
		DomStudentModelContextInfo info = new DomStudentModelContextInfo();
		info.setId(id);
		currentModel.then( p -> 
			description.get(p.getValue(), info))
		.then(p -> {view.setDescription(p.getValue()); return p;}, FAILURE);
	}
}
