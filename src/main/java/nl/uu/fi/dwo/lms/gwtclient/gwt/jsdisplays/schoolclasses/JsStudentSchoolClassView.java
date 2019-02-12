package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gwt.json.client.JSONObject;

import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.account.JsAccountDisplay;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.organisation.JsOrganisationDisplay;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.persons.JsEditPersonDisplay;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClass;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClassCodec;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.StudentSchoolclassPresenter;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
@Singleton
public class JsStudentSchoolClassView implements StudentSchoolclassPresenter.Display{

    private static final Logger LOG = Logger.getLogger(JsStudentSchoolClassView.class.getName());
    
    @Inject JsStudentSchoolClassView() {}
    
    @Override
    public void clear() {
        JsStudentSchoolclassDisplay.clear();
    }

    @Override
    public void setHelp(String url) {
        JsStudentSchoolclassDisplay.setHelp(url);
    }
    
    @Override
    public void init() {
        JsStudentSchoolclassDisplay.init();
    }

    @Override
    public void setEmptyTableMessage() {
      JsStudentSchoolclassDisplay.setEmptyTableMessage();
    }

    @Override
    public void setLoadingTableMessage() {
      JsStudentSchoolclassDisplay.setLoadingTableMessage();
    }

    @Override
    public void setSchoolClasses(Map<String, TaggedDomSchoolClass> schoolClasses) {
        JSONObject json = new JSONObject();
        schoolClasses.forEach((k,v) -> {json.put(k, TaggedDomSchoolClassCodec.CODEC.encode(v).isObject());});        
        JsStudentSchoolclassDisplay.setSchoolClasses(json.getJavaScriptObject());
    }

    @Override
    public void showSchoolClasses(Map<String, TaggedDomSchoolClass> schoolClasses) {
      JSONObject json = new JSONObject();
      schoolClasses.forEach((k,v) -> {json.put(k, TaggedDomSchoolClassCodec.CODEC.encode(v));});        
      JsStudentSchoolclassDisplay.showSchoolClasses(json.getJavaScriptObject());
    }

}
