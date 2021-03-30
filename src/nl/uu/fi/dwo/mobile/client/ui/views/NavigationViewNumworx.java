/**
 * 
 */
package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.Actions;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem.Type;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

/**
 * @author peterboon
 *
 */
@Singleton
public class NavigationViewNumworx extends ResizeComposite implements NavigationView, Comparator<SelectModuleItem> {

	private static final Logger LOG = java.util.logging.Logger.getLogger("NavigationView");

  class NavCell extends AbstractCell<SelectModuleItem> {
		private boolean pointer;
		int x, y;
		final int RADIUS = 20;

		boolean click(String eventType) {
			return "click".equals(eventType) && !pointer || "pointerup".equals(eventType);
		}
		private boolean close(NativeEvent event) {
			int r = Math.abs(x - event.getScreenX()) + Math.abs(y - event.getScreenY());
			return !pointer || r < RADIUS;
		}

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
			super("click", "pointerdown", "pointerup");
		}

		@Override
		public void onBrowserEvent(Context context, Element parent, SelectModuleItem value, NativeEvent event,
				ValueUpdater<SelectModuleItem> valueUpdater) {
		    String eventType = event.getType();
			if("pointerdown".equals(eventType)) {
				pointer = true;
				x = event.getScreenX();
				y = event.getScreenY();
			}
		    Type type = value.getType();
		    if(click(eventType) && type != Type.SEPARATOR && close(event)) {
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

	interface NavigationViewNumworxUiBinder extends UiBinder<DockLayoutPanel, NavigationViewNumworx> {
	}

	@UiField(provided=true) CellList<SelectModuleItem> cells;
	@UiField Tree tree;
	@UiField FlowPanel deck;
	@UiField TreeModuleViewNumworxCss style;
	@UiField FlowPanel beheer;
	@UiField HTML bibliotheek, results, organization, persons, knowledge;
	GotoController presenter;
	final GotoController defaultPresenter;
	private String SCHOOL_MODULES;
	private TreeItem schoolMap;
	private TreeItem standardMap;
	Map<SelectModuleItem,TreeItem> inverseMap = new HashMap<SelectModuleItem, TreeItem>();
	private Widget root;
	private DockLayoutPanel dock;
	private double width;
	private boolean none;
	private RoleType role = RoleType.TEACHER;
    private final DWOplayerParameters PARAMETERS;
    final private DwoGlobalVars vars;
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

	@Inject NavigationViewNumworx(final PlaceController controller, RPCHandler rpc, DWOplayerParameters param, DwoGlobalVars vars) {
	    this.PARAMETERS = param;
	    this.vars = vars;
		HorizontalCellListResources cellResources;
		cellResources = GWT.create(HorizontalCellListResources.class);
		cells = new CellList<SelectModuleItem>(new NavCell(), cellResources);
		cells.setSelectionModel(new SingleSelectionModel<SelectModuleItem>(keyprovider));
		cells.setKeyboardSelectionPolicy(com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);
		cells.addStyleName(cellResources.cellListStyle().navCellList());
		initWidget(dock = uiBinder.createAndBindUi(this));
		// tree stuff
		String standaard = SelectModuleItem.ROOT.getName();
		
		standardMap = new TreeItem(toSafeHTML(standaard, Type.FOLDER));
		
		rpc.getDwoProfile().then(p -> 
			{
				standardMap.setHTML(toSafeHTML(p.getValue().getDwoProfileDescription(), Type.FOLDER));
				return p;
			}
		);
		
		
		standardMap.setState(true);
		standardMap.setUserObject(SelectModuleItem.ROOT);
		none = Actions.isAvailable();
		if(!none) {
		  none = !isTest();
		}
		if(!none) setBeheer(false);
		presenter = defaultPresenter = controller::goTo;
	}

	private boolean isTest() {
		return "test".equals(PARAMETERS.getDwoEnv());
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
	private SafeHtml toSafeHTML(String string, Type type) {
		
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		switch(type) {
		case FOLDER: builder.appendHtmlConstant(treeItemIcon("images/numworx/folder-numworx.svg", "top"));
		    break;
		case MODULE: builder.appendHtmlConstant(treeItemIcon("images/numworx/module-numworx2.svg","-4px"));
			break;
		case SCO: builder.appendHtmlConstant(treeItemIcon("images/numworx/activiteit_numworx2.svg", "-2px"));
		default:
			break;
		}
		SafeHtml html = builder.toSafeHtml(); 
		return new SafeHtmlBuilder().
				appendHtmlConstant("<span class='"+style.treeItem()+"'>").
				append(html).
				appendEscaped(string).
				appendHtmlConstant("</span>").
				toSafeHtml();
	}

	private String treeItemIcon(String string, String align) {
		return "<img style='vertical-align:" + align + "'" 
				+ "width='16' heigth='16' src='" + PARAMETERS.getResource(string) + "' >";
	}

	private TreeItem getTreeItem(SelectModuleItem item) {
		TreeItem treeItem = new TreeItem(toSafeHTML(item.getName(), item.getType()));
//		if (Type.SCO == item.getType()) return treeItem;
//	// try: add / remove item		 XXX werkt voor geen meter!!!
//		TreeItem t = treeItem.addTextItem("");
//		treeItem.removeItem(t);
		return treeItem;
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
		if(vars.withUser() && vars.getSchool() != null)
			schoolName = vars.getSchool().getSchoolName();
		setRole();
		SCHOOL_MODULES = Text.constants.schoolModules() + schoolName;
		schoolMap = new TreeItem(toSafeHTML(SCHOOL_MODULES, Type.FOLDER));
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

	private void setPresenter(GotoController presenter) {
		if (presenter == null) presenter = defaultPresenter;
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

	
//	/*@Inject*/ SecuredUserAccountManager account = new SecuredUserAccountManager();
	
	@UiHandler("results")
	void onResults(ClickEvent e) {
	  if (role != RoleType.TEACHER) return;
	  LOG.info("goto results");
	  if(Actions.isAvailable())
		  Actions.RESULTS.execute();
	}
	@UiHandler("knowledge")
	void onKnowledge(ClickEvent e) {
		if (role == RoleType.STUDENT && vars.isPremium()) {
			LOG.info("goto kennis");
			if (Actions.isAvailable())
				Actions.KNOWLEDGE.execute();
		}
	}
        
    @UiHandler("persons")
    void onPersons(ClickEvent e) {
      LOG.info("goto persons");
      if(Actions.isAvailable())
    	  Actions.PERSONS.execute();
    }
    @UiHandler("classes")
    void onClasses(ClickEvent e) {
      LOG.info("goto classes");
      if(Actions.isAvailable())
    	  Actions.SCHOOLCLASSES.execute();
   }

    @UiHandler("organization")
    void onOrganization(ClickEvent e) {
    	if (role != RoleType.SCHOOLADMIN) return;
    	LOG.info("goto organization");
    	if (Actions.isAvailable())
    		Actions.ORGANISATION.execute();
    }

    private boolean icon;
    public void setBeheer(boolean visible) {
      boolean hidden = !(visible&&icon);
      dock.setWidgetHidden(beheer, hidden);
      
    }
	
//    @UiHandler("bibliotheek") void onModules(ClickEvent e) {
//    	if(Actions.isAvailable())
//    		Actions.showMainNav.execute();
//    	showIcon(false);
//    }

    @Override
    public void showIcon(boolean show) {
    	this.icon = show;
    	setBeheer(show);
    	//bibliotheek.setStyleName("modules-icon", show);
    }

	void setRole(RoleType role) {
		this.role = role;
		// if visible?
		{  	organization.setVisible(role == RoleType.SCHOOLADMIN);
			results.setVisible(role == RoleType.TEACHER); // or student if premium&test.
			persons.setVisible(role != RoleType.STUDENT);
			knowledge.setVisible((role == RoleType.STUDENT && isTest() && vars.isPremium() && vars.getCurrentSchoolClass() != null));
		}
	}

	void setRole() {
		setRole(vars.getRoleType());
	}

	public void setCells(List<SelectModuleItem> items) {
		cells.setRowData(massage(items));
		cells.redraw();
	}

	private List<SelectModuleItem> massage(List<SelectModuleItem> list) {
		if(list == null)
			list = Collections.emptyList();
		int len = list.size();
		if(len > 2) {
			Collections.sort(list, this);
			SelectModuleItem first = list.get(0);
			SelectModuleItem last  = list.get(len-1);
			if( first.isFromSchool() != last.isFromSchool()) 
			{
				list = new ArrayList<SelectModuleItem>(list);
				while(len > 0 && (first.isFromSchool() != last.isFromSchool())) {
					len --;
					last  = list.get(len-1);
				}
				SelectModuleItem separator = new SelectModuleItem(null, SelectModuleItem.Type.SEPARATOR);

				Object schoolName = "school";
				if(vars.withUser() && vars.getSchool() != null)
					schoolName = vars.getSchool().getSchoolName();
				String SCHOOL_MODULES = Text.constants.schoolModules() + schoolName;

				separator.setName(SCHOOL_MODULES);
				list.add(len, separator);
			}
			
		}
		return list;
	}

	public int compare(SelectModuleItem o1, SelectModuleItem o2) {
		boolean b1 = o1.isFromSchool();
		boolean b2 = o2.isFromSchool();
		if(b1 != b2) {
			return Boolean.compare(b1, b2);
		}
		
//		if (o1.getType()== SelectModuleItem.Type.SCO & o2.getType() == SelectModuleItem.Type.SCO)
		return Integer.signum(o1.getSequencenr()-o2.getSequencenr());

//		if(sortModel != null)
//			return sortModel.compare(o1, o2);
//		else
//			return o1.getName().compareTo(o2.getName()); // FIXME NIET GOED
	}

}
