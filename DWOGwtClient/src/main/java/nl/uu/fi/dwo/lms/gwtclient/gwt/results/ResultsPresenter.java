package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.web.bindery.event.shared.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.ModulesOfSchoolclassService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.DomMappedResultsPerTeacher;
import nl.uu.fi.dwo.rest.dom.DomResultPlotMatrix;
import nl.uu.fi.dwo.rest.dom.DomResultTree;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacherv2;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomResultCourseInClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacherv2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

/**
 * Initial section panel. Here the schoolclass, viewState and type of modules are 
 * selected. Additionally the results are fetched.
 *
 * @author Gert van der Plas
 */
public class ResultsPresenter extends AbstractResultsPresenter {

  
  
    private static final Logger LOG = Logger.getLogger(ResultsPresenter.class.getName());

    private final Failure FAILURE;

    private Display view;
    @Inject ResultsService resultService;
    @Inject PersonsService  personService;

   //premodel
    private Promise<DomMappedResultsPerTeacher> mappedResults;
    //model
    private DomResultTree resultTree;
    private DomResultPlotMatrix resultMatrix;
    private DomResultCourseInClass course = null; //null means all courses.
    private DomResultSchoolClass schoolClass = null; //null means all classes.

    private int stage;

    int getStage() {
      return stage;
    }

    public void setStage(int stage) {
      this.stage = stage;
    }

    public interface Display extends BasicDisplay{

        void setResultTree(DomResultTree data);
        void setResultTreeWithContext(DomResultTree data,  JavaScriptObject context);
        void setEmptyTableMessage();

        void setLoadingTableMessage();
        void setChooseModulesTable();
        void setRemedialView(boolean set);
        
    }

    @Inject
	protected ResultsPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
    	super(anEventBus, aDwoGlobalVars);
        FAILURE = new LoggingFailure(LOG, anEventBus);

    }

    @SuppressWarnings("unchecked")
    public void init() {
        view.clear();
        view.init();
        LOG.log(Level.INFO, "DwoGlobalVarsState = " + dwoGlobalVars.getState().name());
        view.setLoadingTableMessage();
        course = null;
        schoolClass = null;
        resultService.clearCache();
        Promise<DomResultsPerTeacherv2> promResults;
        if (stage < 1) {
          promResults = resultService.getResultsPerTeacher();
        } else {
          promResults = personService.getTeachersSchoolClasses().map(list -> {
            DomResultsPerTeacherv2 result = new DomResultsPerTeacherv2();
            result.setClassCourses(Collections.EMPTY_LIST);
            result.setCourses(Collections.EMPTY_LIST);
            result.setFetchTimeStamp(System.currentTimeMillis());
            result.setScoContexts(Collections.EMPTY_LIST);
            result.setStudents(Collections.EMPTY_LIST);
            result.setStudentsOfClasses(Collections.EMPTY_LIST);
            result.setStudentScoContexts(Collections.EMPTY_LIST);
            result.setTeacher(new DomTeacher(dwoGlobalVars.getCurrentUser()));           
            result.setSchoolClasses(list);
            return result;
          });
        }
        //
       
            
        // onSuccess calculate results and show.
        mappedResults = promResults.map( r -> new DomMappedResultsPerTeacher(r));
        mappedResults.
        then(new Success<DomMappedResultsPerTeacher, Void>() {
            @Override
            public Promise<Void> call(Promise<DomMappedResultsPerTeacher> resolved) throws Exception {
                //calculate tree and call plotting
                LOG.log(Level.INFO, "DomResults returned.");
                DomMappedResultsPerTeacher value = resolved.getValue();
                resultTree = new DomResultTree(value);
                resultTree.insertStudentCourses();
                LOG.log(Level.INFO, "ResultTree obtained.");// plots the result tree.
                view.setResultTree(resultTree);
                LOG.log(Level.INFO, "plotted ResultMatrix.");
                if(value.getSchoolClasses().isEmpty())
                  view.setEmptyTableMessage();
                return null;
            }
        },
                FAILURE
        ).recover(p -> {
          view.setEmptyTableMessage();
          return null;
        });
    }    

    
    public void init(JavaScriptObject context){
        view.setResultTreeWithContext(resultTree, context);
    }
    
    public void setView(Display aView) {
        view = aView;
        view.setHelp(dwoGlobalVars.buildHelpUrl("#results"));
        view.setRemedialView(dwoGlobalVars.isPremium() && dwoGlobalVars.isRemedial());
    }

    @JsMethod
    public void showSelectedResults(JavaScriptObject resultState, String classid, JsArrayString courses) {
        if (stage > 0) {
          DomMappedResultsPerTeacher map = mappedResults.getValue();
          DomSchoolClass schoolclass = findSchoolClass(map, new PersistenceId(classid));
          Collection<DomCourse> courseList = new ArrayList<>(courses.length());
          for(int i = 0; i < courses.length(); i++) {
            courseList.add(
              map.getCourses().get(new PersistenceId(courses.get(i)))
              );
          }
          Promise<DomMappedResultsPerTeacher> r = resultService.selectedResultsPerTeacher(schoolclass, courseList).then( p -> {
            DomResultsPerTeacherv2 results = p.getValue();
            inject(mappedResults.getValue(), results);
            return mappedResults;
          }, FAILURE).fallbackTo(mappedResults);
          r.then( 
            p -> {
              DomResultTree resultTree = new DomResultTree(r.getValue());
              resultTree.insertStudentCourses();
              view.setResultTreeWithContext(resultTree, resultState);
              eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SELECTEDRESULTS, resultTree, resultState));
              return null;
              }
          );
          return;
        }
      
        eventBus.fireEvent(
                new SwitchViewEvent(SwitchViewEvent.SelectedView.SELECTEDRESULTS, resultTree, resultState));
    }

    private void inject(DomMappedResultsPerTeacher value, DomResultsPerTeacherv2 results) {
     results.getClassCourses().forEach(entry -> value.getClassCourses().putIfAbsent(entry.getId(), entry));
     results.getCourses().forEach(entry -> value.getCourses().putIfAbsent(entry.getId(), entry));
     value.setFetchTimeStamp(results.getFetchTimeStamp());
     results.getSchoolClasses().forEach(entry -> value.getSchoolClasses().putIfAbsent(entry.getId(), entry));
     results.getScoContexts().forEach(entry -> value.getScoContexts().putIfAbsent(entry.getId(), entry));
     results.getStudents().forEach(entry -> value.getStudents().putIfAbsent(entry.getId(), entry));
     results.getStudentScoContexts().forEach(entry -> value.getStudentScoContexts().putIfAbsent(entry.getId(), entry));
     results.getStudentsOfClasses().forEach(entry -> value.getStudentsOfClasses().putIfAbsent(entry.getId(), entry));
     DomMappedResultsPerTeacher r = new DomMappedResultsPerTeacher(results);
     r.getStudentScoPages().entrySet().forEach(entry -> value.getStudentScoPages().putIfAbsent(entry.getKey(), entry.getValue()));
     value.setTeacher(results.getTeacher());
    }

    @JsMethod
    public void selectStudentResults(JavaScriptObject resultState) {
        LOG.log(Level.SEVERE, "Select StudentResults");
        eventBus.fireEvent(
                new SwitchViewEvent(SwitchViewEvent.SelectedView.SELECTSTUDENTRESULTS, resultTree, resultState)
        );
    }

    @JsMethod
    public void setChooseModulesTable(String schoolclassid) {
      if (stage < 1)
        view.setChooseModulesTable();
      else { 
        PersistenceId id = new PersistenceId(schoolclassid);
        Promise recover = mappedResults;
        // fetch the classcourses of a schoolclass, only once!
        mappedResults = mappedResults.flatMap(map -> {
          if (map.getClassCourses().values().stream().map(DomClassCourse4Teacher::getClassId).anyMatch(id::equals))
            return Promises.resolved(map);
          return resultService.getModules(findSchoolClass(map, id)).map( modules -> inject(map, modules));
        } ).fallbackTo(recover);

        if (dwoGlobalVars.isModulesOnly() && dwoGlobalVars.isPremium() && dwoGlobalVars.isRemedial()) {
        	mappedResults = mappedResults.map(
        		r -> {
        			Collection<DomClassCourse4Teacher> ccs = r.getClassCourses().values();
        			for(DomClassCourse4Teacher item : ccs) {
        				CourseType type = item.getCourseType();
        				ViewState state = item.getViewState();
        				if ( type == CourseType.assesment && state == ViewState.studentsAndTeachers) {
        					state = ViewState.students; // remedial kolom
        				}
        				item.setViewState(state);
        			}
         			return r;
        		});
        }
        
        
        // if no classcourses of schoolclass, fetch them from server....
        mappedResults.then((Promise<DomMappedResultsPerTeacher> resolved) -> {
              //calculate tree and call plotting
              LOG.log(Level.INFO, "DomResults returned.");
              DomMappedResultsPerTeacher value = resolved.getValue();
              resultTree = new DomResultTree(value);
              resultTree.insertStudentCourses();
              LOG.log(Level.INFO, "ResultTree obtained.");// plots the result tree.
              view.setResultTree(resultTree);
              LOG.log(Level.INFO, "plotted ResultMatrix.");
              //stage = 0;
              return null;
          }
        ).onResolve(() -> { 
          view.setChooseModulesTable();
        });
      }
    }

    private DomMappedResultsPerTeacher inject(List<Object> values) {
      return inject((DomMappedResultsPerTeacher)values.get(0), (DomCoursesOfSchoolClass4Teacherv2) values.get(1));
    }
    private DomMappedResultsPerTeacher inject(DomMappedResultsPerTeacher value, DomCoursesOfSchoolClass4Teacherv2 modules) {
      modules.getCourses()
      .stream()
      .filter(entry -> !Boolean.TRUE.equals(entry.getWithChildren()))
      .forEach(entry -> value.getCourses().put(entry.getId(), entry));
      modules.getClassCourses()
      .stream()
      .filter( entry -> value.getCourses().keySet().contains(entry.getCourseId()))
      .forEach(entry -> value.getClassCourses().put(entry.getId(), entry));
      return value;
    }
    
    private DomSchoolClass findSchoolClass(DomMappedResultsPerTeacher map, PersistenceId id) {
      return map.getSchoolClasses().get(id);
    }
    
}
