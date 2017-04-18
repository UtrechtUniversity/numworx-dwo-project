package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.WaitScreen;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorView.AnchorContext;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleViewImplDesktop.SCO_TO_MODULEITEM;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedEvent;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedHandler;

public class TreeModuleViewImplDesktop  extends TreeModuleBase implements ViewModuleView.Loader, CellSelectedHandler 
{

	public class SCO_TO_MODULEITEM implements Function<List<DomScoContext>, List<SelectModuleItem>> {

		private final SelectModuleItem parent;
		public SCO_TO_MODULEITEM(SelectModuleItem item) {
			this.parent = item;
		}

		@Override
		public List<SelectModuleItem> apply(List<DomScoContext> t) {
			List<SelectModuleItem> items = new ArrayList<SelectModuleItem>(t.size());
			for(DomScoContext sco: t) {
				SelectModuleItem item = new SelectModuleItem(sco);
				item.setParent(parent);
				items.add(item);
				SelectModuleItemHolder.insert(item);
			}
			initTree(items, inverseMap.get(parent), true);
			return items;
		}

	}


	@UiField HeaderPanel  navigationHeaderPanel;
	@UiField Label navigationLabel;
	@UiField HeaderButton navigationBackButton;
	@UiField LayoutPanel  navigationPanel;
	
	@UiField HeaderPanel moduleHeaderPanel;
	@UiField HeaderButton moduleBackButton;
	@UiField LayoutPanel modulePanel;
	
	private final class COURSE_TO_MODULEITEM
			implements Function<List<DomCourseStudent>, List<SelectModuleItem>> {
		private final SelectModuleItem item;
		private final boolean open;

		private COURSE_TO_MODULEITEM(SelectModuleItem item, boolean open) {
			this.item = item;
			this.open = open;
		}

		@Override
		public List<SelectModuleItem> apply(List<DomCourseStudent> t) {
			List<SelectModuleItem> items = new ArrayList<SelectModuleItem>(t.size());
			for (Iterator<DomCourseStudent> iterator = t.iterator(); iterator.hasNext();) {
				DomCourseStudent map =  iterator.next();
				SelectModuleItem item = new SelectModuleItem(map,(DomClassCourse)null);
				item.setParent(this.item);
				SelectModuleItemHolder.insert(item);
				items.add(item);
			}
			initTree(items, inverseMap.get(item),open); // FIXME tree= inverseMap.get() is null
			return items;
		}
	}

	class ModuleAnchorContext implements AnchorView.AnchorContext {

		@Override
		public void gotoUrl(String href) {
			if(href.startsWith("goto:")) {
				gotoSelected(href,selected);
			}
		}	
	}
	
	class TreeAnchorContext implements AnchorView.AnchorContext {
		AnchorView.AnchorContext delegate;

		public void gotoUrl(String href) {
			if(href.startsWith("goto:."))
				delegate.gotoUrl(href);
			else
			if(href.startsWith("goto:")) 
			{
				gotoSelected(href, selected.getParent());
				
			}
		}

		TreeAnchorContext(AnchorContext delegate) {
			this.delegate = delegate;
		}
		
		
	}
	
	
	ViewModuleViewImpl loadedModule = null;
	
	@UiField SimplePanel container;
	ModuleViewImpl module = new ModuleViewImpl();
	
	@UiField Tree tree;
	TreeItem standardMap, schoolMap;
	
	private HashMap<SelectModuleItem, TreeItem> inverseMap = new HashMap<SelectModuleItem, TreeItem>();
	private List<SelectModuleItem> model, standardModel, schoolModel;
	private SelectModuleItem selected;
	private Presenter presenter;
	private String SCHOOL_MODULES;
	private Timer tm;
	
	//================================================================================
    // Constructor and UiBinder 
    //================================================================================

	private static TreeModuleViewImplUiBinder uiBinder = GWT
			.create(TreeModuleViewImplUiBinder.class);

	interface TreeModuleViewImplUiBinder extends
			UiBinder<Widget, TreeModuleViewImplDesktop> {
	}
	
	public TreeModuleViewImplDesktop()
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		// Don't use basic button layout, but set FA-style backbutton,
		// Should move this to a Fa Class wrapper
		this.moduleBackButton.getElement().setInnerHTML("<span class='fa fa-2x fa-chevron-left' ></span>");
		this.navigationBackButton.getElement().setInnerHTML("<span class='fa fa-2x fa-power-off' ></span>");
		
		module.list.addStyleName("tree-cells");
		module.list.addCellSelectedHandler(this);
		module.setAnchorContext(new ModuleAnchorContext());
		standardMap = new TreeItem(TEMPLATE.content(Text.constants.standaardModules(), "fa-folder"));
		standardMap.setState(true);
	}

	//================================================================================
    // UiHandlers used by this implementation
    //================================================================================

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
					close();
					container.setWidget(module);
					SelectModuleItem root = SelectModuleItemHolder
							.getItemByID("0");
					module.setDescription(root); // Uit het profiel halen!
					if (item == schoolMap) {
						addChildren(schoolModel);
						navigationLabel.setText(SCHOOL_MODULES);
						tree.setSelectedItem(schoolMap, false);
						schoolMap.setState(true, false);
					} else if (item == standardMap) {
						addChildren(standardModel);
						navigationLabel.setText(root.getName());
						tree.setSelectedItem(standardMap, false);
						standardMap.setState(true, false);
					}
				}
			}
		});
	}

	// werkt niet? @UiHandler("cells")
	public void onCellSelected(CellSelectedEvent event) {
		int index = event.getIndex();
		SelectModuleItem o = module.items.get(index);
		setTreeSelectedItem(o);
		selectItem(o);
	}

	@UiHandler("navigationBackButton")
	public void onTap(TapEvent event) {
		// logout
		//DWOplayer.profiledata = null;
		if(loadedModule != null)
		  OpdrNav.defer(new ScheduledCommand() {
			public void execute() {
				TreeModuleViewImplDesktop.this.presenter.goTo(new LoginPlace());
			}
		}); 
		else
			this.presenter.goTo(new LoginPlace());
	}
	
	@UiHandler("moduleBackButton")
	public void onModuleTap(TapEvent event) {
			toggleNavigationPanel();
	}
	
	private void selectItem(SelectModuleItem o) {
		Place place;
		switch(o.getType()) {
		default:
		case ROOT:
			place = new TreeModulePlace("0");
			break;
		case SCO:
			WaitScreen.instance().w();
			close(); // since we set "loadedModule" to a new value.
			selected = o;
			container.clear();
			Date notAfter = o.getNotAfter();
			if(notAfter != null && notAfter.getTime() < System.currentTimeMillis() + DWOplayer.timezone)
			{
				place = new TreeModulePlace(o.getParent().getID());
				WaitScreen.instance().hide();
				break;
			} else if (notAfter != null) {
				long timeToGo = notAfter.getTime()-System.currentTimeMillis() - DWOplayer.timezone;
				timeToGo = Math.min(timeToGo, Integer.MAX_VALUE);
				timeToGo = Math.max(timeToGo,1); 
				final TreeModulePlace gotoPlace = new TreeModulePlace(o.getParent().getID());
				tm = new Timer() {

					@Override
					public void run() {
						tm = null;
						slideNavigationIn();
						presenter.goTo(gotoPlace);
					}};
				tm.schedule((int)timeToGo); 
			}
			ViewModuleViewImpl viewModuleViewImpl = new ViewModuleViewImpl(false);
			DWOplayer.clientfactory.setEntryView(viewModuleViewImpl);
			viewModuleViewImpl.setAnchorContext(new TreeAnchorContext(viewModuleViewImpl.getAnchorContext()));
			loadedModule = viewModuleViewImpl.initialize(this);
			viewModuleViewImpl.setApi(DWOplayer.api);
			viewModuleViewImpl.setWindowTop(41);
			viewModuleViewImpl.zetMaat();
			container.setWidget(loadedModule.asWidget());
			
			
			place = new ViewModulePlace(o.getID());
			
		
			ViewModulePlace selectedModulePlace = (ViewModulePlace) place;
			String id = selectedModulePlace.getToken();
			viewModuleViewImpl.setUnitId(id);
			final SelectModuleItem item = SelectModuleItemHolder.getScoByID(id);
			
			//
			moduleHeaderPanel.setCenter(item.getName());
			
			DWOplayer.api.setScoID(id);
			AsyncCallback<Void> callback = new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					loadedModule.setupModule(item.getName(), item.getFile());
					
				}

				@Override
				public void onSuccess(Void result) {
					loadedModule.setupModule(item.getName(), item.getFile());
					
					
				}
			};
			
			//loadingArea.setVisible(true);
			DWOplayer.api.Initialize(callback);
			
			place = null;
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
	
	//================================================================================
    // TreeModuleView Interface Methods
    //================================================================================

	@Override
	public void render(List<SelectModuleItem> currentModel)
	{
		
		if (currentModel.isEmpty() )
		{
			tree.removeItems();
			return;
		}
		if (model != currentModel)
		{
			tree.removeItems();
			Logger.getLogger("TreeModuleViewImplDesktop").fine("clear inverseMap");
			inverseMap.clear();
			model = currentModel;
			//sort(model);
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
		container.setWidget(module);
		
		// TODO iets met tree.ensureSelectedItemVisible() na setselectedItem(item)
		if (item != null)
		{
			navigationLabel.setText(item.getName());
			if ( item.isFromSchool() ) // set header of module..
				moduleHeaderPanel.setCenter(Text.constants.schoolModules());
			else
				moduleHeaderPanel.setCenter(Text.constants.standaardModules());
			
			selected = item;
			module.setDescription(item);
			if(item.getType() == SelectModuleItem.Type.FOLDER)
			{
				loadChildren(item);
			} else if(item.getType() == SelectModuleItem.Type.MODULE)
			{	if(item.getChildren() == null)
					loadScos(item);
				else
				{
					if(item.showChildren())
						addChildren(item.getChildren());
					else
						addChildren(Collections.<SelectModuleItem> emptyList());
				}
			} else if(item.getType() == SelectModuleItem.Type.ROOT )
			{
				addChildren(standardModel);
			}
			setTreeSelectedItem(item);
		}
		else
		{
			selected = SelectModuleItem.ROOT;
			module.setDescription(SelectModuleItem.ROOT);
			//container.setWidget(new Label("DWO standaard modules")); // Uit het profiel halen!
			addChildren(standardModel);
		}
	}

	private void setTreeSelectedItem(SelectModuleItem item) {
		TreeItem node = inverseMap.get(item);
		tree.setSelectedItem(null, false);
		if(node != null) {
			
			tree.setSelectedItem(node, false);
			tree.ensureSelectedItemVisible();
		}
	}
	
	//================================================================================
    // Animations
    //================================================================================
	private int animation_duration = 200; // XXX is there a gwt standard value ?
	//private Comparator<SelectModuleItem> sortModel;
	
	private void toggleNavigationPanel(){
		if (navigationPanel.getAbsoluteLeft() == 0) {
			slideNavigationOut();
				
		} 	else if (navigationPanel.getAbsoluteLeft() == -350){
				
			slideNavigationIn();	
		}
		
	}
	
	private void slideNavigationOut() {
		if (navigationPanel.getAbsoluteLeft() == -0) {
			slideNavigationToLeftAnimation anim= new slideNavigationToLeftAnimation(-350);
			anim.run(animation_duration);
			this.moduleBackButton.getElement().setInnerHTML("<span class='fa fa-2x fa-chevron-right' ></span>");	
	
		}
	}
	
	private void slideNavigationIn() {
		
		if (navigationPanel.getAbsoluteLeft() == -350) {
			slideNavigationToLeftAnimation anim = new slideNavigationToLeftAnimation(350);
			anim.run(animation_duration);	
			this.moduleBackButton.getElement().setInnerHTML("<span class='fa fa-2x fa-chevron-left' ></span>");
		}
	}
	
	public class slideNavigationToLeftAnimation  extends Animation {
        // initial offset of the Panels
        private int startOffsetNavigation = 0;
        private int startOffsetModule = 0;
        
        // desired offset of the panels, 
        private int desiredOffset = 0;
   
        public slideNavigationToLeftAnimation(int desiredOffset) {

        	this.startOffsetNavigation =  navigationPanel.getAbsoluteLeft();
        	this.startOffsetModule = modulePanel.getAbsoluteLeft();
        
            this.desiredOffset=  desiredOffset;
           
        }
        
        
        @Override
        protected void onUpdate(double progress) {
        	
        	
        	//  move the navigation panel
            double offset =  (this.startOffsetNavigation  + (this.desiredOffset * progress)) ;
            navigationPanel.getElement().getStyle().setLeft(offset, Unit.PX);
            
            // move the module Panel
            offset =  (this.startOffsetModule  + (this.desiredOffset * progress))  ;
            modulePanel.getElement().getStyle().setLeft(offset, Unit.PX);
            
        }
   
     
    }
	

	//================================================================================
    // Init and format the tree and cells
    //================================================================================
	
	private void initTree()
	{
		schoolModel = new ArrayList(model.size());
		standardModel = new ArrayList(model.size());
		Object schoolName = "school";
		if(DWOplayer.withUser() && DWOplayer.clientfactory.getSchool() != null)
			schoolName = DWOplayer.clientfactory.getSchool().getSchoolName();
		SCHOOL_MODULES = Text.constants.schoolModules() + schoolName;
		schoolMap = new TreeItem(TEMPLATE.content(SCHOOL_MODULES, "fa-folder"));
		schoolMap.setState(true);
		standardMap.removeItems();
		tree.removeItems();
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
		Promise<List<SelectModuleItem>> promise = item.getChildrenAsync();

		if(promise == null || (promise.isDone() && promise.getFailure() != null)) {
			promise = DWOplayer.clientfactory.getRPCHandler().getCourses(item.getID())
					.map(new COURSE_TO_MODULEITEM(item, true));
			item.setChildrenAsync(promise);
			promise
			.then(new Success<List<SelectModuleItem>, List<SelectModuleItem>>() {

				@Override
				public Promise<List<SelectModuleItem>> call(Promise<List<SelectModuleItem>> resolved) throws Exception {
					for(SelectModuleItem item: resolved.getValue()) {
						if(item.getType() == SelectModuleItem.Type.FOLDER) {
							if(item.getChildrenAsync() == null) {
								item.setChildrenAsync(DWOplayer.clientfactory.getRPCHandler().getCourses(item.getID())
										.map(new COURSE_TO_MODULEITEM(item, false)));
							}
						}
					}	
					return resolved;
				}}, new Failure() {
					
					@Override
					public void fail(Promise<?> resolved) throws Exception {
						Window.alert(resolved.getFailure().toString());
						item.setChildrenAsync(null);
					}
				});
		}
		promise.then(new Success<List<SelectModuleItem>, Void>() {

			@Override
			public Promise<Void> call(Promise<List<SelectModuleItem>> resolved) throws Exception {
				addChildren(resolved.getValue());
				return null;
			}
			
		});
	}
	
	private void loadScos(final SelectModuleItem item) {
		Promise<List<SelectModuleItem>> promise = item.getChildrenAsync();

		if(promise == null || (promise.isDone() && promise.getFailure() != null)) {
			promise = DWOplayer.clientfactory.getRPCHandler().getScos(item.getID())
					.map(new SCO_TO_MODULEITEM(item));
			item.setChildrenAsync(promise);
		}
// Same as above
		promise.then(new Success<List<SelectModuleItem>, Void>() {

			@Override
			public Promise<Void> call(Promise<List<SelectModuleItem>> resolved) throws Exception {
				if(item.showChildren())
					addChildren(resolved.getValue());
				else
					addChildren(Collections.<SelectModuleItem> emptyList());
				return null;
			}
			
		});
	}
	
//	class GetChildrenCourses implements AsyncCallback<List<Map<String,Object>>> {
//
//		private SelectModuleItem parent;
//		private TreeItem tree;
//		
//		public GetChildrenCourses(SelectModuleItem item) {
//			parent = item;
//			tree = inverseMap.get(parent);
//			if(tree == null) {
//				Logger.getLogger("TreeModuleViewImplDesktop").severe(item + " has null tree");
//				throw new NullPointerException();
//			}
//		}
//
//		@Override
//		public void onFailure(Throwable caught) {
//			Window.alert(caught.toString());
//		}
//
//		@Override
//		public void onSuccess(List<Map<String,Object>> result) {
//			ArrayList<SelectModuleItem> items = new ArrayList<SelectModuleItem>(result.size());
//			for (Iterator<Map<String, Object>> iterator = result.iterator(); iterator.hasNext();) {
//				Map<String, Object> map = (Map<String, Object>) iterator.next();
//				SelectModuleItem item = new SelectModuleItem(map, SelectModuleItem.Type.MODULE);
//				item.setParent(parent);
//				SelectModuleItemHolder.insert(item);
//				items.add(item);
//			}
//			parent.setChildren(items);
//			addChildren(items);
//			initTree(items, tree, true); // FIXME tree= inverseMap.get() is null
//		}
//		
//	};

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
			initTree(items, inverseMap.get(parent), true);
		}
		
	};
	
	private void addChildren(List<SelectModuleItem> list) {
		if(list == null)
			list = Collections.emptyList();
//		sort(list);
		module.render(list);
	}

	@Override
	public void viewModuleViewSetupDone() {
		// slide out the new area
		slideNavigationOut(); 
		WaitScreen.instance().hide();
	}

	@Override
	public void close() {
		if( tm != null) {
			tm.cancel();
			tm = null;
		}
		if(loadedModule != null) {
			loadedModule.close();
			selected.setScore(loadedModule.getScoreRaw());
			loadedModule = null;
			DWOplayer.clientfactory.setEntryView(null);
		}
		
	}

	@Override
	public void setMenuWidget(IsWidget w) {
		moduleHeaderPanel.setRightWidget(Widget.asWidgetOrNull(w));
	}

	void gotoSelected(String href, SelectModuleItem parent) {
		String page = "";
		href = href.substring(5);
		int dot = href.lastIndexOf('.');
		if(dot > 0) {
			page = href.substring(dot+1);
			href = href.substring(0,dot);
		}
		// try numeric first
		List<SelectModuleItem> children = parent.getChildren();
		try { 
			int sconr = Integer.parseInt(href)-1;
			SelectModuleItem is = children.get(sconr);
			setTreeSelectedItem(is);
			selectItem(is);
		} catch (Exception ex) {
			for (Iterator<SelectModuleItem> iterator = children.iterator(); iterator.hasNext();) {
				SelectModuleItem is = iterator.next();
				if(is.getName().startsWith(href))
				{
					setTreeSelectedItem(is);
					selectItem(is);
					break;
				}
			}
			
		}
	}

//	@Override
//	public void setSortModel(Comparator<SelectModuleItem> sorter) {
//		this.sortModel = sorter;
//	}

//	private void sort(List<SelectModuleItem> items) {
//		if(sortModel != null) {
//			Collections.sort(items, sortModel);
//		}
//	}

}