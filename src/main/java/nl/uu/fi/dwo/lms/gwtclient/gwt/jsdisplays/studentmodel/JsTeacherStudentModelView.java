package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.studentmodel;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClass;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClassCodec;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.StudentModelPresenter;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author Wim van Velthoven
 */
@Singleton
public class JsTeacherStudentModelView extends AbstractStudentModelView implements StudentModelPresenter.Display {
	final RootPanel descriptionwrap;
	
	@Override
    public void clear() {
    	JsTeacherStudentModelDisplay.clear();
    	super.clear();
    	descriptionwrap.clear();
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

    @Inject JsTeacherStudentModelView(EventBus bus) {
		super(JsTeacherStudentModelDisplay.getTreeId(), bus);
		descriptionwrap = RootPanel.get(JsTeacherStudentModelDisplay.getDescriptionId());
    }

	@Override
	public void showStudentModels(Map<String, String> models) {
	      JSONObject json = new JSONObject();
	      models.forEach((k,v) -> json.put(k, new JSONString(v)));        
	      JsTeacherStudentModelDisplay.showModels(json.getJavaScriptObject());
	}

	@Override
	public void setDescription(String title, IsWidget w) {
		descriptionwrap.clear();
		Label header = new Label(title);
		header.setStylePrimaryName("description-title");
		descriptionwrap.add(header);
		descriptionwrap.add(w);
	}
	
	@Override
	public void setTitle(String title) {
		JsTeacherStudentModelDisplay.setTitle(title);
	}

	@Override
	public void setModelSelect(String id) {
		JsTeacherStudentModelDisplay.setModelSelect(id);
	}

	@Override
	public boolean isMethod() {
		return JsTeacherStudentModelDisplay.isMethod();
	}
	
}
