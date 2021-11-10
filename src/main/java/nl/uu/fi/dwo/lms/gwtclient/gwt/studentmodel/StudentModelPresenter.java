package nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import javax.inject.Inject;
import org.osgi.util.promise.Promise;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent.SelectedView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.studentmodel.JsTeacherStudentModelView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClass;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.FilterUtil;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.DomTree;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class StudentModelPresenter extends AbstractStudentModelPresenter implements Comparator<DomStudentModelContext>, SelectionHandler<TreeItem> {
	private static final Logger LOG = Logger.getLogger(StudentModelPresenter.class.getName());

    Display view;
    private Map<String, TaggedDomSchoolClass> schoolClasses = new HashMap<>();
    private Map<String, DomStudentModelContext> models = new LinkedHashMap<>();
    
    public interface Display extends AbstractDisplay {

		void showSchoolClasses(Map<String, TaggedDomSchoolClass> schoolClasses);
		void showStudentModels(Map<String, String> models);
		void setLoadingTreeMessage();
		void setDescription(String string, IsWidget w);
		void setTitle(String title);
		void setModelSelect(String id);
		void setEmptyTreeMessage();
		boolean isMethod();
		void setMethod(String label);
		void showMethods(List<DomMethod> methods);
		void setActiveMethod(PersistenceId id);
    }
    
    @Inject void setView(JsTeacherStudentModelView view) {
        super.setView(view);
    	this.view = view;
    	bus.addHandlerToSource(SelectionEvent.getType(), view, this);
    }
    @Inject DescriptionPresenter description;

	private Promise<DomStudentModelContext> currentModel;
	private Promise<?> allModels;

	@Inject StudentModelPresenter(EventBus bus, DwoGlobalVars vars) {
	    super(bus, LOG, vars);
    }
    
    public void init() {
    	view.clear();
    	view.init();
        view.setHelp(dwoGlobalVars.buildHelpUrl("#studentmodel"));
        filter = Collections.emptyMap();
        updateMethods();
    	updateSchoolclasses();
    	updateStudentModels();
    }

    private void updateMethods() {
		service.getMethods().then( list -> { view.showMethods(list.getValue()); return null; });	
	}

	private void updateStudentModels() {
		allModels = service.getModels().then(this::stap2, FAILURE);
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
		p.getValue().stream()
		.filter(model -> model.getModelStructure().getInfo().getTitle() != null)
		.sorted(this).forEach(model -> { 
			String key = model.getId().getIdString();
			String title = getTitle(model.getModelStructure().getInfo(),lang);
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
		String t1 = getTitle(o1.getModelStructure().getInfo(),lang);
		String t2 = getTitle(o2.getModelStructure().getInfo(),lang);
		return String.CASE_INSENSITIVE_ORDER.compare(t1, t2);
	}
	
	@JsMethod
	public void selectModel(String id) {
		if (id == null|| id.isEmpty()) {
			view.setEmptyTreeMessage();
			view.setTitle("");
			this.currentModel = null;
			return;
		}
		view.setLoadingTreeMessage();
		LOG.info("select Model " + id);
		PersistenceId pid = new PersistenceId(id);
		currentModel = service.getStudentModel(pid);
		if (!view.isMethod())
			currentModel = currentModel.then(this::studentModel, FAILURE);
		else
			currentModel = currentModel.then(this::studentModelMethod, FAILURE);
	}
	
	Promise<DomStudentModelContext> studentModel(Promise<DomStudentModelContext> p) {
		DomStudentModelStructure struc = p.getValue().getModelStructure();
		String t = getTitle(struc.getInfo());
		DomTree<String> tree = new DomTree<>(t);
		Map<String, DomTree<String>> map = new LinkedHashMap<>();
		tree.setChildren(map);
		return service.getActiveMethod(struc.getActiveMethod()).then(m -> {
			view.setActiveMethod(m.getValue().getId());
			for ( DomStudentModelCategory cat : struc.getCategories()) {
				DomTree<String> tcat = new DomTree<>(getTitle(cat.getInfo()));
				tcat.setChildren(children(cat.getObjectives(), m.getValue()));
				if (!tcat.getChildren().isEmpty())
					map.put(cat.getInfo().getId(), tcat);
			}
			view.showTree(tree);
			view.setTitle(FilterUtil.setFilter(filter, m.getValue()));
			return p;
		});	
	}
		
	Promise<DomStudentModelContext> studentModelMethod(Promise<DomStudentModelContext> p) {
		DomStudentModelStructure struc = p.getValue().getModelStructure();
		if (struc.getActiveMethod() == null) return studentModel(p);
		return studentModelMethodTree(struc)
		.flatMap(method -> {
          view.setActiveMethod(method.getId());
		  return p;
		});
	}

  @JsMethod
	public void onGraph() {
		LOG.info("on graph click");
		currentModel.then(p -> {
			DomStudentModelContext item = p.getValue();
			JSONObject json = new JSONObject();
			json.put("title", new JSONString(getTitle(item.getModelStructure().getInfo(),lang)));
			json.put("id", new JSONString(item.getId().getIdString()));
			SwitchViewEvent ev = new SwitchViewEvent(SwitchViewEvent.SelectedView.STUDENTRESULTSGRAPH, json.getJavaScriptObject());
			bus.fireEvent(ev);
			return null;
		});
	}
	
	
	private Map<PersistenceId, Promise<FilterMethodDialog>> filterDialogs = new HashMap<>();
		
	@JsMethod
	public void onFilter() {
		Promise<FilterMethodDialog> p;
		
		p = currentModel.flatMap((DomStudentModelContext m) -> {
			PersistenceId key = m.getModelStructure().getActiveMethod();
			if (key == null) throw new NoSuchElementException();
			return filterDialogs.computeIfAbsent(key, k -> service.getActiveMethod(k).map(FilterMethodDialog::new));
		});
		p.then( q -> {		
			FilterMethodDialog filterPanel = q.getValue();
			filterPanel.setValue(filter);
			filterPanel.addCloseHandler(ev -> { 
				LOG.info("filter settings closed");
				filter = filterPanel.getValue();
				selectModel(currentModel.getValue().getId().getIdString());
			});
			filterPanel.show();
			return null; });
	}
	
	@JsMethod
	public void onSchoolClass(String id) {
		if (id.isEmpty()) return;
		if (currentModel == null) return;
		DomSchoolClass sc = schoolClasses.get(id).getSchoolClass();
		LOG.info("on schoolclass " + sc.getSchoolClassName());
		currentModel.then( p ->  {
			JSONObject state = new JSONObject();
			state.put("id", new JSONString(p.getValue().getId().getIdString()));
			bus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SMCLASSRESULTS, sc, state.getJavaScriptObject()));
			return p;
		});
	}
		
	@JsMethod
	public void onSchoolClassFilter(String id) {		
		currentModel.then(m -> { 
			JSONObject state = new JSONObject();
			state.put("id", new JSONString(m.getValue().getId().getIdString()));
			bus.fireEvent(new SwitchViewEvent(SelectedView.SMCLASSFILTER, state.getJavaScriptObject()));
			return null;
		});		
	}

	@JsMethod
	public void onMethod(boolean value) {
		LOG.info("on method " + value);
		if (currentModel == null) return;
		if (!value)
			currentModel = currentModel.then(this::studentModel, FAILURE);
		else
			currentModel = currentModel.then(this::studentModelMethod, FAILURE);
		
	}
	
	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		String id = (String) event.getSelectedItem().getUserObject();
		LOG.info("on selection " + id);
		if (id.startsWith(String.valueOf(DomMethod.key(currentModel.getValue().getModelStructure().getActiveMethod())))) {
			Widget w = event.getSelectedItem().getWidget();
			if (w instanceof HasText) id = ((HasText) w).getText();
			view.setDescription(id, null);
			return;
		}
		DomStudentModelContextInfo info = new DomStudentModelContextInfo();
		info.setId(id);
		currentModel.then( p -> 
			description.get(p.getValue(), info))
		.then(p -> {view.setDescription(event.getSelectedItem().getText(), p.getValue()); return p;}, FAILURE);
	}

	public void init(JavaScriptObject resultState) {
		init();
		if (resultState != null) {
			allModels.then(p -> {
				String id = new JSONObject(resultState).get("id").isString().stringValue();
				view.setModelSelect(id);
				selectModel(id);				
				return p; } );
		}
	}
	
	@JsMethod
	public void onMethodsSelect(String id) {
		LOG.info("methods change " + id);
		if (currentModel == null) return;
		final PersistenceId activeMethod = id.isEmpty()? null : new PersistenceId(id);
		currentModel = currentModel.then(p -> updateActiveMethod(p, activeMethod));
		currentModel.onResolve( () -> { boolean check = view.isMethod(); onMethod(check); });
	}
	
	private Promise<DomStudentModelContext> updateActiveMethod(Promise<DomStudentModelContext> p, PersistenceId activeMethod) {
		p.getValue().getModelStructure().setActiveMethod(activeMethod);
		return service.updateActiveMethod(p.getValue());
	}
}
