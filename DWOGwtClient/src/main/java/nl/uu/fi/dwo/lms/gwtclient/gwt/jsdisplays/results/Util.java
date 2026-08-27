package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

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
        	  json.put("short", new JSONString(node.getTitle()));
          }
          if (node.getDescription() != null) {
        	  json.put("long", new JSONString(node.getDescription()));
          }
          if (node instanceof DomResultStudentScoContext) {
            DomResultStudentScoContext dssc = (DomResultStudentScoContext) node;
		    DomStudentScoContext studentSco = dssc.getStudentSco();
  	  	    String userIdString = studentSco.getUserID().getIdString();
            json.put("user-id", new JSONString(userIdString));
            json.put("maxScore", dnull(dssc.getMaxScore()));
            String completionStatus = studentSco.getCompletionStatus(); // XXX What if not present? null of "" of unknown?
            if(completionStatus == null) completionStatus = "not attempted";
  			json.put("completionStatus", new JSONString(completionStatus));
  			String totalTime = studentSco.getTotalTime();
  			if (totalTime == null) totalTime = "00:00:00";
  			json.put("totalTime", buildTime(totalTime));
// Add completion time
  			String completionTime = dssc.getCompletionTime();
  			if (completionTime == null) completionTime = "unknown"; // FIXME moet leeg zijn!!!!!
  			json.put("completionTime", new JSONString(completionTime));  			
  			
         } else if (node instanceof DomResultCourseInClass){
             DomResultCourseInClass resultCourse = (DomResultCourseInClass) node;
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
             json.put("sequence", new JSONNumber(page.getNodeId()));
             json.put("maxFactor", dnull(page.getMaxFactor()));
         }
          //Add children.
          if (node.getChildren() != null && !node.getChildren().isEmpty()) {
              //there are children
              JSONObject children = new JSONObject();
              Map ch = node.getChildren();
			  Set set = ch.entrySet();
			  for (Object o : set) {
				  Map.Entry<PersistenceId, DomResultScore> entry = (Entry<PersistenceId, DomResultScore>) o;
                  //child node, keep key
                  String id = entry.getKey().toString();
                  children.put(id, buildSubResultTree(entry.getValue()));
              }
              json.put("children", children);
          }
          return json;
      }

  private static double d(Double d) { // Voorkom npe op double
    if (d != null) return d.doubleValue();
    return 0.0;
  }

  private static JSONValue dnull(Number d) { // Voorkom npe op double
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
      @SuppressWarnings("unchecked")
	static JSONObject buildSubStudentTree(DomResultScore<?> node) {
          JSONObject json = new JSONObject();
          String classType = node.getClass().getSimpleName();
          json.put("classType", buildTime(classType));
          json.put("label", new JSONString(node.getLabel()));
          json.put("sumScore", new JSONNumber(d(node.getScore())));
          json.put("scoCount", new JSONNumber(node.getScoCount()));
          json.put("studentScoCount", new JSONNumber(node.getStudentScoCount()));
          if (node.getFraction() != null) {
        	  json.put("fraction", new JSONNumber(node.getFraction()));
          }
          if (node.getTitle() != null) {
        	  json.put("short", new JSONString(node.getTitle()));
          }
          if (node.getDescription() != null) {
        	  json.put("long", new JSONString(node.getDescription()));
          }
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
                      for (DomResultStudent so : ((DomResultSchoolClass<DomResultStudent>) o).getChildren().values()) {
                          //for each student
                              DomStudent s = so.getStudent();
                              JSONValue encode = DomStudentCodec.CODEC.encode(s);
                              JSONObject courses = new JSONObject();
	                              Map<PersistenceId, DomResultCourseInClass> children = so.getChildren();
	                              if (!children.isEmpty()) {
	                              for (DomResultCourseInClass child: children.values()) {
	                            	  courses.put(child.getId(), buildSubResultTree(child));
	                              }                            
	                              encode.isObject().put("children", courses);
                              }
							students.put(s.getId().getIdString(), encode);
                          
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
