package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentScoDataManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherResultsManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherScormValuesManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent.SelectedView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEventHandler;
import nl.uu.fi.dwo.rest.dom.entities.DomClearStudentDataForScoAndClass;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.entities.RestClearStudentDataForScoAndClass;
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

    private static final Logger LOG = Logger.getLogger(ResultsService.class.getName());

    final static String COMPLETED = "completed";
    final static String INCOMPLETE = "incomplete";

    static final String COMPLETION_STATUS = "cmi.completion_status";

    private SecuredTeacherResultsManager manager = new SecuredTeacherResultsManager();
    private SecuredTeacherScormValuesManager scormValues = new SecuredTeacherScormValuesManager();
    private SecuredStudentScoDataManager scoData = new SecuredStudentScoDataManager();

    private final DwoGlobalVars dwoGlobalVars;

    static final Collection<String> keys = Arrays.asList(
            SUSPEND_DATA,
            "cmi.location",
            REVIEW_DATA
    );

    @Inject
    void setEventBus(EventBus eventBus) {
        eventBus.addHandler(SwitchViewEvent.TYPE, this);
    }

    @Inject
    ResultsService(DwoGlobalVars aDwoGlobalVars) {
        dwoGlobalVars = aDwoGlobalVars;
    }

    Promise<DomResultsPerTeacher> getResultsPerTeacher() {
        DomContext context = getContext();
        return dwoGlobalVars.getProfile().then(new Success<DomDwoProfile, DomResultsPerTeacher>() {

            @Override
            public Promise<DomResultsPerTeacher> call(
                    Promise<DomDwoProfile> resolved) throws Exception {
                return manager.getTeachersResults(context, resolved.getValue());
            }
        });
    }

    Promise<DomResultsPerTeacher> selectedResultsPerTeacher(DomSchoolClass schoolClass, Collection<DomCourse> courseList) {
      DomContext context = getContext();
      DomResultsPerTeacher dom = new DomResultsPerTeacher();
      dom.setSchoolClasses(Collections.singletonList(new DomMapEntry<PersistenceId, DomSchoolClass>(schoolClass.getId(), schoolClass)));
      dom.setCourses(courseList.stream()
        .map( item -> new DomMapEntry<>(item.getId(), item))
        .collect(Collectors.toList()));
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

    public Promise<DomResultsPerTeacher> createStudentResults(DomScoContext sco, DomSchoolClass schoolclass, List<DomStudent> students) {
        RestClearStudentDataForScoAndClass rest = new RestClearStudentDataForScoAndClass();
        rest.setRestContext(getContext());
        rest.setClearStudentDataForScoAndClass(new DomClearStudentDataForScoAndClass());
        rest.getClearStudentDataForScoAndClass().setDomSchoolClass(schoolclass);
        rest.getClearStudentDataForScoAndClass().setDomStudentList(students);
        rest.getClearStudentDataForScoAndClass().setDomScoContext(sco);
        return dwoGlobalVars.getProfile().then(p -> {
            rest.getClearStudentDataForScoAndClass().setDomProfile(p.getValue());
            return manager.createStudentResults(rest);
        });
    }

    Map<PersistenceId, Promise<JSONValue>> launchDataCache = new HashMap<>();
    Map<PersistenceId, Promise<Map<String, String>>> suspendDataCache = new HashMap<>();

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
            values = scormValues.getValues(dom, getContext(), keys);
            suspendDataCache.put(dom.getId(), values);
        }
        return values;
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
    }

    @Override
    public void onSwitchViewEvent(SwitchViewEvent switchViewEvent) {
        // if switch to RESULTS clear cache
        if (SelectedView.RESULTS == switchViewEvent.getEventValue()) {
            clearCache();
        }
    }
    
    
}
