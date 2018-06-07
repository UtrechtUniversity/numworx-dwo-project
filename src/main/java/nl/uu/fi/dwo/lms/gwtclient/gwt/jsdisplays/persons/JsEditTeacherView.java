package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.persons;

import com.google.gwt.json.client.JSONObject;
import fi.dwo.gwt.lib.rest.util.DomUserCodec;
import java.util.Map;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.EditTeacherPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClass;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClassCodec;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsEditTeacherView implements EditTeacherPresenter.Display{

    @Override
    public void clear() {
        JsEditTeacherDisplay.clear();
    }

    @Override
    public void setUser(DomUser user) {
        JsEditTeacherDisplay.setUser(DomUserCodec.CODEC.encode(user).isObject().getJavaScriptObject());
    }

    @Override
    public void setSchoolClasses(Map<String, TaggedDomSchoolClass> schoolClasses) {
        JSONObject json = new JSONObject();
        schoolClasses.forEach((k,v) -> {json.put(k, TaggedDomSchoolClassCodec.CODEC.encode(v).isObject());});        
        JsEditStudentDisplay.setSchoolClasses(json.getJavaScriptObject());
    }

    @Override
    public void setEmptyTableMessage() {
        JsEditTeacherDisplay.setEmptyTableMessage();
    }

    @Override
    public void setLoadingTableMessage() {
        JsEditTeacherDisplay.setLoadingTableMessage();
    }


}
