package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ResultsPresenter;
import nl.uu.fi.dwo.rest.dom.DomResultTree;

/**
 * Mapper to allow java interface implementation.
 *
 * @author G.A.J. van der Plas
 */
public class JsResultsView implements ResultsPresenter.Display {

    private static final Logger LOG = Logger.getLogger(JsResultsView.class.getName());

    @Inject JsResultsView() {}
    
    @Override
    public void clear() {
        JsResultsDisplay.clear();
    }

    @Override
    public void setHelp(String url) {
        JsResultsDisplay.setHelp(url);
    }
    
    @Override
    public void setEmptyTableMessage() {
        JsResultsDisplay.setEmptyTableMessage();
    }

    @Override
    public void setLoadingTableMessage() {
        JsResultsDisplay.setLoadingTableMessage();
    }

    @Override
    public void setResultTree(DomResultTree data) {
        try {
          LOG.log(Level.INFO, "tree data has " + data.getStudentTree().getChildren().values().size() + " student classes.");
          LOG.log(Level.INFO, "tree data has " + data.getResultTree().getChildren().values().size() + "  result classes.");
          LOG.log(Level.INFO, "Building result tree in json.");
          JSONObject results = Util.buildSubResultTree(data.getResultTree());
          LOG.log(Level.INFO, "resultTree json string is:\n " + results.toString());
          LOG.log(Level.INFO, "Building student tree in json.");
          JSONObject students = Util.buildSubStudentTree(data.getStudentTree());
          LOG.log(Level.INFO, "studentTree json string is:\n " + students.toString());
          JsResultsDisplay.setResultTree(results.getJavaScriptObject(), students.getJavaScriptObject());
        } catch (Exception e) {
          LOG.log(Level.SEVERE, "set result tree", e);
        }
    }
    
    
    @Override
    public void setResultTreeWithContext(DomResultTree data,  JavaScriptObject context) {
        try {
          LOG.log(Level.INFO, "tree data has " + data.getStudentTree().getChildren().values().size() + " student classes.");
          LOG.log(Level.INFO, "tree data has " + data.getResultTree().getChildren().values().size() + "  result classes.");
          LOG.log(Level.INFO, "Building result tree in json.");
          JSONObject results = Util.buildSubResultTree(data.getResultTree());
          LOG.log(Level.INFO, "resultTree json string is:\n " + results.toString());
          LOG.log(Level.INFO, "Building student tree in json.");
          JSONObject students = Util.buildSubStudentTree(data.getStudentTree());
          LOG.log(Level.INFO, "studentTree json string is:\n " + students.toString());
          JsResultsDisplay.setResultTreeWithContext(results.getJavaScriptObject(), students.getJavaScriptObject(), context);
        } catch (Exception e) {
          LOG.log(Level.SEVERE, "set result tree", e);
        }        
    }
    

    @Override
    public void init() {
        JsResultsDisplay.init();
    }

    @Override
    public void setChooseModulesTable() {
      JsResultsDisplay.setChooseModulesTable();
    }

	@Override
	public void setRemedialView(boolean set) {
		JsResultsDisplay.setRemedialView(set);
		
	}
}
