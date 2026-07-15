/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;

import dagger.Lazy;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherClassCourseManager;
import fi.dwo.gwt.lib.rest.util.StringFormatter;

import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent.SelectedView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results.JsSelectedResultsDisplay;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelDeferred;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.ProgressDialogWithAbortDeferred;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.ProgressDialogWithAbortEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.ProgressDialogWithAbortEvent.EventType;
import nl.uu.fi.dwo.rest.dom.DomResultTree;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultCourseInClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoPage;
import nl.uu.fi.dwo.rest.dom.entities.DomResultTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.xapi.Account;
import nl.uu.fi.dwo.rest.dom.xapi.Activity;
import nl.uu.fi.dwo.rest.dom.xapi.ActivityDefinition;
import nl.uu.fi.dwo.rest.dom.xapi.Agent;
import nl.uu.fi.dwo.rest.dom.xapi.Context;
import nl.uu.fi.dwo.rest.dom.xapi.Group;
import nl.uu.fi.dwo.rest.dom.xapi.Statement;
import nl.uu.fi.dwo.rest.dom.xapi.StatementsResult;
import nl.uu.fi.dwo.rest.dom.xapi.Verb;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.RestyDateTimeFormat;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Failure;

/**
 *
 * @author plas0006
 */
public class SelectedResultsPresenter implements ResultEventHandler {

    private static final long PREPARE_TIMEOUT = 1000L;

    private static final Logger LOG = Logger.getLogger(SelectedResultsPresenter.class.getName());

    private final LoggingFailure FAILURE;
    private final EventBus eventBus;
    private final DwoGlobalVars dwoGlobalVars;

    private Display view;
    @Inject
    ResultsService resultService;
    private JavaScriptObject resultState;
    //model
    private DomResultTree resultTree;
    
    /*@Inject*/ Lazy<SecuredTeacherClassCourseManager> manager = SecuredTeacherClassCourseManager::new;
    

    public interface Display extends BasicDisplay {

        void updateResultTree(DomResultTree data);

        void init(JavaScriptObject aResultState);

        void setEmptyTableMessage();

        void setLoadingTableMessage();

        public void showPages(DomResultTree resultTree);

    }

    @Inject
    SelectedResultsPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        FAILURE = new LoggingFailure(LOG, eventBus);
        eventBus.addHandler(ResultEvent.TYPE, this);
    }

    public void init(DomResultTree aResultTree, JavaScriptObject aResultState) {
        view.clear();
        aResultTree.insertStudentCourses();
        resultTree = aResultTree;
        resultState = aResultState;
        view.init(aResultState);
    }

//    public void updateTree() {
//        //view.clear();
//        LOG.log(Level.INFO, "DwoGlobalVarsState = " + dwoGlobalVars.getState().name());
//        Promise<DomResultsPerTeacher> promResults;
//        promResults = resultService.getResultsPerTeacher();
//        // onSuccess calculate results and show.
//        promResults.then(new Success<DomResultsPerTeacher, Void>() {
//            @Override
//            public Promise<Void> call(Promise<DomResultsPerTeacher> resolved) throws Exception {
//                //calculate tree and call plotting
//                LOG.log(Level.INFO, "DomResults returned.");
//                resultTree = new DomResultTree(resolved.getValue());
//                resultTree.insertStudentCourses();
//                LOG.log(Level.INFO, "ResultTree obtained.");// plots the result tree.
//                view.updateResultTree(resultTree);
//                LOG.log(Level.INFO, "plotted ResultMatrix.");
//                return null;
//            }
//        },
//                FAILURE);
//    }

    public void setView(Display aView) {
        view = aView;
        view.setHelp(dwoGlobalVars.buildHelpUrl("#selectedResults"));
   }

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
        for (PersistenceId scoid : scos) {
            DomScoContext sco = new DomScoContext();
            sco.setId(scoid);
            promises.add(resultService.createStudentResults(sco, domschoolclass.getSchoolClass(), students)
                    .map(dom -> dom.getStudentScoContexts())
                    .flatMap(resultService::sealList)
                    .then(this::updateResultTree)
                    );
        }
        Promises.all(promises).then(null, FAILURE);
    }

    private static final String WAIT = DwoLocalesForGWT.instance.NUM_DLG_SELECTEDRESULTS_PAGES();
    
    class SendCompleted implements Success<List<DomStudentScoContext>,List<DomStudentScoContext>> {
    	final Verb COMPLETED = StudentScoResultPresenter.COMPLETED;
    	Group team;
    	Activity activity;
    	Promise<Context> context;
    	XAPIService service;
    	Map<String,String> students;
    	
		SendCompleted(DomSchoolClass schoolClass, List<DomStudent> students, DomScoContext sco) {
			team = new Group();
			team.name = schoolClass.getSchoolClassName();
			team.account = new Account();
			team.account.name = "pid:" + schoolClass.getId().getIdString();
			activity = new Activity();
			activity.id = "pid:" + sco.getId();
			activity.definition = new ActivityDefinition();
			activity.definition.type = "http://www.dwo.nl/type/" +sco.getId().getType();
			service = xapiService.get();
			context = service.getAgent().map(instructor -> { Context c = new Context();
					c.instructor = instructor;
					team.account.homePage = instructor.account.homePage;
					c.team = team;
					return c; });
			this.students = students.stream().collect(Collectors.toMap(s -> s.getId().getIdString(), s -> s.getUserName()));
			
		}

		public Promise<List<DomStudentScoContext>> call(Promise<Context> context, Promise<List<DomStudentScoContext>> resolved) throws Exception {
			List<DomStudentScoContext> list = resolved.getValue();
			List<Statement> statements = new ArrayList<>();
			for(DomStudentScoContext item: list) {
				Agent actor = new Agent();
				String uid = item.getUserID().getIdString();
				String name = students.get(uid);
				actor.account = new Account();
				actor.name = name;
				actor.account.name = "pid:"+ uid;
				actor.account.homePage = context.getValue().instructor.account.homePage;
				Statement s = new Statement();
				s.actor = actor;
				s.object = activity;
				s.verb = COMPLETED;
				s.context = context.getValue();
				statements.add(s);
			}
			return service.saveStatements(statements).then(x -> resolved);
		}
		@Override 
		public Promise<List<DomStudentScoContext>> call(Promise<List<DomStudentScoContext>> resolved) throws Exception {
			return context.then(p -> call(p, resolved));
		}
    }
    
    
    @JsMethod
    public void sealSingleActivity(String scoId, String classid) {
        PersistenceId schoolclass = new PersistenceId(classid);
        DomResultTeacher<DomResultStudent> studentTree = resultTree.getStudentTree();
        DomResultSchoolClass<DomResultStudent> domschoolclass = studentTree.getChildren().get(schoolclass);
        List<DomStudent> students = domschoolclass.getChildren().values().stream().map(DomResultStudent::getStudent).collect(Collectors.toList());

        PersistenceId scoid = new PersistenceId(scoId);
        DomScoContext sco = new DomScoContext();
        sco.setId(scoid);
        Promise<List<DomStudentScoContext>> seals = resultService.createStudentResults(sco, domschoolclass.getSchoolClass(), students)
                .map(dom -> dom.getStudentScoContexts())
                .flatMap(resultService::sealList);
 // optional if 't' + premium
        if (dwoGlobalVars.isTrace()) {
        	seals = seals.then(new SendCompleted(domschoolclass.getSchoolClass(), students, sco));
        }
        seals.then(p -> { 
                	resultTree.updateResultStudentSco(p.getValue());
                	progress = new Deferred<Boolean>().getPromise();
                	Promise<?> s = preparePages0(scoId, classid);
                	return s;
                })
                .then(p -> {view.updateResultTree(resultTree);return p;}, FAILURE);
    }

    Promise<Object> updateResultTree(Promise<List<DomStudentScoContext>> p) {
        if (!p.isDone() || p.getFailure() != null) {
            return null;
        }
        resultTree.updateResultStudentSco(p.getValue());
        view.updateResultTree(resultTree);
        return null;
    }

    long prepareStart = Long.MAX_VALUE;
    Promise<Boolean> progress;
    float step,count;

    private int stage;

    private boolean firestep() {
      count += step;
      eventBus.fireEvent(new ProgressDialogWithAbortEvent(EventType.Update, Math.round(count), WAIT, null));
      return progress.isDone();
    }
    
    
    
    @JsMethod
    public void abandonPages() {
    	prepareStart = Long.MAX_VALUE;
    }
    
    
    @JsMethod
    public void preparePages(String scoid, String classid) {
        view.setLoadingTableMessage();
        ProgressDialogWithAbortDeferred defer = new ProgressDialogWithAbortDeferred(WAIT);
        progress = defer.getPromise();
        eventBus.fireEvent(new ProgressDialogWithAbortEvent(EventType.Init, 0, WAIT, defer));
        Promise<?> result = preparePages0(scoid, classid);
        result
        .onResolve( () -> eventBus.fireEvent(new ProgressDialogWithAbortEvent(EventType.Complete, 100, WAIT, null)));
    }

	private Promise<?> preparePages0(String scoid, String classid) {
		prepareStart = System.currentTimeMillis();
        LOG.log(Level.FINE, "scoid = " + scoid + " classid = " + classid);
        PersistenceId sco = new PersistenceId(scoid);
        PersistenceId schoolclass = new PersistenceId(classid);
        final Promise <StatementsResult> aanHetEind = completedQuery(schoolclass, sco);
        final Promise<Void> result0 = preparePages(schoolclass, sco);
        final Promise<?> result = 
        Promises.all(aanHetEind, result0)
        .then(x -> {
        	List<Statement> statements = aanHetEind.getValue().statements;
        	LOG.severe("DEBUG HIER" + statements.size());
        	if (statements.isEmpty())       		
        		return result0;
        	
        	Map<String, String> map = new HashMap<>();
        	for (Statement s: statements) {
        		String time = s.timestamp;
        		DateTimeFormat utc = DateTimeFormat.getFormat(RestyDateTimeFormat.RESTY_DATETIME_FORMAT);
        	    Date date = utc.parse(time.substring(0,RestyDateTimeFormat.RESTY_DATETIME_FORMAT.length()-1 ) + "+00:00");
        		DateTimeFormat local = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM);
        		time = local.format(date);
        		
        		String key  = s.actor.account.name.substring(4); // starts with pid:
        		map.put(key, time);
        	}
            // loop over studensco van deze klas en sco
        	DomResultSchoolClass<DomResultCourseInClass> rsc = resultTree.getResultTree().getChildren().get(schoolclass);
        	Collection<DomResultCourseInClass> list = rsc.getChildren().values();
        	Optional<DomResultScoContext> opt = list.stream().flatMap(item -> item.getChildren().values().stream())
        			.filter(t -> t.getId().equals(scoid))
        			.findAny();
        	if (opt.isPresent()) {
        		Collection<DomResultStudentScoContext> sscs = opt.get().getChildren().values();
        		for( DomResultStudentScoContext ssc: sscs) {
        			String u = ssc.getStudentSco().getUserID().getIdString();
        			ssc.setCompletionTime(map.get(u));
        		}
        	}
        	return result0;
        })
        
        .then(p -> {
        	if (prepareStart < Long.MAX_VALUE)
        	{
        		resultTree.insertStudentCourses();
        		view.updateResultTree(resultTree);
        	}
            return null;
        }, p -> LOG.log(Level.SEVERE, "preparePages", p.getFailure()))
                .then(p -> {
                    if (prepareStart < Long.MAX_VALUE) {
                    	prepareStart = Long.MAX_VALUE;
                    	resultTree.insertStudentCourses();
                        view.showPages(resultTree);
                    }
                    return null;
                }, FAILURE);
		return result;
	}

	@Inject Lazy<XAPIService> xapiService;
	
	private final Promise<StatementsResult> EMPTY; 
	{ 
		StatementsResult empty = new StatementsResult();
		empty.more = "";
		empty.statements = Collections.emptyList();
		EMPTY = Promises.resolved(empty);
	}

    private Promise<StatementsResult> completedQuery(PersistenceId schoolclass, PersistenceId sid) {
		DomSchoolClass sc = new DomSchoolClass();
		sc.setId(schoolclass);
		sc.setSchoolClassName(resultTree.getResultTree().getChildren().get(schoolclass).getLabel());
		DomScoContextId sco = new DomScoContextId(); sco.setId(sid);
		
		Promise<StatementsResult> query = xapiService.get().query(sc, sco);
		query.onResolve(() -> { 
			LOG.severe("DEBUG HIER" + query);
			Throwable failure = query.getFailure();
			if (failure != null) {
				LOG.log(Level.SEVERE, "Error in query", failure);
			} else {
				StatementsResult value = query.getValue();
				LOG.info("result query " + value);
				LOG.info("statements " + value.statements);
			}			
		});
		return query.fallbackTo(EMPTY);
	}

	@JsMethod
    public void showLogResults(JavaScriptObject context, String scoid, String classid) {
      LOG.fine("entering showLogResults " + context + ", " + scoid);
      PersistenceId schoolclass = new PersistenceId(classid);
      PersistenceId sco = new PersistenceId(scoid);
      DomResultSchoolClass<DomResultStudent> domschoolclass = resultTree.getStudentTree().getChildren().get(schoolclass);
      DomResultScoContext domsco = null;
      DomResultSchoolClass<DomResultCourseInClass> coursetree = resultTree.getResultTree().getChildren().get(schoolclass);
      Collection<DomResultCourseInClass> courses = coursetree.getChildren().values();
      for( DomResultCourseInClass item: courses) {
        domsco = item.getChildren().get(sco);
        if(domsco != null) break;
      }
      SwitchViewEvent event = new SwitchViewEvent(SelectedView.LOGRESULTS, resultTree, context, domsco, domschoolclass.getSchoolClass());
      eventBus.fireEvent(event);
    }
    
    
    @JsMethod
    public void showStudentResults(JavaScriptObject context, String scoid, String studentid, String classid) {
        LOG.fine("entering showStudentResults " + context + "," + scoid);
        showStudentResults(context, scoid, studentid, classid, null);
    }

    public void showStudentResults(JavaScriptObject context, String scoid, String studentid, String classid, String location) {
        PersistenceId schoolclass = new PersistenceId(classid);
        DomResultTeacher<DomResultStudent> studentTree = resultTree.getStudentTree();
        DomResultSchoolClass<DomResultStudent> domschoolclass = studentTree.getChildren().get(schoolclass);
        PersistenceId key = new PersistenceId(studentid);

        DomStudent student = domschoolclass.getChildren().get(key).getStudent();
        DomScoContext sco = new DomScoContext();
        sco.setId(new PersistenceId(scoid));
        Promise<DomStudentScoContext> p1 = resultService.createStudentResults(sco, domschoolclass.getSchoolClass(), Collections.singletonList(student))
                .map(p -> p.getStudentScoContexts().get(0));
        Promise<JSONValue> p2 = resultService.getJSONLaunchDataBytes(sco, domschoolclass.getSchoolClass());
        Promise<Map<String, String>> p3 = p1.then(p -> resultService.getValues(p.getValue()));

        Promises.all(p2, p3).then(new Success<Object, Object>() {

            @Override
            public Promise<Object> call(Promise<Object> resolved) throws Exception {
                DomResultStudentScoContext ssc = new DomResultStudentScoContext(p1.getValue(), student);
                ssc.setParent(domschoolclass);
                String launch_data = p2.getValue().toString();
                Map<String, String> userState = p3.getValue();
                userState.put("cmi.launch_data", launch_data);
                userState.put(ResultsService.COMPLETION_STATUS, p1.getValue().getCompletionStatus());
                userState.put("cmi.score.raw", Double.toString(p1.getValue().getScore()));
                if (location != null) {
                    userState.put("cmi.location", location);
                }
                updateResultTree(Promises.resolved(Collections.singletonList(p1.getValue())));
                eventBus.fireEvent(new SwitchViewEvent(SelectedView.RESULTSSTUDENT, resultTree, ssc, context, userState));
                return null;
            }
        }, FAILURE)
                .onResolve(() -> {
                    preparePages(schoolclass, sco.getId()).then(
                            p -> {
                                LOG.log(Level.FINE, "prepare pages done");
                                view.updateResultTree(resultTree);
                                return null;
                            },
                            p -> LOG.log(Level.WARNING, "preparePages", p.getFailure()));

                });

    }

    @JsMethod
    public void showStudentResultsPage(JavaScriptObject context, String scoid, String studentid, String classid, int page) {
        LOG.fine("entering showStudentResultsPage " + page);
        showStudentResults(context, scoid, studentid, classid, String.valueOf(page));
    }

    Promise<Void> preparePages(PersistenceId schoolclass, PersistenceId scoid) {
    	if (prepareStart == Long.MAX_VALUE) return Promises.resolved(null);
        // find studentscocontexts:
        DomResultSchoolClass<DomResultCourseInClass> cc = resultTree.getResultTree().getChildren().get(schoolclass);
        Map<PersistenceId, DomResultCourseInClass> children = cc.getChildren();
        for (DomResultCourseInClass cic : children.values()) {
            Map<PersistenceId, DomResultScoContext> items = cic.getChildren();
            DomResultScoContext sco = items.get(scoid);
            if (sco != null) {
                Collection<DomResultStudentScoContext> values = sco.getChildren().values();
// collection aanvullen met ontbrekende studenten.
                DomResultTeacher<DomResultStudent> studentTree = resultTree.getStudentTree();
                DomResultSchoolClass<DomResultStudent> domschoolclass = studentTree.getChildren().get(schoolclass);
                List<DomStudent> students = domschoolclass.getChildren().values().stream().map(DomResultStudent::getStudent).collect(Collectors.toList());
                if (values.size() != students.size()) {
                	LOG.info("AANVULLEN HIER van " + values.size() + " tot " + students.size());
                	Function<DomResultStudentScoContext, PersistenceId> keyMapper = context -> context.getStudentSco().getUserID();
					Function<DomResultStudentScoContext, DomResultStudentScoContext> valueMapper = Function.identity();
					Collector<DomResultStudentScoContext, ?, Map<PersistenceId, DomResultStudentScoContext>> collector = Collectors.<DomResultStudentScoContext, PersistenceId, DomResultStudentScoContext>toMap(keyMapper , valueMapper);
					Map<PersistenceId, DomResultStudentScoContext> asMap = 
                			values.stream().collect(collector);
					for (DomStudent student : students) {
						if (! asMap.containsKey(student.getId())) {
							DomStudentScoContext studentscocontext = new DomStudentScoContext();
							studentscocontext.setUserID(student.getId());
							studentscocontext.setScoID(scoid);
							/// ....
							DomResultStudentScoContext value = new DomResultStudentScoContext(studentscocontext, student);
// als bij mappedResultsForTeacher					
							if (sco.getTemplate() != null) {
								DomResultTree.initResultScoPages(value, sco.getTemplate());
							}
							sco.getChildren().put(student.getId(), value);
						}
					}
					//values = asMap.values();
					// terugstoppen
                }
                
                
                
                
                int size = values.size(); if (size < 1) size = 1;
                count = 0;
                step = 100.0F/size;              
                Iterator<DomResultStudentScoContext> iterator = values.iterator();
                return preparePages(cc.getSchoolClass(), sco.getScoContext(), iterator);

            }
        }
        return Promises.failed(new RuntimeException("not found"));
    }

    private Promise<Void> preparePages(DomSchoolClass schoolclass,
            DomScoContext scocontext,
            Iterator<DomResultStudentScoContext> iterator) {
    	if (prepareStart == Long.MAX_VALUE || firestep()) return Promises.resolved(null);
    	if (iterator.hasNext()) {
            DomResultStudentScoContext ssc = iterator.next();

            if (!ssc.getChildren().isEmpty()) // skip if non-empty
            {
                return preparePages(schoolclass, scocontext, iterator);
            }

            Promise<JSONValue> p2 = resultService.getJSONLaunchDataBytes(scocontext, schoolclass);
            Promise<Map<String, String>> p3 = resultService.getValues(ssc.getStudentSco());

            return Promises.all(p2, p3).then(new Success<Object, Void>() {

                @Override
                public Promise<Void> call(Promise<Object> resolved) throws Exception {
                    JSONValue launchdata = p2.getValue();
// never null, see Finish (map.remove)
                    String review_data = p3.getValue().getOrDefault(ResultsService.REVIEW_DATA, "");
                    String suspend_data = p3.getValue().getOrDefault(ResultsService.SUSPEND_DATA, "");
                    String review_check = p3.getValue().getOrDefault(ResultsService.REVIEW_CHECK, "");
                    boolean premium = dwoGlobalVars.isPremium();
                    premium = premium && ResultsService.COMPLETED.equals(ssc.getStudentSco().getCompletionStatus());
					Map<PersistenceId, DomResultStudentScoPage> children = Util.getPages(launchdata, suspend_data, review_data, review_check, premium);
                    LOG.info("setChildren for " + ssc.getId() + " " + children);
                    ssc.setChildren(children);
                    if (System.currentTimeMillis() - PREPARE_TIMEOUT > prepareStart) {
                        LOG.info("timeout: showPages");
                        resultTree.insertStudentCourses();
                        view.showPages(resultTree);
                        prepareStart = System.currentTimeMillis();
                    }

                    return preparePages(schoolclass, scocontext, iterator);
                }
            }).recoverWith(p -> {
              LOG.log(Level.WARNING, "failure for " + ssc.getLabel() , p.getFailure());
              return preparePages(schoolclass, scocontext, iterator);
            });
        }
        return Promises.resolved(null);
    }

    public void reinit(DomResultTree aResultTree, JavaScriptObject aResultState) {
        resultTree = aResultTree;
        resultState = aResultState;
        view.updateResultTree(resultTree);
        JsSelectedResultsDisplay.backtoCurrentActivitiesStudents(); // even valsspelen....
    }

    @JsMethod
    public boolean hasCompareClasses() {
        return false;
    }

    @JsMethod
    public void compareSchoolClasses(JavaScriptObject resultState) {
        LOG.log(Level.SEVERE, "Select StudentResults");
        eventBus.fireEvent(
                new SwitchViewEvent(SwitchViewEvent.SelectedView.RESULTSSCHOOLCLASSES, resultTree, resultState)
        );
    }

    @JsMethod
    public void clearStudentScoResults(String courseID, String classId) {
        PersistenceId schoolclass = new PersistenceId(classId);
        DomResultTeacher<DomResultStudent> studentTree = resultTree.getStudentTree();
        DomResultSchoolClass<DomResultStudent> domschoolclass = studentTree.getChildren().get(schoolclass);
        PersistenceId course = new PersistenceId(courseID);
        DomResultSchoolClass<?> domclassresults = resultTree.getResultTree().getChildren().get(schoolclass);
        DomResultScore<?> courseResults = domclassresults.getChildren().get(course);

        //Verify purpose
        Promise<Boolean> p = Promises.resolved(true); //empty promise
        p = p.then(new Success<Boolean, Boolean>() {
            @Override
            //Are you sure?
            public Promise<Boolean> call(Promise<Boolean> resolved) throws Exception {//do dialog check
                String msg = StringFormatter.format(DwoLocalesForGWT.instance.NUM_DLG_Results_ConfirmClearingStudentResults(), domschoolclass.getLabel(), courseResults.getLabel());
                AlertDialogWithConfirmCancelDeferred dialogPromise = new AlertDialogWithConfirmCancelDeferred(msg);
                AlertDialogWithConfirmCancelEvent event = new AlertDialogWithConfirmCancelEvent(AlertDialogWithConfirmCancelEvent.EventType.ConfirmDialog, dialogPromise);
                eventBus.fireEvent(event);
                return dialogPromise.getPromise();
            }
        }, new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                view.setEmptyTableMessage();
                FAILURE.fail(resolved);
            }
        }).then(new Success<Boolean,Boolean >() {
            //sure so remove
            @Override
            public Promise<Boolean> call(Promise<Boolean> resolved) throws Exception {
                if (resolved.getValue()) {

                    List<DomStudent> students = domschoolclass.getChildren().values().stream().map(DomResultStudent::getStudent).collect(Collectors.toList());

                    Set<PersistenceId> scos = courseResults.getChildren().keySet();
                    Collection<Promise<Object>> promises = new ArrayList<>();
                    for (PersistenceId scoid : scos) {
                        DomScoContext sco = new DomScoContext();
                        sco.setId(scoid);
                        promises.add(resultService.clearStudentResults(sco, domschoolclass.getSchoolClass(), students)
                                .then(new Success<Boolean, Boolean>() {
                                    @Override
                                    public Promise<Boolean> call(Promise<Boolean> resolved) throws Exception {
                                        LOG.log(Level.INFO, "Results clear resolved.");
                                        Boolean result = resolved.getValue();
                                        LOG.log(Level.INFO, "Returned " + result + ".");
                                        return Promises.resolved(result);
                                    }
                                }));
                    };
                    return Promises.all(promises).then(null, FAILURE);
                } else {
                    LOG.log(Level.INFO, "update user cancelled.");
                    return Promises.failed(null);
                }
            }
        });
//
//        Promises.all(promises).then(new Success<Object, Void>() {
//                        @Override
//                        public Promise<Void> call(Promise<Object> resolved) throws Exception {
//                            //calculate tree and call plotting
//                            new SwitchViewEvent(SwitchViewEvent.SelectedView.RESULTS);
//                            return null;
//                        }
//                    }, FAILURE);
    }

    @JsMethod
    public void back(JavaScriptObject context) {
        LOG.log(Level.FINE, "Select Back from SelectedResults to Results");
        eventBus.fireEvent(
                new SwitchViewEvent(SwitchViewEvent.SelectedView.BACKTORESULTS, resultTree, resultState)
        );
    }
    
    @JsMethod
    public boolean hasLogResults() {
      return stage >= 2;
    }
    
    public void setStage(int stage) {
      this.stage = stage;
    }

	@Override
	public void onResult(ResultEvent event) {
		DomResultStudentScoContext rssc = event.getRSsc();
		DomStudentScoContext ssc = rssc.getStudentSco();
		PersistenceId student = ssc.getUserID();
		PersistenceId sco = ssc.getScoID();
		PersistenceId sc = rssc.getAncestralSchoolClass().getSchoolClass().getId();
		double score = ssc.getScore();
        LOG.warning("update score of " + student + " to " + score);
        // Clear children of original ResultStudentScoContext
        
        
        
        view.updateResultTree(resultTree);
		
	}
	
	@JsMethod
	public void openDashboard(String module, String schoolClass) {		
		DomContext context = new DomContext();
		context.setDomHasRole(dwoGlobalVars.getActiveSchoolRoleAndClass().getHasRole());
		DomClassCourse classCourse = new DomClassCourse();
		classCourse.setClassId(new PersistenceId(schoolClass));
		classCourse.setCourseId(new PersistenceId(module));
		Promise<String> url = manager.get().getDashboardUI(context, classCourse, null);
		url.then( s -> {
			Window.open(s.getValue(), "dashboardUI", "");
			return s;
		}, FAILURE);
		
	}
	
	static private NumberFormat f = NumberFormat.getFormat("0.#");

	
	
	
	@JsMethod
	public JavaScriptObject getCijfer(double score, double frac, int parts, int totalParts, double cesuur) {
		JSONObject result = new JSONObject();
		double cijfer;
		double off = cesuur / 100.0 * frac; // cesuur in points.
		if (score <= off) // lineair tot 1 .. 5.5
		{ 
		   cijfer = 1.0 + score / off * 4.5;
		} else { // lineair 5.5 .. 10
		   cijfer = (score - off) / (frac - off) * 4.5 + 5.5;
		}
				
		String format = f.format(cijfer);
		result.put("longlabel", new JSONString(f.format(score) + "/" + f.format(frac) + " " + format));
		result.put("label", new JSONString(format));
		result.put("fraction", new JSONNumber(parts/(double)totalParts));
		result.put("score", new JSONNumber(cijfer * 10.0)); // 0..100
		result.put("value", new JSONString(format));
		return result.getJavaScriptObject();

	}
}
