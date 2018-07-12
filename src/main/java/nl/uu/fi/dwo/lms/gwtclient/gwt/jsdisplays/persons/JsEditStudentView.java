package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.persons;

import com.google.gwt.json.client.JSONObject;
import fi.dwo.gwt.lib.rest.util.DomUserCodec;
import fi.dwo.gwt.lib.rest.util.DomUserFullCodec;
import java.util.Map;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.EditStudentPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClass;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClassCodec;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

/**
 * Mapper to allow java interface implementation.
 *
 * @author G.A.J. van der Plas
 */
public class JsEditStudentView implements EditStudentPresenter.Display {

    @Override
    public void clear() {
        JsEditPersonDisplay.clear();
    }

    @Override
    public void init() {
        JsEditPersonDisplay.init();
    }
    
    @Override
    public void setHelp(String url) {
        JsEditPersonDisplay.setHelp(url);
    }
    
    @Override
    public void setSchoolClasses(Map<String, TaggedDomSchoolClass> schoolClasses) {
        JSONObject json = new JSONObject();
        schoolClasses.forEach((k,v) -> {json.put(k, TaggedDomSchoolClassCodec.CODEC.encode(v).isObject());});        
        JsEditPersonDisplay.setSchoolClasses(json.getJavaScriptObject());
    }

    @Override
    public void setEmptyTableMessage() {
        JsEditPersonDisplay.setEmptyTableMessage();
    }

    @Override
    public void setLoadingTableMessage() {
       JsEditPersonDisplay.setLoadingTableMessage();
    }

    @Override
    public void setUser(DomUser student) {
        JsEditPersonDisplay.setUser(RoleType.STUDENT.name(), DomUserCodec.CODEC.encode(student).isObject().getJavaScriptObject());
    }

    @Override
    public void setSingleSchoolStudent(DomUserFull student) {
        JsEditPersonDisplay.setSingleSchoolStudent(DomUserFullCodec.CODEC.encode(student).isObject().getJavaScriptObject());
    }


}
