package nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.web.bindery.event.shared.EventBus;

import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.studentmodel.JsTeacherStudentModelView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClass;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.DomTree;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;

public class StudentModelPresenter implements Comparator<DomStudentModelContext>{
	private static final Logger LOG = Logger.getLogger(StudentModelPresenter.class.getName());

    private Display view;
    private Map<String, TaggedDomSchoolClass> schoolClasses = new HashMap<>();
    private Map<String, DomStudentModelContext> models = new LinkedHashMap<>();
    
    public interface Display extends BasicDisplay {

		void showSchoolClasses(Map<String, TaggedDomSchoolClass> schoolClasses);
		void showStudentModels(Map<String, String> models);
		void showTree(DomTree<String> tree);
    }
    
    @Inject void setView(JsTeacherStudentModelView view) {
    	this.view = view;
    }
    @Inject PersonsService persons;
    @Inject StudentModelService service;

	private final LoggingFailure FAILURE;

	private final String lang;
    
    @Inject StudentModelPresenter(EventBus bus) {
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
		LOG.info("select Model " + id);
	}
}
