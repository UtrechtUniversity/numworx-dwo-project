package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.persons;

import com.google.gwt.json.client.JSONObject;
import fi.dwo.gwt.lib.rest.util.DomUserCodec;
import java.util.Map;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.EditTeacherPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClass;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClassCodec;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsEditTeacherView implements EditTeacherPresenter.Display{

    @Override
    public void setHelp(String url) {
        JsEditPersonDisplay.setHelp(url);
    }
    
    @Override
    public void clear() {
        JsEditPersonDisplay.clear();
    }

    @Override
    public void init() {
        JsEditPersonDisplay.init();
    }

    @Override
    public void setUser(DomUser user) {
        JsEditPersonDisplay.setUser(RoleType.TEACHER.name(), DomUserCodec.CODEC.encode(user).isObject().getJavaScriptObject());
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


}
