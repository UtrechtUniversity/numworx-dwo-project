package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.web.bindery.event.shared.EventBus;

import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.DomResultTree;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;

public class LogResultsPresenter {

  private static final Logger LOG = Logger.getLogger(LogResultsPresenter.class.getName());

  public interface Display extends BasicDisplay {

    void hide();

    void init(JavaScriptObject context);
  }


  @Inject ResultsService resultService;
  @Inject EventBus eventBus;

  private DomResultTree resultTree;
  private Display view;


  @Inject
  LogResultsPresenter() {
    LOG.log(Level.INFO, "LogResultsPresenter constructed");
  }

  public void init(DomResultTree resultTree, DomResultScoContext sco,
      DomSchoolClass schoolClass, JavaScriptObject context) {
    this.resultTree = resultTree;
    LOG.log(Level.INFO, "show logs for " + sco.getLabel() + " in class " + schoolClass.getSchoolClassName());
    view.init(context);
  }

  @Inject
  void setView(Display aView) {
    view = aView;
  }

  @JsMethod
  public void close(JavaScriptObject resultState) {
    view.clear();
    view.hide();
    SwitchViewEvent event = new SwitchViewEvent(SwitchViewEvent.SelectedView.SELECTEDRESULTSRETURN,
        resultTree, resultState);
    eventBus.fireEvent(event);
  }


}
