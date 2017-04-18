package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleCell;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorView.AnchorContext;
import nl.uu.fi.dwo.mobile.client.ui.SCO_TO_MODULEITEM;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.place.shared.Place;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.ui.client.widget.CellList;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedEvent;

public class TreeModuleViewImplTablet  extends TreeModuleBase implements ViewModuleView.Loader, Comparator<SelectModuleItem>, AnchorContext
{

	@UiField HeaderPanel  navigationHeaderPanel;
	@UiField Label navigationLabel;
	@UiField HeaderButton navigationBackButton;
	@UiField HeaderButton navigationUpButton;
	@UiField LayoutPanel  navigationPanel;
	
	@UiField HeaderPanel moduleHeaderPanel;
	@UiField HeaderButton moduleBackButton;
	@UiField LayoutPanel modulePanel;
	@UiField ScrollPanel cellPanel;
		
	
	ViewModuleView loadedModule = null;
	
	@UiField LayoutPanel infoArea;
	@UiField HTMLPanel loadingArea;
	@UiField LayoutPanel container;
	@UiField (provided=true) CellList<SelectModuleItem> cells;
	

	
	private List<SelectModuleItem> cellItems;
	private List<SelectModuleItem> model;
	private SelectModuleItem selected;
	private Presenter presenter;
	private Timer tm;
	
	class TreeAnchorContext implements AnchorView.AnchorContext {
		AnchorView.AnchorContext delegate;

		@Override
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
			super();
			this.delegate = delegate;
		}

	}
	
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

	private void log(String log) {	
		Logger.getLogger("TreeModuleViewImplTablet").log(Level.INFO, log);
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
		log("Go up " + parent);
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
			selected = o;
			Date notAfter = o.getNotAfter();
			if(notAfter != null && notAfter.getTime() < System.currentTimeMillis() + DWOplayer.timezone)
			{
				place = new TreeModulePlace(o.getParentID());
				this.loadingArea.setVisible(false);
				break;
			} else if (notAfter != null) {
				long timeToGo = notAfter.getTime()-System.currentTimeMillis() - DWOplayer.timezone;
				timeToGo = Math.min(timeToGo, Integer.MAX_VALUE);
				timeToGo = Math.max(timeToGo,1); 
				final TreeModulePlace gotoPlace = new TreeModulePlace(o.getParentID());
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
			int h = moduleHeaderPanel.getOffsetHeight();
			log("window top = " + h);
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
	private Comparator<SelectModuleItem> sortModel;
	private SelectModuleItem infoItem;
	
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
			model = currentModel;
			sort(model);
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
		renderCell(model);
		addChildren(model);
	}

	private void initTree(List<SelectModuleItem> model) {
		if(model== null)
			model = Collections.emptyList();
		this.cellItems = model;
		renderCell(model);
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
			infoArea.clear();
			if(description != null)
			{
				Widget w;
				if(description.startsWith(DescriptionView.GZIPPREFIX))
				{
					infoItem = item;
					w = new DescriptionViewImpl(item.getID(), this).asWidget();
				} else
				if(description.startsWith("<html>")) {
					w = new HTML(description);
				}else
				{
					w = new Label(description);
				}
				w.addStyleDependentName("infoArea");
				infoArea.add(w);
			} else {
//				infoArea.add(new Label(""));
			}
			if(item.getType() == SelectModuleItem.Type.FOLDER)
			{
//				if(item.getChildren() == null)
					loadChildren(item);
//				else
//					addChildren(item.getChildren());
			} else if(item.getType() == SelectModuleItem.Type.MODULE)
			{	//if(item.getChildren() == null)
					loadScos(item);
//				else
//					addChildren(item.getChildren());
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

//	private class GetChildrenCourses implements AsyncCallback<List<Map<String,Object>>> {
//
//		private SelectModuleItem parent;
//		
//		public GetChildrenCourses(SelectModuleItem item) {
//			parent = item;
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
//			initTree(items);
//		}
//		
//	};

//	class GetChildrenScos implements AsyncCallback<List<Map<String,Object>>> {
//
//		private SelectModuleItem parent;
//		
//		public GetChildrenScos(SelectModuleItem item) {
//			parent = item;
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
//				SelectModuleItem item = new SelectModuleItem(map, SelectModuleItem.Type.SCO);
//				item.setParent(parent);
//				items.add(item);
//				SelectModuleItemHolder.insert(item);
//			}
//			parent.setChildren(items);
//			addChildren(items);
//			initTree(items);
//		}
//		
//	};
	
	private final class COURSE_TO_MODULEITEM implements Function<List<DomCourseStudent>, List<SelectModuleItem>> {
		private final SelectModuleItem item;

		private COURSE_TO_MODULEITEM(SelectModuleItem item) {
			this.item = item;
		}

		@Override
		public List<SelectModuleItem> apply(List<DomCourseStudent> t) {
			List<SelectModuleItem> items = new ArrayList<SelectModuleItem>(t.size());
			for (Iterator<DomCourseStudent> iterator = t.iterator(); iterator.hasNext();) {
				DomCourseStudent map = iterator.next();
				SelectModuleItem item = new SelectModuleItem(map, (DomClassCourse) null);
				item.setParent(this.item);
				SelectModuleItemHolder.insert(item);
				items.add(item);
			}
			return items;
		}
	}

	private void loadChildren(final SelectModuleItem item) {
		Promise<List<SelectModuleItem>> promise = item.getChildrenAsync();

		if(promise == null || (promise.isDone() && promise.getFailure() != null)) {
			promise = DWOplayer.clientfactory.getRPCHandler().getCourses(item.getID())
					.map(new COURSE_TO_MODULEITEM(item));
			item.setChildrenAsync(promise);
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
						item.setChildrenAsync(null);
					}
				});
		}
		promise.then(new Success<List<SelectModuleItem>, Void>() {

			@Override
			public Promise<Void> call(Promise<List<SelectModuleItem>> resolved) throws Exception {
				
				if(item.showChildren())
					addChildren(resolved.getValue());
				else
					addChildren(null);
				//initTree(resolved.getValue());
				return null;
			}
			
		});
	}

//	private static class SCO_TO_MODULEITEM implements Function<List<DomScoContext>, List<SelectModuleItem>> {
//
//		private final SelectModuleItem parent;
//		public SCO_TO_MODULEITEM(SelectModuleItem item) {
//			this.parent = item;
//		}
//
//		@Override
//		public List<SelectModuleItem> apply(List<DomScoContext> t) {
//			List<SelectModuleItem> items = new ArrayList<SelectModuleItem>(t.size());
//			for(DomScoContext sco: t) {
//				SelectModuleItem item = new SelectModuleItem(sco);
//				item.setParent(parent);
//				items.add(item);
//				SelectModuleItemHolder.insert(item);
//			}
//			return items;
//		}
//	}
	
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
					addChildren(null);
				//initTree(resolved.getValue());
				return null;
			}
			
		});
	}

	private void addChildren(List<SelectModuleItem> list) {
		if(list == null)
			list = Collections.emptyList();
		this.cellItems = list;
		int len = list.size();
		if(len > 2) {
			SelectModuleItem first = list.get(0);
			SelectModuleItem last  = list.get(len-1);
			if( first.isFromSchool() != last.isFromSchool()) 
			{
				list = new ArrayList<SelectModuleItem>(len + 1);
				list.addAll(cellItems);
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
				this.cellItems = list;
			}
			
		}
		renderCell(list);
		cellPanel.scrollTo(0, 0);
	}

	private void renderCell(List<SelectModuleItem> list) {
		cells.render(list);
		cellPanel.refresh();
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
		if( tm != null) {
			tm.cancel();
			tm = null;
		}
		if(loadedModule != null) {
			loadedModule.close();
			selected.setScore(loadedModule.getScoreRaw());
			loadedModule = null;
			DWOplayer.clientfactory.setEntryView(null);
			rerender();
			moduleHeaderPanel.setCenter(Text.constants.schoolModules());
		}
		container.clear();
	}

	private void rerender() {
		renderCell(cellItems); // repaint the cells, after update of score. TODO optimize (selected.isVisible?)
	}

//	@Override
//	public void setSortModel(Comparator<SelectModuleItem> sorter) {
//		this.sortModel = sorter;
//	}

	private void sort(List<SelectModuleItem> items) {
		int i = 1000;
		for (SelectModuleItem item : items) {
			if(item.getType() != SelectModuleItem.Type.SCO)
			{
				item.setSequencenr(i++);
			}
		}
		Collections.sort(items, this);
	}

	@Override
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

	@Override
	public void setMenuWidget(IsWidget w) {
		moduleHeaderPanel.setRightWidget(Widget.asWidgetOrNull(w));
	}

	@Override
	public void gotoUrl(String href) {
		gotoSelected(href, infoItem);
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
			selectItem(is);
		} catch (Exception ex) {
			for (Iterator<SelectModuleItem> iterator = children.iterator(); iterator.hasNext();) {
				SelectModuleItem is = iterator.next();
				if(is.getName().startsWith(href))
				{
					selectItem(is);
					break;
				}
		}
}
	}

}