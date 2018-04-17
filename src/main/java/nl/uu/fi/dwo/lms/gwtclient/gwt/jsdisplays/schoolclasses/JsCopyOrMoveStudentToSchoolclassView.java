package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import com.google.gwt.json.client.JSONObject;
import fi.dwo.gwt.lib.rest.util.DomStudentCodec;
import java.util.List;
import java.util.Map;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.CopyOrMoveStudentToSchoolclassPresenter;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsCopyOrMoveStudentToSchoolclassView implements CopyOrMoveStudentToSchoolclassPresenter.Display{

    @Override
    public void clear() {
        JsAddStudentToSchoolclassDisplay.clear();
    }

    @Override
    public void init() {
        JsAddStudentToSchoolclassDisplay.init();
    }

    @Override
    public void setEmptyTableMessage() {
        JsAddStudentToSchoolclassDisplay.setEmptyTableMessage();
    }

    @Override
    public void setsetLoadingTableMessage() {
       JsAddStudentToSchoolclassDisplay.setLoadingTableMessage();
    }

//
//    @Override
//    public void showSchoolClasses(List<DomSchoolClass> schoolClasses) {
//        JSONObject object = new JSONObject();
//        for(DomSchoolClass schoolClass : schoolClasses){
//            object.put(schoolClass.getId().getIdString(), DomSchoolClassCodec.CODEC.encode(schoolClass));
//        }
//        JsCopyOrMoveStudentToSchoolclassDisplay.showSchoolClasses(object.getJavaScriptObject());
//    }    
    
    @Override
    public void showStudentsClassA(Map<String, DomStudent> students) {
        JSONObject object = new JSONObject();
        for(DomStudent student : students.values()){
            object.put(student.getId().getIdString(), DomStudentCodec.CODEC.encode(student));
        }
        JsCopyOrMoveStudentToSchoolclassDisplay.showStudentsClassA(object.getJavaScriptObject());
    }

    @Override
    public void showStudentsClassB(Map<String, DomStudent> students) {
        JSONObject object = new JSONObject();
        for(DomStudent student : students.values()){
            object.put(student.getId().getIdString(), DomStudentCodec.CODEC.encode(student));
        }
        JsCopyOrMoveStudentToSchoolclassDisplay.showStudentsClassB(object.getJavaScriptObject());
    }
        
}
