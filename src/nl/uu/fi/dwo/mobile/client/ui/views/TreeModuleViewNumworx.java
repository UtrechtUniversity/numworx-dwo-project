package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;

public class TreeModuleViewNumworx extends TreeModuleBase {

	public class NavCell extends AbstractCell<SelectModuleItem> {

		@Override
		public void render(Context context, SelectModuleItem value,
				SafeHtmlBuilder sb) {
			sb.appendHtmlConstant("<div class='numworx-navItem'>");
			sb.appendEscaped(value.getName());
			sb.appendHtmlConstant("</div>");
		}

		public NavCell() {
			super("click");
		}

		@Override
		public void onBrowserEvent(Context context, Element parent, SelectModuleItem value, NativeEvent event,
				ValueUpdater<SelectModuleItem> valueUpdater) {
		    String eventType = event.getType();
		    if("click".equals(eventType)) {
		    	GWT.log(value.getName());
		    	presenter.goTo(new TreeModulePlace(value.getID()));
		    	return;
		    }
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}

	}

	private static TreeModuleViewNumworxUiBinder uiBinder = GWT.create(TreeModuleViewNumworxUiBinder.class);

	@UiField DockLayoutPanel root;
	@UiField(provided=true)
	CellList<SelectModuleItem> cells;
	@UiField HTML title;
	@UiField SimplePanel description;
	private List<SelectModuleItem> list;
	
	interface TreeModuleViewNumworxUiBinder extends UiBinder<Widget, TreeModuleViewNumworx> {
	}

	public TreeModuleViewNumworx() {
		cells = new CellList<SelectModuleItem>(new NavCell());
		initWidget(uiBinder.createAndBindUi(this));
		root.forceLayout();
	}

	@Override
	public void render(List<SelectModuleItem> currentModel) {
		this.list = currentModel;
		cells.setRowData(list);
		cells.redraw();
	}

	@Override
	public void selectModule(SelectModuleItem item) {
		switch(item.getType()) {
		case ROOT:
				title.setText(item.getName());
				description.setWidget(new Label(item.getDescription()));
			break;
		case FOLDER:
				title.setText(item.getName());
				description.setWidget(new Label(item.getDescription()));
			break;
		case MODULE:
				title.setText(item.getName());
				description.setWidget(new Label(item.getDescription()));
			break;
		default:
			
		}
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	Presenter presenter;
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setMenuWidget(IsWidget w) {
		// TODO Auto-generated method stub
		
	}

}
