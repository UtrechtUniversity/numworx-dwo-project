package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.studentmodel;

import java.util.Map;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.ScoreIcon;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.SummaryIcon;
import nl.uu.fi.dwo.rest.dom.DomTree;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

public abstract class AbstractStudentModelView implements SelectionHandler<TreeItem> {

	protected final EventBus bus;
	protected final Panel treewrap;

	public AbstractStudentModelView(String treeId, EventBus bus) {
		treewrap = RootPanel.get(treeId);
		this.bus = bus;
	}
	
	public AbstractStudentModelView(Panel parent, EventBus bus) {
		treewrap = parent;
		this.bus = bus;
	}

	void clear() {
		treewrap.clear();
	}

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		bus.fireEventFromSource(event, this);
	}

	public void showTree(DomTree<String> tree) {
		treewrap.clear();
		Tree t = new Tree();
		t.addSelectionHandler(this);
		treewrap.add(t);
		for (Map.Entry<String,DomTree<String>> item: tree.getChildren().entrySet())
		{
			TreeItem ti = t.addItem(html(item.getValue().getObject(), item.getValue().getChildren() != null));
			ti.setUserObject(item.getKey());
			children(ti, item.getValue().getChildren());
		}
	}
    private void children(TreeItem t, Map<String, DomTree<String>> children) {
    	if (children != null) {
		for (Map.Entry<String,DomTree<String>> item: children.entrySet())
		{
			TreeItem ti = t.addItem(html(item.getValue().getObject(), item.getValue().getChildren() != null));
			ti.setUserObject(item.getKey());
			children(ti, item.getValue().getChildren());
		}}
	}

    private Widget html(String object, boolean folder) {
		if (folder) {
			return new SummaryIcon(object);
		} else
			return new ScoreIcon(object);
	}

	public void setEmptyTreeMessage() {
		treewrap.clear();
	}

    public void setLoadingTreeMessage() {
        treewrap.clear();
        Label l = new Label(DwoLocalesForGWT.instance.NUM_TBL_FETCHINGDATA());
        treewrap.add(l);
    }

}
