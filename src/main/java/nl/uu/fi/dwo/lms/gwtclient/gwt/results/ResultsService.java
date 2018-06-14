package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentScoDataManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherResultsManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherScormValuesManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.entities.DomClearStudentDataForScoAndClass;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.entities.RestClearStudentDataForScoAndClass;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import com.google.gwt.json.client.JSONValue;

/**
 * Persistent model service for Teacher results. Retrieves DomResultsPerTeacher data.
 * In the future it may cache this data and merge updates into it. it may also request
 * Updates if required. In example, fetch new data if older than xx seconds or
 * Check if changes of results exist within the schoolgroup.
 * 
 * @author Gert van der Plas
 */
class ResultsService {

    private static final Logger LOG = Logger.getLogger(ResultsService.class.getName());
    
    private final static String COMPLETED = "completed";
    private final static String INCOMPLETE = "incomplete";
    
    static final String COMPLETION_STATUS = "cmi.completion_status";


    private SecuredTeacherResultsManager manager = new SecuredTeacherResultsManager();
    private SecuredTeacherScormValuesManager scormValues = new SecuredTeacherScormValuesManager();
    private SecuredStudentScoDataManager scoData = new SecuredStudentScoDataManager();
    
    private final DwoGlobalVars dwoGlobalVars;

    static final Collection<String> keys = Arrays.asList(
            "cmi.suspend_data",
            "cmi.location",
            //"cmi.score.raw",
            //ResultsService.COMPLETION_STATUS,
            "cmi.comments_from_lms.0.comment"
    );
    
   @Inject ResultsService(DwoGlobalVars aDwoGlobalVars){
        dwoGlobalVars=aDwoGlobalVars;
    }
    
    public Promise<DomResultsPerTeacher> getResultsPerTeacher() {
        DomContext context = getContext();
        return dwoGlobalVars.getProfile().then(new Success<DomDwoProfile, DomResultsPerTeacher>() {

			@Override
			public Promise<DomResultsPerTeacher> call(
					Promise<DomDwoProfile> resolved) throws Exception {
				return manager.getTeachersResults(context, resolved.getValue());
			}});
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
    	List<Promise<DomStudentScoContext>> list = new ArrayList<> ();
    	for( DomStudentScoContext item: doms) {
    		if (! COMPLETED .equals(item.getCompletionStatus()) )
    				list.add(seal(item, true)); // XXX as fast as you can?
    	}
    	return Promises.all(list);
    }
    
    
    public Promise<DomResultsPerTeacher> createStudentResults(DomScoContext sco, DomSchoolClass schoolclass, List<DomStudent> students) {
    	RestClearStudentDataForScoAndClass rest = new RestClearStudentDataForScoAndClass();
    	rest.setRestContext(getContext());
    	rest.setClearStudentDataForScoAndClass(new DomClearStudentDataForScoAndClass());
    	rest.getClearStudentDataForScoAndClass().setDomSchoolClass(schoolclass);
    	rest.getClearStudentDataForScoAndClass().setDomStudentList(students);
    	return dwoGlobalVars.getProfile().then(p -> { 
        	rest.getClearStudentDataForScoAndClass().setDomProfile(p.getValue());
        	return manager.createStudentResults(rest);
    	});
    }
    
    public Promise<JSONValue> getJSONLaunchDataBytes(DomScoContext sco, DomSchoolClassId schoolClass) {
    	return dwoGlobalVars.getProfile().then(
    			p-> scoData.getJSONLaunchDataBytes(sco, p.getValue(), schoolClass, getContext()));
    }

	public Promise<Map<String, String>> getValues(DomStudentScoContext dom, Collection<String> keys) {
		return scormValues.getValues(dom, getContext(), keys);
	}

  public Promise<DomStudentScoContext> setValues(DomStudentScoContext studentSco, Map<String, String> userState) {
      return scormValues.setValues(studentSco, getContext(), userState);
  }
}
