package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results;

import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import fi.dwo.gwt.lib.rest.util.DomStudentCodec;
import nl.uu.fi.dwo.rest.dom.entities.DomResultCourseInClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoPage;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.util.SumOfSubTreeVisitor;

class Util {

  private Util() {    // TODO Auto-generated constructor stub
  }

  static JSONObject buildSubResultTree(DomResultScore<?> node) {
          JSONObject json = new JSONObject();
          //node.calculateSumOfSubtreeScore();
          node.visit(new SumOfSubTreeVisitor());
          //set course data in node.
          String classType = node.getClass().getSimpleName();
          json.put("classType", new JSONString(classType));
          json.put("label", new JSONString(node.getLabel()));
          json.put("sumScore", new JSONNumber(d(node.getScore())));
          json.put("scoCount", new JSONNumber(node.getScoCount()));
          json.put("studentScoCount", new JSONNumber(node.getStudentScoCount()));
          if (node.getFraction() != null) {
        	  json.put("fraction", new JSONNumber(node.getFraction()));
          }
          if (node.getTitle() != null) {
        	  json.put("tite", new JSONString(node.getTitle()));
          }
          if (node.getDescription() != null) {
        	  json.put("description", new JSONString(node.getDescription()));
          }
          if (node instanceof DomResultStudentScoContext) {
            DomResultStudentScoContext dssc = (DomResultStudentScoContext) node;
		    DomStudentScoContext studentSco = dssc.getStudentSco();
  	  	    String userIdString = studentSco.getUserID().getIdString();
            json.put("user-id", new JSONString(userIdString));
            json.put("maxScore", dnull(dssc.getMaxScore()));
            String completionStatus = studentSco.getCompletionStatus(); // XXX What if not present? null of "" of unknown?
            if(completionStatus == null) completionStatus = "unknown";
  			json.put("completionStatus", new JSONString(completionStatus));
  			String totalTime = studentSco.getTotalTime();
  			if (totalTime == null) totalTime = "00:00:00";
  			json.put("totalTime", buildTime(totalTime));
         } else if (node instanceof DomResultCourseInClass){
             DomResultCourseInClass<?> resultCourse = (DomResultCourseInClass<?>) node;
             String viewState = resultCourse.getViewState().name();
             json.put("viewState", new JSONString(viewState));
             Long sequenceNr = resultCourse.getCourse().getSequenceNr();
             if (sequenceNr != null )
               json.put("sequence", new JSONNumber(sequenceNr));
             else 
               json.put("sequence", new JSONNumber(Integer.MAX_VALUE));
         } else if (node instanceof DomResultScoContext) {
             DomResultScoContext resultSco = (DomResultScoContext) node;
             json.put("maxScore", dnull(resultSco.getMaxScore()));
             Long sequencenr = resultSco.getScoContext().getSequencenr();
             if (sequencenr != null) {
               json.put("sequence", new JSONNumber(sequencenr));
             } else {
               json.put("sequence", new JSONNumber(Integer.MAX_VALUE));
             }
         } else if (node instanceof DomResultStudentScoPage) {
             DomResultStudentScoPage page = (DomResultStudentScoPage) node;
             json.put("maxScore", dnull(page.getMaxScore()));
             json.put("bonus", new JSONNumber(d(page.getCorrectie())));
             //json.put("label", new JSONString(page.getLabel()));
             json.put("sequence", new JSONNumber(page.getNodeId()));
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

  private static double d(Double d) { // Voorkom npe op double
    if (d != null) return d.doubleValue();
    return 0.0;
  }

  private static JSONValue dnull(Double d) { // Voorkom npe op double
	    if (d != null) return new JSONNumber(d.doubleValue());
	    return JSONNull.getInstance();
	  }

  private static JSONString buildTime(String totalTime) {
    if("00:00:00".equals(totalTime))
      return new JSONString("0s");
    if(totalTime.startsWith("00:00:0"))
      totalTime = totalTime.substring(7) + "s";
    else if (totalTime.startsWith("00:00:"))
      totalTime = totalTime.substring(6) + "s";
    else if (totalTime.startsWith("00:0"))
      totalTime = totalTime.substring(4, 5) + "m";
    else if (totalTime.startsWith("00:"))
      totalTime = totalTime.substring(3, 5) + "m";
    else if (totalTime.startsWith("0"))
      totalTime = totalTime.substring(1,2) + "h";
    else 
      totalTime = totalTime.split(":",2)[0] + "h";
    return new JSONString(totalTime);
  }

  /**
       * Assumes DomResultTeacher with DomResultSchoolClasses with DomResultStudents
       *
       * @param node
       * @return
       */
      static JSONObject buildSubStudentTree(DomResultScore<?> node) {
          JSONObject json = new JSONObject();
          String classType = node.getClass().getSimpleName();
          json.put("classType", buildTime(classType));
          json.put("label", new JSONString(node.getLabel()));
          json.put("sumScore", new JSONNumber(d(node.getScore())));
          json.put("scoCount", new JSONNumber(node.getScoCount()));
          json.put("studentScoCount", new JSONNumber(node.getStudentScoCount()));
          //Add children.
          if (node.getChildren() != null && !node.getChildren().isEmpty()) {
              //there are schoolclasses
              JSONObject schoolClasses = new JSONObject();
              for (DomResultScore o : node.getChildren().values()) {
                  //for each schoolclass
                  //child node
                  String id;
                  if (o instanceof DomResultSchoolClass) {
                      //for each schoolclass
                      JSONObject schoolClass = new JSONObject();
                      classType = o.getClass().getSimpleName();
                      schoolClass.put("classType", buildTime(classType));
                      schoolClass.put("label", new JSONString(o.getLabel()));
                      schoolClass.put("sumScore", new JSONNumber(d(node.getScore())));
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
                              students.put(s.getId().getIdString(), DomStudentCodec.CODEC.encode(s));
                          }
                      }
                      //put students in schoolclass
                      schoolClass.put("children", students);
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
