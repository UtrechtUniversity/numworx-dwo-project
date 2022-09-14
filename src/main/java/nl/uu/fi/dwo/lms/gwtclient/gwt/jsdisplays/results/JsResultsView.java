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

//    private JSONObject buildSubResultTree(DomResultScore node) {
//        JSONObject json = new JSONObject();
//        node.calculateSumOfSubtreeScore();
//        //set course data in node.
//        String classType = node.getClass().getSimpleName();
//        json.put("classType", new JSONString(classType));
//        json.put("label", new JSONString(node.getLabel()));
//        json.put("sumScore", new JSONNumber(node.getScore()));
//        json.put("scoCount", new JSONNumber(node.getScoCount()));
//        json.put("studentScoCount", new JSONNumber(node.getStudentScoCount()));
//        if (node instanceof DomResultStudentScoContext) {
//            DomStudentScoContext studentSco = ((DomResultStudentScoContext) node).getStudentSco();
//            String userIdString = studentSco.getUserID().getIdString();
//            json.put("user-id", new JSONString(userIdString));
//            String completionStatus = studentSco.getCompletionStatus(); // XXX What if not present? null of "" of unknown?
//            if(completionStatus == null) completionStatus = "unknown";
//			json.put("completionStatus", new JSONString(completionStatus));
//			String totalTime = studentSco.getTotalTime();
//			if (totalTime == null) totalTime = "00:00:00";
//			json.put("totalTime", new JSONString(totalTime));
//        }else if (node instanceof DomResultCourseInClass){
//            String viewState = ((DomResultCourseInClass) node).getViewState().name();
//            
//            Long sequence = ((DomResultCourseInClass) node).getCourse().getSequenceNr();
//            json.put("sequence", new JSONNumber(sequence == null ? Integer.MAX_VALUE : sequence.intValue()));
//            json.put("viewState", new JSONString(viewState));
//        }
////        json.put("node-id", new JSONNumber(node.getNodeId()));
//        //Add children.
//        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
//            //there are children
//            JSONObject children = new JSONObject();
//            for (Object o : node.getChildren().values()) {
//                //child node
//                String id;
//                if (o instanceof DomResultSchoolClass) {
//                    id = ((DomResultSchoolClass) o).getSchoolClass().getId().getIdString();
//                } else if (o instanceof DomResultCourseInClass) {
//                    id = ((DomResultCourseInClass) o).getCourse().getId().getIdString();
//
//                } else if (o instanceof DomResultScoContext) {
//                    id = ((DomResultScoContext) o).getScoContext().getId().getIdString();
//                } else if (o instanceof DomResultStudentScoContext) {
//                    id = ((DomResultStudentScoContext) o).getStudentSco().getId().getIdString();
//                } else {
////                if(o instanceof DomResultStudent){
////                 id = ((DomResultStudent) o).getStudent().getId().getIdString();
////                }else{
//                    id = "";
//                }
//                if (o instanceof DomResultScore) {
//                    DomResultScore childScore = (DomResultScore) o;
//                    children.put(id, buildSubResultTree(childScore));
//                }
//            }
//            json.put("children", children);
//        }
//        return json;
//    }
//
//    /**
//     * Assumes DomResultTeacher with DomResultSchoolClasses with DomStudents
//     *
//     * @param node
//     * @return
//     */
//    private JSONObject buildSubStudentTree(DomResultScore node) {
//        JSONObject json = new JSONObject();
//        String classType = node.getClass().getSimpleName();
//        json.put("classType", new JSONString(classType));
//        json.put("label", new JSONString(node.getLabel()));
//        json.put("sumScore", new JSONNumber(node.getScore()));
//        json.put("scoCount", new JSONNumber(node.getScoCount()));
//        json.put("studentScoCount", new JSONNumber(node.getStudentScoCount()));
//        //Add children.
//        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
//            //there are schoolclasses
//            JSONObject schoolClasses = new JSONObject();
//            for (Object o : node.getChildren().values()) {
//                //for each schoolclass
//                //child node
//                String id;
//                if (o instanceof DomResultSchoolClass) {
//                    //for each schoolclass
//                    JSONObject schoolClass = new JSONObject();
//                    classType = o.getClass().getSimpleName();
//                    schoolClass.put("classType", new JSONString(classType));
//                    schoolClass.put("label", new JSONString(((DomResultSchoolClass) o).getLabel()));
//                    schoolClass.put("sumScore", new JSONNumber(node.getScore()));
//                    schoolClass.put("scoCount", new JSONNumber(node.getScoCount()));
//                    schoolClass.put("studentScoCount", new JSONNumber(node.getStudentScoCount()));
//                    JSONObject students = new JSONObject();
//                    for (Object so : ((DomResultSchoolClass) o).getChildren().values()) {
//                        //for each student
//                        if (so instanceof DomResultStudent) {
//                          so = ((DomResultStudent) so).getStudent();
//                        }   
//                        if (so instanceof DomStudent) {
//                            DomStudent s = (DomStudent) so;
//                            students.put(s.getId().getIdString(), DomStudentCodec.CODEC.encode(s));
//                        }
//                        
//                    }
//                    //put students in schoolclass
//                    id = ((DomResultSchoolClass) o).getSchoolClass().getId().getIdString();
//                    schoolClass.put("children", students);
//                    //put schoolclass in schoolclasses
//                    id = ((DomResultSchoolClass) o).getSchoolClass().getId().getIdString();
//                    schoolClasses.put(id, schoolClass);
//                }
//            }
//            json.put("children", schoolClasses);
//        }
//        return json;
//    }
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
