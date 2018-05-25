package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results;

import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ResultsPresenter;
import nl.uu.fi.dwo.rest.dom.DomResultTree;

/**
 * Mapper to allow java interface implementation.
 *
 * @author G.A.J. van der Plas
 */
public class JsResultsView implements ResultsPresenter.Display {

    private static final Logger LOG = Logger.getLogger(JsResultsView.class.getName());

    @Override
    public void clear() {
        JsResultsDisplay.clear();
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
        LOG.log(Level.INFO,"tree data has "+data.getStudentTree().getChildren().values().size()+" student classes.");
        LOG.log(Level.INFO,"tree data has "+data.getResultTree().getChildren().values().size()+"  result classes.");
        String s = "tree : {id1 : c34534; name :rootnode}";
        JsResultsDisplay.setResultTree(s);
    }
}
