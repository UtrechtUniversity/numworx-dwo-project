package nl.uu.fi.dwo.mobile.client.sco;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.testing.StubScheduler;
import com.google.gwt.junit.client.GWTTestCase;

import nl.numworx.gwtpatch.client.GWTPatch;
import nl.numworx.gwtpatch.client.JSONBuilder;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;
import nl.uu.fi.dwo.mobile.promise.client.PromiseImpl;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;

public class GwtTestMemento extends GWTTestCase {
  @Override
  public String getModuleName()
  {
      return "nl.uu.fi.dwo.mobile.DWO2playerDebug";
  }

  Memento m;
  SCORM_guest api;
  ViewModuleView view;
  PromiseImpl<DomStudentModelContext> defer;
  Map<String,String> map;
  GWTPatch patch;
  Scheduler scheduler;
  
  public void gwtSetUp() throws Exception {
    patch = new GWTPatch(new JSONBuilder());
    map  = new HashMap<>();
    api = new SCORM_guest() {

      @Override
      public String GetValue(String name) {
        return map.getOrDefault(name, "");
      }

      @Override
      public String SetValue(String name, String value) {
        map.put(name, value);
        return super.SetValue(name, value);
      } };

      scheduler = new StubScheduler();
      defer = new PromiseImpl<>(scheduler);
      m = new Memento(api, view, defer) {

        @Override
        void register() { // whipeout registrations
          
        } } ;
  }

  public void gwtTearDown() throws Exception {}

  @SuppressWarnings("unchecked")
  @Test
  public void test() {
      String review = "{}";
      String suspend_data = "{}";
      api.SetValue(Memento.REVIEW_DATA, review);
      api.SetValue(Memento.SUSPEND_DATA, suspend_data);
      api.SetValue(Memento.COMPLETION_STATUS, Memento.COMPLETED);
      HashMap<String, Object>[][] state = new HashMap[1][1];
      m.getOpdrContStates(state);
      m.mergeIntoReview(0, 0, state[0][0]);
      String result = api.GetValue(Memento.REVIEW_DATA);
      String test = patch.createPatch(review, result);
      assertEquals("patch equals", "[]", test);
  }

  @SuppressWarnings("unchecked")
  @Test 
  public void testRealdata() {
    String suspend_data = "{\"onsState\":{\"opdrContStates\":[[{\"interactiePanelStates\":[null,null,null,null,null,{\"hoogtes\":[27],\"interactiePanelStates\":[{\"formuleVakInhouden\":[\"$f40+2@\"],\"antwoordString\":\"$f40+2@\",\"ingevuld\":true,\"nagekeken\":false,\"editable\":true,\"isVeranderdNaNakijken\":false,\"errorCount\":0}],\"selected\":false,\"ingeklapt\":false,\"popupUsed\":false,\"nagekeken\":false,\"visible\":true,\"goedHalfFoutStatistiek\":4,\"feedbackStatistiek\":\"\"}],\"RandomVarNamen\":[],\"RandomVarWaarden\":{}},{\"interactiePanelStates\":[null,null,null,null,null,{\"hoogtes\":[27],\"interactiePanelStates\":[{\"formuleVakInhouden\":[\"$f7*6@\"],\"antwoordString\":\"$f7*6@\",\"ingevuld\":true,\"nagekeken\":false,\"editable\":true,\"isVeranderdNaNakijken\":false,\"errorCount\":0}],\"selected\":false,\"ingeklapt\":false,\"popupUsed\":false,\"nagekeken\":false,\"visible\":true,\"goedHalfFoutStatistiek\":4,\"feedbackStatistiek\":\"\"}],\"RandomVarNamen\":[],\"RandomVarWaarden\":{}}]],\"aantalSessies\":1,\"activiteitNr\":0,\"orScores\":[[5,10]],\"bezocht\":[[true,true]],\"zelftoetsNagekeken\":false,\"tempotoetsLocked\":false,\"tempotoetsSecondsLeft\":0,\"scoresZelftoets\":[[0,0]],\"isCorrectZelftoets\":[[false,false]],\"nakijkenZelftoetsPending\":[[false,false]]}}";
    String review = "{\"opdrContStates\":[[{\"interactiePanelStates\":[null,null,null,null,null,{\"reviewInteractieData\":{\"reviewScoreCorrectie\":15}}]},{\"interactiePanelStates\":[null,null,null,null,null,{\"reviewInteractieData\":{\"reviewScoreCorrectie\":10}}]}]]}";
    api.SetValue(Memento.REVIEW_DATA, review);
    api.SetValue(Memento.SUSPEND_DATA, suspend_data);
    api.SetValue(Memento.COMPLETION_STATUS, Memento.COMPLETED);
    HashMap<String, Object>[][] state = new HashMap[1][1];
    m.getOpdrContStates(state);
    m.mergeIntoReview(0, 0, state[0][0]);
    String result = api.GetValue(Memento.REVIEW_DATA);
    String test = patch.createPatch(review, result);
    assertEquals("patch equals", "[]", test);
   
  } 
  
}
