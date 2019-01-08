package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import javax.inject.Inject;

import nl.uu.fi.dwo.lms.gwtclient.gwt.results.StudentScoResultPresenter;
import nl.uu.fi.dwo.rest.dom.DomResultTree;

/**
 * Mapper to allow java interface implementation.
 *
 * @author G.A.J. van der Plas
 */
public class JsStudentScoResultView implements StudentScoResultPresenter.Display {

    private static final Logger LOG = Logger.getLogger(JsStudentScoResultView.class.getName());

    @Override
    public void clear() {
        JsStudentScoResultDisplay.clear();
    }

    @Override
    public void setHelp(String url) {
        JsStudentScoResultDisplay.setHelp(url);
    }    

    @Override
    public void setResultTree(DomResultTree data) {
      LOG.log(Level.INFO, "tree data has " + data.getStudentTree().getChildren().values().size() + " student classes.");
      LOG.log(Level.INFO, "tree data has " + data.getResultTree().getChildren().values().size() + "  result classes.");
      LOG.log(Level.INFO, "Building result tree in json.");
      JSONObject results = Util.buildSubResultTree(data.getResultTree());
      LOG.log(Level.INFO, "resultTree json string is:\n " + results.toString());
      LOG.log(Level.INFO, "Building student tree in json.");
      JSONObject students = Util.buildSubStudentTree(data.getStudentTree());
      LOG.log(Level.INFO, "studentTree json string is:\n " + students.toString());
      JsStudentScoResultDisplay.updateResultTree(results.getJavaScriptObject(),students.getJavaScriptObject());
    }

    @Override
    public void setEmptyTableMessage() {
      JsStudentScoResultDisplay.setEmptyTableMessage();
    }

    @Override
    public void setLoadingTableMessage() {
      JsStudentScoResultDisplay.setLoadingTableMessage();
    }

    @Override
    public void openUrl(String url) {
      JsStudentScoResultDisplay.openUrl(url);
    }

    @Override
    public void init() {
      JsStudentScoResultDisplay.init0();     
    }
    
    @Override
    public void init(JavaScriptObject context) {
      JsStudentScoResultDisplay.init(context);     
    }

     @Inject JsStudentScoResultView() {}

    @Override
    public void hide() {
      JsStudentScoResultDisplay.hide();
    }

    @Override
    public void resetSeal(boolean bool) {
      JsStudentScoResultDisplay.resetSeal(bool);
    }
}
