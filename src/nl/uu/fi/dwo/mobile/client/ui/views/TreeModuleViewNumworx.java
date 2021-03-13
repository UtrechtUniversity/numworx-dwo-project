package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SetSelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.googlecode.mgwt.ui.client.MGWT;

import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SCO_TO_MODULEITEM;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem.Type;
import nl.uu.fi.dwo.mobile.client.ui.places.ReloginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.rest.dom.entities.util.ScoType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

public class TreeModuleViewNumworx extends TreeModuleBase implements AnchorContext {

	static final Logger LOG = Logger.getLogger(TreeModuleViewNumworx.class.getName());
	
	private static final class ChildrenFailure implements Failure {
		private final SelectModuleItem parent;

		private ChildrenFailure(SelectModuleItem parent) {
			this.parent = parent;
		}

		@Override
		public void fail(Promise<?> resolved) throws Exception {
			Throwable failure = resolved.getFailure();
			parent.setChildrenAsync(null);
			if (failure instanceof Dwo2Exception) {
				Dwo2ExceptionCode code = ((Dwo2Exception) failure).getDwo2Code();
				switch(code) {
				case Exam_AuthenticationError:
				case Exam_InvalidSession: 
					//parent.setChildren(Collections.emptyList());
					return; // No message
				default:
				}
			}
			Window.alert(failure.toString());
		}
	}



	final class ProvideCells implements Success<List<SelectModuleItem>, Void> {
		@Override
		public Promise<Void> call(
				Promise<List<SelectModuleItem>> resolved)
				throws Exception {
			List<SelectModuleItem> value = resolved.getValue();
			((SetSelectionModel<?>) tiles.getSelectionModel()).clear();
			tiles.setRowData(value);
			tiles.redraw();
			return null;
		}
	}
	


	//int flip;

	class TileCell extends AbstractCell<SelectModuleItem> {

		@Override
		public void render(Context context,
				SelectModuleItem value, SafeHtmlBuilder sb) {
		    Type typeof = value.getType();
			sb.appendHtmlConstant("<div class='"+style.tile()+"'>");
			  sb.appendHtmlConstant("<div class='" + style.tileHeader() + "'><span class='" + style.tileSpan() + "'>");
			  if(typeof == Type.SCO)
			  {
				  sb.append(value.getSequencenr()).appendHtmlConstant(". ");
			  }
			  sb.appendEscaped(value.getName());
			  sb.appendHtmlConstant("</span></div>");

			  sb.appendHtmlConstant("<div class='" + style.tileBody() + "'>");
			    String description = value.getDescription();
				if(true || description.isEmpty()||description.startsWith(DescriptionView.GZIPPREFIX)) {
					sb.appendHtmlConstant("<icon class='" + style.tileBodySpan() + "'>");
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
			    		if(value.getImage() != null) {
			    			String html = "<img src='"+value.getImage()+"' class='"+style.tileBodyImg() + "'/>";
			    			sb.appendHtmlConstant(html);
			    		} else {
			    			sb.appendHtmlConstant("<img style='height: 85px' src='"
			    				+ r("images/numworx/activiteit_numworx.svg")
			    				+ "' class='" + style.tileBodyImg()
			    				+ "' />");
			    		}
			    		break;
			    	default:
			    	}
			    	sb.appendHtmlConstant("</icon>");
			    	
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
			  		//if(score < 20) type = "fout";
			  		//else 
			  		if(score >= 100) type = "goed";
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
// TODO wat is het juiste antwoord?
				  	if(value.getScoType() == ScoType.EINDTOETS || value.getScoType() == ScoType.ZELFTOETS)
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
			super("click", "pointerdown", "pointerup");
		}

		boolean pointer;
		int x, y;
		final int RADIUS = 20;
		@Override
		public void onBrowserEvent(Context context, Element parent, SelectModuleItem value, NativeEvent event,
				ValueUpdater<SelectModuleItem> valueUpdater) {
		    String eventType = event.getType();
			if( "pointerdown".equals(eventType)) {
				pointer = true;
				x = event.getScreenX();
				y = event.getScreenY();
			}
		    if(click(eventType) && close(event)) {
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
		    
		    
		    if(click(eventType) && close(event)) {
		    	Place place;
		    	if (value.getPlace() != null)
		    	  place = value.getPlace();
		    	else if(value.getType() == Type.SCO)
		    		place = new ViewModulePlace(value.getID());
		    	else
		    		place = new TreeModulePlace(value.getID());
		    	presenter.goTo(place);
		    	return;
		    }
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}

		private boolean close(NativeEvent event) {
			int r = Math.abs(x - event.getScreenX()) + Math.abs(y - event.getScreenY());
			return !pointer || r < RADIUS;
		}

		public boolean click(String eventType) {
			return "click".equals(eventType) && !pointer || "pointerup".equals(eventType);
		}
		
		
	}
	

	private static TreeModuleViewNumworxUiBinder uiBinder = GWT.create(TreeModuleViewNumworxUiBinder.class);

	@UiField DockLayoutPanel root;
	@UiField(provided=true)
	CellList<SelectModuleItem> tiles;
	@UiField HTML title;
	@UiField SimplePanel description;
	@UiField Image favIcon;
	@UiField TreeModuleViewNumworxCss style;
	@UiField ScrollPanel centerPanel;
	NavigationViewNumworx westPanel;
	
	HeaderView header;
	
	private List<SelectModuleItem> list;

	@UiField(provided=true) String pfx;

    final private ClientFactory clientFactory;
	
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
	
	private static String r(String resource) {
		return GWT.getModuleBaseURL() + "../" + resource;
	}

	interface TreeModuleViewNumworxUiBinder extends UiBinder<Widget, TreeModuleViewNumworx> {
	}

	@Inject
	public TreeModuleViewNumworx(HeaderView headerView, NavigationViewNumworx navigationView, ClientFactory clientFactory) {
	    this.clientFactory = clientFactory;
		HorizontalCellListResources cellResources;
		cellResources = GWT.create(HorizontalCellListResources.class);
		tiles = new CellList<SelectModuleItem>(new TileCell(), cellResources);
		tiles.setKeyboardSelectionPolicy(com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);
		tiles.addStyleName(cellResources.cellListStyle().tileCellList());
		pfx = r("");
		header = headerView;
		westPanel = navigationView;
		initWidget(uiBinder.createAndBindUi(this));

		SingleSelectionModel<SelectModuleItem> model = new SingleSelectionModel<SelectModuleItem>(westPanel.keyprovider);
		tiles.setSelectionModel(model);

		root.forceLayout();
// Strategy stuff desktop/tablet
		selectStrategy();
	}


	private void selectStrategy() {
		boolean desktop = MGWT.getOsDetection().isDesktop() /*&& false*/;
		if (!clientFactory.isIconizer()) desktop = false; // platte versie bij klas zonder tree
		navigation = desktop ? new TreeNavStrategy() : new ListNavStrategy();
	}

	interface NavStrategy {
		
	}
	
	NavStrategy navigation;

	private SelectModuleItem selection;

	@UiField Text rb;

	
	class ListNavStrategy implements NavStrategy {
		ListNavStrategy() {
			westPanel.showCells();
		}
	}
	
	class TreeNavStrategy implements NavStrategy {
		TreeNavStrategy() {
			westPanel.showTree();
			//root.setWidgetSize(westPanel, 300);
		}
	}
	
	@Override
	public void render(List<SelectModuleItem> currentModel) {
		
		header.show();		
		boolean nieuw = this.list != currentModel;
		
		
		
		this.list = currentModel;
		westPanel.setCells(list);
		
		//Scheduler.get().scheduleDeferred(cmd);
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

		if(nieuw)
			westPanel.initTree(currentModel);
	}

	Promise<List<SelectModuleItem>> getChildrenPromise(final SelectModuleItem parent) {
		Promise<List<SelectModuleItem>> promise = parent.getChildrenAsync();

		if(promise == null || (promise.isDone() && promise.getFailure() != null)) {
			promise = clientFactory.getRPCHandler().getCourses(parent.original())
					.map(new COURSE_TO_MODULEITEM(parent));
			parent.setChildrenAsync(promise);
			promise
			.then(new Success<List<SelectModuleItem>, List<SelectModuleItem>>() {

				@Override
				public Promise<List<SelectModuleItem>> call(Promise<List<SelectModuleItem>> resolved) throws Exception {
					for(SelectModuleItem item: resolved.getValue()) {
						if(item.getType() == SelectModuleItem.Type.FOLDER) {
							if(item.getChildrenAsync() == null) {
								item.setChildrenAsync(clientFactory.getRPCHandler().getCourses(item.original())
										.map(new COURSE_TO_MODULEITEM(item)));
							}
						}
					}	
					return resolved;
				}}, new ChildrenFailure(parent));
		}
		return promise;
	}
	Promise<List<SelectModuleItem>> getScosPromise(final SelectModuleItem parent) {
		Promise<List<SelectModuleItem>> promise = parent.getChildrenAsync();

		if(promise == null || (promise.isDone() && promise.getFailure() != null)) {
			promise = clientFactory.getRPCHandler().getScos(parent.getID())
					.map(new SCO_TO_MODULEITEM(parent));
			parent.setChildrenAsync(promise);
			promise
			.then(null, new ChildrenFailure(parent));
		}
		return promise;
	}
	
	
	@Override
	public void selectModule(SelectModuleItem item) {
		this.selection = item;
		((SetSelectionModel<?>) tiles.getSelectionModel()).clear();
		tiles.setRowData(Collections.emptyList());
		boolean hasImage = item.getImage() != null;
		westPanel.selectModule(item);
		switch(item.getType()) {
		case ROOT:
			header.setUpPlace(header.getHomePlace());
			title.setText(item.getName());
			description.setWidget(getLabel(item));
			favIcon.setVisible(false);
			centerPanel.setStyleName(style.centerBackground(), true);
			centerPanel.setStyleName(style.folderBackground(), false);
			((SetSelectionModel<?>) westPanel.cells.getSelectionModel()).clear();
			break;
		case SEARCH:
			((SetSelectionModel<?>) westPanel.cells.getSelectionModel()).clear();
		case FOLDER:
				title.setText(item.getName());
				String url = (hasImage) ? item.getImage(): r("images/courses/2.png");
				favIcon.setUrl(url);
//				flip = (flip%5)+1; //flip=1;
				favIcon.setVisible( (hasImage /*|| flip!=1*/) && isLabel(item));
				centerPanel.setStyleName(style.folderBackground(), !hasImage/* && flip==1*/);
				centerPanel.setStyleName(style.centerBackground(), false);
				description.setWidget(getLabel(item));
				if(item.showChildren())
				{	TreeItem parent = westPanel.inverseMap.get(item);
					getChildrenPromise(item).recover(p -> Collections.emptyList())
					.then(westPanel.new ProvideTreeItems(parent))
					.then(new ProvideCells());
				}	

				favIcon.getParent().setStyleName(style.faviconOFF(), !isLabel(item));
				title.getParent().setStyleName(style.titlePanelFULL(), !isLabel(item));
				Object upId=item.getParentID();
                upId = item.getParent();
                if(upId==null) header.setUpPlace(header.getHomePlace());
                else header.setUpPlace(item.getParent().getPlace());
			break;
		case MODULE:
				title.setText(item.getName());
				description.setWidget(getLabel(item));
				url = (hasImage) ? item.getImage(): r("images/courses/1.png");
				favIcon.setUrl(url);
				favIcon.setVisible(hasImage);
				centerPanel.setStyleName(style.centerBackground(), false);
				centerPanel.setStyleName(style.folderBackground(), !hasImage);
				if(item.showChildren())
				{	TreeItem parent = westPanel.inverseMap.get(item);
					Promise<List<SelectModuleItem>> p = getScosPromise(item).recover(x -> Collections.emptyList());
					if(!item.isExam())
						p.then(westPanel.new ProvideTreeItems(parent));
					p.then(new ProvideCells());
				}
				favIcon.getParent().setStyleName(style.faviconOFF(), !isLabel(item));
				title.getParent().setStyleName(style.titlePanelFULL(), !isLabel(item));
				upId = item.getParent();
				if(upId==null) header.setUpPlace(header.getHomePlace());
				else header.setUpPlace(item.getParent().getPlace());
			break;
		default:
			
		}
		tiles.redraw();
	}

	private Promise<List<SelectModuleItem>> getChildrenOrScosPromise(SelectModuleItem parent) {
		if(parent.getType() == Type.MODULE)
			return getScosPromise(parent);
		return getChildrenPromise(parent);
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
			w.setStyleName(style.description());
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

	GotoController presenter;

	@Override
	public void setPresenter(GotoController presenter) {
		this.presenter = presenter;
		westPanel.presenter = presenter;
	}

	@Override
	public void gotoUrl(String href) {
		gotoSelected(href, selection);		
	}
	
	void gotoSelected(String href, SelectModuleItem parent) {
		//String page = "";
		href = href.substring(5);
		String location  = null;
		int dot = href.lastIndexOf('.');
		if(dot > 0) {
			//page = href.substring(dot+1);
			location = Integer.toString(Integer.parseInt(href.substring(dot+1))-1);
			href = href.substring(0,dot);
		}
		Promise<List<SelectModuleItem>> children = getChildrenOrScosPromise(parent);
		final String ref = href;
		final String loc = location;
		children.then(
			new Success<List<SelectModuleItem>,Void>() {

				@Override
				public Promise<Void> call(Promise<List<SelectModuleItem>> resolved) throws Exception {
					List<SelectModuleItem> children = resolved.getValue();
					try { 
						// try numeric first
						int sconr = Integer.parseInt(ref)-1;
						SelectModuleItem is = children.get(sconr);
						westPanel.selectItem(is,loc);
					} catch (Exception ex) {
						for (Iterator<SelectModuleItem> iterator = children.iterator(); iterator.hasNext();) {
							SelectModuleItem is = iterator.next();
							if(is.getName().startsWith(ref))
							{
								westPanel.selectItem(is,loc);
								break;
							}
						}
					}
					return null;
				}}
		);
	}

	//@Override
	public void execute() {
		presenter.goTo(new ReloginPlace());
	}


  @Override
  public void setBeheer(boolean b) {
    westPanel.setBeheer(b);
  }

  @Override
  public void showIcon(boolean b) {
    westPanel.showIcon(b);
  }
}
