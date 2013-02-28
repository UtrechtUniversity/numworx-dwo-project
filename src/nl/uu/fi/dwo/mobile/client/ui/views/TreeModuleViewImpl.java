package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.List;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;

public class TreeModuleViewImpl extends Composite implements TreeModuleView, SelectionHandler<TreeItem>
{

	private HeaderButton backButton;
	private HeaderPanel headerPanel;
	private List<SelectModuleItem> model;
	private VerticalPanel main;
	private SplitLayoutPanel split;
	private SimplePanel container;
	private Tree tree;

	public TreeModuleViewImpl()
	{
		headerPanel = new HeaderPanel();
		backButton = new HeaderButton();
		backButton.setBackButton(true);
		backButton.setText("Terug");
		headerPanel.setLeftWidget(backButton);
		headerPanel.setCenter("Modules");

		main = new VerticalPanel();
		main.add(headerPanel);

		main.setHeight("100%");
		main.setWidth("100%");

		split = new SplitLayoutPanel();
		split.setWidth("100%");
		split.setHeight("99%");
		tree = new Tree();
		tree.addSelectionHandler(this);
		split.addWest(tree, 200);
		container = new SimplePanel();
		split.add(container);
		main.add(split);
		initWidget(main);
	}

	@Override
	public void render(List<SelectModuleItem> currentModel)
	{
		if (currentModel.isEmpty())
			return;
		if (model != currentModel)
		{
			tree.removeItems();
			model = currentModel;
			initTree();
		}

	}

	private void initTree()
	{
		TreeItem last = null;
		for (SelectModuleItem item : model)
		{
			TreeItem treeItem = new TreeItem(new SafeHtmlBuilder().appendEscaped(item.getName()).toSafeHtml());
			treeItem.setUserObject(item);
			Widget x = treeItem.getWidget();
			if (last == null)
			{
				tree.addItem(treeItem);
				if (tree.getItemCount() > 3)
					last = treeItem;
			}
			else
			{
				last.addItem(treeItem);
				if (last.getChildCount() > 3)
					last = treeItem;
			}
		}

	}

	@Override
	public void selectModule(SelectModuleItem item)
	{
		if (item != null)
			container.setWidget(new Label("contents of " + item.getName()));
		else
			container.setWidget(new Label("EMPTY"));
	}

	@Override
	public HeaderButton getBackButton()
	{
		return backButton;
	}

	@Override
	public void onSelection(SelectionEvent<TreeItem> event)
	{
		TreeItem item = event.getSelectedItem();
		SelectModuleItem o = (SelectModuleItem) item.getUserObject();
		GWT.log(o.toString());
		//selectModule(o); // push o on the stack...
		DWOplayer.clientfactory.getPlaceController().goTo(new TreeModulePlace(o.getID()));
	}
}
