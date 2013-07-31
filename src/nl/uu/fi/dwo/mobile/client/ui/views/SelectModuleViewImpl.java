package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleCell;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.widget.CellList;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedEvent;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedHandler;
import com.googlecode.mgwt.ui.client.widget.celllist.HasCellSelectedHandler;

/**
 * 
 * @author Danny Hendrix
 * 
 */
public class SelectModuleViewImpl implements SelectModuleView
{
	
	class GetScosCallback implements AsyncCallback<List<Map<String,Object>>> {

		private SelectModuleItem parent;
		
		public GetScosCallback(SelectModuleItem item) {
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
			render(items);
		}
		
	};

	private LayoutPanel main;
	private CellList<SelectModuleItem> list;
	private HeaderButton backbutton;
	private List<SelectModuleItem> items;
	private HeaderPanel header;
	private SimplePanel description;

	public HasTapHandlers getBackBtn() {
		return backbutton;
	}
	
	public SelectModuleViewImpl()
	{
		main = new LayoutPanel();

		header = new HeaderPanel();
		header.setCenter("Selecteer activiteit");
		main.add(header);

		backbutton = new HeaderButton();
		backbutton.setBackButton(true);
		backbutton.setText("Terug");
		header.setLeftWidget(backbutton);
		
		description = new SimplePanel();
		main.add(description);
		
		list = new CellList<SelectModuleItem>(new SelectModuleCell());
		main.add(list);
	}

	HandlerRegistration back,sel;
	
	@Override
	public Widget asWidget()
	{
		return main;
	}

	@Override
	public void render(List<SelectModuleItem> items)
	{
		this.items = items;
		list.render(items);
	}

	public HasCellSelectedHandler getList()
	{
		return list;
	}

	@Override
	public void render(final SelectModuleItem item) {
		GetScosCallback getScosCallback;
		if(item.getChildren() != null)
			render(item.getChildren());
		else
		{
			getScosCallback = new GetScosCallback(item);
			DWOplayer.clientfactory.getRPCHandler().getScos(item.getID(), getScosCallback);
		}
	}

	@Override
	public List<SelectModuleItem> getItems() {
		return items;
	}

	@Override
	public void setDescription(SelectModuleItem item) {
		header.setCenter(item.getName());
		String description = item.getDescription();
		if(description != null)
		{
			if(description.startsWith(DescriptionView.GZIPPREFIX))
			{
				this.description.setWidget(new DescriptionViewImpl(item.getID()));
			} else
			if(description.startsWith("<html>"))
				this.description.setWidget(new HTML(description));
			else
			{
				this.description.setWidget(new Label(description));
			}
		} else
			this.description.setWidget(new Label(""));


	}
	
}
