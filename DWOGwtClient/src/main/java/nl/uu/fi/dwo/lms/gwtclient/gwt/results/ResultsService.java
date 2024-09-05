package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentScoDataManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherResultsManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherScormValuesManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherStudentModelManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserScoContextManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent.SelectedView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEventHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsServiceTeacher;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.ModulesOfSchoolclassService;
import nl.uu.fi.dwo.rest.dom.entities.DomClearStudentDataForScoAndClass;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacherv2;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacherv2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.entities.RestClearStudentDataForScoAndClass;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import com.google.gwt.json.client.JSONValue;
import com.google.web.bindery.event.shared.EventBus;

import dagger.Reusable;

/**
 * Persistent model service for Teacher results. Retrieves DomResultsPerTeacher
 * data. In the future it may cache this data and merge updates into it. it may
 * also request Updates if required. In example, fetch new data if older than xx
 * seconds or Check if changes of results exist within the schoolgroup.
 *
 * @author Gert van der Plas
 */
@Reusable
public class ResultsService implements SwitchViewEventHandler {

    static final String SUSPEND_DATA = "cmi.suspend_data";
    static final String REVIEW_DATA = "cmi.comments_from_lms.0.comment";
    static final String REVIEW_CHECK = "cmi.comments_from_lms.1.comment";
    static final String REVIEW_CORRECT = "cmi.comments_from_lms.2.comment";

    private static final Logger LOG = Logger.getLogger(ResultsService.class.getName());

    final static String COMPLETED = "completed";
    final static String INCOMPLETE = "incomplete";

    static final String COMPLETION_STATUS = "cmi.completion_status";

    private SecuredTeacherResultsManager manager = new SecuredTeacherResultsManager();
    private SecuredTeacherScormValuesManager scormValues = new SecuredTeacherScormValuesManager();
    private SecuredStudentScoDataManager scoData = new SecuredStudentScoDataManager();
    private SecuredUserScoContextManager scoContext = new SecuredUserScoContextManager();
    private SecuredTeacherStudentModelManager studentModel = new SecuredTeacherStudentModelManager();

    private ModulesOfSchoolclassService modules;
    private final DwoGlobalVars dwoGlobalVars;

    static final Collection<String> keys = Arrays.asList(
            SUSPEND_DATA,
            "cmi.location",
            REVIEW_DATA,
            REVIEW_CHECK,
            REVIEW_CORRECT
    );

    @Inject
    void setEventBus(EventBus eventBus) {
        eventBus.addHandler(SwitchViewEvent.TYPE, this);
    }

    @Inject
    ResultsService(DwoGlobalVars aDwoGlobalVars) {
        dwoGlobalVars = aDwoGlobalVars;
    }
    
    @Inject
    void setModulesService(ModulesOfSchoolclassService service) {
    	modules = service;
    }
    
    Promise<DomCoursesOfSchoolClass4Teacherv2> getModules(DomSchoolClass dsc) {
    	return modules.getModules(dsc, true).then(this::modulesSort);
    }
    
    private void logfailure(Promise<?> q) {
    	LOG.log(Level.SEVERE, "failure", q.getFailure());
    }

    Promise<DomResultsPerTeacherv2> getResultsPerTeacher() {
        DomContext context = getContext();
        return dwoGlobalVars.getProfile().then(new Success<DomDwoProfile, DomResultsPerTeacherv2>() {

            @Override
            public Promise<DomResultsPerTeacherv2> call(
                    Promise<DomDwoProfile> resolved) throws Exception {
                return manager.selectedTeachersResults(context, resolved.getValue(), new DomResultsPerTeacherv2());
            }
        }).then(this::courseSort).then(null, this::logfailure);
    }

    Promise<DomCoursesOfSchoolClass4Teacherv2> modulesSort(Promise<DomCoursesOfSchoolClass4Teacherv2> p ) {
    	List<DomCourse> courses = p.getValue().getCourses();
    	absoluteOrdening(courses, courses);
    	p.getValue().setCourses(courses);
    	return p;
    }
    
    
    Promise<DomResultsPerTeacherv2> courseSort(Promise<DomResultsPerTeacherv2> p) {
    	List<DomSchoolClass> list = p.getValue().getSchoolClasses();
    	if (!list.isEmpty()) {
    		DomSchoolClass sample = list.get(0);
    		return modules.getModules(sample, true)
    				.then(q -> {
    			
    			absoluteOrdening(q.getValue().getCourses(), p.getValue().getCourses());
    			return p;
    		});
    	}
    	return p;
    }
    
    
    private void setPath(Map<PersistenceId, DomCourse> tree, DomCourse course) {
    	StringBuilder sb = new StringBuilder();
    	DomCourse item = course;
    	do {
    		Long sequenceNr = item.getSequenceNr();
    		char sequence;
    		if (sequenceNr != null)
			  sequence = (char) (sequenceNr.intValue()+'0');
    		else
    		  sequence = ' ';
    		sb.insert(0, sequence);
    		PersistenceId parent = item.getParentID();
    		item = tree.get(parent);
    	} while(item != null);
    	if (course.getSchoolId() == null) {
    		sb.insert(0,'0');
    	} else {
    		sb.insert(0,  '1');
    	}
    	course.setTreeIndex(sb.toString());
    }
    
    // sort the array courses useing 'all' courses tree
    private void absoluteOrdening(List<DomCourse> all,
			List<DomCourse> courses) {
		final Map<PersistenceId, DomCourse> tree = all.stream().collect(Collectors.toMap(DomCourse::getId, Function.identity()));
		courses.forEach(item -> setPath(tree, item));
		Collections.sort(courses, (a,b) -> a.getTreeIndex().compareTo(b.getTreeIndex()));
		long seq = 1;
		for( DomCourse item: courses) {
			item.setSequenceNr(seq++);
		}
	}

	Promise<DomResultsPerTeacherv2> selectedResultsPerTeacher(DomSchoolClass schoolClass, Collection<DomCourse> courseList) {
      DomContext context = getContext();
      DomResultsPerTeacherv2 dom = new DomResultsPerTeacherv2();
      dom.setSchoolClasses(Collections.singletonList(schoolClass));
      dom.setCourses(new ArrayList<>(courseList));
      return dwoGlobalVars.getProfile().flatMap((profile) -> manager.selectedTeachersResults(context, profile, dom)).then(this::courseSort);
    }
    
	Promise<DomResultsPerTeacherv2> selectedResultsPerStudentSco(DomSchoolClass schoolClass, DomStudent student, DomStudentScoContext ssc) {
	      DomContext context = getContext();
	      DomResultsPerTeacherv2 dom = new DomResultsPerTeacherv2();
	      dom.setSchoolClasses(Collections.singletonList(schoolClass));
	      dom.setClassCourses(Collections.emptyList());
	      dom.setCourses(Collections.emptyList());
// Hier gaat het om...
	      DomScoContext sco = new DomScoContext();
	      sco.setId(ssc.getScoID());
	      dom.setScoContexts(Collections.singletonList(sco)); // 
	      dom.setStudents(Collections.singletonList(student));
	      dom.setStudentScoContexts(Collections.singletonList(ssc));
	      return dwoGlobalVars.getProfile().flatMap((profile) -> manager.selectedTeachersResults(context, profile, dom));
	}
	
    
    private DomContext getContext() {
        DomContext context = new DomContext();
        context.setDomHasRole(dwoGlobalVars.getActiveSchoolRoleAndClass().getHasRole());
        return context;
    }

    public Promise<DomStudentScoContext> seal(DomStudentScoContext dom, boolean value) {
        String status = value ? COMPLETED : INCOMPLETE;
        return scormValues.setValues(dom, getContext(), Collections.singletonMap(COMPLETION_STATUS, status));
    }

    public Promise<List<DomStudentScoContext>> sealList(List<DomStudentScoContext> doms) {
        List<Promise<DomStudentScoContext>> list = new ArrayList<>();
        for (DomStudentScoContext item : doms) {
            if (!COMPLETED.equals(item.getCompletionStatus())) {
                list.add(seal(item, true)); // XXX as fast as you can?
            }
        }
        return Promises.all(list);
    }

    public Promise<DomResultsPerTeacherv2> createStudentResults(DomScoContext sco, DomSchoolClass schoolclass, List<DomStudent> students) {
        RestClearStudentDataForScoAndClass rest = new RestClearStudentDataForScoAndClass();
        rest.setRestContext(getContext());
        rest.setClearStudentDataForScoAndClass(new DomClearStudentDataForScoAndClass());
        rest.getClearStudentDataForScoAndClass().setDomSchoolClass(schoolclass);
        rest.getClearStudentDataForScoAndClass().setDomStudentList(students);
        rest.getClearStudentDataForScoAndClass().setDomScoContext(sco);
        return dwoGlobalVars.getProfile().then(p -> {
            rest.getClearStudentDataForScoAndClass().setDomProfile(p.getValue());
            return manager.createStudentResultsv2(rest);
        });
    }

    Map<PersistenceId, Promise<JSONValue>> launchDataCache = new HashMap<>();
    Map<PersistenceId, Promise<Map<String, String>>> suspendDataCache = new HashMap<>();
    Map<PersistenceId, Promise<DomScoContext>> scoContextCache = new HashMap<>();
    Map<PersistenceId, Promise<DomStudentModelContext>> studentModelCache = new HashMap<>();

    public Promise<JSONValue> getJSONLaunchDataBytes(DomScoContext sco, DomSchoolClassId schoolClass) {
        Promise<JSONValue> cache = launchDataCache.get(sco.getId());
        if (cache != null) {
            return cache;
        }

        cache = dwoGlobalVars.getProfile().then(
                p -> scoData.getJSONLaunchDataBytes(sco, p.getValue(), schoolClass, getContext()));

        launchDataCache.put(sco.getId(), cache);
        return cache;
    }

    public Promise<Map<String, String>> getValues(DomStudentScoContext dom) {
        Promise<Map<String, String>> values;
        values = suspendDataCache.get(dom.getId());
        if (values == null || (values.isDone() && values.getFailure() != null)) {
        	if (dom.getId() != null)
        		values = scormValues.getValues(dom, getContext(), keys);
        	else {
        		values = Promises.resolved(keys.stream().collect(Collectors.toMap(Function.identity(), t -> "")));
        	}
            suspendDataCache.put(dom.getId(), values);
        }
        return values;
    }
    
    public Promise<Map<String,String>> getValuesAsync(DomStudentScoContext dom, Collection<String> keys) {
        Promise<Map<String, String>> values;
        values = suspendDataCache.get(dom.getId());
    	if (values == null || (values.isDone() && values.getFailure() != null)) {
    		return scormValues.getValues(dom, getContext(), keys);
    		
    	}
    	if (values.isDone()) {
    		if (values.getValue().keySet().containsAll(keys)) {
    			return values;
    		}
    	}
    	Promise<Map<String, String>> result = scormValues.getValues(dom, getContext(), keys);
    	Promises.all(result, values).then(x -> { values.getValue().putAll(result.getValue()); return null;});
    	return result;
    }

    public Promise<DomStudentScoContext> setValues(DomStudentScoContext studentSco, Map<String, String> userState) {
        Promise<Map<String, String>> promise = suspendDataCache.get(studentSco.getId());
        if (promise != null && promise.isDone() && promise.getFailure() == null) {
            promise.getValue().putAll(userState);
        } else {
        	if (promise != null) {       		
        		Map<String,String> copy = new HashMap<>(userState);
        		suspendDataCache.put(studentSco.getId(), promise.map(v -> {v.putAll(copy); return v;}));
        	}
        }
        return scormValues.setValues(studentSco, getContext(), userState);
    }

    public Promise<Boolean> clearStudentResults(DomScoContext domScoContext,
    DomSchoolClass domSchoolClass,List<DomStudent> domStudentList) {
        RestClearStudentDataForScoAndClass rest = new RestClearStudentDataForScoAndClass();
        rest.setRestContext(getContext());
        DomClearStudentDataForScoAndClass data = new DomClearStudentDataForScoAndClass();
        data.setDomSchoolClass(domSchoolClass);
        data.setDomScoContext(domScoContext);
        data.setDomStudentList(domStudentList);
        return dwoGlobalVars.getProfile().then(p -> {
            rest.getClearStudentDataForScoAndClass().setDomProfile(p.getValue());
            return manager.clearStudentResults(rest);
        });
    }

// FIXME caching policy
    void clearCache() {
        suspendDataCache.clear();
        launchDataCache.clear();
        scoContextCache.clear();
    }

    @Override
    public void onSwitchViewEvent(SwitchViewEvent switchViewEvent) {
        // if switch to RESULTS clear cache
        if (SelectedView.RESULTS == switchViewEvent.getEventValue()) {
            clearCache();
        }
    }
    
    Promise<DomScoContext> getSco0(PersistenceId scoId) {
    	DomScoContext sco = new DomScoContext(); sco.setId(scoId);
    	Promise<DomDwoProfileFull> profile = dwoGlobalVars.getProfile();
    	return profile.flatMap(p -> scoContext.getSco(sco, p, null, getContext()));
    }
    
    Promise<DomScoContext> getSco(PersistenceId scoId) {
    	return scoContextCache.computeIfAbsent(scoId, this::getSco0);
    }
    
    Promise<DomStudentModelContext> getStudentModel0(PersistenceId id) {
    	if (id == null) return Promises.resolved(null);
    	if (id.getType() == PersistenceClassType.PersistentScoContext) {
    		return getSco(id)
    				.map(DomScoContext::getStudentModelContext)
    				.flatMap(this::getStudentModel);
    	}
    	DomStudentModelContextId smid = new DomStudentModelContextId(id);
    	return studentModel.getStudentModel(getContext(), smid);
    }
    
    Promise<DomStudentModelContext> getStudentModel(PersistenceId id) {
    	return studentModelCache.computeIfAbsent(id, this::getStudentModel0);
    }
    
}
