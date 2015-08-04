package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleCell;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginViewImpl.LoginViewImplUiBinder;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.widget.CellList;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.celllist.HasCellSelectedHandler;

/**
 * 
 * @author Danny Hendrix
 * 
 */
public class SelectModuleViewImpl extends Composite implements SelectModuleView
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


	@UiField (provided=true) CellList<SelectModuleItem> list;
	@UiField HeaderButton backbutton;
	private List<SelectModuleItem> items;
	@UiField HeaderPanel header;
	@UiField SimplePanel description;

	public HasTapHandlers getBackBtn() {
		return backbutton;
	}
	
	private static SelectModuleViewImplUiBinder uiBinder = GWT
			.create(SelectModuleViewImplUiBinder.class);

	interface SelectModuleViewImplUiBinder extends
			UiBinder<Widget, SelectModuleViewImpl> {
	}
	
	public SelectModuleViewImpl()
	{
		list = new CellList<SelectModuleItem>(new SelectModuleCell());
		list.addStyleName(DWOplayer.PARAMETERS.navigationcss().bodyText());
		initWidget(uiBinder.createAndBindUi(this));
	}

	HandlerRegistration back,sel;
	

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
