package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleCell;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.places.SelectModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.ui.client.widget.CellList;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedEvent;
import com.googlecode.mgwt.ui.client.widget.celllist.HasCellSelectedHandler;

public class TreeModuleViewImpl  extends Composite implements TreeModuleView 
{

	@UiField HeaderButton backButton;
	@UiField HeaderPanel headerPanel;
	
	@UiField SimplePanel container;
	@UiField (provided=true) CellList<SelectModuleItem> cells;
	@UiField Tree tree;
	
	private HashMap<SelectModuleItem, TreeItem> inverseMap = new HashMap<SelectModuleItem, TreeItem>();
	private List<SelectModuleItem> cellItems;
	private List<SelectModuleItem> model;

	private Presenter presenter;
	
	//================================================================================
    // Constructor and UiBinder 
    //================================================================================

	private static TreeModuleViewImplUiBinder uiBinder = GWT
			.create(TreeModuleViewImplUiBinder.class);

	interface TreeModuleViewImplUiBinder extends
			UiBinder<Widget, TreeModuleViewImpl> {
	}
	
	public TreeModuleViewImpl()
	{
		cells = new CellList<SelectModuleItem>(new SelectModuleCell());
		initWidget(uiBinder.createAndBindUi(this));
	}

	//================================================================================
    // UiHandlers used by this implementation
    //================================================================================

	@UiHandler("tree")
	public void onSelection(SelectionEvent<TreeItem> event)
	{
		TreeItem item = event.getSelectedItem();
		SelectModuleItem o = (SelectModuleItem) item.getUserObject();
		selectItem(o);
	}

	@UiHandler("cells")
	public void onCellSelected(CellSelectedEvent event) {
		int index = event.getIndex();
		selectItem(this.cellItems.get(index));
	}

	@UiHandler("backButton")
	public void onTap(TapEvent event) {
		History.back();
	}
	
	private void selectItem(SelectModuleItem o) {
		Place place;
		switch(o.getType()) {
		default:
		case ROOT:
			place = new TreeModulePlace("0");
			break;
		case SCO:
			place = new ViewModulePlace(o.getID());
			break;
		case MODULE:
			place = new SelectModulePlace(o.getID());
			break;
		case FOLDER:
			place = new TreeModulePlace(o.getID());
			break;
		}
		this.presenter.goTo(place);
	}
	
	//================================================================================
    // TreeModuleView Interface Methods
    //================================================================================

	@Override
	public void render(List<SelectModuleItem> currentModel)
	{
		
		if (currentModel.isEmpty() )
			return;
		if (model != currentModel)
		{
			tree.removeItems();
			inverseMap.clear();
			model = currentModel;
			initTree();
		}
	
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public void selectModule(SelectModuleItem item)
	{
		// TODO iets met tree.ensureSelectedItemVisible() na setselectedItem(item)
		if (item != null)
		{
			headerPanel.setCenter(item.getName());
			String description = item.getDescription();
			if(description != null)
			{
				if(description.startsWith(DescriptionView.GZIPPREFIX))
				{
					container.setWidget(new DescriptionViewImpl(item.getID()));
				} else
				if(description.startsWith("<html>"))
					container.setWidget(new HTML(description));
				else
				{
					container.setWidget(new Label(description));
				}
			} else
				container.setWidget(new Label(""));
			if(item.getType() == SelectModuleItem.Type.FOLDER)
			{
				if(item.getChildren() == null)
					loadChildren(item);
				else
					addChildren(item.getChildren());
			} else if(item.getType() == SelectModuleItem.Type.MODULE)
			{	if(item.getChildren() == null)
					loadScos(item);
				else
					addChildren(item.getChildren());
			} else if(item.getType() == SelectModuleItem.Type.ROOT )
			{
				addChildren(model);
			}
			TreeItem node = inverseMap.get(item);
			tree.setSelectedItem(null, false);
			if(node != null) {
				
				tree.setSelectedItem(node, false);
				tree.ensureSelectedItemVisible();
			}
		}
		else
		{
			container.setWidget(new Label("DWO standaard modules")); // Uit het profiel halen!
			addChildren(model);
		}
	}

	//================================================================================
    // Init and format the tree and cells
    //================================================================================
	
	private void initTree()
	{
		for (SelectModuleItem item : model)
		{
			
			TreeItem treeItem = getTreeItem(item);
			treeItem.setUserObject(item);
			inverseMap.put(item, treeItem);
			tree.addItem(treeItem);
			
			if(item.getChildren() != null)
				initTree(item.getChildren(), treeItem);
		}
	}

	private void initTree(List<SelectModuleItem> model, TreeItem tree) {
		for (SelectModuleItem item : model)
		{
			TreeItem treeItem = getTreeItem(item);
			treeItem.setUserObject(item);
			inverseMap.put(item, treeItem);
			tree.addItem(treeItem);	
			if(item.getChildren() != null)
				initTree(item.getChildren(), treeItem);
		}
		tree.setState(true);
	}

	private static final TreeItemTemplate TEMPLATE = GWT.create(TreeItemTemplate.class);
	
	public interface TreeItemTemplate extends SafeHtmlTemplates {
		@SafeHtmlTemplates.Template("<div class=''><i class='fa {1} fa-1x treeItem-dwo-icon'></i> <span>{0}</span></div>")
		SafeHtml content(String text, String type);	
	}
	
	private TreeItem getTreeItem(SelectModuleItem item) {
		
		
		switch (item.getType()) {
		default:
		case ROOT:
			return new TreeItem(TEMPLATE.content(item.getName(), "fa-folder"));				
		case SCO:
			return new TreeItem(TEMPLATE.content(item.getName(),  "fa-file"));
		case MODULE:
			return new TreeItem(TEMPLATE.content(item.getName(),  "fa-book"));		
		case FOLDER:
			return new TreeItem(TEMPLATE.content(item.getName(),  "fa-folder"));
			
		
		}		
	}
	
	//================================================================================
    // Load children and Scos
    //================================================================================
	
	private void loadChildren(final SelectModuleItem item) {
		GetChildrenCourses getCoursesCallback = new GetChildrenCourses(item);
		DWOplayer.clientfactory.getRPCHandler().getCourses(item.getID(), getCoursesCallback);
	}
	
	private void loadScos(final SelectModuleItem item) {
		GetChildrenScos getScosCallback = new GetChildrenScos(item);
		DWOplayer.clientfactory.getRPCHandler().getScos(item.getID(), getScosCallback);
	}
	
	class GetChildrenCourses implements AsyncCallback<List<Map<String,Object>>> {

		private SelectModuleItem parent;
		
		public GetChildrenCourses(SelectModuleItem item) {
			parent = item;
		}

		@Override
		public void onFailure(Throwable caught) {
			Window.alert(caught.toString());
		}

		@Override
		public void onSuccess(List<Map<String,Object>> result) {
			ArrayList<SelectModuleItem> items = new ArrayList<SelectModuleItem>(result.size());
			for (Iterator<Map<String, Object>> iterator = result.iterator(); iterator.hasNext();) {
				Map<String, Object> map = (Map<String, Object>) iterator.next();
				SelectModuleItem item = new SelectModuleItem(map, SelectModuleItem.Type.MODULE);
				item.setParent(parent);
				SelectModuleItemHolder.insert(item);
				items.add(item);
			}
			parent.setChildren(items);
			addChildren(items);
			initTree(items, inverseMap.get(parent));
		}
		
	};

	class GetChildrenScos implements AsyncCallback<List<Map<String,Object>>> {

		private SelectModuleItem parent;
		
		public GetChildrenScos(SelectModuleItem item) {
			parent = item;
		}

		@Override
		public void onFailure(Throwable caught) {
			Window.alert(caught.toString());
		}

		@Override
		public void onSuccess(List<Map<String,Object>> result) {
			ArrayList<SelectModuleItem> items = new ArrayList<SelectModuleItem>(result.size());
			for (Iterator<Map<String, Object>> iterator = result.iterator(); iterator.hasNext();) {
				Map<String, Object> map = (Map<String, Object>) iterator.next();
				SelectModuleItem item = new SelectModuleItem(map, SelectModuleItem.Type.SCO);
				item.setParent(parent);
				items.add(item);
				SelectModuleItemHolder.insert(item);
			}
			parent.setChildren(items);
			addChildren(items);
			initTree(items, inverseMap.get(parent));
		}
		
	};
	
	private void addChildren(List<SelectModuleItem> list) {
		if(list == null)
			list = Collections.emptyList();
		this.cellItems = list;
		cells.render(list);
	}


}