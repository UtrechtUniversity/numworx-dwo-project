/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.web.bindery.event.shared.EventBus;

import jsinterop.annotations.JsMethod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent.SelectedView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.DomResultTree;
import nl.uu.fi.dwo.rest.dom.entities.DomClearStudentDataForScoAndClass;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomResultCourseInClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

/**
 *
 * @author plas0006
 */
public class SelectedResultsPresenter {

    private static final Logger LOG = Logger.getLogger(SelectedResultsPresenter.class.getName());

	private final LoggingFailure FAILURE;
    private final EventBus eventBus;
    private final DwoGlobalVars dwoGlobalVars;

    private Display view;
    @Inject ResultsService resultService;
    private JavaScriptObject resultState;
    //model
    private DomResultTree resultTree;

    public interface Display  extends BasicDisplay{

        void updateResultTree(DomResultTree data);

        void init(JavaScriptObject aResultState);

        void setEmptyTableMessage();

        void setLoadingTableMessage();

    }

    @Inject SelectedResultsPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        FAILURE = new LoggingFailure(LOG, eventBus);
    }

    public void init(DomResultTree aResultTree, JavaScriptObject aResultState) {
        resultTree = aResultTree;
        resultState = aResultState;
        view.init(aResultState);
    }

    public void updateTree() {
        //view.clear();
        LOG.log(Level.INFO, "DwoGlobalVarsState = " + dwoGlobalVars.getState().name());
        Promise<DomResultsPerTeacher> promResults;
        promResults = resultService.getResultsPerTeacher();
        // onSuccess calculate results and show.
        promResults.then(new Success<DomResultsPerTeacher, Void>() {
            @Override
            public Promise<Void> call(Promise<DomResultsPerTeacher> resolved) throws Exception {
                //calculate tree and call plotting
                LOG.log(Level.INFO, "DomResults returned.");
                resultTree = new DomResultTree(resolved.getValue());
                LOG.log(Level.INFO, "ResultTree obtained.");// plots the result tree.
                view.updateResultTree(resultTree);
                LOG.log(Level.INFO, "plotted ResultMatrix.");
                return null;
            }
        },
                FAILURE);
    }

    public void setView(Display aView) {
        view = aView;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@JsMethod
    public void sealModuleActivities(String courseID, String classid) {
    	PersistenceId schoolclass = new PersistenceId(classid);
    	DomResultTeacher<DomResultStudent> studentTree = resultTree.getStudentTree();
    	DomResultSchoolClass<DomResultStudent> domschoolclass = studentTree.getChildren().get(schoolclass);
    	List<DomStudent> students = domschoolclass.getChildren().values().stream().map(DomResultStudent::getStudent).collect(Collectors.toList());

    	PersistenceId course = new PersistenceId(courseID);
    	DomResultSchoolClass<?> domclassresults = resultTree.getResultTree().getChildren().get(schoolclass);
    	DomResultScore<?> courseResults = domclassresults.getChildren().get(course);
    	Set<PersistenceId> scos = courseResults.getChildren().keySet();
    // TODO verzegel course, dus alle activitetien
    	Collection<Promise<Object>> promises = new ArrayList<>();
    	for(PersistenceId scoid : scos) {
    		DomScoContext sco = new DomScoContext();
    		sco.setId(scoid);
			promises.add(resultService.createStudentResults(sco, domschoolclass.getSchoolClass(), students)
					.map(dom -> dom.getStudentScoContexts().stream().map(DomMapEntry::getValue).collect(Collectors.toList()))
					.flatMap(resultService::sealList)
					.then(this::updateResultTree));
    	}
    	Promises.all(promises).then(null, FAILURE);
    	
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@JsMethod
	public void sealSingleActivity(String scoId, String classid) {
		PersistenceId schoolclass = new PersistenceId(classid);
        DomResultTeacher<DomResultStudent> studentTree = resultTree.getStudentTree();
        DomResultSchoolClass<DomResultStudent> domschoolclass = studentTree.getChildren().get(schoolclass);
        List<DomStudent> students = domschoolclass.getChildren().values().stream().map(DomResultStudent::getStudent).collect(Collectors.toList());

		PersistenceId scoid = new PersistenceId(scoId);
		DomScoContext sco = new DomScoContext();
		sco.setId(scoid);
		resultService.createStudentResults(sco, domschoolclass.getSchoolClass(), students)
			.map(dom -> dom.getStudentScoContexts().stream().map(DomMapEntry::getValue).collect(Collectors.toList()))
			.flatMap(resultService::sealList)
			.then(this::updateResultTree, FAILURE);
	}
    
    Promise<Object> updateResultTree(Promise<List<DomStudentScoContext>> p) {
        if(!p.isDone() || p.getFailure() != null) return null;
        resultTree.updateResultStudentSco(p.getValue());
    	view.updateResultTree(resultTree);
    	return null;
    }
    
    @JsMethod 
    public void showStudentResults (JavaScriptObject context, String scoid, String studentid, String classid) {
        LOG.fine("entering showStudentResults " + context + "," + scoid);
		PersistenceId schoolclass = new PersistenceId(classid);
		DomResultTeacher<DomResultStudent> studentTree = resultTree.getStudentTree();
		DomResultSchoolClass<DomResultStudent> domschoolclass = studentTree.getChildren().get(schoolclass);
		PersistenceId key = new PersistenceId(studentid);
		DomStudent student = domschoolclass.getChildren().get(key).getStudent();
		DomScoContext sco = new DomScoContext(); sco.setId(new PersistenceId(scoid));
		Promise<DomStudentScoContext> p1 = resultService.createStudentResults(sco, domschoolclass.getSchoolClass(), Collections.singletonList(student))
		.map(p -> p.getStudentScoContexts().get(0).getValue());		
		Promise<JSONValue> p2 = resultService.getJSONLaunchDataBytes(sco, domschoolclass.getSchoolClass());		
		Promise<Map<String,String>> p3 = p1.then(  p-> resultService.getValues(p.getValue(), ResultsService.keys));
    	
		Promises.all(p1,p2,p3).then(new Success<Object, Object>() {

			@Override
			public Promise<Object> call(Promise<Object> resolved) throws Exception {
			    DomResultStudentScoContext ssc = new DomResultStudentScoContext(p1.getValue(), student);
			    ssc.setParent(domschoolclass);
				String launch_data = p2.getValue().toString();
                Map<String, String> userState = p3.getValue();
                userState.put("cmi.launch_data", launch_data);
                userState.put(ResultsService.COMPLETION_STATUS, p1.getValue().getCompletionStatus());
                userState.put("cmi.score.raw", Double.toString(p1.getValue().getScore()));
                updateResultTree(Promises.resolved(Collections.singletonList(p1.getValue())));
                eventBus.fireEvent(new SwitchViewEvent(SelectedView.RESULTSSTUDENT, resultTree, ssc, context, userState));
				return null;
			}
		}, FAILURE);
				
    	
    	
    }

    public void reinit(DomResultTree aResultTree, JavaScriptObject aResultState) {
      resultTree = aResultTree;
      resultState = aResultState;
      view.updateResultTree(resultTree);
    }
}
