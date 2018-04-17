package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import com.google.gwt.json.client.JSONObject;
import fi.dwo.gwt.lib.rest.util.DomStudentCodec;
import java.util.Map;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddStudentToSchoolclassPresenter;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
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
    public void setsetLoadingTableMessage() {
       JsAddStudentToSchoolclassDisplay.setLoadingTableMessage();
    }
    
}
