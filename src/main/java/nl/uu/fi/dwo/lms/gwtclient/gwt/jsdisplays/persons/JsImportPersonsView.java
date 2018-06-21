package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.persons;

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
        JsImportPersonsDisplay.init();
    }

    @Override
    public String fetchFileName() {
        return JsImportPersonsDisplay.fetchFileName();
    }


    @Override
    public void setEmptyPeopleTableMessage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLoadingPeopleTableMessage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setEmptySchoolClassesTableMessage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLoadingSchoolClassesTableMessage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setHelp(String url) {
        JsImportPersonsDisplay.setHelp(url);
    }

    @Override
    public void setPersonImportList(List<DomSingleSchoolStudent> persons) {
        JSONObject json = new JSONObject();
        for(int i=0;i<persons.size();i++){
            json.put(""+i, DomSingleSchoolStudentCodec.CODEC.encode(persons.get(i)).isObject());        
        }
        
        JsEditPersonDisplay.setSchoolClasses(json.getJavaScriptObject());

    }

    @Override
    public void showSchoolClasses(Map<String, TaggedDomSchoolClass> schoolClasses) {
        JSONObject json = new JSONObject();
        schoolClasses.forEach((k,v) -> {json.put(k, TaggedDomSchoolClassCodec.CODEC.encode(v).isObject());});        
        JsEditPersonDisplay.setSchoolClasses(json.getJavaScriptObject());
    }


}
