package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import fi.dwo.gwt.lib.rest.util.DomSchoolClassCodec;
import fi.dwo.gwt.lib.rest.util.DomTeacherCodec;
import java.util.Map;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddTeacherToSchoolclassPresenter;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsAddTeacherToSchoolclassView implements AddTeacherToSchoolclassPresenter.Display{


    @Override
    public void init() {
        JsAddTeacherToSchoolclassDisplay.init();
    }

    @Override
    public void clear() {
        JsAddTeacherToSchoolclassDisplay.clear();
    }

    @Override
    public void setHelp(String url) {
        JsAddTeacherToSchoolclassDisplay.setHelp(url);
    }
    
    @Override
    public void showTeachers(Map<String, DomTeacher> teachers) {
        JSONObject object = new JSONObject();
        for(DomTeacher teacher : teachers.values()){
            object.put(teacher.getId().getIdString(), DomTeacherCodec.CODEC.encode(teacher));
        }
        JsAddTeacherToSchoolclassDisplay.showTeachers(object.getJavaScriptObject());
    }

    @Override
    public void setEmptyTableMessage() {
        JsAddTeacherToSchoolclassDisplay.setEmptyTableMessage();
    }

    @Override
    public void setLoadingTableMessage() {
       JsAddTeacherToSchoolclassDisplay.setLoadingTableMessage();
    }

    @Override
    public void setSchoolClass(DomSchoolClass schoolClass) {
        JSONValue sc = DomSchoolClassCodec.CODEC.encode(schoolClass);       
        JsAddTeacherToSchoolclassDisplay.setSchoolClass(sc);       
    }    
}
