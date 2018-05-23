/**
 * 
 */
package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactoryImpl;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem.Type;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;

/**
 * @author peterboon
 *
 */
public class NavigationViewNumworx extends ResizeComposite implements NavigationView {

	class NavCell extends AbstractCell<SelectModuleItem> {

		@Override
		public void render(Context context, SelectModuleItem value,
				SafeHtmlBuilder sb) {
		    Type type = value.getType();
			String clazz = style.navItem();
			if(type == Type.SEPARATOR) clazz = style.navTitle();
			sb.appendHtmlConstant("<div class='" + clazz +"'>");
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
		    Type type = value.getType();
		    if("click".equals(eventType) && type != Type.SEPARATOR) {
		    	GWT.log(value.getName());
		    	presenter.goTo(new TreeModulePlace(value.getID()));
		    	return;
		    }
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}

	}

	class ProvideTileKey implements ProvidesKey<SelectModuleItem> {

		//List<SelectModuleItem> tiles = Collections.emptyList();
		
		@Override
		public Object getKey(SelectModuleItem item) {
			if(item != null)
				return item.getID();
			return null;
		}
		
	}
	ProvideTileKey keyprovider = new ProvideTileKey();

	final class ProvideTreeItems implements Success<List<SelectModuleItem>,List<SelectModuleItem>> {

		private TreeItem parent;
		
		ProvideTreeItems(TreeItem parent) {
			this.parent = parent;
		}
		
		@Override
		public Promise<List<SelectModuleItem>> call(Promise<List<SelectModuleItem>> resolved) throws Exception {
			if(parent != null && parent.getChildCount() == 0)
			{
				initTree(resolved.getValue(), parent, true);
				while((parent = parent.getParentItem()) != null) {
					parent.setState(true);
				}
			}
			return resolved;
		}
		
	}

	
	private static NavigationViewNumworxUiBinder uiBinder = GWT.create(NavigationViewNumworxUiBinder.class);

	interface NavigationViewNumworxUiBinder extends UiBinder<Widget, NavigationViewNumworx> {
	}

	@UiField(provided=true) CellList<SelectModuleItem> cells;
	@UiField Tree tree;
	@UiField FlowPanel deck;
	@UiField TreeModuleViewNumworxCss style;

	GotoController presenter;
	private String SCHOOL_MODULES;
	private TreeItem schoolMap;
	private TreeItem standardMap;
	Map<SelectModuleItem,TreeItem> inverseMap = new HashMap<SelectModuleItem, TreeItem>();
	private Widget root;
	private double width;
	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	  *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 */

	public NavigationViewNumworx() {
		HorizontalCellListResources cellResources;
		cellResources = GWT.create(HorizontalCellListResources.class);
		cells = new CellList<SelectModuleItem>(new NavCell(), cellResources);
		cells.setSelectionModel(new SingleSelectionModel<SelectModuleItem>(keyprovider));
		cells.setKeyboardSelectionPolicy(com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);
		cells.addStyleName(cellResources.cellListStyle().navCellList());
		initWidget(uiBinder.createAndBindUi(this));
		// tree stuff		
		standardMap = new TreeItem(toSafeHTML(Text.constants.standaardModules()));
		standardMap.setState(true);
		standardMap.setUserObject(SelectModuleItem.ROOT);
	}

	public void showCells() {
		cells.setVisible(true);
		tree.setVisible(false);
		width = 192;
		show();
	}
	public void showTree() {
		tree.setVisible(true);
		cells.setVisible(false);
		width = 300;
		show();
	}
	
	@UiHandler("tree")
	public void onSelection(final SelectionEvent<TreeItem> event)
	{
		final TreeItem item = event.getSelectedItem();
		final SelectModuleItem o = (SelectModuleItem) item.getUserObject();
		OpdrNav.defer(
		new ScheduledCommand() {
			public void execute() {
				if (o != null)
					selectItem(o); // send stop event
			}
		});
	}

	void selectItem(SelectModuleItem o) {
		Place place ;
		switch(o.getType()) {
		default:
		case ROOT:
			place = new TreeModulePlace("0");
			break;
		case SCO:
			place = new ViewModulePlace(o.getID());
			break;
		case MODULE:
			//place = new SelectModulePlace(o.getID());
			place = new TreeModulePlace(o.getID());
			break;
		case FOLDER:
			place = new TreeModulePlace(o.getID());
			break;
		}
		presenter.goTo(place);
	}

	void selectModule(SelectModuleItem o) {
		TreeItem item = inverseMap.get(o);
		tree.setSelectedItem(item, false);
	}
	private SafeHtml toSafeHTML(String string) {
		return new SafeHtmlBuilder().
				appendHtmlConstant("<span class='"+style.treeItem()+"'>").
				appendEscaped(string).
				appendHtmlConstant("</span>").
				toSafeHtml();
	}

	private TreeItem getTreeItem(SelectModuleItem item) {
		return new TreeItem(toSafeHTML(item.getName()));
	}
	private void initTree(List<SelectModuleItem> model, TreeItem tree, boolean open) {
//		sort(model);
		tree.removeItems(); // the tree should be empty, but it is not always. NPE HERE, tree = null?
		for (SelectModuleItem item : model)
		{
			TreeItem treeItem = getTreeItem(item);
			treeItem.setUserObject(item);
			inverseMap.put(item, treeItem);
			tree.addItem(treeItem);	
			if(item.getChildren() != null)
				initTree(item.getChildren(), treeItem, false);
		}
		tree.setState(open);
	}

	void initTree(List<SelectModuleItem> list)
	{
		List<SelectModuleItem> model = list;
		ArrayList<SelectModuleItem> schoolModel = new ArrayList<SelectModuleItem>(model.size());
		ArrayList<SelectModuleItem> standardModel = new ArrayList<SelectModuleItem>(model.size());
		Object schoolName = "school";
		if(DWOplayer.withUser() && DWOplayer.clientfactory.getSchool() != null)
			schoolName = DWOplayer.clientfactory.getSchool().getSchoolName();
		SCHOOL_MODULES = Text.constants.schoolModules() + schoolName;
		schoolMap = new TreeItem(toSafeHTML(SCHOOL_MODULES));
		schoolMap.setUserObject(SelectModuleItem.ROOT);
		schoolMap.setState(true);
		standardMap.removeItems();
		tree.removeItems();
		inverseMap.clear();
		for (SelectModuleItem item : model)
		{
			
			TreeItem treeItem = getTreeItem(item);
			treeItem.setUserObject(item);
			inverseMap.put(item, treeItem);
			(item.isFromSchool() ? schoolModel : standardModel).add(item);
			(item.isFromSchool() ? schoolMap : standardMap).addItem(treeItem);
			
			if(item.getChildren() != null)
				initTree(item.getChildren(), treeItem, true);
		}
		if(standardMap.getChildCount() != 0) {
			standardMap.setState(true);
			tree.addItem(standardMap);
		}
		if(schoolMap.getChildCount() != 0) {
			schoolMap.setState(true);
			tree.addItem(schoolMap);
		}
	}

	public void setPresenter(GotoController presenter) {
		this.presenter = presenter;		
	}

	@Override
	public void setDisplay(Widget display) {
		root = display;
	}

	@Override
	public void show() {
		RootLayoutPanel p = RootLayoutPanel.get();
		p.setWidgetVisible(this,true);
		p.setWidgetLeftRight(root, this.width, Unit.PX, 0, Unit.PX);
		p.setWidgetLeftWidth(this, 0, Unit.PX, this.width, Unit.PX);
	}

	@Override
	public void hide() {
		RootLayoutPanel p = RootLayoutPanel.get();
		p.setWidgetVisible(this, false);
		p.setWidgetLeftRight(root, 0, Unit.PX, 0, Unit.PX);
	}

	public void selectItem(SelectModuleItem o, String location) {
		Place place ;
		switch(o.getType()) {
		default:
		case ROOT:
			place = new TreeModulePlace("0");
			break;
		case SCO:
			place = new ViewModulePlace(o.getID(), location);
			break;
		case MODULE:
			//place = new SelectModulePlace(o.getID());
			place = new TreeModulePlace(o.getID());
			break;
		case FOLDER:
			place = new TreeModulePlace(o.getID());
			break;
		}
		presenter.goTo(place);
		
		
	}

}
