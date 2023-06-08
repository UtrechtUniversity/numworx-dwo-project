package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.persons;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import fi.dwo.gwt.lib.rest.util.DomSingleSchoolStudentCodec;
import java.util.List;
import java.util.Map;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.ImportPersonsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClass;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClassCodec;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsImportPersonsView implements ImportPersonsPresenter.Display{

    @Override
    public void clear() {
        JsImportPersonsDisplay.clear();
    }

    @Override
    public void init() {
        JsImportPersonsDisplay.init();
    }

    @Override
    public void setEmptyPeopleTableMessage() {
      JsImportPersonsDisplay.setEmptyPeopleTableMessage();
    }

    @Override
    public void setLoadingPeopleTableMessage() {
      JsImportPersonsDisplay.setLoadingPeopleTableMessage();
    }

    @Override
    public void setEmptySchoolClassesTableMessage() {
      JsImportPersonsDisplay.setEmptySchoolClassesTableMessage();
    }

    @Override
    public void setLoadingSchoolClassesTableMessage() {
      JsImportPersonsDisplay.setLoadingSchoolClassesTableMessage();
    }

    @Override
    public void setHelp(String url) {
        JsImportPersonsDisplay.setHelp(url);
    }

    @Override
    public void setPersonImportList(List<DomSingleSchoolStudent> persons) {
        JSONArray json = new JSONArray();
        for(int i=0;i<persons.size();i++) {
            json.set(i, DomSingleSchoolStudentCodec.CODEC.encode(persons.get(i)));        
        }       
        JsImportPersonsDisplay.setPersonImportList(json.getJavaScriptObject());
    }

    @Override
    public void showSchoolClasses(Map<String, TaggedDomSchoolClass> schoolClasses) {
        JSONObject json = new JSONObject();
        schoolClasses.forEach((k,v) -> {json.put(k, TaggedDomSchoolClassCodec.CODEC.encode(v));});        
        JsImportPersonsDisplay.showSchoolClasses(json.getJavaScriptObject());
    }


}
