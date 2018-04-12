package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import fi.dwo.gwt.lib.rest.util.DomCourseCodec;
import fi.dwo.gwt.lib.rest.util.DomSchoolClassFullCodec;
import fi.dwo.gwt.lib.rest.util.DomStudentCodec;
import fi.dwo.gwt.lib.rest.util.DomTeacherCodec;
import java.util.List;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditSchoolclassPresenter;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsEditSchoolClassView implements EditSchoolclassPresenter.Display{
    @Override
    public void clear() {
        JsEditSchoolclassDisplay.clear();
    }

    @Override
    public void init() {
        JsEditSchoolclassDisplay.init();
    }

    @Override
    public void showSchoolClass(DomSchoolClassFull schoolClass) {
        JSONValue sc = DomSchoolClassFullCodec.CODEC.encode(schoolClass);
	JsEditSchoolclassDisplay.showSchoolClass(sc);
    }

    @Override
    public void showStudents(List<DomStudent> students) {
        JSONObject object = new JSONObject();
        for(DomStudent item : students){
            object.put(item.getId().getIdString(), DomStudentCodec.CODEC.encode(item));
        }
	JsEditSchoolclassDisplay.showStudents(object);
    }

    @Override
    public void showTeachers(List<DomTeacher> teachers) {
        JSONObject object = new JSONObject();
        for(DomTeacher item : teachers){
            object.put(item.getId().getIdString(), DomTeacherCodec.CODEC.encode(item));
        }
	JsEditSchoolclassDisplay.showTeachers(object);
    }

    @Override
    public void showModules(List<DomCourse> modules) {
        JSONObject object = new JSONObject();
        for(DomCourse item : modules){
            object.put(item.getId().getIdString(), DomCourseCodec.CODEC.encode(item));
        }
	JsEditSchoolclassDisplay.showShowModels(object);
    }

}
