package nl.uu.fi.dwo.mobile.client.sco;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.testing.StubScheduler;
import com.google.gwt.junit.client.GWTTestCase;

import nl.numworx.gwtpatch.client.GWTPatch;
import nl.numworx.gwtpatch.client.JSONBuilder;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;
import nl.uu.fi.dwo.mobile.promise.client.PromiseImpl;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;

public class GwtTestMemento extends GWTTestCase {
  @Override
  public String getModuleName()
  {
      return "nl.uu.fi.dwo.mobile.DWO2playerDebug";
  }

  Memento m;
  SCORM_guest api;
  ViewModuleView view;
  PromiseImpl<DomStudentModelContextId> defer;
  Map<String,String> map;
  GWTPatch patch;
  Scheduler scheduler;
  int getter, setter;
  
  private Logger LOG;
  public void gwtSetUp() throws Exception {
    LOG = Logger.getLogger("Memento");

    patch = new GWTPatch(new JSONBuilder());
    map  = new HashMap<>();
    api = new SCORM_guest() {

      @Override
      public String GetValue(String name) {
        LOG.fine("getting  " + name);
        getter++;
        return map.getOrDefault(name, "");
      }

      @Override
      public String SetValue(String name, String value) {
        LOG.fine("setting " + name + ", " + value);
        map.put(name, value);
        setter++;
        return super.SetValue(name, value);
      } };

      scheduler = new StubScheduler();
      defer = new PromiseImpl<>(scheduler);
  }

  public void gwtTearDown() throws Exception {}

  @SuppressWarnings("unchecked")
  @Test
  public void test() {
      String review = "{}";
      String review2 = "{\"opdrContStates\":[[null]]}";
      String suspend_data = "{}";
      api.SetValue(Memento.REVIEW_DATA, review);
      api.SetValue(Memento.SUSPEND_DATA, suspend_data);
      api.SetValue(Memento.COMPLETION_STATUS, Memento.COMPLETED);
      api.SetValue(Memento.LESSON_MODE, LessonMode.review.name());
      m = new Memento(null, api, view, defer) {

        @Override
        void register() { // whipeout registrations
          
        } } ;
     HashMap<String, Object>[][] state = new HashMap[1][1];
      m.getOpdrContStates(state);
      m.mergeIntoReview(0, 0, state[0][0]);
      String result = api.GetValue(Memento.REVIEW_DATA);
      String test = patch.createPatch(review2, result);
      assertEquals("patch equals", "[]", test);
      assertEquals("getter", 8, getter);
      assertEquals("setter", 5, setter);
  }

  @SuppressWarnings("unchecked")
  @Test 
  public void testRealdata() {
    String suspend_data = "{\"onsState\":{\"opdrContStates\":[[{\"interactiePanelStates\":[null,null,null,null,null,{\"hoogtes\":[27],\"interactiePanelStates\":[{\"formuleVakInhouden\":[\"$f40+2@\"],\"antwoordString\":\"$f40+2@\",\"ingevuld\":true,\"nagekeken\":false,\"editable\":true,\"isVeranderdNaNakijken\":false,\"errorCount\":0}],\"selected\":false,\"ingeklapt\":false,\"popupUsed\":false,\"nagekeken\":false,\"visible\":true,\"goedHalfFoutStatistiek\":4,\"feedbackStatistiek\":\"\"}],\"RandomVarNamen\":[],\"RandomVarWaarden\":{}},{\"interactiePanelStates\":[null,null,null,null,null,{\"hoogtes\":[27],\"interactiePanelStates\":[{\"formuleVakInhouden\":[\"$f7*6@\"],\"antwoordString\":\"$f7*6@\",\"ingevuld\":true,\"nagekeken\":false,\"editable\":true,\"isVeranderdNaNakijken\":false,\"errorCount\":0}],\"selected\":false,\"ingeklapt\":false,\"popupUsed\":false,\"nagekeken\":false,\"visible\":true,\"goedHalfFoutStatistiek\":4,\"feedbackStatistiek\":\"\"}],\"RandomVarNamen\":[],\"RandomVarWaarden\":{}}]],\"aantalSessies\":1,\"activiteitNr\":0,\"orScores\":[[5,10]],\"bezocht\":[[true,true]],\"zelftoetsNagekeken\":false,\"tempotoetsLocked\":false,\"tempotoetsSecondsLeft\":0,\"scoresZelftoets\":[[0,0]],\"isCorrectZelftoets\":[[false,false]],\"nakijkenZelftoetsPending\":[[false,false]]}}";
    String review = "{\"opdrContStates\":[[{\"interactiePanelStates\":[null,null,null,null,null,{\"reviewInteractieData\":{\"reviewScoreCorrectie\":15}}]},{\"interactiePanelStates\":[null,null,null,null,null,{\"reviewInteractieData\":{\"reviewScoreCorrectie\":10}}]}]]}";
    api.SetValue(Memento.REVIEW_DATA, review);
    api.SetValue(Memento.SUSPEND_DATA, suspend_data);
    api.SetValue(Memento.COMPLETION_STATUS, Memento.COMPLETED);
    api.SetValue(Memento.LESSON_MODE, LessonMode.review.name());
    m = new Memento(null, api, view, defer) {

      @Override
      void register() { // whipeout registrations
        
      } } ;
   HashMap<String, Object>[][] state = new HashMap[1][2];
    m.getOpdrContStates(state);
    m.mergeIntoReview(0, 0, state[0][0]);
    m.mergeIntoReview(0, 1, state[0][1]);

    String result = api.GetValue(Memento.REVIEW_DATA);
    String test = patch.createPatch(review, result);
    assertEquals("patch equals", "[]", test);
    assertEquals("review ", review, result);
    assertEquals("getter", 9, getter);
    assertEquals("setter", 6, setter);
    
  } 
  
}
