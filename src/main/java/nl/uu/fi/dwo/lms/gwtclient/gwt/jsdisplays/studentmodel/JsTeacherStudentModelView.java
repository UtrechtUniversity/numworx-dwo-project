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

import nl.uu.fi.dwo.lms.gwtclient.gwt.BootPanelController;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses.JsModulesOfSchoolclassDisplay;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClass;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClassCodec;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.StudentModelPresenter;
import nl.uu.fi.dwo.rest.dom.DomTree;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author Wim van Velthoven
 */
@Singleton
public class JsTeacherStudentModelView implements StudentModelPresenter.Display, SelectionHandler<TreeItem> {
	private final RootPanel treewrap, descriptionwrap;
	
	@Inject EventBus bus;

	@Override
    public void clear() {
    	JsTeacherStudentModelDisplay.clear();
    	treewrap.clear();
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

    @Inject JsTeacherStudentModelView() {
		treewrap = RootPanel.get(JsTeacherStudentModelDisplay.getTreeId());
		descriptionwrap = RootPanel.get(JsTeacherStudentModelDisplay.getDescriptionId());
    }

	@Override
	public void showStudentModels(Map<String, String> models) {
	      JSONObject json = new JSONObject();
	      models.forEach((k,v) -> json.put(k, new JSONString(v)));        
	      JsTeacherStudentModelDisplay.showModels(json.getJavaScriptObject());
	}

	@Override
	public void showTree(DomTree<String> tree) {
		treewrap.clear();
		Tree t = new Tree();
		t.addSelectionHandler(this);
		treewrap.add(t);
		for (Map.Entry<String,DomTree<String>> item: tree.getChildren().entrySet())
		{
			TreeItem ti = t.addTextItem(item.getValue().getObject());
			ti.setUserObject(item.getKey());
			children(ti, item.getValue().getChildren());
		}
	}
	
    private void children(TreeItem t, Map<String, DomTree<String>> children) {
    	if (children != null) {
		for (Map.Entry<String,DomTree<String>> item: children.entrySet())
		{
			TreeItem ti = t.addTextItem(item.getValue().getObject());
			ti.setUserObject(item.getKey());
			children(ti, item.getValue().getChildren());
		}}
	}

	@Override
    public void setLoadingTreeMessage() {
        treewrap.clear();
        Label l = new Label(DwoLocalesForGWT.instance.NUM_TBL_FETCHINGDATA());
        treewrap.add(l);
    }

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		bus.fireEventFromSource(event, this);
	}

	@Override
	public void setDescription(IsWidget w) {
		descriptionwrap.clear();
		descriptionwrap.add(w);
	}
	
	@Override
	public void setTitle(String title) {
		JsTeacherStudentModelDisplay.setTitle(title);
	}
	
}
