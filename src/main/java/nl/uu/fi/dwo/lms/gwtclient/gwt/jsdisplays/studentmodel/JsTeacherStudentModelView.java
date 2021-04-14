package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.studentmodel;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClass;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClassCodec;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.StudentModelPresenter;
import nl.uu.fi.dwo.rest.dom.DomTree;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author Wim van Velthoven
 */
@Singleton
public class JsTeacherStudentModelView implements StudentModelPresenter.Display {
    @Override
    public void clear() {
    	JsTeacherStudentModelDisplay.clear();
    }

    @Override
    public void setHelp(String url) {
    	JsTeacherStudentModelDisplay.setHelp(url);
    }
    
    @Override
    public void init() {
    	JsTeacherStudentModelDisplay.init();
    }

    @Override
    public void showSchoolClasses(Map<String, TaggedDomSchoolClass> schoolClasses) {
      JSONObject json = new JSONObject();
      schoolClasses.forEach((k,v) -> {json.put(k, TaggedDomSchoolClassCodec.CODEC.encode(v));});        
      JsTeacherStudentModelDisplay.showSchoolClasses(json.getJavaScriptObject());
    }

    @Inject JsTeacherStudentModelView() {}

	@Override
	public void showStudentModels(Map<String, String> models) {
	      JSONObject json = new JSONObject();
	      models.forEach((k,v) -> json.put(k, new JSONString(v)));        
	      JsTeacherStudentModelDisplay.showModels(json.getJavaScriptObject());
	}

	@Override
	public void showTree(DomTree<String> tree) {
		JSONObject json = new JSONObject();
		JsTeacherStudentModelDisplay.showTree(json.getJavaScriptObject());
		
	}
}
