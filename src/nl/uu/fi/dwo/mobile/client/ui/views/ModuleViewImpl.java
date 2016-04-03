package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.List;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleCell;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorView.AnchorContext;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.CellList;
import com.googlecode.mgwt.ui.client.widget.celllist.HasCellSelectedHandler;

class ModuleViewImpl extends Composite implements ModuleView {

	@UiField (provided=true) CellList<SelectModuleItem> list;
	@UiField SimplePanel description;
	List<SelectModuleItem> items;
	AnchorContext context;

	public ModuleViewImpl() {
		list = new CellList<SelectModuleItem>(new SelectModuleCell());
		list.addStyleName(DWOplayer.PARAMETERS.navigationcss().bodyText());
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setAnchorContext(AnchorContext c) {
		context = c;
	}
	
	private static ModuleViewImplUiBinder uiBinder = GWT
			.create(ModuleViewImplUiBinder.class);

	interface ModuleViewImplUiBinder extends
			UiBinder<Widget, ModuleViewImpl> {
	}

	public void setDescription(IsWidget widget) {
		widget.asWidget().addStyleDependentName("moduleArea");
		description.setWidget(widget);
	}
	
	public void setDescription(SelectModuleItem item) {
		String description = item.getDescription();
		if(description != null)
		{
			if(description.startsWith(DescriptionView.GZIPPREFIX))
			{
				setDescription(new DescriptionViewImpl(item.getID(), context));
			} else
			if(description.startsWith("<html>"))
				setDescription(new HTML(description));
			else
			{
				setDescription(new Label(description));
			}
		} else
			setDescription(new Label(""));
	}
	
	public void render(List<SelectModuleItem> items)
	{
		this.items = items;
		list.render(items);
	}

	public HasCellSelectedHandler getList()
	{
		return list;
	}

}
