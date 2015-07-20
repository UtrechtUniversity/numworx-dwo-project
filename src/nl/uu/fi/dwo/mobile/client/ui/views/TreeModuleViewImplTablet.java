package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleCell;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.SelectModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.CellList;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedEvent;
import com.googlecode.mgwt.ui.client.widget.celllist.HasCellSelectedHandler;

public class TreeModuleViewImplTablet  extends Composite implements TreeModuleView, ViewModuleView.Loader
{

	@UiField HeaderPanel  navigationHeaderPanel;
	@UiField Label navigationLabel;
	@UiField HeaderButton navigationBackButton;
	@UiField HeaderButton navigationUpButton;
	@UiField LayoutPanel  navigationPanel;
	
	@UiField HeaderPanel moduleHeaderPanel;
	@UiField HeaderButton moduleBackButton;
	@UiField LayoutPanel modulePanel;

		
	
	ViewModuleView loadedModule = null;
	
	@UiField LayoutPanel infoArea;
	@UiField HTMLPanel loadingArea;
	@UiField LayoutPanel container;
	@UiField (provided=true) CellList<SelectModuleItem> cells;
	

	
	private List<SelectModuleItem> cellItems;
	private List<SelectModuleItem> model;
	private Presenter presenter; 
	
	//================================================================================
    // Constructor and UiBinder 
    //================================================================================

	private static TreeModuleViewImplTabletUiBinder uiBinder = GWT
			.create(TreeModuleViewImplTabletUiBinder.class);

	interface TreeModuleViewImplTabletUiBinder extends
			UiBinder<Widget, TreeModuleViewImplTablet> {
	}
	
	public TreeModuleViewImplTablet()
	{
		cells = new CellList<SelectModuleItem>(new SelectModuleCell());
		cells.addStyleName(DWOplayer.PARAMETERS.navigationcss().bodyText());
		initWidget(uiBinder.createAndBindUi(this));
		
		// Don't use basic button layout, but set FA-style backbutton,
		// Should move this to a Fa Class wrapper
		this.moduleBackButton.getElement().setInnerHTML("<span class='fa fa-2x fa-chevron-left' ></span>");
		this.moduleBackButton.addStyleName(DWOplayer.PARAMETERS.navigationcss().headerText());
		this.navigationBackButton.getElement().setInnerHTML("<span class='fa fa-2x fa-power-off' ></span>");
		this.navigationUpButton.getElement().setInnerHTML("<span class='fa fa-2x fa-arrow-up' ></span>");

		this.navigationBackButton.addStyleName(DWOplayer.PARAMETERS.navigationcss().headerText());
		this.navigationUpButton.addStyleName(DWOplayer.PARAMETERS.navigationcss().headerText());
		
		//this.fullscreenButton.getElement().setInnerHTML("<span class='fa fa-expand' ></span>");

		this.loadingArea.setVisible(false);
	}

	//================================================================================
    // UiHandlers used by this implementation
    //================================================================================

	private void Log(String log) {
		
		Logger.getLogger("DWOplayer").log(Level.INFO, log);
	}
	
	@UiHandler("cells")
	public void onCellSelected(CellSelectedEvent event) {
		int index = event.getIndex();
		selectItem(this.cellItems.get(index));
	}

	@UiHandler("navigationBackButton")
	public void onNavigationTap(TapEvent event) {
		// go back one spot in the tree.
		//History.back();
		presenter.goTo(new LoginPlace());
		
	}
	@UiHandler("navigationUpButton")
	public void onNavigationUpTab(TapEvent event) {
		Log("Go up " + parent);
		if(parent != null) 
			selectItem(parent);
		else 
			presenter.goTo(new TreeModulePlace());
	}


	
	@UiHandler("moduleBackButton")
	public void onModuleTap(TapEvent event) {
		
			toggleNavigationPanel();
		
	}
	
	
	private void selectItem(SelectModuleItem o) {
		Place place = null;
		switch(o.getType()) {
		default:
		case ROOT:
			place = new TreeModulePlace("0");
			break;
		case SCO:
			//
			//load sum in the edit area
			close();
			ViewModuleViewImpl viewModuleViewImpl = new ViewModuleViewImpl(false);
			loadedModule = viewModuleViewImpl.initialize(this);
			viewModuleViewImpl.setApi(DWOplayer.api);
			int h = moduleHeaderPanel.getOffsetHeight();
			Log("window top = " + h);
			viewModuleViewImpl.setWindowTop(h);
			viewModuleViewImpl.zetMaat();
			container.add(loadedModule.asWidget());
			
			
			place = new ViewModulePlace(o.getID());
			
		
			ViewModulePlace selectedModulePlace = (ViewModulePlace) place;
			final String id = selectedModulePlace.getToken();
			final SelectModuleItem item = SelectModuleItemHolder.getScoByID(id);
			
			//
			moduleHeaderPanel.setCenter(item.getName());
			
			DWOplayer.api.setScoID(id);
			viewModuleViewImpl.setUnitId(id);
			AsyncCallback<Void> callback = new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					loadedModule.setupModule(item.getName(), item.getFile());
					//loadingArea.setVisible(false);
				}

				@Override
				public void onSuccess(Void result) {
					loadedModule.setupModule(item.getName(), item.getFile());
					//loadingArea.setVisible(false);
					
				}
			};
			
			loadingArea.setVisible(true);
			DWOplayer.api.Initialize(callback);
			
			place = null;
			
			break;
		case MODULE:
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
    // Animations
    //================================================================================
	private int animation_duration = 200; // XXX is there a gwt standard value ?
	private SelectModuleItem parent;
	
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
    // TreeModuleView Interface Methods
    //================================================================================

	@Override
	public void render(List<SelectModuleItem> currentModel)
	{
		
		if (currentModel.isEmpty() )
			return;
		if (model != currentModel)
		{
			//tree.removeItems();
			//inverseMap.clear();
			model = currentModel;	
		}
	
	}
	
	//================================================================================
    // Init and format the tree and cells
    //================================================================================

	private void initTree()
	{
		
		if(model== null)
			model = Collections.emptyList();
		this.cellItems = model;
		cells.render(model);
		addChildren(model);
	}

	private void initTree(List<SelectModuleItem> model) {
		if(model== null)
			model = Collections.emptyList();
		this.cellItems = model;
		cells.render(model);
		addChildren(model);
		
	}

	@Override
	public void selectModule(SelectModuleItem item)
	{
		
		// TODO iets met tree.ensureSelectedItemVisible() na setselectedItem(item)
		if (item != null)
		{
			navigationLabel.setText(item.getName());
			parent = item.getParent();
			navigationBackButton.setVisible(true);
			navigationUpButton.setVisible(item.getType() != SelectModuleItem.Type.ROOT);
			String description = item.getDescription();
			if(description != null)
			{
				if(description.startsWith(DescriptionView.GZIPPREFIX))
				{
					infoArea.clear();
					infoArea.add(new DescriptionViewImpl(item.getID()).asWidget());
				} else
				if(description.startsWith("<html>")) {
					infoArea.clear();
					infoArea.add(new HTML(description).asWidget());
				}else
				{
					infoArea.clear();
					infoArea.add(new Label(description).asWidget());
				}
			} else {
				infoArea.clear();
				infoArea.add(new Label(""));
			}
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
				navigationBackButton.setVisible(true); // was false
				addChildren(model);
			}
		}
		else
		{
			//container.clear();
			//container.add(new Label("DWO standaard modules").asWidget()); // Uit het profiel halen!
			
			addChildren(model);
		}
		
	}
	
	//================================================================================
    // Load children and Scos
    //================================================================================

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
			initTree(items);
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
			initTree(items);
		}
		
	};
	
	
	private void loadChildren(final SelectModuleItem item) {
		GetChildrenCourses getCoursesCallback = new GetChildrenCourses(item);
		DWOplayer.clientfactory.getRPCHandler().getCourses(item.getID(), getCoursesCallback);
	}
	
	private void loadScos(final SelectModuleItem item) {
		GetChildrenScos getScosCallback = new GetChildrenScos(item);
		DWOplayer.clientfactory.getRPCHandler().getScos(item.getID(), getScosCallback);
	}

	private void addChildren(List<SelectModuleItem> list) {
		if(list == null)
			list = Collections.emptyList();
		this.cellItems = list;
		cells.render(list);
	}

	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	


	@Override
	public void viewModuleViewSetupDone() {
		// loading is done
		this.loadingArea.setVisible(false);
		slideNavigationOut();
	}

	@Override
	public void close() {
		if(loadedModule != null) {
			loadedModule.close();
			loadedModule = null;
		}
		container.clear();
	}

	@Override
	public void setSortModel(Comparator<SelectModuleItem> sorter) {
		// TODO Auto-generated method stub
		
	}

}