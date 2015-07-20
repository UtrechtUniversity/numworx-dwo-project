package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleCell;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.WaitScreen;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.SelectModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleViewImplTablet.slideNavigationToLeftAnimation;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.ui.client.widget.CellList;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedEvent;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedHandler;
import com.googlecode.mgwt.ui.client.widget.celllist.HasCellSelectedHandler;

public class TreeModuleViewImplDesktop  extends Composite implements TreeModuleView, ViewModuleView.Loader, CellSelectedHandler 
{

	@UiField HeaderPanel  navigationHeaderPanel;
	@UiField Label navigationLabel;
	@UiField HeaderButton navigationBackButton;
	@UiField LayoutPanel  navigationPanel;
	
	@UiField HeaderPanel moduleHeaderPanel;
	@UiField HeaderButton moduleBackButton;
	@UiField LayoutPanel modulePanel;
	
	
	ViewModuleView loadedModule = null;
	
	@UiField SimplePanel container;
	@UiField (provided=true) CellList<SelectModuleItem> cells;
	@UiField Tree tree;
	
	private HashMap<SelectModuleItem, TreeItem> inverseMap = new HashMap<SelectModuleItem, TreeItem>();
	private List<SelectModuleItem> cellItems;
	private List<SelectModuleItem> model;

	private Presenter presenter;
	
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
		cells = new CellList<SelectModuleItem>(new SelectModuleCell());
		initWidget(uiBinder.createAndBindUi(this));
		
		// Don't use basic button layout, but set FA-style backbutton,
		// Should move this to a Fa Class wrapper
		this.moduleBackButton.getElement().setInnerHTML("<span class='fa fa-2x fa-chevron-left' ></span>");
		this.navigationBackButton.getElement().setInnerHTML("<span class='fa fa-2x fa-power-off' ></span>");
		
		cells.addStyleName("tree-cells");
		cells.addCellSelectedHandler(this);
	}

	//================================================================================
    // UiHandlers used by this implementation
    //================================================================================

	@UiHandler("tree")
	public void onSelection(SelectionEvent<TreeItem> event)
	{
		TreeItem item = event.getSelectedItem();
		SelectModuleItem o = (SelectModuleItem) item.getUserObject();
		selectItem(o);
	}

	// werkt niet? @UiHandler("cells")
	public void onCellSelected(CellSelectedEvent event) {
		int index = event.getIndex();
		selectItem(this.cellItems.get(index));
	}

	@UiHandler("navigationBackButton")
	public void onTap(TapEvent event) {
		// logout
		DWOplayer.profiledata = null;
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
			container.clear();
			cells.getElement().getStyle().setDisplay(Display.NONE);
			ViewModuleViewImpl viewModuleViewImpl = new ViewModuleViewImpl(false);
			loadedModule = viewModuleViewImpl.initialize(this);
			viewModuleViewImpl.setApi(DWOplayer.api);
			viewModuleViewImpl.setWindowTop(41);
			viewModuleViewImpl.zetMaat();
			container.add(loadedModule.asWidget());
			
			
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
			return;
		if (model != currentModel)
		{
			tree.removeItems();
			inverseMap.clear();
			model = currentModel;
			sort(model);
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
		// TODO iets met tree.ensureSelectedItemVisible() na setselectedItem(item)
		if (item != null)
		{
			navigationLabel.setText(item.getName());
			String description = item.getDescription();
			if(description != null)
			{
				if(description.startsWith(DescriptionView.GZIPPREFIX))
				{
					container.setWidget(new DescriptionViewImpl(item.getID()));
				} else
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
			} else if(item.getType() == SelectModuleItem.Type.ROOT )
			{
				addChildren(model);
			}
			TreeItem node = inverseMap.get(item);
			tree.setSelectedItem(null, false);
			if(node != null) {
				
				tree.setSelectedItem(node, false);
				tree.ensureSelectedItemVisible();
			}
		}
		else
		{
			container.setWidget(new Label("DWO standaard modules")); // Uit het profiel halen!
			addChildren(model);
		}
	}
	
	//================================================================================
    // Animations
    //================================================================================
	private int animation_duration = 200; // XXX is there a gwt standard value ?
	private Comparator<SelectModuleItem> sortModel;
	
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
		for (SelectModuleItem item : model)
		{
			
			TreeItem treeItem = getTreeItem(item);
			treeItem.setUserObject(item);
			inverseMap.put(item, treeItem);
			tree.addItem(treeItem);
			
			if(item.getChildren() != null)
				initTree(item.getChildren(), treeItem);
		}
	}

	private void initTree(List<SelectModuleItem> model, TreeItem tree) {
		sort(model);
		tree.removeItems(); // the tree should be empty, but it is not always.
		for (SelectModuleItem item : model)
		{
			TreeItem treeItem = getTreeItem(item);
			treeItem.setUserObject(item);
			inverseMap.put(item, treeItem);
			tree.addItem(treeItem);	
			if(item.getChildren() != null)
				initTree(item.getChildren(), treeItem);
		}
		tree.setState(true);
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
		GetChildrenCourses getCoursesCallback = new GetChildrenCourses(item);
		DWOplayer.clientfactory.getRPCHandler().getCourses(item.getID(), getCoursesCallback);
	}
	
	private void loadScos(final SelectModuleItem item) {
		GetChildrenScos getScosCallback = new GetChildrenScos(item);
		DWOplayer.clientfactory.getRPCHandler().getScos(item.getID(), getScosCallback);
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
	
	private void addChildren(List<SelectModuleItem> list) {
		if(list == null)
			list = Collections.emptyList();
		sort(list);
		this.cellItems = list;
		cells.render(list);
	}

	@Override
	public void viewModuleViewSetupDone() {
		// TODO Auto-generated method stub
		
		// slide out the new area
		slideNavigationOut(); 
		WaitScreen.instance().hide();
	}

	@Override
	public void close() {
		if(loadedModule != null) {
			cells.getElement().getStyle().clearDisplay();
			loadedModule.close();
			loadedModule = null;
		}
		
	}

	@Override
	public void setSortModel(Comparator<SelectModuleItem> sorter) {
		this.sortModel = sorter;
	}

	private void sort(List<SelectModuleItem> items) {
		if(sortModel != null) {
			Collections.sort(items, sortModel);
		}
	}

}