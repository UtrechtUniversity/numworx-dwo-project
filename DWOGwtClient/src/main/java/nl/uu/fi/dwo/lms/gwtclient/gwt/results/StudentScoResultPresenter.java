package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.web.bindery.event.shared.EventBus;

import dagger.Lazy;
import fi.dwo.gwt.lib.rest.util.DomMethodCodec;
import fi.dwo.gwt.lib.rest.util.DomStudentModelStructureCodec;
import fi.dwo.gwt.lib.rest.util.RestAuthenticator;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;

import org.fusesource.restygwt.client.JsonEncoderDecoder;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.BootPanelController;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent.SelectedView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.locale.GwtClientMessages;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.StudentModelService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelDeferred;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelEvent.EventType;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.DomResultTree;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacherv2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoPage;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;
import nl.uu.fi.dwo.rest.dom.xapi.Account;
import nl.uu.fi.dwo.rest.dom.xapi.Activity;
import nl.uu.fi.dwo.rest.dom.xapi.ActivityDefinition;
import nl.uu.fi.dwo.rest.dom.xapi.Agent;
import nl.uu.fi.dwo.rest.dom.xapi.Context;
import nl.uu.fi.dwo.rest.dom.xapi.Group;
import nl.uu.fi.dwo.rest.dom.xapi.Statement;
import nl.uu.fi.dwo.rest.dom.xapi.Verb;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Presents studentsco data.
 *
 * @author Gert van der Plas
 */
public class StudentScoResultPresenter {

  private static final Logger LOG = Logger.getLogger(StudentScoResultPresenter.class.getName());
  private final Failure FAILURE;
  private final EventBus eventBus;
  private final DwoGlobalVars dwoGlobalVars;

  private Display view;
  @Inject ResultsService resultService;
  @Inject Lazy<StudentModelService> studentModelService;
  @Inject Lazy<XAPIService> xapiService;
  @Inject @Named("responsive") boolean responsive;
  private DomResultTree resultTree;
  private DomResultStudentScoContext ssc;
  private Map<String,String> userState;
  @SuppressWarnings("rawtypes")
  private DomResultSchoolClass parent;
  @Inject GwtClientMessages rb;
  private JavaScriptObject resultState;
  private DomStudent student;
  private int profile;

  public interface Display  extends BasicDisplay{

    void openUrl(String url);
    void init(JavaScriptObject aResultState);

    
    void setResultTree(DomResultTree data);

    void setEmptyTableMessage();

    void setLoadingTableMessage();
    void hide();
    void resetSeal(boolean bool);

  }

  @Inject StudentScoResultPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars, BootPanelController boot) {
    eventBus = anEventBus;
    dwoGlobalVars = aDwoGlobalVars;
    FAILURE = new LoggingFailure(LOG, anEventBus); 
    this.profile = boot.getProfile();
  }

  private String mapRealm(DomUser uu) {
	String realm = Objects.toString(dwoGlobalVars.getRealm(),"");
	String username = uu.getUserName();
	int at = username.indexOf('@');
	if (at == -1) {
		username += realm;
	} else if (at == username.length()-1) {
		username = username.substring(0, at);
	}
	return username;
  }
  
  public void init(DomResultTree aResultTree, DomResultStudentScoContext ssc, JavaScriptObject context, Map<String,String> userState) {
    LOG.fine("entering init");
    closed = finished = false;
    resultTree = aResultTree;
    this.userState = userState;
    this.ssc = ssc;
 
    parent = ssc.getAncestralSchoolClass();
    PersistenceId studentid = ssc.getStudentSco().getUserID();
    DomResultSchoolClass<DomResultStudent> domschoolclass = parent;
    student = domschoolclass.getChildren().get(studentid).getStudent();
   
    
    userState.put("cmi.mode", "review");
    userState.put("dme.abo_type", dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool().getAboType().name());
    userState.put("dme.authorization", RestAuthenticator.instance.getAuthorization());
    String learnerId = getLearnerId(studentid.toString(), domschoolclass.getId());
    userState.put("cmi.learner_id", learnerId);
// uitzoeken waarom er voor het ene of andere format wordt gekozen.
// afspraken met mc2?
    
    String learnerName; //  = mapRealm(student); // FULL NAME!!!!
    learnerName = student.getUniqueDisplayName(); // voor printheader
    userState.put("cmi.learner_name", learnerName);
// find the name of the sco
    LOG.severe("HIER DEBUG");
    String sco_name = findScoName(context);
    userState.put("dme.sco_name", sco_name);

    userState.put("dme.team", domschoolclass.getLabel());
    userState.put("cmi.total_time", ssc.getStudentSco().getTotalTime()); // FIXME in scorm 1.2 format!!!!!
// if premium && completed
// find out if we have studentmodel in launchdata.
    if ( AboType.premium == dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool().getAboType() && ResultsService.COMPLETED.equals(userState.get(ResultsService.COMPLETION_STATUS))) {
    	LOG.fine("KIJK VOOR STUDENTMODEL"); // XXX
    	String launchdata = userState.get("cmi.launch_data");
    	// search usermodel....
    	LOG.fine(launchdata);
    	String MAGIC = "\"studentModelId\":\"MYSQL;PersistentStudentModelContext";
    	int start = launchdata.indexOf(MAGIC);
    	if (start > 0) {
    		start += 18;
    		int end = launchdata.indexOf('"', start);
    		String id = launchdata.substring(start, end);
    		PersistenceId pid = new PersistenceId(id);
    		resultService.getStudentModel(pid).then(p -> {
    			userState.put("dme.studentmodelstructure", DomStudentModelStructureCodec.toString(p.getValue().getModelStructure()));
    			return studentModelService.get().getActiveMethod(p.getValue().getModelStructure().getActiveMethod());
    		})
    		.then(p -> {
    			userState.put("dme.studentmodelmethod", DomMethodCodec.toString(p.getValue()));
    			return p;
    		})
    		.onResolve(() -> initTail(ssc, context));
    		return;
    	}
    }
    initTail(ssc, context);
  }

/*
 * stolen from StudentScoResultDisplay.js
 */
private static native String findScoName(JavaScriptObject state)/*-{
	var activeActivity;
	activeActivity = state.resultsTree.children[state.activeSchoolClass].children[state.activeModule].children[state.activeActivity];
	return activeActivity.label;
}-*/;

protected void initTail(DomResultStudentScoContext ssc, JavaScriptObject context) {
	setAPI(this);
    LOG.info("view.init " + context + "  " + view);
    resultState = context;
    view.init(context);   
    LOG.info("update Frame for " + ssc.getStudentSco().getScoID());
    updateFrame(ssc.getStudentSco());
}

  /* learnerid proxy, geen hasrole voor deze student, maar wel z'n schoolklas waar die in zit */
  
  private String getLearnerId(String studentid, String schoolclassid) {
	int index;
	index = studentid.lastIndexOf(";");
	studentid = studentid.substring(index+1);
	index = schoolclassid.lastIndexOf(";");
	schoolclassid = schoolclassid.substring(index+1);
	return "2-" + studentid + "-" + schoolclassid;
  }

  public void setView(Display aView) {
    view = aView;
  }

  DomStudentScoContext updateResultTree(DomStudentScoContext ssc) {    
    resultTree.updateResultStudentSco(Collections.singleton(ssc));
    view.setResultTree(resultTree);
    return ssc;
  }
  
  DomStudentScoContext updateResultTree(DomResultStudentScoContext rssc) {
	DomStudentScoContext ssc = rssc.getStudentSco();
	resultTree.updateResultStudentSco(Collections.singleton(ssc));
	if (!rssc.getChildren().isEmpty())
		resultTree.updateResultStudentScoPages(ssc.getId(), rssc.getChildren());
	view.setResultTree(resultTree);
	return ssc;
  }
  
  boolean closed, finished;
  
  @JsMethod 
  public void close(JavaScriptObject resultState) {
    view.clear();
    view.hide();
    this.resultState = resultState;
    closed = true;
    fireSelectedResultReturn();
// expect Finish
//    SwitchViewEvent event = new SwitchViewEvent(SwitchViewEvent.SelectedView.SELECTEDRESULTSRETURN, resultTree, resultState);   
//    eventBus.fireEvent(event);
  }
  
  
  @JsMethod 
  public void showStudentResults (JavaScriptObject context, String studentid) {
      view.clear();
      
      @SuppressWarnings("unchecked")
      DomResultSchoolClass<DomResultStudent> domschoolclass = parent;
      PersistenceId key = new PersistenceId(studentid);
      student = domschoolclass.getChildren().get(key).getStudent();
      PersistenceId scoid = ssc.getStudentSco().getScoID();
      DomScoContext sco = new DomScoContext(); sco.setId(scoid);
      Promise<DomResultStudentScoContext> p1 = resultService.createStudentResults(sco, domschoolclass.getSchoolClass(), Collections.singletonList(student))
      .map(this::mapToResultStudentScoContext);     
      Promise<JSONValue> p2 = resultService.getJSONLaunchDataBytes(sco, domschoolclass.getSchoolClass());     
      Promise<Map<String,String>> p3 = p1.then(  p-> resultService.getValues(p.getValue().getStudentSco()));
      String location = userState.get("cmi.location");
      Promises.all(p1,p2,p3).then(new Success<Object, Object>() {

          @Override
          public Promise<Object> call(Promise<Object> resolved) throws Exception {
              DomResultStudentScoContext ssc = p1.getValue();
              ssc.setParent(parent);
              String launch_data = p2.getValue().toString();
              Map<String, String> userState = p3.getValue();
              if(location != null) userState.put("cmi.location", location);
              userState.put("cmi.launch_data", launch_data);
              userState.put(ResultsService.COMPLETION_STATUS, ssc.getStudentSco().getCompletionStatus());
              userState.put("cmi.score.raw", Double.toString(ssc.getStudentSco().getScore()));
              updateResultTree(ssc);
              eventBus.fireEvent(new SwitchViewEvent(SelectedView.RESULTSSTUDENT, resultTree, ssc, context, userState));
              return null;
          }
      }, FAILURE);
  }

  
  

  private native static void setAPI(StudentScoResultPresenter view) /*-{
		var api = {
			"LMSGetValue" : function(key) {
				return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.results.StudentScoResultPresenter::getValue(Ljava/lang/String;)(key)
			},
			"LMSInitialize" : function(dummy) {
				return "true"
			},
			"LMSGetLastError" : function() {
				return "0"
			},
			"LMSGetDiagnostic" : function(dummy) {
				return ""
			},
			"LMSGetErrorString" : function(code) {
				return ""
			},
			"LMSCommit" : function(dummy) {
				return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.results.StudentScoResultPresenter::Commit(Ljava/lang/String;)(dummy)
			},
			"LMSFinish" : function(dummy) {
				return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.results.StudentScoResultPresenter::Finish(Ljava/lang/String;)(dummy)
			},
			"LMSSetValue" : function(key, value) {
				return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.results.StudentScoResultPresenter::setValue(Ljava/lang/String;Ljava/lang/String;)(key, value)
			},
            "Initialize" : function(dummy) {
                return "true"
            },
			"GetValue" : function(key) {
				return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.results.StudentScoResultPresenter::getValue(Ljava/lang/String;)(key)
			},
			"GetValueAsync" : function(key, callback) {
				view.@nl.uu.fi.dwo.lms.gwtclient.gwt.results.StudentScoResultPresenter::getValueAsync(Ljava/lang/String;Lnl/uu/fi/dwo/lms/gwtclient/gwt/results/StudentScoResultPresenter$Callback;)(key, callback)
			},
			
			"SetValue" : function(key, value) {
				return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.results.StudentScoResultPresenter::setValue(Ljava/lang/String;Ljava/lang/String;)(key, value)
			},
            "GetLastError" : function() {
                return "0"
            },
            "GetDiagnostic" : function(dummy) {
                return ""
            },
            "GetErrorString" : function(code) {
                return ""
            },
            "Commit" : function(dummy) {
                return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.results.StudentScoResultPresenter::Commit(Ljava/lang/String;)(dummy)
            },
            "Terminate" : function(dummy) {
                return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.results.StudentScoResultPresenter::Finish(Ljava/lang/String;)(dummy)
            },
		// TODO more to follow...			
		};
		$wnd.API = api;
		$wnd.API_1484_11 = api;
  }-*/;

  private String getValue(String key) {
    LOG.info("GetValue " + key);
    String value = userState.get(key);
    if(value == null) value = "";
    String shortValue = value.length() > 10 ? value.substring(0, 10) + "..." : value;
    LOG.info("result GetValue: " + shortValue);
    return value;
  }

  private String setValue(String key, String value) {
    String shortValue = value.length() > 10 ? value.substring(0, 10) + "..." : value;
    LOG.info("SetValue " + key + ", " + shortValue);

    if ( ResultsService.SUSPEND_DATA.equals(key)
    	|| ResultsService.COMPLETION_TIMESTAMP.equals(key)	
       ) return "false"; // never writable
    if ( "dme.statement".equals(key)) {
      createStatement(value);
      return "true";
    }
    
    userState.put(key,value);
    return "true";
  }

  public interface StatementCodec extends JsonEncoderDecoder<Statement> {
    StatementCodec CODEC = GWT.create(StatementCodec.class);
  }

  private void createStatement(String value) {
    final Statement s = StatementCodec.CODEC.decode(value);
    createStatement(s);
  }

    static final Verb COMPLETED = new Verb();
	static {
		COMPLETED.id = XAPIService.COMPLETED;
		COMPLETED.display = Collections.singletonMap("en-US", "completed");
	}

  private void createCompletedStatement() {
	Statement s = new Statement();
	s.verb = COMPLETED;
	Activity activity = new Activity();
	activity.id = "pid:" + ssc.getStudentSco().getScoID();
	activity.definition = new ActivityDefinition();
	activity.definition.type = "http://www.dwo.nl/type/" +ssc.getStudentSco().getScoID().getType();
	//activity.definition.name = Collections.singletonMap("unk", ?????);
	s.object = activity;
    createStatement(s);
  }
  
private void createStatement(final Statement s) {
	s.actor = new Agent();
    s.actor.account = new Account();
    s.actor.name = student.getUserName();
    s.actor.account.name =  "pid:"+student.getId();
    Group group = new Group();
    DomSchoolClass team = parent.getSchoolClass();
	group.account = new Account();
	group.account.name = "pid:" +team.getId().getIdString();
	group.name = team.getSchoolClassName();
    if (s.context == null) s.context = new Context();
    s.context.team = group;
    XAPIService x = xapiService.get();
	x.getAgent().then( a -> { 
      s.actor.account.homePage = a.getValue().account.homePage;
      s.context.team.account.homePage = a.getValue().account.homePage;
      s.context.instructor = a.getValue();
      return x.saveStatement(s);
    });
}

  private String Commit(String dummy) {
    LOG.info("Commit " + dummy);
    return "true";
  }

  private String Finish(String dummy) {
    LOG.info("Finish " + dummy);
    boolean sealed = ResultsService.COMPLETED.equals(ssc.getStudentSco().getCompletionStatus());
	if (!sealed) {
      this.userState.remove(ResultsService.REVIEW_DATA);
      this.userState.remove(ResultsService.REVIEW_CHECK);
      this.userState.remove(ResultsService.REVIEW_CORRECT);
    }
    Map<String,String> userState = new TreeMap<> (this.userState);
    userState.keySet().retainAll(Arrays.asList("cmi.score.raw",ResultsService.REVIEW_DATA, ResultsService.REVIEW_CHECK, ResultsService.REVIEW_CORRECT));
    //boolean empty = this.userState.getOrDefault(ResultsService.SUSPEND_DATA, "").isEmpty();
    LOG.info( "update Score/Review " + userState);
    
    if (dwoGlobalVars.isPremium() && sealed)
    {	String score = userState.get("cmi.score.raw");
    	if (score != null) ssc.getStudentSco().setScore(Double.parseDouble(score));
    	ResultEvent ev = new ResultEvent(ssc, userState);
    	eventBus.fireEvent(ev);
LOG.severe("log studentscopages : " + ssc.getChildren().size());
    	resultService.setValues(ssc.getStudentSco(), userState)
// haal beide op, alleen als ssc.getchildren 
    	.then( x -> {
    		Promise<DomResultsPerTeacherv2> p = resultService.selectedResultsPerStudentSco(parent.getSchoolClass(), student, ssc.getStudentSco());
 // eigenlijk return x + list of studentscopages.
    		return p.map(this::mapToResultStudentScoContext);
    	})
       	.map(this::updateResultTree).then(null,FAILURE).onResolve(
    			() -> {
    			    finished = true;
    			    fireSelectedResultReturn();
    			});
    } else {
        finished = true;
		fireSelectedResultReturn();
    	
    }
    return "true";
  }

  private DomResultStudentScoContext mapToResultStudentScoContext(DomResultsPerTeacherv2 q) {
	LOG.severe("log studenscopages2 : " + q.getStudentScoPages().size());
	DomResultStudentScoContext result = new DomResultStudentScoContext(q.getStudentScoContexts().get(0), student);
	List<DomStudentScoPage> list = q.getStudentScoPages(); // ordered list: templates first, userdata last.
	DomResultTree.initResultScoPages(result, list);
	return result;
  }

  Deferred<Boolean> startPrint;
  private void fireSelectedResultReturn() {
	if (closed && finished) {
		SwitchViewEvent event = new SwitchViewEvent(SwitchViewEvent.SelectedView.SELECTEDRESULTSRETURN, resultTree, resultState);   
		eventBus.fireEvent(event);
	}
	if (finished && startPrint != null) {
		startPrint.resolve(Boolean.TRUE);
		startPrint = null;
	}
  }
  
  private String scoId;
  public void updateFrame(DomStudentScoContext sco) {
    scoId = "96797";
    String pid = sco.getScoID().getIdString();
    int komma = pid.lastIndexOf(';');
    if(komma >=0) {
        scoId = pid.substring(komma+1);
    }
// remove leading 00000
    while(scoId.length() > 1 && scoId.startsWith("0")) scoId = scoId.substring(1);

    String random = String.valueOf(System.currentTimeMillis());
    LOG.info("Frame = "+random);
    String locale = LocaleInfo.getCurrentLocale().getLocaleName();
    if ("default".equals(locale) ) locale =  "nl";
    String profile = Integer.toString(this.profile);

    String url;
    UrlBuilder u = new UrlBuilder();
    u.setProtocol(Location.getProtocol());
    u.setHost(Location.getHost());
    u.setPath("dwo/apps/player.html");
    u.setParameter("locale", locale);
    u.setParameter("profile", profile);
    u.setParameter("env", (dwoGlobalVars.isTest()?"test":"app"));
    u.setParameter("t", random);
	if (responsive) u.setParameter("responsive", "true");
	u.setHash("cmi.launch_data:"+scoId);
	url = u.buildString();
    LOG.info("openUrl " + url);
    view.openUrl(url);
}

  @JsMethod
  public void sealSingleActivity(boolean value) {
    DomStudentScoContext dssc = ssc.getStudentSco();
    Promise<DomStudentScoContext> seal;
    if (value == false && dwoGlobalVars.isPremium()) {
      String aMsg = DwoLocalesForGWT.instance.NUM_LBL_UNSEAL();
      AlertDialogWithConfirmCancelDeferred aPromise = new AlertDialogWithConfirmCancelDeferred(aMsg);
      AlertDialogWithConfirmCancelEvent event = new AlertDialogWithConfirmCancelEvent(EventType.ConfirmDialog, aPromise);
      
      eventBus.fireEvent(event);
      seal = aPromise.getPromise().then(p -> p.getValue() ? seal(value, dssc) : resetSeal(dssc));
      
    } else
      seal = seal(value, dssc);
    if (value && dwoGlobalVars.isTrace()) {
    	createCompletedStatement();
    }
    seal
    .map(this::updateResultTree)
    .then( p-> {
      dssc.setCompletionStatus(p.getValue().getCompletionStatus());
      setValue(ResultsService.COMPLETION_STATUS, p.getValue().getCompletionStatus());
      updateFrame(p.getValue()); 
      return null;
      }, FAILURE);
  }

  private static final Map<String, String> CLEAR_REVIEW = new LinkedHashMap<>();
  static {
	  CLEAR_REVIEW.put(ResultsService.REVIEW_DATA, "");
	  CLEAR_REVIEW.put(ResultsService.REVIEW_CHECK, "");
  }
  
  private Promise<DomStudentScoContext> seal(boolean value, DomStudentScoContext dssc) {
    if (!value) { 
      setValue(ResultsService.REVIEW_DATA, "");
      setValue(ResultsService.REVIEW_CHECK, "");
      return 
        resultService.setValues(ssc.getStudentSco(), CLEAR_REVIEW)
        .then(p -> resultService.seal(dssc, false));
    }
    return resultService.seal(dssc, value);
  }

  private Promise<DomStudentScoContext> resetSeal(DomStudentScoContext dssc) {
    view.resetSeal(ResultsService.COMPLETED.equals(dssc.getCompletionStatus()));
    return Promises.resolved(dssc);
  }

  @JsMethod
  public void log(JavaScriptObject context) {
    LOG.info("calling log");
  }
  @JsMethod
  public void download(JavaScriptObject context) {
    LOG.info("calling download");
  }
  @JsMethod
  public String print(JavaScriptObject context) {
	if (startPrint == null) {
		startPrint = new Deferred<>();
		updateFrame(ssc.getStudentSco());
	}
	 
	startPrint.getPromise().onResolve( () -> {
	  
    LOG.info("calling print " + scoId);
//    String pid = scoId; // sco.getScoID().getIdString();
//    int komma = pid.lastIndexOf(';');
//    if(komma >=0) {
//        scoId = pid.substring(komma+1);
//    }
// remove leading 00000
    while(scoId.length() > 1 && scoId.startsWith("0")) scoId = scoId.substring(1);

    String random = String.valueOf(System.currentTimeMillis());
    LOG.info("Frame = "+random);
    String locale = LocaleInfo.getCurrentLocale().getLocaleName();
    if ("default".equals(locale) ) locale =  "nl";
    String profile = Integer.toString(this.profile);

    String url;
    UrlBuilder u = new UrlBuilder();
    u.setProtocol(Location.getProtocol());
    u.setHost(Location.getHost());
    u.setPath("dwo/apps/PrintPlayer.jsp");
    u.setParameter("locale", locale);
    u.setParameter("profile", profile);
    u.setParameter("env", (dwoGlobalVars.isTest()?"test":"app"));
    u.setParameter("t", random);
	if (responsive) u.setParameter("responsive", "true");
	u.setHash("cmi.launch_data:"+scoId);
	url = u.buildString();
    LOG.info("openUrl " + url);
    Window_open(url, "PrintPlayer"); // see StudentScoResultDisplay.js
    //return url;
	});
	return null; // nu even niet.
  }

  private static native void Window_open(String url, String string) /*-{
	var w = $wnd.open(url, string);
	if (w) w.focus();
	
}-*/;

final public static class Callback extends JavaScriptObject {
	  protected Callback() {
	}

	public native final void resolve(String value) /*-{ 
	  	this.resolve(value);
	 }-*/;
  }
  
  private void getValueAsync(String key, Callback callback) {
	  resultService.getValuesAsync(ssc.getStudentSco(), Collections.singleton(key))
	  	.map(v -> v.getOrDefault(key, ""))
	  	.recover(fail -> "")
	  	.then(r -> {callback.resolve(r.getValue()); return r;});
  }
  
  
}
