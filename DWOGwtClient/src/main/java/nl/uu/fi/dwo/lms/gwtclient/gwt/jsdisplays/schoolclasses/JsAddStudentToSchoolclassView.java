package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import fi.dwo.gwt.lib.rest.util.DomSchoolClassCodec;
import fi.dwo.gwt.lib.rest.util.DomStudentCodec;
import java.util.Map;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddStudentToSchoolclassPresenter;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsAddStudentToSchoolclassView implements AddStudentToSchoolclassPresenter.Display{

    @Override
    public void clear() {
        JsAddStudentToSchoolclassDisplay.clear();
    }

    @Override
    public void init() {
        JsAddStudentToSchoolclassDisplay.init();
    }
//
//    @Override
//    public void setSchoolClass(DomSchoolClassFull schoolClass) {
//        
//    }

    @Override
    public void showStudents(Map<String, DomStudent> students) {
        JSONObject object = new JSONObject();
        for(DomStudent student : students.values()){
            object.put(student.getId().getIdString(), DomStudentCodec.CODEC.encode(student));
        }
        JsAddStudentToSchoolclassDisplay.showStudents(object.getJavaScriptObject());
    }

    @Override
    public void setEmptyTableMessage() {
        JsAddStudentToSchoolclassDisplay.setEmptyTableMessage();
    }

    @Override
    public void setLoadingTableMessage() {
       JsAddStudentToSchoolclassDisplay.setLoadingTableMessage();
    }

    @Override
    public void setSchoolClass(DomSchoolClass schoolClass) {
        JSONValue sc = DomSchoolClassCodec.CODEC.encode(schoolClass);
        JsAddStudentToSchoolclassDisplay.setSchoolClass(sc);
    }

    @Override
    public void setHelp(String url) {
        JsAddStudentToSchoolclassDisplay.setHelp(url);
    }
    
}
