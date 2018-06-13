package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import com.google.web.bindery.event.shared.EventBus;

import java.util.logging.Logger;
import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.old.ScoResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.rest.dom.DomResultTree;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

/**
 * Presents studentsco data.
 *
 * @author Gert van der Plas
 */
public class StudentScoResultPresenter {

  private static final Logger LOG = Logger.getLogger(StudentScoResultPresenter.class.getName());

  private final EventBus eventBus;
  private final DwoGlobalVars dwoGlobalVars;

  private Display view;
  private ResultsService resultService;
  private DomResultTree resultTree;

  public interface Display {

    void clear();

    void setResultTree(DomResultTree data);

    void setEmptyTableMessage();

    void setLoadingTableMessage();

  }

  public StudentScoResultPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
    eventBus = anEventBus;
    dwoGlobalVars = aDwoGlobalVars;
    resultService = new ResultsService(dwoGlobalVars);
  }

  public void init(DomResultTree aResultTree) {
    resultTree = aResultTree;
  }

  public void setView(Display aView) {
    view = aView;
  }

  @JsMethod
  public void showSelectedScoResults() {
    eventBus.fireEvent(
        new AlertDialogWithOKEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
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
				return "true"
			},
			"LMSFinish" : function(dummy) {
				return "true"
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
    String value = "";
    String shortValue = value.length() > 10 ? value.substring(0, 10) + "..." : value;
    LOG.info("result GetValue: " + shortValue);
    return value;
  }

  private String setValue(String key, String value) {
    return "true";
  }

}
