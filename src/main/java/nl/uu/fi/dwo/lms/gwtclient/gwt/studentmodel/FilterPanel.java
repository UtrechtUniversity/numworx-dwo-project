package nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.web.bindery.event.shared.EventBus;

import dagger.Lazy;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.studentmodel.JsTeacherClassFilterDisplay;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsService;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class FilterPanel extends Composite implements ProvidesKey<DomStudentModelContext4Student> {
	private static final Logger LOG = Logger.getLogger(FilterPanel.class.getName());

	@Inject PersonsService persons;
	@Inject StudentModelService service;
	
	private final List<DomStudentModelContext4Student> filters;
	private Map<String,String> classes;
	private Map<String,Boolean> check = new HashMap<>();
	private Set<String> changed = new HashSet<>();
	private CellTable<DomStudentModelContext4Student> table;
	private DomStudentModelContextId modelId;
	private final LoggingFailure FAILURE;
	private final RootPanel root;
	
	
    TextColumn<DomStudentModelContext4Student> nameColumn = new TextColumn<DomStudentModelContext4Student>() {
        @Override
        public String getValue(DomStudentModelContext4Student item) {
          return classes.get(getKey(item));
        }
      };
     
	Column<DomStudentModelContext4Student, Boolean> checkColumn = new Column<DomStudentModelContext4Student, Boolean>(new CheckboxCell()) {

		@Override
		public Boolean getValue(DomStudentModelContext4Student object) {
			return check.getOrDefault(getKey(object), Boolean.FALSE);
		}
		
	};
	
	Column<DomStudentModelContext4Student, String> buttonColumn = new Column<DomStudentModelContext4Student, String>(new ButtonCell()) {

	  @Override
	  public String getValue(DomStudentModelContext4Student object) {
	    return "open Filter";
	  }

	@Override
	public String getCellStyleNames(Context context, DomStudentModelContext4Student object) {
		Boolean v = check.getOrDefault(getKey(object), Boolean.FALSE);
		if (!v) return "hidden-node";
		return super.getCellStyleNames(context, object);
	}
	};
	
	@Inject Lazy<FilterDialog> settings;
	
	@Inject FilterPanel(EventBus bus) {
		root = RootPanel.get(JsTeacherClassFilterDisplay.getId());
		FAILURE = new LoggingFailure(LOG, bus);
		nameColumn.setSortable(true);
		
		buttonColumn.setFieldUpdater(new FieldUpdater<DomStudentModelContext4Student, String>() {
			  public void update(int index, DomStudentModelContext4Student object, String value) {
					Map<String, Map<String, Set<Integer>>> filter = object.getFilter();
					settings.get().setValue(filter);
					
					settings.get().addCloseHandler(ev -> { 
						LOG.info("filter settings closed");
						Map<String, Map<String, Set<Integer>>> nieuw = settings.get().getValue();
						changed.add(getKey(object));
						object.setFilter(nieuw);
						if (check.getOrDefault(getKey(object), Boolean.FALSE)) {
							service.updateForClass(object);
						}
					});
					settings.get().show();
			  }
			});
		checkColumn.setFieldUpdater(new FieldUpdater<DomStudentModelContext4Student, Boolean>() {

			@Override
			public void update(int index, DomStudentModelContext4Student object, Boolean value) {
				String key = getKey(object);
				Boolean prev = check.put(key, value);
				if (!value.equals(prev)) {
					if (value) {
						service.updateForClass(object);
					} else {
						DomStudentModelContext4Student copy = new DomStudentModelContext4Student(object.getId());
						copy.setSchoolClass(object.getSchoolClass());
						copy.setFilter(null);
						copy.setOptLock(object.getOptLock());
						copy.setModelStructure(null);	
						service.updateForClass(copy);
					}
					table.redrawRow(index);
				}
			}
		});
		ProvidesKey<DomStudentModelContext4Student> keyProvider = this;
		table = new CellTable<>(keyProvider);
		table.addStyleName("dwo");
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		table.addColumn(nameColumn, "Naam");
		table.addColumn(checkColumn, "Filter");
		table.addColumn(buttonColumn, "");
		// Create a data provider.
	    ListDataProvider<DomStudentModelContext4Student> dataProvider = new ListDataProvider<DomStudentModelContext4Student>();
	    filters = dataProvider.getList();
	    dataProvider.addDataDisplay(table);
	    initWidget(table);
	}

	public Promise<?> init(JavaScriptObject resultState) {
		root.clear();
		filters.clear();

		JSONObject state = new JSONObject(resultState);
		Promise<List<DomSchoolClass>> classes = persons.getTeachersSchoolClasses();
		classes = classes.then(this::toClassName);
		String id = state.get("id").isString().stringValue();
		PersistenceId pid = new PersistenceId(id);
		modelId = new DomStudentModelContextId(pid);
		Promise<List<DomStudentModelContext4Student>> promises = classes.then(p -> { 
			
			List<Promise<DomStudentModelContext4Student>> list =
			p.getValue().stream().map(this::toFilter).collect(Collectors.toList());
			return Promises.all(list);
		});
		return promises.then(this::toView, FAILURE);
		
	}
	
	Promise<List<DomSchoolClass>> toClassName(Promise<List<DomSchoolClass>> p) {
		
		classes = p.getValue().stream()
				.collect(Collectors.toMap((DomSchoolClass item) -> item.getId().getIdString(), 
										  (DomSchoolClass item) -> item.getSchoolClassName()));
		return p;
	}
	
	Promise<DomStudentModelContext4Student> toFilter(DomSchoolClass sc) {
		return service.getForClass(modelId, sc).then(p -> {
			check.put(sc.getId().getIdString(), null != p.getValue());
			return service.stap0(p, modelId, sc);
		});
	}
	
	Promise<List<DomStudentModelContext4Student>> toView(Promise<List<DomStudentModelContext4Student>> p) {
		filters.addAll(p.getValue());
		root.clear();
	    root.add(this);
		return p;
	}

	@Override
	public String getKey(DomStudentModelContext4Student item) {		
		return item.getSchoolClass().getId().getIdString();
	}
	
	
}
