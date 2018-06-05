package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import fi.dwo.gwt.lib.rest.util.DomStudentCodec;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.SelectedResultsPresenter;
import nl.uu.fi.dwo.rest.dom.DomResultTree;
import nl.uu.fi.dwo.rest.dom.entities.DomResultCourseInClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;

/**
 * Mapper to allow java interface implementation.
 *
 * @author G.A.J. van der Plas
 */
public class JsSelectedResultsView implements SelectedResultsPresenter.Display {

    private static final Logger LOG = Logger.getLogger(JsSelectedResultsView.class.getName());

    @Override
    public void clear() {
        JsResultsDisplay.clear();
    }

    @Override
        public void setEmptyTableMessage() {
        JsSelectedResultsDisplay.setEmptyTableMessage();
    }

    @Override
        public void setLoadingTableMessage() {
        JsSelectedResultsDisplay.setLoadingTableMessage();
    }
        

    private JSONObject buildSubResultTree(DomResultScore node) {
        JSONObject json = new JSONObject();
        node.calculateSumOfSubtreeScore();
        //set course data in node.
        String classType = node.getClass().getSimpleName();
        json.put("classType", new JSONString(classType));
        json.put("label", new JSONString(node.getLabel()));
        json.put("sumScore", new JSONNumber(node.getScore()));
        json.put("scoCount", new JSONNumber(node.getScoCount()));
        json.put("studentScoCount", new JSONNumber(node.getStudentScoCount()));
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
                    children.put(id, buildSubResultTree(childScore));
                }
            }
            json.put("children", children);
        }
        return json;
    }

    /**
     * Assumes DomResultTeacher with DomResultSchoolClasses with DomStudents
     *
     * @param node
     * @return
     */
    private JSONObject buildSubStudentTree(DomResultScore node) {
        JSONObject json = new JSONObject();
        String classType = node.getClass().getSimpleName();
        json.put("classType", new JSONString(classType));
        json.put("label", new JSONString(node.getLabel()));
        json.put("sumScore", new JSONNumber(node.getScore()));
        json.put("scoCount", new JSONNumber(node.getScoCount()));
        json.put("studentScoCount", new JSONNumber(node.getStudentScoCount()));
        //Add children.
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            //there are schoolclasses
            JSONObject schoolClasses = new JSONObject();
            for (Object o : node.getChildren().values()) {
                //for each schoolclass
                //child node
                String id;
                if (o instanceof DomResultSchoolClass) {
                    //for each schoolclass
                    JSONObject schoolClass = new JSONObject();
                    schoolClass.put("classType", new JSONString(classType));
                    schoolClass.put("label", new JSONString(node.getLabel()));
                    schoolClass.put("sumScore", new JSONNumber(node.getScore()));
                    schoolClass.put("scoCount", new JSONNumber(node.getScoCount()));
                    schoolClass.put("studentScoCount", new JSONNumber(node.getStudentScoCount()));
                    JSONObject students = new JSONObject();
                    for (Object so : ((DomResultSchoolClass) o).getChildren().values()) {
                        //for each student
                        if (so instanceof DomStudent) {
                            DomStudent s = (DomStudent) so;
                            students.put(s.getId().getIdString(), DomStudentCodec.CODEC.encode(s));
                        }
                    }
                    //put students in schoolclass
                    id = ((DomResultSchoolClass) o).getSchoolClass().getId().getIdString();
                    schoolClass.put(id, students);
                    //put schoolclass in schoolclasses
                    id = ((DomResultSchoolClass) o).getSchoolClass().getId().getIdString();
                    schoolClasses.put(id, schoolClass);
                }
            }
            json.put("schoolclasses", schoolClasses);
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
    public void updateResultTree(DomResultTree data) {
        LOG.log(Level.INFO, "tree data has " + data.getStudentTree().getChildren().values().size() + " student classes.");
        LOG.log(Level.INFO, "tree data has " + data.getResultTree().getChildren().values().size() + "  result classes.");
        LOG.log(Level.INFO, "Building result tree in json.");
        JSONObject results = buildSubResultTree(data.getResultTree());
        LOG.log(Level.INFO, "resultTree json string is:\n " + results.toString());
        LOG.log(Level.INFO, "Building student tree in json.");
        JSONObject students = buildSubStudentTree(data.getStudentTree());
        LOG.log(Level.INFO, "studentTree json string is:\n " + students.toString());
        JsSelectedResultsDisplay.updateResultTree(results.getJavaScriptObject(),students.getJavaScriptObject());
    }        

    @Override
    public void init(JSONObject aResultState) {
        JsSelectedResultsDisplay.init(aResultState.getJavaScriptObject());
    }
}
