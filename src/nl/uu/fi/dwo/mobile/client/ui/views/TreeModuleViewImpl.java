package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleCell;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.widget.CellList;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedEvent;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedHandler;

public class TreeModuleViewImpl implements TreeModuleView, SelectionHandler<TreeItem>, CellSelectedHandler
{

	private HeaderButton backButton;
	private HeaderPanel headerPanel;
	private List<SelectModuleItem> model;
	private LayoutPanel main;
	private SplitLayoutPanel split;
	private SimplePanel container;
	private VerticalPanel children;
	private CellList<SelectModuleItem> cells;
	private Tree tree;
	private HashMap<SelectModuleItem, TreeItem> inverseMap = new HashMap<SelectModuleItem, TreeItem>();
	private List<SelectModuleItem> cellItems;
	protected Presenter presenter;

	public TreeModuleViewImpl()
	{
		headerPanel = new HeaderPanel();
		backButton = new HeaderButton();
		backButton.setBackButton(true);
		backButton.setText("Terug");
		headerPanel.setLeftWidget(backButton);
		headerPanel.setCenter("Modules");

		main = new LayoutPanel();
		main.add(headerPanel);

		//main.setHeight("100%");
		//main.setWidth("100%");

		split = new SplitLayoutPanel();
		split.setWidth("100%");
		split.setHeight("99%");
		tree = new Tree();
		tree.addSelectionHandler(this);
		split.addWest(tree, 200);
		container = new SimplePanel();
		children = new VerticalPanel();
		split.add(children);
		children.add(container);
		cells = new CellList<SelectModuleItem>(new SelectModuleCell());
		cells.addCellSelectedHandler(this);
		children.add(cells);
		main.add(split);
		
		backButton.addTapHandler(new TapHandler()
		{

			@Override
			public void onTap(TapEvent event)
			{
				presenter.back();
			}
		});

	}

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

	private void initTree()
	{
		for (SelectModuleItem item : model)
		{
			TreeItem treeItem = new TreeItem(new SafeHtmlBuilder().appendEscaped(item.getName()).toSafeHtml());
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
			TreeItem treeItem = new TreeItem(new SafeHtmlBuilder().appendEscaped(item.getName()).toSafeHtml());
			treeItem.setUserObject(item);
			inverseMap.put(item, treeItem);
			tree.addItem(treeItem);
			if(item.getChildren() != null)
				initTree(item.getChildren(), treeItem);
		}
	}

	@Override
	public void selectModule(SelectModuleItem item)
	{
		if (item != null)
		{
			headerPanel.setCenter(item.getName());
			String description = item.getDescription();
			if(description != null)
			{
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
			}	
			
		}
		else
		{
			container.setWidget(new Label("DWO standaard modules")); // Uit het profiel halen!
			addChildren(model);
		}
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
	
	
	private void loadChildren(final SelectModuleItem item) {
		GetChildrenCourses getCoursesCallback = new GetChildrenCourses(item);
		DWOplayer.clientfactory.getRPCHandler().getCourses(item.getID(), getCoursesCallback);
	}
	
	private void loadScos(final SelectModuleItem item) {
		GetChildrenScos getScosCallback = new GetChildrenScos(item);
		DWOplayer.clientfactory.getRPCHandler().getScos(item.getID(), getScosCallback);
	}

	private void addChildren(List<SelectModuleItem> list) {
		this.cellItems = list;
		cells.render(list);
	}

	@Override
	public void onSelection(SelectionEvent<TreeItem> event)
	{
		TreeItem item = event.getSelectedItem();
		SelectModuleItem o = (SelectModuleItem) item.getUserObject();
		onSelection(o);
	}


	private void onSelection(SelectModuleItem o) {
		presenter.selectItem(o);
	}

	@Override
	public void onCellSelected(CellSelectedEvent event) {
		int index = event.getIndex();
		onSelection(cellItems.get(index));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public Widget asWidget() {
		return main;
	}
}
