package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import fi.dwo.gwt.lib.rest.util.DomStudentCodec;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;

class Util {

  private Util() {    // TODO Auto-generated constructor stub
  }

  static JSONObject buildSubResultTree(DomResultScore<?> node) {
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
              DomStudentScoContext studentSco = ((DomResultStudentScoContext) node).getStudentSco();
  			String userIdString = studentSco.getUserID().getIdString();
              json.put("user-id", new JSONString(userIdString));
              String completionStatus = studentSco.getCompletionStatus(); // XXX What if not present? null of "" of unknown?
              if(completionStatus == null) completionStatus = "unknown";
  			json.put("completionStatus", new JSONString(completionStatus));
  			String totalTime = studentSco.getTotalTime();
  			if (totalTime == null) totalTime = "00:00:00";
  			json.put("totalTime", new JSONString(totalTime));
         }
  //        json.put("node-id", new JSONNumber(node.getNodeId()));
          //Add children.
          if (node.getChildren() != null && !node.getChildren().isEmpty()) {
              //there are children
              JSONObject children = new JSONObject();
              for (DomResultScore<?> o : node.getChildren().values()) {
                  //child node
                  String id = o.getId();
                  children.put(id, buildSubResultTree(o));
              }
              json.put("children", children);
          }
          return json;
      }

  /**
       * Assumes DomResultTeacher with DomResultSchoolClasses with DomResultStudents
       *
       * @param node
       * @return
       */
      static JSONObject buildSubStudentTree(DomResultScore node) {
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
                      for (Object so : ((DomResultSchoolClass<?>) o).getChildren().values()) {
                          //for each student
                          if (so instanceof DomResultStudent) {
                            so = ((DomResultStudent) so).getStudent();
                          }
                          if (so instanceof DomStudent) {
                              DomStudent s = (DomStudent) so;
                              students.put("children", DomStudentCodec.CODEC.encode(s));
                          }
                      }
                      //put students in schoolclass
                      id = ((DomResultSchoolClass<?>) o).getId();
                      schoolClass.put(id, students);
                      //put schoolclass in schoolclasses
                      id = ((DomResultSchoolClass<?>) o).getId();
                      schoolClasses.put(id, schoolClass);
                  }
              }
              json.put("children", schoolClasses);
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

}
