package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ResultsPresenter;
import nl.uu.fi.dwo.rest.dom.DomResultTree;
import nl.uu.fi.dwo.rest.dom.DomTree;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseOfClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;

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

    private JSONObject buildSubTree(DomResultScore node) {
        JSONObject json = new JSONObject();
        //set course data in node.
        String name = node.getClass().getSimpleName();        
        json.put(name, new JSONString(node.getLabel()));
        LOG.log(Level.INFO, "name: "+name+" label: "+node.getLabel());
        //Add children.
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            //there are children
            JSONObject children = new JSONObject();
            for (Object o : node.getChildren().values()) {
                //child node
                if(o instanceof DomResultScore){
                    DomResultScore childScore = (DomResultScore) o;
                    name = childScore.getClass().getSimpleName();   
                    children.put(childScore.getLabel(), buildSubTree(childScore));
                }
            }
            json.put("children", children);
        }
        return json;
    }
//
//    @Override
//    public void setTree(DomTree<DomCourseOfClass> tree) {
//        //this.DFSTreePrint(tree);
//
//        JSONObject object = buildSubTree(tree);
//        LOG.log(Level.INFO, "tree: " + object.toString());
//        JsModulesOfSchoolclassDisplay.setTree(object);
//    }
    
    @Override
    public void setResultTree(DomResultTree data) {
        LOG.log(Level.INFO,"tree data has "+data.getStudentTree().getChildren().values().size()+" student classes.");
        LOG.log(Level.INFO,"tree data has "+data.getResultTree().getChildren().values().size()+"  result classes.");
        LOG.log(Level.INFO,"Building tree in json.");
        JSONObject object = buildSubTree(data.getResultTree());
//        JSONValue json = DomResultTreeCodec.CODEC.encode(data);
        LOG.log(Level.INFO,"tree json string is:\n "+object.toString());
        JsResultsDisplay.setResultTree(object);
    }
}
