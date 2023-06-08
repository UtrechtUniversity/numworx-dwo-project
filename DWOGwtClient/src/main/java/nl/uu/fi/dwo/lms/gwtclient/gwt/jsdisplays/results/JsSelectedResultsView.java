package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import nl.uu.fi.dwo.lms.gwtclient.gwt.results.SelectedResultsPresenter;
import nl.uu.fi.dwo.rest.dom.DomResultTree;

/**
 * Mapper to allow java interface implementation.
 *
 * @author G.A.J. van der Plas
 */
public class JsSelectedResultsView implements SelectedResultsPresenter.Display {

    private static final Logger LOG = Logger.getLogger(JsSelectedResultsView.class.getName());

    @Override
    public void clear() {
      JsSelectedResultsDisplay.clear();
    }

    @Override
    public void setHelp(String url) {
      JsSelectedResultsDisplay.setHelp(url);
    }
    
    @Override
        public void setEmptyTableMessage() {
        JsSelectedResultsDisplay.setEmptyTableMessage();
    }

    @Override
        public void setLoadingTableMessage() {
        JsSelectedResultsDisplay.setLoadingTableMessage();
    }
        

    @Override
    public void updateResultTree(DomResultTree data) {
        LOG.log(Level.INFO, "tree data has " + data.getStudentTree().getChildren().values().size() + " student classes.");
        LOG.log(Level.INFO, "tree data has " + data.getResultTree().getChildren().values().size() + "  result classes.");
        LOG.log(Level.INFO, "Building result tree in json.");
        JSONObject results = Util.buildSubResultTree(data.getResultTree());
        LOG.log(Level.INFO, "resultTree json string is:\n " + results.toString());
        LOG.log(Level.INFO, "Building student tree in json.");
        JSONObject students = Util.buildSubStudentTree(data.getStudentTree());
        LOG.log(Level.INFO, "studentTree json string is:\n " + students.toString());
        JsSelectedResultsDisplay.updateResultTree(results.getJavaScriptObject(),students.getJavaScriptObject());
    }        

    @Override
    public void init(JavaScriptObject aResultState) {
        JsSelectedResultsDisplay.init(aResultState);
    }
    
  
    @Inject JsSelectedResultsView() {}

    @Override
    public void showPages(DomResultTree resultTree) {
//        LOG.log(Level.INFO, "tree data has " + data.getStudentTree().getChildren().values().size() + " student classes.");
//        LOG.log(Level.INFO, "tree data has " + data.getResultTree().getChildren().values().size() + "  result classes.");
        LOG.log(Level.INFO, "Building result tree in json.");
        JSONObject results = Util.buildSubResultTree(resultTree.getResultTree());
        LOG.log(Level.INFO, "resultTree json string is:\n " + results.toString());
//        LOG.log(Level.INFO, "Building student tree in json.");
//        JSONObject students = Util.buildSubStudentTree(data.getStudentTree());
//        LOG.log(Level.INFO, "studentTree json string is:\n " + students.toString());
        JsSelectedResultsDisplay.showPages(results.getJavaScriptObject());//,students.getJavaScriptObject());
    }        

    @Override
    public void init() {
        JsSelectedResultsDisplay.init0();
    }

}
