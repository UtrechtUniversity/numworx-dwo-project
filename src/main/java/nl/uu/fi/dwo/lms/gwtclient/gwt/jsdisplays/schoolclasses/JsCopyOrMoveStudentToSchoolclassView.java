package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import fi.dwo.gwt.lib.rest.util.DomSchoolClassCodec;
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
public class JsCopyOrMoveStudentToSchoolclassView implements CopyOrMoveStudentToSchoolclassPresenter.Display {

    @Override
    public void clear() {
        JsCopyOrMoveStudentToSchoolclassDisplay.clear();
    }

    @Override
    public void setHelp(String url) {
        JsCopyOrMoveStudentToSchoolclassDisplay.setHelp(url);
    }
    
    @Override
    public void init() {
        JsCopyOrMoveStudentToSchoolclassDisplay.init();
    }

    @Override
    public void setEmptyTableMessageA() {
        JsCopyOrMoveStudentToSchoolclassDisplay.setEmptyTableMessageA();
    }

    @Override
    public void setLoadingTableMessageA() {
        JsCopyOrMoveStudentToSchoolclassDisplay.setLoadingTableMessageA();
    }

    @Override
    public void setEmptyTableMessageB() {
        JsCopyOrMoveStudentToSchoolclassDisplay.setEmptyTableMessageB();
    }

    @Override
    public void setLoadingTableMessageB() {
        JsCopyOrMoveStudentToSchoolclassDisplay.setLoadingTableMessageB();
    }    

    @Override
    public void setEmptyTableMessageClasses() {
        JsCopyOrMoveStudentToSchoolclassDisplay.setEmptyTableMessageClasses();
    }

    @Override
    public void setLoadingTableMessageClasses() {
        JsCopyOrMoveStudentToSchoolclassDisplay.setLoadingTableMessageClasses();
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
        for (DomStudent student : students.values()) {
            object.put(student.getId().getIdString(), DomStudentCodec.CODEC.encode(student));
        }
        JsCopyOrMoveStudentToSchoolclassDisplay.showStudentsClassA(object.getJavaScriptObject());
    }

    @Override
    public void showStudentsClassB(Map<String, DomStudent> students) {
        JSONObject object = new JSONObject();
        for (DomStudent student : students.values()) {
            object.put(student.getId().getIdString(), DomStudentCodec.CODEC.encode(student));
        }
        JsCopyOrMoveStudentToSchoolclassDisplay.showStudentsClassB(object.getJavaScriptObject());
    }

    @Override
    public void SetClassA(DomSchoolClass schoolClass) {
        JSONValue sc = DomSchoolClassCodec.CODEC.encode(schoolClass);
        //JsAddTeacherToSchoolclassDisplay.setSchoolClass(sc);
        JsCopyOrMoveStudentToSchoolclassDisplay.setSchoolClassA(sc.isObject().getJavaScriptObject());
   }

    @Override
    public void SetClassB(DomSchoolClass schoolClass) {
        JSONValue sc = DomSchoolClassCodec.CODEC.encode(schoolClass);
        //JsAddTeacherToSchoolclassDisplay.setSchoolClass(sc);
        JsCopyOrMoveStudentToSchoolclassDisplay.setSchoolClassB(sc.isObject().getJavaScriptObject());
    }

    @Override
    public void SetClassList(List<DomSchoolClass> classList) {
        JSONObject object = new JSONObject();
        for(DomSchoolClass sc : classList){
            object.put(sc.getId().getIdString(), DomSchoolClassCodec.CODEC.encode(sc));
        }
        JsCopyOrMoveStudentToSchoolclassDisplay.setClassList(object.getJavaScriptObject());
    }

}
