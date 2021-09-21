package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.studentmodel;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClass;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClassCodec;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.StudentModelPresenter;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author Wim van Velthoven
 */
@Singleton
public class JsTeacherStudentModelView extends AbstractStudentModelView implements StudentModelPresenter.Display, ResizeHandler {
	final RootPanel descriptionwrap;
	final ResizeLayoutPanel rlp;
	
	@Override
    public void clear() {
    	JsTeacherStudentModelDisplay.clear();
    	super.clear();
    	descriptionwrap.clear();
    	rlp.setWidget(null);
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
		rlp = new ResizeLayoutPanel();
		rlp.addResizeHandler(this);
    }

	@Override
	public void showStudentModels(Map<String, String> models) {
	      JSONObject json = new JSONObject();
	      models.forEach((k,v) -> json.put(k, new JSONString(v)));        
	      JsTeacherStudentModelDisplay.showModels(json.getJavaScriptObject());
	}

	@Override
	public void showMethods(List<DomMethod> methods) {
	    JSONObject json = new JSONObject();
		for(DomMethod m: methods) {
			String key = Objects.toString(m.getId(), "");
			String value = m.getMethod();
			json.put(key, new JSONString(value));
		}
		JsTeacherStudentModelDisplay.showMethods(json.getJavaScriptObject());
	}
	
	
	@Override
	public void setDescription(String title, IsWidget w) {
		descriptionwrap.clear();
		Label header = new Label(title);
		header.setStylePrimaryName("description-title");
		descriptionwrap.add(header);
		if (w != null) {
			rlp.setWidget(w);
			Style s = rlp.getElement().getStyle();
			s.setTop(40, Unit.PX); // size of title
			s.setBottom(2, Unit.PX);
			s.setLeft(20, Unit.PX);
			s.setRight(2, Unit.PX);
			s.setPosition(Position.ABSOLUTE);
		descriptionwrap.add(rlp);
		}
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
	
	@Override
	public void setMethod(String label) {
		JsTeacherStudentModelDisplay.setMethodLabel(label);
	}

	@Override
	public void setActiveMethod(PersistenceId id) {
		String key = Objects.toString(id, "");
		JsTeacherStudentModelDisplay.setActiveMethod(key);
	}
	
	
	@Override
	public void onResize(ResizeEvent event) {
		Widget w = rlp.getWidget();
		w.getElement().getStyle().setHeight(event.getHeight(), Unit.PX);	
	}
	
	
}
