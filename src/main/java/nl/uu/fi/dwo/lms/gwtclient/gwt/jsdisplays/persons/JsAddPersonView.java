package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.persons;

import com.google.gwt.json.client.JSONObject;
import java.util.Comparator;
import java.util.Map;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.AddPersonPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClass;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClassCodec;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsAddPersonView implements AddPersonPresenter.Display{

    @Override
    public void clear() {
        JsAddPersonDisplay.clear();
    }

    @Override
    public void setHelp(String url) {
        JsAddPersonDisplay.setHelp(url);
    }

    @Override
    public void init(RoleType role) {
        JsAddPersonDisplay.init(role.name());
    }

    @Override
    public void showSchoolClasses(Map<String, TaggedDomSchoolClass> schoolClasses) {
                JSONObject object = new JSONObject();
                Comparator<TaggedDomSchoolClass> byName = 
                        (TaggedDomSchoolClass e1, nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClass e2) 
                                -> e1.getSchoolClass().getSchoolClassName().compareTo(e2.getSchoolClass().getSchoolClassName());
        schoolClasses.values().stream().sorted(byName).iterator().forEachRemaining(
                (nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClass sc) 
                        ->{object.put(sc.getSchoolClass().getId().getIdString(), TaggedDomSchoolClassCodec.CODEC.encode(sc));});
        JsAddPersonDisplay.showSchoolClasses(object.getJavaScriptObject());
    }

    @Override
    public void setEmptyTableMessage() {
        JsAddPersonDisplay.setEmptyTableMessage();
    }

    @Override
    public void setLoadingTableMessage() {
        JsAddPersonDisplay.setLoadingTableMessage();
    }

    @Override
    public void init() {
        return;
    }

}
