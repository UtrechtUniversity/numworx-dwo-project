package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

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
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.old.ScoResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.rest.dom.DomResultTree;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultTeacher;
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

  public interface Display {

    void clear();
    void openUrl(String url);
    void init(JavaScriptObject aResultState);

    
    void setResultTree(DomResultTree data);

    void setEmptyTableMessage();

    void setLoadingTableMessage();

  }

  @Inject StudentScoResultPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
    eventBus = anEventBus;
    dwoGlobalVars = aDwoGlobalVars;
    FAILURE = new LoggingFailure(LOG, anEventBus);
  }

  public void init(DomResultTree aResultTree, DomResultStudentScoContext ssc, JavaScriptObject context, Map<String,String> userState) {
    resultTree = aResultTree;
    this.userState = userState;
    this.ssc = ssc;
    userState.put("cmi.mode", "review");
    setAPI(this);
    view.init(context);    
    updateFrame(ssc.getStudentSco());
  }

  public void setView(Display aView) {
    view = aView;
  }

  Void updateResultTree(DomStudentScoContext ssc) {    
    // resultTree.update(Collections.singleton(ssc));
    view.setResultTree(resultTree);
    return null;
  }
  
  @JsMethod 
  public void showStudentResults (JavaScriptObject context, String scoid, String studentid, String classid) {
      view.clear();
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
              String launch_data = p2.getValue().toString();
              Map<String, String> userState = p3.getValue();
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
				return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.results.StudentScoResultPresenter::Commit(Ljava/lang/String;)(key)
			},
			"LMSFinish" : function(dummy) {
				return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.results.StudentScoResultPresenter::Finish(Ljava/lang/String;)(key)
			},
			"LMSSetValue" : function(key, value) {
				return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.results.StudentScoResultPresenter::setValue(Ljava/lang/String;Ljava/lang/String;)(key, value)
			},
			"GetValue" : function(key) {
				return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.results.StudentScoResultPresenter::getValue(Ljava/lang/String;)(key)
			},
			"SetValue" : function(key, value) {
				return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.results.StudentScoResultPresenter::setValue(Ljava/lang/String;Ljava/lang/String;)(key, value)
			},
		// TODO more to follow...			
		};
		$wnd.API = api;
		$wnd.API_1484_11 = api;
  }-*/;

  private String getValue(String key) {
    LOG.info("GetValue " + key);
    String value = userState.getOrDefault(key, "");
    String shortValue = value.length() > 10 ? value.substring(0, 10) + "..." : value;
    LOG.info("result GetValue: " + shortValue);
    return value;
  }

  private String setValue(String key, String value) {
    userState.put(key,value);
    return "true";
  }

  private String Commit(String dummy) {
    return "true";
  }
  private String Finish(String dummy) {
    Map<String,String> userState = new HashMap<> (this.userState);
    userState.keySet().retainAll(Arrays.asList("cmi.score.raw","cmi.comments_from_lms.0.comment"));
    resultService.setValues(ssc.getStudentSco(), userState).map(this::updateResultTree).then(null,FAILURE);
    return "true";
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
    view.openUrl(url);
}

}
