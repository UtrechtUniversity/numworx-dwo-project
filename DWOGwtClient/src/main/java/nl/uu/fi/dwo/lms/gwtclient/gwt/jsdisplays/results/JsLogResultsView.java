package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results;

import java.util.logging.Logger;

import javax.inject.Inject;

import com.google.gwt.core.client.JavaScriptObject;

import dagger.Reusable;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.LogResultsPresenter;

/**
 * Mapper to allow java interface implementation.
 *
 * @author G.A.J. van der Plas
 */
@Reusable
public class JsLogResultsView implements LogResultsPresenter.Display {

  private static final Logger LOG = Logger.getLogger(JsLogResultsView.class.getName());

  @Inject
  JsLogResultsView() {}

  @Override
  public void clear() {
    JsLogResultsDisplay.clear();
  }

  @Override
  public void setHelp(String url) {
    JsLogResultsDisplay.setHelp(url);
  }

  @Override
  public void init() {
    JsLogResultsDisplay.init0();
  }

  @Override
  public void hide() {
    JsLogResultsDisplay.hide();
  }

  @Override
  public void init(JavaScriptObject context) {
    JsLogResultsDisplay.init(context);
  }

}
