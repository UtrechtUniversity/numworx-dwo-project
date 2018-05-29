package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ResultsPresenter;
import nl.uu.fi.dwo.rest.dom.DomResultTree;
import nl.uu.fi.dwo.rest.dom.entities.DomResultCourseInClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultTeacher;

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
        String classType = node.getClass().getSimpleName();
        json.put("classType", new JSONString(classType));
        json.put("label", new JSONString(node.getLabel()));
        if (node instanceof DomResultStudentScoContext) {
            String userIdString = ((DomResultStudentScoContext) node).getStudentSco().getUserID().getIdString();
            json.put("user-id", new JSONString(userIdString));
        }
//        json.put("node-id", new JSONNumber(node.getNodeId()));
        //Add children.
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            //there are children
            JSONObject children = new JSONObject();
            for (Object o : node.getChildren().values()) {
                //child node
                String id;
                if (o instanceof DomResultSchoolClass) {
                    id = ((DomResultSchoolClass) o).getSchoolClass().getId().getIdString();
                } else if (o instanceof DomResultCourseInClass) {
                    id = ((DomResultCourseInClass) o).getCourse().getId().getIdString();
                } else if (o instanceof DomResultScoContext) {
                    id = ((DomResultScoContext) o).getScoContext().getId().getIdString();
                } else if (o instanceof DomResultStudentScoContext) {
                    id = ((DomResultStudentScoContext) o).getStudentSco().getId().getIdString();
                } else {
//                if(o instanceof DomResultStudent){
//                 id = ((DomResultStudent) o).getStudent().getId().getIdString();
//                }else{
                    id = "";
                }
                if (o instanceof DomResultScore) {
                    DomResultScore childScore = (DomResultScore) o;
                    children.put(id, buildSubTree(childScore));
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
        LOG.log(Level.INFO, "tree data has " + data.getStudentTree().getChildren().values().size() + " student classes.");
        LOG.log(Level.INFO, "tree data has " + data.getResultTree().getChildren().values().size() + "  result classes.");
        LOG.log(Level.INFO, "Building tree in json.");
        JSONObject object = buildSubTree(data.getResultTree());
//        JSONValue json = DomResultTreeCodec.CODEC.encode(data);
        LOG.log(Level.INFO, "tree json string is:\n " + object.toString());
        JsResultsDisplay.setResultTree(object);
    }
}
