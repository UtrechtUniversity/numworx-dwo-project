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
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.SMClassResultsPresenter;
import nl.uu.fi.dwo.rest.dom.DomTree;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author Wim van Velthoven
 */
@Singleton
public class JsTeacherSMClassResultsView implements SMClassResultsPresenter.Display, SelectionHandler<TreeItem> {

	private RootPanel treewrap;
	@Inject EventBus bus;

	@Override
    public void clear() {
		JsTeacherSMClassResultsDisplay.clear();
    }

    @Override
    public void setHelp(String url) {
    	JsTeacherSMClassResultsDisplay.setHelp(url);
    }
    
    @Override
    public void init() {
    	JsTeacherSMClassResultsDisplay.init();
    }

    @Override
    public void showSchoolClasses(Map<String, TaggedDomSchoolClass> schoolClasses) {
      JSONObject json = new JSONObject();
      schoolClasses.forEach((k,v) -> {json.put(k, TaggedDomSchoolClassCodec.CODEC.encode(v));});        
      JsTeacherSMClassResultsDisplay.showSchoolClasses(json.getJavaScriptObject());
    }

    @Inject JsTeacherSMClassResultsView() {
		treewrap = RootPanel.get(JsTeacherSMClassResultsDisplay.getTreeId());
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
	public void setEmptyTreeMessage() {
		treewrap.clear();
	}

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		bus.fireEventFromSource(event, this);
	}
	
	@Override
	public void setTitle(String title) {
		JsTeacherSMClassResultsDisplay.setTitle(title);
	}
	
}
