package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window.Location;
import com.google.web.bindery.event.shared.EventBus;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent.SelectedView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.locale.GwtClientMessages;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelDeferred;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelEvent.EventType;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.DomResultTree;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
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
  private DomResultTree resultTree;
  private DomResultStudentScoContext ssc;
  private Map<String,String> userState;
  @SuppressWarnings("rawtypes")
  private DomResultSchoolClass parent;
  @Inject GwtClientMessages rb;
private JavaScriptObject resultState;

  public interface Display  extends BasicDisplay{

    void openUrl(String url);
    void init(JavaScriptObject aResultState);

    
    void setResultTree(DomResultTree data);

    void setEmptyTableMessage();

    void setLoadingTableMessage();
    void hide();
    void resetSeal(boolean bool);

  }

  @Inject StudentScoResultPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
    eventBus = anEventBus;
    dwoGlobalVars = aDwoGlobalVars;
    FAILURE = new LoggingFailure(LOG, anEventBus);    
  }

  public void init(DomResultTree aResultTree, DomResultStudentScoContext ssc, JavaScriptObject context, Map<String,String> userState) {
    LOG.fine("entering init");
    closed = finished = false;
    resultTree = aResultTree;
    this.userState = userState;
    this.ssc = ssc;
    userState.put("cmi.mode", "review");
    userState.put("dme.abo_type", dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool().getAboType().name());
    setAPI(this);
    LOG.info("view.init " + context + "  " + view);
    resultState = context;
    view.init(context);   
    LOG.info("update Frame for " + ssc.getStudentSco().getScoID());
    parent = ssc.getAncestralSchoolClass();
    updateFrame(ssc.getStudentSco());
  }

  public void setView(Display aView) {
    view = aView;
  }

  DomStudentScoContext updateResultTree(DomStudentScoContext ssc) {    
    resultTree.updateResultStudentSco(Collections.singleton(ssc));
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
      DomStudent student = domschoolclass.getChildren().get(key).getStudent();
      PersistenceId scoid = ssc.getStudentSco().getScoID();
      DomScoContext sco = new DomScoContext(); sco.setId(scoid);
      Promise<DomStudentScoContext> p1 = resultService.createStudentResults(sco, domschoolclass.getSchoolClass(), Collections.singletonList(student))
      .map(p -> p.getStudentScoContexts().get(0).getValue());     
      Promise<JSONValue> p2 = resultService.getJSONLaunchDataBytes(sco, domschoolclass.getSchoolClass());     
      Promise<Map<String,String>> p3 = p1.then(  p-> resultService.getValues(p.getValue()));
      String location = userState.get("cmi.location");
      Promises.all(p1,p2,p3).then(new Success<Object, Object>() {

          @Override
          public Promise<Object> call(Promise<Object> resolved) throws Exception {
              DomResultStudentScoContext ssc = new DomResultStudentScoContext(p1.getValue(), student);
              ssc.setParent(parent);
              String launch_data = p2.getValue().toString();
              Map<String, String> userState = p3.getValue();
              if(location != null) userState.put("cmi.location", location);
              userState.put("cmi.launch_data", launch_data);
              userState.put(ResultsService.COMPLETION_STATUS, p1.getValue().getCompletionStatus());
              userState.put("cmi.score.raw", Double.toString(p1.getValue().getScore()));
              updateResultTree(p1.getValue());
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

    if ( ResultsService.SUSPEND_DATA.equals(key)) return "false"; // never writable
    
    userState.put(key,value);
    return "true";
  }

  private String Commit(String dummy) {
    LOG.info("Commit " + dummy);
    return "true";
  }

  private String Finish(String dummy) {
    LOG.info("Finish " + dummy);
    if (!ResultsService.COMPLETED.equals(ssc.getStudentSco().getCompletionStatus())) {
      this.userState.remove(ResultsService.REVIEW_DATA);
    }
    Map<String,String> userState = new HashMap<> (this.userState);
    userState.keySet().retainAll(Arrays.asList("cmi.score.raw",ResultsService.REVIEW_DATA));
    LOG.info( "update Score/Review " + userState);
    if (dwoGlobalVars.isPremium())
    {	String score = userState.get("cmi.score.raw");
    	if (score != null) ssc.getStudentSco().setScore(Double.parseDouble(score));
    	ResultEvent ev = new ResultEvent(ssc, userState);
    	eventBus.fireEvent(ev);
    	resultService.setValues(ssc.getStudentSco(), userState).map(this::updateResultTree).then(null,FAILURE).onResolve(
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

  private void fireSelectedResultReturn() {
	if (closed && finished) {
		SwitchViewEvent event = new SwitchViewEvent(SwitchViewEvent.SelectedView.SELECTEDRESULTSRETURN, resultTree, resultState);   
		eventBus.fireEvent(event);
	}
  }
  
  
  public void updateFrame(DomStudentScoContext sco) {
    String scoId = "96797";
    String pid = sco.getScoID().getIdString();
    int komma = pid.lastIndexOf(';');
    if(komma >=0) {
        scoId = pid.substring(komma+1);
    }
    String random = String.valueOf(System.currentTimeMillis());
    LOG.info("Frame = "+random);
    String locale = LocaleInfo.getCurrentLocale().getLocaleName();
    if ("default".equals(locale) ) locale =  "nl";
    String profile = Location.getParameter("profile");
    if(profile == null || profile.isEmpty()) profile = "77";

    String url = "/dwo/apps/player.html?locale="
        + locale
        + "&profile="
        + profile
        + "&t=" + random + "#cmi.launch_data:"+scoId;
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
    seal
    .map(this::updateResultTree)
    .then( p-> {
      dssc.setCompletionStatus(p.getValue().getCompletionStatus());
      setValue(ResultsService.COMPLETION_STATUS, p.getValue().getCompletionStatus());
      updateFrame(p.getValue()); 
      return null;
      }, FAILURE);
  }

  private Promise<DomStudentScoContext> seal(boolean value, DomStudentScoContext dssc) {
    if (!value) { 
      setValue(ResultsService.REVIEW_DATA, "");
      return 
        resultService.setValues(ssc.getStudentSco(), Collections.singletonMap(ResultsService.REVIEW_DATA, ""))
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
  public void print(JavaScriptObject context) {
    LOG.info("calling print");
  }

}
