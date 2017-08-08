package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SetSelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.googlecode.mgwt.ui.client.MGWT;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.account.client.ProfileCommand;
import nl.uu.fi.dwo.account.client.SchoolClassStudentCommand;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.SCO_TO_MODULEITEM;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem.Type;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.WaitScreen;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ReloginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.SearchPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorView.AnchorContext;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleViewImplDesktop.TreeAnchorContext;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

public class TreeModuleViewNumworx extends TreeModuleBase implements AnchorContext, Command, Comparator<SelectModuleItem> {

	class ProvideTileKey implements ProvidesKey<SelectModuleItem> {

		List<SelectModuleItem> tiles = Collections.emptyList();
		
		@Override
		public Object getKey(SelectModuleItem item) {
			if(item != null)
				return item.getID();
			return null;
		}
		
	}
	ProvideTileKey keyprovider = new ProvideTileKey();
	
	final class ProvideCells implements Success<List<SelectModuleItem>, Void> {
		@Override
		public Promise<Void> call(
				Promise<List<SelectModuleItem>> resolved)
				throws Exception {
			List<SelectModuleItem> value = resolved.getValue();
			((SetSelectionModel<?>) tiles.getSelectionModel()).clear();
			keyprovider.tiles = value;
			tiles.setRowData(value);
			tiles.redraw();
			return null;
		}
	}
	
	final class ProvideTreeItems implements Success<List<SelectModuleItem>,List<SelectModuleItem>> {

		private TreeItem parent;
		
		ProvideTreeItems(TreeItem parent) {
			this.parent = parent;
		}
		
		@Override
		public Promise<List<SelectModuleItem>> call(Promise<List<SelectModuleItem>> resolved) throws Exception {
			if(parent != null)
			{
				initTree(resolved.getValue(), parent, true);
				while((parent = parent.getParentItem()) != null) {
					parent.setState(true);
				}
			}
			return resolved;
		}
		
	}

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

	//int flip;

	class TileCell extends AbstractCell<SelectModuleItem> {

		@Override
		public void render(Context context,
				SelectModuleItem value, SafeHtmlBuilder sb) {
/* 					<g:FlowPanel styleName='{style.tile}' >
						<g:HTML styleName='{style.tileHeader}'>Exponentiele functies</g:HTML>
						<g:HTML styleName='{style.tileBody}'>TILE BODY</g:HTML>
						<g:FlowPanel styleName='{style.tileFooter}'>
							<g:InlineHTML styleName='{style.tileResult}'><span class='fa-stack fa-lg'><i class='fa fa-circle fa-stack-1x' style='color:red;'></i><i class='fa fa-times fa-stack-1x' style='color:white;'></i></span></g:InlineHTML>
							<g:InlineHTML styleName='{style.tileScore}'>10%</g:InlineHTML>
							<g:InlineHTML styleName='{style.tileType}'><i class='fa fa-file-text-o'></i></g:InlineHTML>
						</g:FlowPanel>					
					</g:FlowPanel>
*/			
			sb.appendHtmlConstant("<div class='"+style.tile()+"'>");
			  sb.appendHtmlConstant("<div class='" + style.tileHeader() + "'><span class='" + style.tileSpan() + "'>");
			    sb.appendEscaped(value.getName());
			  sb.appendHtmlConstant("</span></div>");

			  sb.appendHtmlConstant("<div class='" + style.tileBody() + "'>");
			    String description = value.getDescription();
			    Type typeof = value.getType();
				if(true || description.isEmpty()||description.startsWith(DescriptionView.GZIPPREFIX)) {
					//flip = ( flip  ) % 5+1;
					sb.appendHtmlConstant("<span class='" + style.tileBodySpan() + "'>");
					switch(typeof) {
			    	case MODULE:
			    		if(value.getImage() != null) {
			    			String html = "<img src='"+value.getImage()+"' class='"+style.tileBodyImg() + "'/>";
			    			sb.appendHtmlConstant(html);
			    		} else {
			    			sb.appendHtmlConstant("<img style='height: 85px' src='"
			    				+ r("images/numworx/module-numworx.svg")
			    				+ "' class='" + style.tileBodyImg()
			    				+ "'/>");
			    		}
			    		break;
			    	case FOLDER:
			    		if(value.getImage() != null) {
			    			String html = "<img src='"+value.getImage()+"' class='"+style.tileBodyImg() + "'/>";
			    			sb.appendHtmlConstant(html);
			    		} else {
			    			sb.appendHtmlConstant("<img style='height: 85px' src='"
			    				+ r("images/numworx/folder-numworx.svg")
			    				+ "' class='" + style.tileBodyImg()
			    				+ "' />");
			    		}
			    		break;
			    	case SCO:
//			    		if(flip != 1)
//			    		sb.appendHtmlConstant("<img style='margin: auto auto' src='"
//			    				+ r("images/courses/"
//			    						+ flip
//			    						+ ".png")
//			    				+ "' class='" + style.tileBodyImg()
//			    				+ "' />");
//			    		else 
			    			sb.appendHtmlConstant("<img style='height: 85px' src='"
			    				+ r("images/numworx/activiteit_numworx.svg")
			    				+ "' class='" + style.tileBodyImg()
			    				+ "' />");
			    		break;
			    	default:
			    	}
			    	sb.appendHtmlConstant("</span>");
			    	
			    } else {
			    	if(description.startsWith("<html"))
			    		sb.appendHtmlConstant(description);
			    	else
			    		sb.appendEscaped(description);
			    }
			    sb.appendHtmlConstant("</div>");

			  sb.appendHtmlConstant("<div class='" + style.tileFooter() + "'>");
			  	sb.appendHtmlConstant("<span class='"+style.tileResult()+ "'>");
			  	String type;
			  	if(value.isShowScore() && typeof == Type.SCO) {
			  		int score = value.getScore().intValue();
			  		if(score < 20) type = "fout";
			  		else if(score >=65) type = "goed";
			  		else type = "half";
			  	} else {
			  		type = "geen-score";
			  	}
			  	if(typeof == Type.SCO || typeof == Type.MODULE)
			  	sb.appendHtmlConstant("<img src='"+r("images/numworx/"+type+"-numworx.svg")+"' />");
			  	sb.appendHtmlConstant("</span>");
			  	if(value.isShowScore()) {
			  		sb.appendHtmlConstant("<span class='"+style.tileScore()+ "'>");
			  		sb.append(value.getScore().intValue()); sb.appendEscaped("%");
			  		sb.appendHtmlConstant("</span>");
			  	}
			  	if(typeof == Type.SCO)
			  	{
				  	sb.appendHtmlConstant("<span class='"+style.tileType()+ "'>");
				  	String lesstof = "lesstof";
				  	if(value.getName().contains("oets"))
				  		lesstof = "zelftoets";
					sb.appendHtmlConstant("<img height='18' src='"+r("images/numworx/"
				  			+ lesstof
				  			+ "-numworx.svg")+"'/>");
				  	sb.appendHtmlConstant("</span>");
			  	}	
			  sb.appendHtmlConstant("</div>");
			  if(!description.isEmpty())
			  sb.appendHtmlConstant("<div class='" + style.tileInfo()
			  		+ "'>"
			  		+ "<img height='18' src='"+r("images/numworx/"
				  			+ "info"
				  			+ "-numworx.svg")+"'/>"
			  		+ "</div>");
		    sb.appendHtmlConstant("</div>");
			  
			  
			
		}
	
		public TileCell() {
			super("click");
		}

		@Override
		public void onBrowserEvent(Context context, Element parent, SelectModuleItem value, NativeEvent event,
				ValueUpdater<SelectModuleItem> valueUpdater) {
		    String eventType = event.getType();
		    
		    if("click".equals(eventType)) {
		    	EventTarget eventTarget = event.getEventTarget();
		    	Element e = null;
		    	if(Element.is(eventTarget)) {
		    		e = Element.as(eventTarget);
		    		while ( e != null && e != parent && !style.tileInfo().equals(e.getClassName()))
		    			e = e.getParentElement();
		    	}
		    	if ( e.getClassName().equals(style.tileInfo()))
		    	{ final PopupPanel popup = new PopupPanel(true, true);
		    		popup.setStyleName(style.popup());
		    		popup.setGlassEnabled(false);
		    		popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
		            public void setPosition(int offsetWidth, int offsetHeight) {
		                int left = (Window.getClientWidth() - offsetWidth) / 4;
		                int top = (Window.getClientHeight() - offsetHeight) / 4;
		                popup.setPopupPosition(left, top);
		              }
		            });
		    		InfoPanel info = new InfoPanel(popup);
		    		info.setName(value.getName());
		    		info.setDescription(getLabel(value));
					popup.setWidget(info);
		    		popup.show();
		    		return;
		    	}
		    }
		    
		    
		    if("click".equals(eventType)) {
		    	Place place;
		    	if(value.getType() == Type.SCO)
		    		place = new ViewModulePlace(value.getID());
		    	else
		    		place = new TreeModulePlace(value.getID());
				presenter.goTo(place);
		    	return;
		    }
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}
		
		
	}
	

	private static TreeModuleViewNumworxUiBinder uiBinder = GWT.create(TreeModuleViewNumworxUiBinder.class);

	@UiField DockLayoutPanel root;
	@UiField(provided=true)
	CellList<SelectModuleItem> cells;
	@UiField
	Tree tree;
	@UiField(provided=true)
	CellList<SelectModuleItem> tiles;
	@UiField HTML title;
	@UiField SimplePanel description;
	@UiField Image favIcon;
	@UiField TreeModuleViewNumworxCss style;
	@UiField FocusPanel homeBtn, upBtn;
	@UiField InlineHTML searchBtn;
	@UiField TextBox searchInput;
	//@UiField ToggleButton fullBtn;
	@UiField Label loginLabel;
	@UiField FlowPanel centerPanel;
	@UiField DockLayoutPanel westPanel;
	
	@UiHandler("homeBtn")
	void onHomeBtn(ClickEvent ev) {
		presenter.goTo(new TreeModulePlace());
	}
	
	Object upId = "0";
	@UiHandler("upBtn")
	void onUpBtn(ClickEvent ev) {
		Object parent = upId;
		if (parent == null) parent = "0"; // wrong place?
		presenter.goTo(new TreeModulePlace(parent));
	}
	
	
	@UiHandler("searchBtn")
	void onSearch(ClickEvent ev) {
		long id = System.currentTimeMillis();
		String search = searchInput.getText().trim();
		SelectModuleItem item = SelectModuleItemHolder.getSearch(search);
		if(item == null)
		{
			item = new SelectModuleItem(id, SelectModuleItem.Type.SEARCH);
			item.setName(search);
			item.setDescription("Nog geen resultaat..."); // XXX wat komt hier....
// FIXME hier de zoek functie....
			Promise<List<SelectModuleItem>> searchMock = searchMock(search);			
			final SelectModuleItem i = item;
			item.setChildrenAsync(searchMock.then(new Success<List<SelectModuleItem>,List<SelectModuleItem>>(){

				@Override
				public Promise<List<SelectModuleItem>> call(Promise<List<SelectModuleItem>> resolved) throws Exception {
					i.setDescription("Aantal resultaten: " + resolved.getValue().size());
					return resolved;
				}})
				.recover(new Function<Promise<?>, List<SelectModuleItem>>(){

					@Override
					public List<SelectModuleItem> apply(Promise<?> t) {
						i.setDescription("Geen resultaat: " + t.getFailure().getMessage());
						return Collections.emptyList();
					}})	
					
					);
			SelectModuleItemHolder.insert(item);
		}
		presenter.goTo(new SearchPlace(item.getID()));
	}

	private Promise<List<SelectModuleItem>> searchMock(String search) {
		if(search.contains("error")) {
			return Promises.failed(new IllegalArgumentException(search));
		}
		search = search.toLowerCase();
		List<SelectModuleItem> list = new ArrayList<>();
		List<SelectModuleItem> items = SelectModuleItemHolder.getItems();
		search(search, items, list);
		return Promises.resolved(list);
	}
	
	private void search(String search, List<SelectModuleItem> items, List<SelectModuleItem> list) {
		for(SelectModuleItem item: items) {
			String name = item.getName();
			String description = item.getDescription();
			if( (name + description).toLowerCase().contains(search)) {
				list.add(item);
			} else {
				Promise<List<SelectModuleItem>> p = item.getChildrenAsync();
				if(p != null && p.isDone() && p.getFailure() == null) {
					search(search, p.getValue(), list);
				}
			}
		}
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
				else {
//					close();
//					container.setWidget(module);
//					SelectModuleItem root = SelectModuleItemHolder
//							.getItemByID("0");
//					module.setDescription(root); // Uit het profiel halen!
//					if (item == schoolMap) {
//						addChildren(schoolModel);
//						moduleHeaderLabel.setText(SCHOOL_MODULES);
//						tree.setSelectedItem(schoolMap, false);
//						schoolMap.setState(true, false);
//					} else if (item == standardMap) {
//						addChildren(standardModel);
//						moduleHeaderLabel.setText(root.getName());
//						tree.setSelectedItem(standardMap, false);
//						standardMap.setState(true, false);
//					}
				}
			}
		});
	}
	
	private void selectItem(SelectModuleItem o) {
		Place place = null;
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
		
		if (place != null) {
			this.presenter.goTo(place);
		}
	}
	
//	@UiHandler("fullBtn")
//	void onFull(ClickEvent ev) {
//		System.err.println("Full = "+fullBtn.getValue());
//		if(fullBtn.getValue())
//			gwtfullscreen.Fullscreen.requestFullscreen(true);
//		else 
//			gwtfullscreen.Fullscreen.exitFullscreen();
//	}
	
	private List<SelectModuleItem> list;

	@UiField(provided=true) String pfx;

	private MenuBar items = new MenuBar(true);

	@UiField(provided=true)
	MenuItem user;

	private TreeItem standardMap;
	
	static String getFaviconUrl() {
		return "url('"+
				r("images/numworx/favicon-numworx-wit.svg") +
				"')";	
	}

	static String getFolderUrl() {
		return "url('"+
				r("images/numworx/folder-wit-numworx.svg") +
				"')";	
	}
	
	private static String r(String string) {
		return DWOplayer.PARAMETERS.getResource(string);
	}

	interface TreeModuleViewNumworxUiBinder extends UiBinder<Widget, TreeModuleViewNumworx> {
	}

	public TreeModuleViewNumworx() {
		HorizontalCellListResources cellResources;
		cellResources = GWT.create(HorizontalCellListResources.class);
		cells = new CellList<SelectModuleItem>(new NavCell(), cellResources);
		cells.setSelectionModel(new SingleSelectionModel<SelectModuleItem>(keyprovider));
		cells.setKeyboardSelectionPolicy(com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);
		cells.addStyleName(cellResources.cellListStyle().navCellList());
		tiles = new CellList<SelectModuleItem>(new TileCell(), cellResources);
		tiles.setKeyboardSelectionPolicy(com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);
		tiles.addStyleName(cellResources.cellListStyle().tileCellList());
		SingleSelectionModel<SelectModuleItem> model = new SingleSelectionModel<SelectModuleItem>(keyprovider);
		tiles.setSelectionModel(model);
		pfx = r("");
        final int correctie = 10; // width popup 
		user = new MenuItem("<i class='fa fa-caret-down fa-2x'></i>", true, items) {
            @Override
            public int getAbsoluteLeft() {
                int w1 = items.getOffsetWidth();
                int w2 = this.getOffsetWidth();
                return super.getAbsoluteLeft() - w1 + w2 - correctie;
            }
		};
		
		initWidget(uiBinder.createAndBindUi(this));
		searchInput.getElement().setPropertyString("placeholder", "Zoek toets of lesstof");
		root.forceLayout();
// tree stuff		
		standardMap = new TreeItem(toSafeHTML(Text.constants.standaardModules()));
		standardMap.setState(true);
// Strategy stuff desktop/tablet
		final boolean desktop = MGWT.getOsDetection().isDesktop() /*&& false*/;
		navigation = desktop ? new TreeNavStrategy() : new ListNavStrategy();
	}

	interface NavStrategy {
		
	}
	
	NavStrategy navigation;

	
	class ListNavStrategy implements NavStrategy {
		ListNavStrategy() {
			tree.removeFromParent();
		}
	}
	
	class TreeNavStrategy implements NavStrategy {
		TreeNavStrategy() {
			cells.removeFromParent();
			root.setWidgetSize(westPanel, 300);
		}
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
				if(DWOplayer.withUser() && DWOplayer.clientfactory.getSchool() != null)
					schoolName = DWOplayer.clientfactory.getSchool().getSchoolName();
				String SCHOOL_MODULES = Text.constants.schoolModules() + schoolName;

				separator.setName(SCHOOL_MODULES);
				list.add(len, separator);
			}
			
		}
		return list;
	}

	
	@Override
	public void render(List<SelectModuleItem> currentModel) {
		
		
		boolean nieuw = this.list != currentModel;
		
		
		
		this.list = currentModel;
		cells.setRowData(massage(list));
		cells.redraw();
		String login = DWOplayer.withUser()? DwoGlobalVars.instance().getCurrentUser().getDisplayName() : "GUEST";
		loginLabel.setText(login);
		
//		fullBtn.setValue(gwtfullscreen.Fullscreen.isFullscreen(), false);

		items.clearItems();
		MenuItem m;
		if(DWOplayer.withUser()) {
			m=items.addItem(DwoLocalesForGWT.instance.GUI_MyProfile(), new ProfileCommand());
			m.addStyleName(style.menuItem());
			if(DWOplayer.clientfactory.getRoleType() == RoleType.STUDENT) {
				ScheduledCommand cmd = new SchoolClassStudentCommand(this);
				m=items.addItem(DwoLocalesForGWT.instance.GUI_MySchoolClasses(), cmd);
				m.addStyleName(style.menuItem());
			}
			
			m=items.addItem("Logout", new ScheduledCommand() {
				
				@Override
				public void execute() {
					presenter.goTo(new LoginPlace());					
				}
			});
		} else {
			m=items.addItem("Aanmelden", new ScheduledCommand() {
				
				@Override
				public void execute() {
					presenter.goTo(new LoginPlace());
				}
			});
		}
		m.addStyleName(style.menuItem());
// Slow get all stuff;
		final Iterator<SelectModuleItem> iterator = currentModel.iterator();
		final ScheduledCommand cmd = (new ScheduledCommand() {
			ScheduledCommand cmd = this;
			@Override
			public void execute() {
				if(iterator.hasNext()) {
					SelectModuleItem item = iterator.next();
					if(item.getType() == SelectModuleItem.Type.FOLDER)
						getChildrenPromise(item).onResolve(new Runnable() {

							@Override
							public void run() {
								Scheduler.get().scheduleDeferred(cmd);
							}});
				}
			}
		});
		//Scheduler.get().scheduleDeferred(cmd);
		
		if(nieuw)
			initTree();
	}

	Promise<List<SelectModuleItem>> getChildrenPromise(final SelectModuleItem parent) {
		Promise<List<SelectModuleItem>> promise = parent.getChildrenAsync();

		if(promise == null || (promise.isDone() && promise.getFailure() != null)) {
			promise = DWOplayer.clientfactory.getRPCHandler().getCourses(parent.getID())
					.map(new COURSE_TO_MODULEITEM(parent));
			parent.setChildrenAsync(promise);
			promise
			.then(new Success<List<SelectModuleItem>, List<SelectModuleItem>>() {

				@Override
				public Promise<List<SelectModuleItem>> call(Promise<List<SelectModuleItem>> resolved) throws Exception {
					for(SelectModuleItem item: resolved.getValue()) {
						if(item.getType() == SelectModuleItem.Type.FOLDER) {
							if(item.getChildrenAsync() == null) {
								item.setChildrenAsync(DWOplayer.clientfactory.getRPCHandler().getCourses(item.getID())
										.map(new COURSE_TO_MODULEITEM(item)));
							}
						}
					}	
					return resolved;
				}}, new Failure() {
					
					@Override
					public void fail(Promise<?> resolved) throws Exception {
						Window.alert(resolved.getFailure().toString());
						parent.setChildrenAsync(null);
					}
				});
		}
		return promise;
	}
	Promise<List<SelectModuleItem>> getScosPromise(final SelectModuleItem parent) {
		Promise<List<SelectModuleItem>> promise = parent.getChildrenAsync();

		if(promise == null || (promise.isDone() && promise.getFailure() != null)) {
			promise = DWOplayer.clientfactory.getRPCHandler().getScos(parent.getID())
					.map(new SCO_TO_MODULEITEM(parent));
			parent.setChildrenAsync(promise);
			promise
			.then(null, new Failure() {
					
					@Override
					public void fail(Promise<?> resolved) throws Exception {
						Window.alert(resolved.getFailure().toString());
						parent.setChildrenAsync(null);
					}
				});
		}
		return promise;
	}
	
	
	@Override
	public void selectModule(SelectModuleItem item) {
		((SetSelectionModel<?>) tiles.getSelectionModel()).clear();
		keyprovider.tiles = Collections.emptyList();
		tiles.setRowData(keyprovider.tiles);
		boolean hasImage = item.getImage() != null;
		switch(item.getType()) {
		case ROOT:
			upId=null;
			title.setText(item.getName());
			description.setWidget(getLabel(item));
//String AanJolanda = "Hallo Jolanda van den Berg, welkom bij Numworx!";
//				title.setText(AanJolanda);
//String Jolanda =
//"Numworkx helpt je met wiskunde en rekenen. Werken met formules lastig op een computer? Schrijf de fomule op je tablet en Numworx"
//+ " zet je handschrift om in echte wiskudige invoer. Met slepen en swipen maak je grafieken. Opgaven los je in stappen op, waarbij"
//+ "Numworx steeds vertelt wat je wel en niet goed doet.<br><br>"
//+ "Je docent biedt les- en oefenmateriaal precies op maat aan, zodat je gericht kunt werken aan de onderwerpen die belangrijk voor"
//+ "jou zijn. Dat materiaal vind je in de map in het <span style='color: #1b75BB;font-family:inherit'>linkermenu</span>.<br><br>"
//+ "In de bibliotheek van Numworx is het lesmateriaal georganiseerd in schooltypen en"
//+ " <span style='color:#1b75BB;font-family:inherit'>mappen</span>. In de mappen vind je "
//+ "<span style='color:#1b75BB;font-family:inherit'>modules</span> en daarbinnen de <span style='color:#1b75BB;font-family:inherit'>activiteiten</span>. "
//+ "Activiteiten zijn afgeronde stukken lesstof of toetsen over een bepaald onderwerp. Je kunt in alle mappen kijken en werken.<br>"
//+ "Klik op een keuze in het menu aan de linkerkant om te beginnen.";
//			Widget w = new HTML(Jolanda);
//			w.getElement().getStyle().setFontSize(18, Style.Unit.PX);
//			w.getElement().getStyle().setProperty("fontFamily", "Ubuntu");
//			w.getElement().getStyle().setLineHeight(27, Style.Unit.PX);
//			description.setWidget(w);
				favIcon.setVisible(false);
				centerPanel.setStyleName(style.centerBackground(), true);
				centerPanel.setStyleName(style.folderBackground(), false);
				((SetSelectionModel<?>) cells.getSelectionModel()).clear();
			break;
		case SEARCH:
			((SetSelectionModel<?>) cells.getSelectionModel()).clear();
		case FOLDER:
				title.setText(item.getName());
				String url = (hasImage) ? item.getImage(): r("images/courses/2.png");
				favIcon.setUrl(url);
//				flip = (flip%5)+1; //flip=1;
				favIcon.setVisible( (hasImage /*|| flip!=1*/) && isLabel(item));
				centerPanel.setStyleName(style.folderBackground(), !hasImage/* && flip==1*/);
				centerPanel.setStyleName(style.centerBackground(), false);
				description.setWidget(getLabel(item));
				if(item.showChildren());
				{	TreeItem parent = inverseMap.get(item);
					getChildrenPromise(item)
					.then(new ProvideTreeItems(parent))
					.then(new ProvideCells());
				}	

				favIcon.getParent().setStyleName(style.faviconOFF(), !isLabel(item));
				title.getParent().setStyleName(style.titlePanelFULL(), !isLabel(item));
				upId=item.getParentID();
			break;
		case MODULE:
				title.setText(item.getName());
				description.setWidget(getLabel(item));
				url = (hasImage) ? item.getImage(): r("images/courses/1.png");
				favIcon.setUrl(url);
				favIcon.setVisible(hasImage);
				centerPanel.setStyleName(style.centerBackground(), false);
				centerPanel.setStyleName(style.folderBackground(), !hasImage);
				if(item.showChildren());
				{	TreeItem parent = inverseMap.get(item);
					getScosPromise(item)
					.then(new ProvideTreeItems(parent))
					.then(new ProvideCells());
				}
				favIcon.getParent().setStyleName(style.faviconOFF(), !isLabel(item));
				title.getParent().setStyleName(style.titlePanelFULL(), !isLabel(item));
				upId = item.getParentID();
			break;
		default:
			
		}
		tiles.redraw();
	}

	private boolean isLabel(SelectModuleItem item) {
		String description = item.getDescription();
		if(description.startsWith(DescriptionView.GZIPPREFIX))
			return false;
		if(description.startsWith("<html>"))
			return false;
		return true;
	}
	
	private Widget getLabel(SelectModuleItem item) {
		Widget w;
		String description = item.getDescription();
		if(description.startsWith(DescriptionView.GZIPPREFIX))
		{
			w = new DescriptionViewImpl(item.getID(), this).asWidget();
		} else
		if(description.startsWith("<html>")) {
			w = new HTML(description);
		}else
		{
			w = new Label(description);
			w.setStyleName(style.description());
		}
		return w;
	}
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	Presenter presenter;

	private String SCHOOL_MODULES;
	private TreeItem schoolMap;
	private Map<SelectModuleItem,TreeItem> inverseMap = new HashMap<SelectModuleItem, TreeItem>();

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setMenuWidget(IsWidget w) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void gotoUrl(String href) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute() {
		presenter.goTo(new ReloginPlace());
	}

	//================================================================================
    // Init and format the tree and cells
    //================================================================================
	
	private void initTree()
	{
		List<SelectModuleItem> model = this.list;
		ArrayList<SelectModuleItem> schoolModel = new ArrayList<SelectModuleItem>(model.size());
		ArrayList<SelectModuleItem> standardModel = new ArrayList<SelectModuleItem>(model.size());
		Object schoolName = "school";
		if(DWOplayer.withUser() && DWOplayer.clientfactory.getSchool() != null)
			schoolName = DWOplayer.clientfactory.getSchool().getSchoolName();
		SCHOOL_MODULES = Text.constants.schoolModules() + schoolName;
		schoolMap = new TreeItem(toSafeHTML(SCHOOL_MODULES));
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
		if(standardMap.getChildCount() != 0) tree.addItem(standardMap);
		if(schoolMap.getChildCount() != 0) tree.addItem(schoolMap);
	}

	private SafeHtml toSafeHTML(String string) {
		return new SafeHtmlBuilder().
				appendHtmlConstant("<span class='"+style.treeItem()+"'>").
				appendEscaped(string).
				appendHtmlConstant("</span>").
				toSafeHtml();
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

	private TreeItem getTreeItem(SelectModuleItem item) {
		return new TreeItem(toSafeHTML(item.getName()));
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
